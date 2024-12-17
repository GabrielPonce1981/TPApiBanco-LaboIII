package ar.edu.utn.frbb.tup.presentacion;

import ar.edu.utn.frbb.tup.presentacion.DTOs.ClienteDto;
import ar.edu.utn.frbb.tup.presentacion.DTOs.CuentaDto;
import ar.edu.utn.frbb.tup.presentacion.DTOs.TransferenciaDto;
import org.springframework.stereotype.Component;


@Component
public class ValidacionesPresentacion {

    //Debemos validar que el dni ingresado sea un numero, tenga entre 7 y 8 digitos y no sea 0
    public void validarDni(Long dni) {
        try {
            if (dni <=0 || dni < 1000000 || dni > 99999999) {
                throw new IllegalArgumentException("Error: el dni debe ser numero positivo y tener entre 7 y 8 digitos");
            }
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Error: el dni debe ser un numero");
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


    public void validarIngresosDeCbu(Long cbuOrigen, Long cbuDestino) throws IllegalArgumentException {
        validarCbu(cbuOrigen);
        validarCbu(cbuDestino);

        if(cbuOrigen.equals(cbuDestino)) {
            throw new IllegalArgumentException("Error: El cbu de origen y destino no pueden ser iguales");
        }
    }

    public void validarCbu(Long cbu) throws IllegalArgumentException {
        if (cbu == null || cbu < 10000000L || cbu > 99999999L) {
            throw new IllegalArgumentException("Cbu incorrecto, debe tener 8 digitos: " + cbu);
        }
    }

    public void validarMonto(Double monto) throws IllegalArgumentException {
        if (monto == null || monto <= 0) {
            throw new IllegalArgumentException("El monto debe ser mayor a 0");
        }
    }

    public void validarTransferencia(TransferenciaDto transferenciaDto) throws IllegalArgumentException{
        if (transferenciaDto.getCbuOrigen() == null) throw new IllegalArgumentException ("Error: debe ingresar un cbu de origen");
        if (transferenciaDto.getCbuDestino() == null) throw new IllegalArgumentException ("Error: debe ingresar un cbu de destino");
        if (transferenciaDto.getTipoMoneda() == null) throw new IllegalArgumentException ("Error: debe ingresar un tipo de moneda");
        if (transferenciaDto.getMonto() == 0) throw new IllegalArgumentException ("Error: el monto debe ser mayor a 0");
    }

}

