package ar.edu.utn.frbb.tup.servicios;

import ar.edu.utn.frbb.tup.excepciones.ClienteExistenteException;
import ar.edu.utn.frbb.tup.excepciones.ClienteMenorDeEdadException;
import ar.edu.utn.frbb.tup.excepciones.TipoCuentaExistenteException;
import ar.edu.utn.frbb.tup.modelos.*;
import ar.edu.utn.frbb.tup.persistencia.ClienteDao;
import ar.edu.utn.frbb.tup.persistencia.CuentaDao;
import ar.edu.utn.frbb.tup.presentacion.DTOs.ClienteDto;
import ar.edu.utn.frbb.tup.presentacion.DTOs.CuentaDto;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.Objects;
import java.util.Set;

//Validaciones de clientes, cuentas y movimientos

@Service
public class ValidacionesServicios {

    //validacion de cliente mayor de edad
    public void esMayordeEdad(LocalDate fechaNacimiento) throws ClienteMenorDeEdadException {
        int edad = LocalDate.now().getYear() - fechaNacimiento.getYear();
        if (edad < 18) {
            throw new ClienteMenorDeEdadException("El cliente debe ser mayor de edad");
        }
    }

    //Debemos validar que el dni ingresado sea un numero, tenga entre 7 y 8 digitos y no sea 0
    public void validarDni(Long dni) {
        try {
            if (dni ==0 || dni < 1000000 || dni > 99999999) {
                throw new IllegalArgumentException("Error: el dni debe tener entre 7 y 8 digitos");
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

    //Necesita ingresar todos los campos para poder registrarse
    public void validarCampos(ClienteDto clienteDto) {
        if (clienteDto.getNombre() == null || clienteDto.getNombre().isEmpty()) throw new IllegalArgumentException("Error: Ingrese un nombre");
        if (clienteDto.getApellido() == null || clienteDto.getApellido().isEmpty()) throw new IllegalArgumentException("Error: Ingrese un apellido");
        if (clienteDto.getDomicilio() == null || clienteDto.getDomicilio().isEmpty()) throw new IllegalArgumentException("Error: Ingrese una direccion");
        if (clienteDto.getFechaNacimiento() == null) throw new IllegalArgumentException("Error: Ingrese una fecha de nacimiento");
        if (clienteDto.getBanco() == null || clienteDto.getBanco().isEmpty()) throw new IllegalArgumentException("Error: Ingrese un banco");
        if (clienteDto.getDni() == null) throw new IllegalArgumentException("Error: debe ingresar un dni");
        if (clienteDto.getTipoPersona() == null || clienteDto.getTipoPersona().isEmpty()) throw new IllegalArgumentException("Error: Ingrese un tipo de persona");
    }

    //validacion de cuenta
    public void validarCuenta(CuentaDto cuentaDto) {
        if(cuentaDto.getDniTitular() == null) throw new IllegalArgumentException("Error: debe ingresar un dni");
        if(cuentaDto.getDniTitular() < 1000000 || cuentaDto.getDniTitular() > 99999999)
            throw new IllegalArgumentException("Error: el dni debe tener entre 7 y 8 digitos");
        if (cuentaDto.getTipoMoneda() == null) throw new IllegalArgumentException("Error: debe ingresar una moneda");
        if (cuentaDto.getTipoCuenta() == null)
            throw new IllegalArgumentException("Error: debe ingresar un tipo de cuenta");

    }

    //validar el tipo de moneda y tipo de cuenta para que no se repitan
    //verificar si un cliente ya tiene una cuenta con el mismo tipo de moneda (TipoMoneda) y tipo de cuenta (TipoCuenta) antes de permitir que se cree una nueva.
    public void validarTipoMonedaCuenta(TipoCuenta tipoCuenta, TipoMoneda tipoMoneda, Long dniTitular) throws TipoCuentaExistenteException {
        CuentaDao cuentaDao = new CuentaDao();
        Set<Cuenta> cuentas = cuentaDao.findAllCuentasDelCliente(dniTitular);
        for (Cuenta cuenta : cuentas) {
            if (cuenta.getTipoMoneda() == tipoMoneda && cuenta.getTipoCuenta() == tipoCuenta) {
                throw new TipoCuentaExistenteException("Ya existe una cuenta con el tipo de moneda y tipo de cuenta ingresados");
            }
        }
    }

    //validacion cuenta destino correcta
    public boolean validarCuentaDestino(String cuentaOrigen, String cuentaDestino) {
        if (!Objects.equals(cuentaOrigen, cuentaDestino)) {
            return true;
        } else {
            System.out.println("Error: el cbu de origen es igual al de destino.");
            return false;
        }
    }

    public boolean validarMonedaDestino(TipoMoneda tipoMonedaOrigen, TipoMoneda tipoMonedaDestino) {
        return tipoMonedaOrigen == tipoMonedaDestino;
    }





}