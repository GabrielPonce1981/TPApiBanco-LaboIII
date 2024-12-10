package ar.edu.utn.frbb.tup.presentacion;

import ar.edu.utn.frbb.tup.excepciones.CuentaDistintaMonedaException;
import ar.edu.utn.frbb.tup.excepciones.CuentaNoEncontradaException;
import ar.edu.utn.frbb.tup.excepciones.CuentaSinDineroException;
import ar.edu.utn.frbb.tup.modelos.Cuenta;
import ar.edu.utn.frbb.tup.presentacion.DTOs.CuentaDto;
import ar.edu.utn.frbb.tup.presentacion.DTOs.TransferenciaDto;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

public class ValidacionesPresentacion {
    public boolean esEntero(String numero) {
        boolean esEntero = false;
        try {
            Integer.parseInt(numero);
            esEntero = true;
        } catch (NumberFormatException e) {
            System.out.println("El numero ingresado debe ser un entero.");
        }
        return esEntero;
    }

    public boolean esDouble(String numero){
        boolean esDouble = false;
        try {
            Double.parseDouble(numero);
            esDouble = true;
        } catch (NumberFormatException e) {
            System.out.println("El numero ingresado debe ser un entero.");
        }
        return esDouble;
    }

    public boolean esLong(String numero) {
        boolean esLong = false;
        try {
            Long.parseLong(numero);
            esLong = true;
        } catch (NumberFormatException e) {
            System.out.println("El numero ingresado debe ser un entero.");
        }
        return esLong;
    }

    public boolean validarFecha(String fecha){
        boolean esFecha = false;
        if (estaVacio(fecha)) {
            return esFecha;
        } else {
            try {
                LocalDate.parse(fecha);
                esFecha = true;
            } catch (DateTimeParseException e) {
                System.out.println("Debe ser una fecha válida en formato YYYY-MM-DD");
            }
        }
        return esFecha;
    }

    public boolean estaVacio(String entrada) {
        boolean empty = false;
        if (entrada == null || entrada.trim().isEmpty()) {
            empty = true;
            System.out.println("La entrada no puede estar vacia");
        }
        return empty;
    }

    public boolean esLetra(String entrada) {
        boolean esLetra = false;
        if (entrada.matches("[a-zA-Z']+")) {
            esLetra = true;
            return esLetra;
        } else {
            System.out.println("Debe ingresar solo letras o apostrofes");
        }
        return esLetra;
    }

    public void validarTransferencia(TransferenciaDto transferenciaDto) throws IllegalArgumentException {
        if (transferenciaDto.getCbuOrigen() == null) throw new IllegalArgumentException ("Error: debe ingresar un cbu de origen");
        if (transferenciaDto.getCbuDestino() == null) throw new IllegalArgumentException ("Error: debe ingresar un cbu de destino");
        if (transferenciaDto.getTipoMoneda() == null) throw new IllegalArgumentException ("Error: debe ingresar un tipo de moneda");
        if (transferenciaDto.getMonto() == 0) throw new IllegalArgumentException ("Error: el monto debe ser mayor a 0");
    }

    public void validarIngresosDeCbu(Long cbuOrigen, Long cbuDestino) throws IllegalArgumentException {
        if (cbuOrigen < 10000000L || cbuOrigen > 99999999L) {
            throw new IllegalArgumentException("Error: El cbu Ingresado no es valido, debe tener 8 digitos");
        }
        if (cbuDestino < 10000000L || cbuDestino > 99999999L) {
            throw new IllegalArgumentException("Error: El cbu Ingresado no es valido, debe tener 8 digitos");
        }
        if(cbuOrigen.equals(cbuDestino)) {
            throw new IllegalArgumentException("Error: El cbu de origen y destino no pueden ser iguales");
        }
    }
}

