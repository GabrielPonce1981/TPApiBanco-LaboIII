package ar.edu.utn.frbb.tup.servicios;

import ar.edu.utn.frbb.tup.excepciones.*;
import ar.edu.utn.frbb.tup.modelos.*;
import ar.edu.utn.frbb.tup.persistencia.ClienteDao;
import ar.edu.utn.frbb.tup.persistencia.CuentaDao;
import ar.edu.utn.frbb.tup.presentacion.DTOs.ClienteDto;
import org.springframework.stereotype.Service;
import java.time.LocalDate;

import java.util.List;
import java.util.Set;


@Service
public class ValidacionesServicios {
    private final CuentaDao cuentaDao;
    private final ClienteDao clienteDao;

    public ValidacionesServicios(CuentaDao cuentaDao, ClienteDao clienteDao) {
        this.cuentaDao = cuentaDao;
        this.clienteDao = clienteDao;
    }
    //Validaciones de cliente

    public void esMayordeEdad(LocalDate fechaNacimiento) throws ClienteMenorDeEdadException {
        int edad = LocalDate.now().getYear() - fechaNacimiento.getYear();
        if (edad < 18) {
            throw new ClienteMenorDeEdadException("El cliente debe ser mayor de edad");
        }
    }

    public void validarClienteExistente(ClienteDto clienteDto) throws ClienteExistenteException {
        if (clienteDao.findCliente(clienteDto.getDni()) != null){
            throw new ClienteExistenteException("Ya existe un cliente con el dni ingresado");
        }
    }
    public void validarClienteNoExistente(Long dni) throws ClienteNoEncontradoException {
        if (clienteDao.findCliente(dni) == null) {
            throw new ClienteNoEncontradoException("No se encontro el cliente con el DNI: " + dni);
        }
    }

    //valido que el cliente no tenga cuentas asociadas antes de eliminarlo
    public void validarClienteSinCuentas(Long dni) throws ClienteTieneCuentasException {
        Cliente cliente = clienteDao.findCliente(dni);
        if (cliente != null && !cliente.getCuentas().isEmpty()) {
            throw new ClienteTieneCuentasException("El cliente tiene cuentas asociadas y no puede ser eliminado.");
        }
    }

    //validaciones de cuenta

    public void validarCuentaExistente(Long cbu) throws CuentaNoEncontradaException {
        Cuenta cuenta = cuentaDao.findCuenta(cbu);
        if (cuenta == null){
            throw new CuentaNoEncontradaException("No se encontro ninguna cuenta con el CBU: " + cbu);
        }
    }

    //valido cuentas asociadas al cliente
    public void validarCuentasAsociadasCLiente(Long dni) throws CuentasVaciasException {
        List<Long> cuentasCbu = cuentaDao.getRelacionesDni(dni);
        if (cuentasCbu.isEmpty()) {
            throw new CuentasVaciasException("El cliente no tiene cuentas asociadas.");
        }
    }

    //verificar si un cliente ya tiene una cuenta con el mismo tipo de moneda (TipoMoneda) y tipo de cuenta (TipoCuenta) antes de permitir que se cree una nueva.
    public void validarTipoMonedaCuenta(TipoCuenta tipoCuenta, TipoMoneda tipoMoneda, Long dniTitular) throws TipoCuentaExistenteException {
        Set<Cuenta> cuentas = cuentaDao.findAllCuentasDelCliente(dniTitular);
        for (Cuenta cuenta: cuentas) {
            if (tipoCuenta.equals(cuenta.getTipoCuenta()) && tipoMoneda.equals(cuenta.getTipoMoneda())) {
                throw new TipoCuentaExistenteException("Ya tiene una cuenta con ese tipo de cuenta y moneda ");
            }
        }
    }

    //validar Operaciones
    //validar que las cuentas no sean de distinto tipo moneda y que no sean nulas
    public void validarCuentasOrigenDestino(Cuenta cuentaOrigen, Cuenta cuentaDestino) throws CuentaDistintaMonedaException {
        if (!cuentaOrigen.getTipoMoneda().equals(cuentaDestino.getTipoMoneda())) {
            throw new CuentaDistintaMonedaException("Las monedas de origen y destino deben coincidir.");
        }
    }


    public void validarSaldo(Cuenta cuenta, double monto) throws CuentaSinDineroException {
        if (cuenta.getSaldo() < monto){
            throw new CuentaSinDineroException("No posee saldo suficiente para realizar la operacion, su saldo es de $" + cuenta.getSaldo());
        }
    }

    public void validarSaldoDisponible(Long cbu) throws CuentaTieneSaldoException {
        Cuenta cuenta = new CuentaDao().findCuenta(cbu);
        if (cuenta.getSaldo() > 0){
            throw new CuentaTieneSaldoException("La cuenta tiene saldo disponible, no puede ser eliminada");
        }
    }

}