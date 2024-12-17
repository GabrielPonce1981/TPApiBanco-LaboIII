package ar.edu.utn.frbb.tup.servicios;

import ar.edu.utn.frbb.tup.excepciones.*;
import ar.edu.utn.frbb.tup.modelos.*;
import ar.edu.utn.frbb.tup.persistencia.ClienteDao;
import ar.edu.utn.frbb.tup.persistencia.CuentaDao;
import ar.edu.utn.frbb.tup.persistencia.MovimientosDao;
import ar.edu.utn.frbb.tup.presentacion.DTOs.TransferenciaDto;
import org.springframework.stereotype.Component;

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

        // Obtener cuentas origen y destino
        Cuenta cuentaOrigen = cuentaDao.findCuenta(transferenciaDto.getCbuOrigen());
        Cuenta cuentaDestino = cuentaDao.findCuenta(transferenciaDto.getCbuDestino());

        // Validar existencia de cuentas
        if (cuentaOrigen == null || cuentaDestino == null) {
            throw new CuentaNoEncontradaException("Error: Una o ambas cuentas no existen.");
        }

        // Validar tipo de moneda
        validar.validarCuentasOrigenDestino(cuentaOrigen, cuentaDestino);

        // Calcular monto con intereses
        double montoConIntereses = procesarMontoConIntereses(transferenciaDto.getMonto(), cuentaOrigen.getTipoMoneda(), cuentaOrigen);

        // Obtener clientes origen y destino
        Cliente clienteOrigen = clienteDao.findCliente(cuentaOrigen.getDniTitular());
        Cliente clienteDestino = clienteDao.findCliente(cuentaDestino.getDniTitular());

        if (clienteOrigen.getBanco().equals(clienteDestino.getBanco())) {
            // Transferencia entre cuentas del mismo banco
            generarTransferencia(cuentaOrigen, cuentaDestino, montoConIntereses, transferenciaDto.getMonto());
        } else if (esBancoDisponible(clienteDestino.getBanco())) {
            // Transferencia con servicio Banelco (sin límite de monto)
            realizarServicioBanelco(cuentaOrigen, transferenciaDto.getMonto());
        } else {
            throw new TransferenciaBancoNoDisponibleException("El banco destino no está disponible para este servicio.");
        }
    }

    public void generarTransferencia(Cuenta cuentaOrigen, Cuenta cuentaDestino, double montoConIntereses, double monto) {

        cuentaDao.deleteCuenta(cuentaOrigen.getCbu());
        cuentaDao.deleteCuenta(cuentaDestino.getCbu());

        cuentaOrigen.setSaldo(cuentaOrigen.getSaldo() - montoConIntereses);
        cuentaDestino.setSaldo(cuentaDestino.getSaldo() + monto);

        movimientosDao.saveMovimiento("Transferencia enviada", monto, cuentaOrigen.getCbu());
        movimientosDao.saveMovimiento("Transferencia recibida", monto, cuentaDestino.getCbu());

        if (montoConIntereses - monto > 0) {
            movimientosDao.saveMovimiento("Comisión aplicada", montoConIntereses - monto, cuentaOrigen.getCbu());
        }

        cuentaDao.saveCuenta(cuentaOrigen);
        cuentaDao.saveCuenta(cuentaDestino);
    }

    //Metodo que realiza la transferencia con el servicio Banelco
    public void realizarServicioBanelco(Cuenta cuentaOrigen, double monto) throws CuentaSinDineroException {

        final double interesBanelco = 0.02;

        double comision = monto * interesBanelco;
        double montoTotal = monto + comision;

        validar.validarSaldo(cuentaOrigen, montoTotal);
        cuentaDao.deleteCuenta(cuentaOrigen.getCbu());
        cuentaDao.deleteCuenta(cuentaOrigen.getCbu());
        cuentaOrigen.setSaldo(cuentaOrigen.getSaldo() - montoTotal);
        movimientosDao.saveMovimiento("Transferencia enviada (Banelco)", montoTotal, cuentaOrigen.getCbu());
        cuentaDao.saveCuenta(cuentaOrigen);
    }
    //Metodo que valida si el banco es disponible para realizar la transferencia
    public boolean esBancoDisponible(String banco) {
        return banco.equals("Nacion") || banco.equals("Provincia") || banco.equals("HSBC") || banco.equals("BBVA") || banco.equals("Santander");
    }

    public double procesarMontoConIntereses(double monto, TipoMoneda tipoMoneda, Cuenta cuentaOrigen) throws CuentaSinDineroException {
        double interes = calcularInteresSobreTransferencia(monto, tipoMoneda);
        double montoConIntereses = monto + interes;

        validar.validarSaldo(cuentaOrigen, montoConIntereses);
        return montoConIntereses;
    }

    public double calcularInteresSobreTransferencia(double monto, TipoMoneda tipoMoneda) {
        if (tipoMoneda == TipoMoneda.DOLARES && monto > 5000) {
            return monto * 0.005;
        } else if (tipoMoneda == TipoMoneda.PESOS && monto > 1000000) {
            return monto * 0.2;
        }
        return 0;
    }
}
