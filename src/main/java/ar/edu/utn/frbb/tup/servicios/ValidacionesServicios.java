package ar.edu.utn.frbb.tup.servicios;

import ar.edu.utn.frbb.tup.excepciones.*;
import ar.edu.utn.frbb.tup.modelos.*;
import ar.edu.utn.frbb.tup.persistencia.ClienteDao;
import ar.edu.utn.frbb.tup.persistencia.CuentaDao;
import ar.edu.utn.frbb.tup.presentacion.DTOs.ClienteDto;
import ar.edu.utn.frbb.tup.presentacion.DTOs.TransferenciaDto;
import org.springframework.stereotype.Service;
import java.time.LocalDate;

import java.util.Set;

//Validaciones de clientes, cuentas y movimientos

@Service
public class ValidacionesServicios {

    //Validaciones de cliente

    public void esMayordeEdad(LocalDate fechaNacimiento) throws ClienteMenorDeEdadException {
        int edad = LocalDate.now().getYear() - fechaNacimiento.getYear();
        if (edad < 18) {
            throw new ClienteMenorDeEdadException("El cliente debe ser mayor de edad");
        }
    }

    //Debemos validar que el dni ingresado sea un numero, tenga entre 7 y 8 digitos y no sea 0
    public void validarDni(Long dni) {
        try {
            if (dni <=0 || dni < 1000000 || dni > 99999999) {
                throw new IllegalArgumentException("Error:el dni debe ser numero positivo y tener entre 7 y 8 digitos");
            }
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Error: el dni debe ser un numero");
        }

    }

    public void validarClienteExistente(ClienteDto clienteDto) throws ClienteExistenteException {
        ClienteDao clienteDao = new ClienteDao();
        if (clienteDao.findCliente(clienteDto.getDni()) != null){
            throw new ClienteExistenteException("Ya existe un cliente con el dni ingresado");
        }
    }

    //validaciones de cuenta

    public void validarCuentaExistente(Long cbu) throws CuentaNoEncontradaException {
        CuentaDao cuentaDao = new CuentaDao();
        Cuenta cuenta = cuentaDao.findCuenta(cbu);
        if (cuenta == null){
            throw new CuentaNoEncontradaException("No se encontro ninguna cuenta con el CBU: " + cbu);
        }
    }


    //verificar si un cliente ya tiene una cuenta con el mismo tipo de moneda (TipoMoneda) y tipo de cuenta (TipoCuenta) antes de permitir que se cree una nueva.
    public void validarTipoMonedaCuenta(TipoCuenta tipoCuenta, TipoMoneda tipoMoneda, Long dniTitular) throws TipoCuentaExistenteException {
        CuentaDao cuentaDao = new CuentaDao();
        Set<Cuenta> cuentas = cuentaDao.findAllCuentasDelCliente(dniTitular);
        for (Cuenta cuenta: cuentas) {
            if (tipoCuenta.equals(cuenta.getTipoCuenta()) && tipoMoneda.equals(cuenta.getTipoMoneda())) {
                throw new TipoCuentaExistenteException("Ya tiene una cuenta con ese tipo de cuenta y moneda ");
            }
        }
    }


    //validar Operaciones
    //validar que las cuentas no sean de distinto tipo moneda y que no sean nulas
    public void validarCuentasOrigenDestino(Cuenta cuentaOrigen, Cuenta cuentaDestino) throws CuentaDistintaMonedaException, CuentaNoEncontradaException {
        if (cuentaOrigen == null || cuentaDestino == null) {
            throw new CuentaNoEncontradaException("Error: Una o ambas cuentas no existen.");
        }
        if (cuentaOrigen.getTipoMoneda() != cuentaDestino.getTipoMoneda()) {
            throw new CuentaDistintaMonedaException("El CBU de origen no puede ser igual al de destino.");
        }

    }

    public void validarSaldo(Cuenta cuenta, double monto) throws CuentaSinDineroException {
        if (cuenta.getSaldo() < monto){
            throw new CuentaSinDineroException("No posee saldo suficiente para realizar la operacion, su saldo es de $" + cuenta.getSaldo());
        }
    }


    //valido el saldo antes de realizar la transferencia
    public void validarSaldoTransferencia(Cuenta cuentaOrigen, TransferenciaDto transferencia) throws CuentaSinDineroException {
        if (transferencia.getMonto() < cuentaOrigen.getSaldo())
            throw new CuentaSinDineroException("Error: no posee saldo suficiente para realizar la operacion, su saldo es de $" + cuentaOrigen.getSaldo());
    }


}