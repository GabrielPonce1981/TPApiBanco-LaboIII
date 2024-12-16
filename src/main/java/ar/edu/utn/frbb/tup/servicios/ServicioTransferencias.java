package ar.edu.utn.frbb.tup.servicios;

import ar.edu.utn.frbb.tup.excepciones.*;
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
    private final ValidacionesServicios validar;

    public ServicioTransferencias(CuentaDao cuentaDao, MovimientosDao movimientosDao, ClienteDao clienteDao, ValidacionesServicios validar) {
        this.cuentaDao = cuentaDao;
        this.movimientosDao = movimientosDao;
        this.clienteDao = clienteDao;
        this.validar = validar;
    }

    public void realizarTransferencia(TransferenciaDto transferenciaDto) throws CuentaDistintaMonedaException, CuentaNoEncontradaException, CuentaSinDineroException, TransferenciaFailException, TransferenciaBancoNoDisponibleException {
        Cuenta cuentaOrigen = cuentaDao.findCuenta(transferenciaDto.getCbuOrigen());
        Cuenta cuentaDestino = cuentaDao.findCuenta(transferenciaDto.getCbuDestino());

        //validar que las cuentas existan y que el tipo de moneda sea el mismo
        validar.validarCuentasOrigenDestino(cuentaOrigen,cuentaDestino);
       // if(cuentaDestino.getTipoMoneda() != TipoMoneda.fromString(transferenciaDto.getTipoMoneda())) {
       //     throw new CuentaDistintaMonedaException("Las cuentas deben ser de la misma moneda");
       // }

        double montoConIntereses = procesarMontoConIntereses(transferenciaDto.getMonto(), cuentaOrigen.getTipoMoneda(), cuentaOrigen );

        //Busco los clientes para poder buscar el banco de cada cliente
        Cliente clienteOrigen = clienteDao.findCliente(cuentaOrigen.getDniTitular());
        Cliente clienteDestino = clienteDao.findCliente(cuentaDestino.getDniTitular());

        if (clienteOrigen.getBanco().equals(clienteDestino.getBanco())) {
            //realizo transferencia entre cuentas del mismo banco
            generarTransferencia(cuentaOrigen, cuentaDestino, montoConIntereses, transferenciaDto.getMonto());
        } else if (esBancoDisponible(clienteDestino.getBanco())) {
            realizarServivioBanelco(cuentaOrigen,transferenciaDto.getMonto());
        } else {
            throw new TransferenciaBancoNoDisponibleException("La transferencia no se pudo realizar, el banco destino no esta disponible para el servico solicitado");
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
        movimientosDao.saveMovimiento("Transferencia enviada", monto, cuentaOrigen.getCbu());
        movimientosDao.saveMovimiento("Transferencia recibida", monto, cuentaDestino.getCbu());
        if (montoConIntereses - monto > 0) {
            movimientosDao.saveMovimiento("Comision aplicada", montoConIntereses - monto, cuentaOrigen.getCbu());
        }
        //Se guardan las cuentas actualizadas en la base de datos
        cuentaDao.saveCuenta(cuentaOrigen);
        cuentaDao.saveCuenta(cuentaDestino);

    }


    //realizo funcion para realizar el servicio banelco que simula la transferencia entre distintos bancos
    public void realizarServivioBanelco(Cuenta cuentaOrigen, double monto) throws TransferenciaFailException, CuentaSinDineroException {
        final double limite_transferencia = 2000000;
        final double Interes_Banelco = 0.02;

        if(monto > limite_transferencia) {
            throw new TransferenciaFailException("El monto supera el límite permitido para transferencias Banelco.");
        }

        double comision = monto * Interes_Banelco;
        double montoTotal = monto + comision;

        validar.validarSaldo(cuentaOrigen, montoTotal);
        cuentaDao.deleteCuenta(cuentaOrigen.getCbu());
        cuentaOrigen.setSaldo(cuentaOrigen.getSaldo() - montoTotal);
        movimientosDao.saveMovimiento("Transferencia enviada (Banelco)", montoTotal, cuentaOrigen.getCbu());
        cuentaDao.saveCuenta(cuentaOrigen);

    }

    public boolean esBancoDisponible(String banco) {
        return banco.equals("Nacion") || banco.equals("Provincia") || banco.equals("HSBC") || banco.equals("BBVA") || banco.equals("Santander");
    }

    public double calcularInteresSobreTransferencia(double monto, TipoMoneda tipoMoneda) {
        //calcular el interés sobre la transferencia
        if (tipoMoneda == TipoMoneda.DOLARES && monto > 5000) {
            return  monto * 0.005 ;
        } else if (tipoMoneda == TipoMoneda.PESOS && monto > 1000000) {
            return monto * 0.2;
        }
        return 0;
    }

    public double procesarMontoConIntereses(double monto, TipoMoneda tipoMoneda, Cuenta cuentaOrigen) throws CuentaSinDineroException {
        double interes = calcularInteresSobreTransferencia(monto, tipoMoneda);
        double montoConIntereses = monto + interes;
        validar.validarSaldo(cuentaOrigen, montoConIntereses);
        return montoConIntereses;
    }

}
