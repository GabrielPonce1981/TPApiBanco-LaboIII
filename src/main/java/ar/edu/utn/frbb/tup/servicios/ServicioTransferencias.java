package ar.edu.utn.frbb.tup.servicios;

import ar.edu.utn.frbb.tup.excepciones.CuentaDistintaMonedaException;
import ar.edu.utn.frbb.tup.excepciones.CuentaNoEncontradaException;
import ar.edu.utn.frbb.tup.excepciones.CuentaSinDineroException;
import ar.edu.utn.frbb.tup.excepciones.TransferenciaFailException;
import ar.edu.utn.frbb.tup.modelos.*;
import ar.edu.utn.frbb.tup.persistencia.ClienteDao;
import ar.edu.utn.frbb.tup.persistencia.CuentaDao;
import ar.edu.utn.frbb.tup.persistencia.MovimientosDao;
import ar.edu.utn.frbb.tup.presentacion.DTOs.CuentaDto;
import ar.edu.utn.frbb.tup.presentacion.DTOs.TransferenciaDto;
import org.springframework.stereotype.Component;


import java.time.LocalDate;
import java.time.LocalTime;

@Component
public class ServicioTransferencias {
    private final CuentaDao cuentaDao;
    private final MovimientosDao movimientosDao;
    private final ClienteDao clienteDao;
    ValidacionesServicios validar = new ValidacionesServicios();

    public ServicioTransferencias(CuentaDao cuentaDao, MovimientosDao movimientosDao, ClienteDao clienteDao) {
        this.cuentaDao = cuentaDao;
        this.movimientosDao = movimientosDao;
        this.clienteDao = clienteDao;
    }

    public void inicializarTransferencias() {
        movimientosDao.inicializarMovimientos();
    }

    public void realizarTransferencia(TransferenciaDto transferenciaDto) throws CuentaDistintaMonedaException, CuentaNoEncontradaException, CuentaSinDineroException, TransferenciaFailException {
        Cuenta cuentaOrigen = cuentaDao.findCuenta(transferenciaDto.getCbuOrigen());
        Cuenta cuentaDestino = cuentaDao.findCuenta(transferenciaDto.getCbuDestino());

        //validar que las cuentas existan y que el tipo de moneda sea el mismo
        validar.validarCuentasOrigenDestino(cuentaOrigen,cuentaDestino);
        //validar que el saldo de la cuenta origen no sea menor al monto de la transferencia
        validar.validarSaldoTransferencia(cuentaOrigen, transferenciaDto);

        //traigo el monto para calcular el interes y lo agrego al monto de transferencia
        double monto = transferenciaDto.getMonto();
        double montoConIntereses = calcularInteresSobreTransferencia(transferenciaDto.getMonto(), cuentaOrigen.getTipoMoneda());

        //Busco los clientes para poder buscar el banco de cada cliente
        Cliente clienteOrigen = clienteDao.findCliente(cuentaOrigen.getDniTitular());
        Cliente clienteDestino = clienteDao.findCliente(cuentaDestino.getDniTitular());

        if (clienteOrigen.getBanco().equals(clienteDestino.getBanco())) {
            generarTransferencia(cuentaOrigen, cuentaDestino, montoConIntereses, monto);
        } else {
            realizarServivioBanelco(cuentaOrigen, cuentaDestino, monto);
        }
    }


    //realizo funcion para generar transferencia, cambiar los montos de las cuentas y guardar el movimiento
    public void generarTransferencia(Cuenta cuentaOrigen, Cuenta cuentaDestino, double montoConIntereses, double monto) {
        //borro las cuentas de la base de datos
        cuentaDao.deleteCuenta(cuentaOrigen.getCbu());
        cuentaDao.deleteCuenta(cuentaDestino.getCbu());

        //Actualizar los saldos de las cuentas
        cuentaOrigen.setSaldo(cuentaOrigen.getSaldo() - montoConIntereses);
        cuentaDestino.setSaldo(cuentaDestino.getSaldo() + monto);

        //Se registra los movimientos en ambas cuentas
        movimientosDao.saveMovimiento("Transferencia enviada", montoConIntereses, cuentaOrigen.getCbu());
        movimientosDao.saveMovimiento("Transferencia recibida", monto, cuentaDestino.getCbu());

        //Se guardan las cuentas actualizadas en la base de datos
        cuentaDao.saveCuenta(cuentaOrigen);
        cuentaDao.saveCuenta(cuentaDestino);

    }



    //realizo funcion para realizar el servicio banelco que simula la transferencia entre distintos bancos
    public void realizarServivioBanelco(Cuenta cuentaOrigen, Cuenta cuentaDestino, double monto) throws TransferenciaFailException {
        double limiteAtransferir = 2000000;
        double comision = 0.02 * monto;
        double montoTotal = monto + comision;

        if (cuentaOrigen.getSaldo() >= montoTotal && montoTotal <= limiteAtransferir) {
            generarTransferencia(cuentaOrigen, cuentaDestino, montoTotal, monto);
        }else{
            throw new TransferenciaFailException("La transferencia falló, el monto es mayor al límite permitido.");
        }

    }


    public double calcularInteresSobreTransferencia(double monto, TipoMoneda tipoMoneda) {
        //calcular el interés sobre la transferencia
        if (tipoMoneda == TipoMoneda.DOLARES && monto > 5000) {
            return monto + (monto * 0.005) ;
        } else if (tipoMoneda == TipoMoneda.PESOS && monto > 1000000) {
            return monto + (monto * 0.2);
        }
        // Si no se aplica interés, devolver el monto original
        return monto;
    }

}
