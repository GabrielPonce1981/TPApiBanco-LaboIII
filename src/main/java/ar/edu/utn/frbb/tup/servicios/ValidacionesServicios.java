package ar.edu.utn.frbb.tup.servicios;

import ar.edu.utn.frbb.tup.modelo.Cuenta;
import ar.edu.utn.frbb.tup.modelo.TipoMoneda;

import java.time.LocalDate;
import java.util.Objects;
import java.util.Set;

public class ValidacionesServicios {
    public boolean esMayordeEdad(String fecha) {
        boolean esMayordeEdad = false;
        LocalDate fechaNacimiento = LocalDate.parse(fecha);
        LocalDate fechaActual = LocalDate.now();
        int edad = fechaActual.getYear() - fechaNacimiento.getYear();
        if (edad >= 18) {
            esMayordeEdad = true;
            return esMayordeEdad;
        }
        System.out.println("El cliente no es mayor de edad");
        return esMayordeEdad;
    }

    public boolean validarDni(String dni) {
        boolean dniValido = false;

        if (dni.isEmpty()) {
            System.out.println("El dni no puede ser vacio");
        } else {
            try {
                //controlamos que el dni sea numerico y que tenga 8 digitos, de ser asi,
                //en caso de poder parserlo, devolvemos booleano true
                long dniLong = Long.parseLong(dni);
                if (dniLong < 10000000 || dniLong > 99999999) {
                    System.out.println("El dni debe tener 8 digitos");
                } else {
                    dniValido = true;
                }

            } catch (NumberFormatException e) {
                System.out.println("El dni debe ser numerico");
            }
        }
        return dniValido;
    }

    public boolean validarCuentaDestino(String cuentaOrigen, String cuentaDestino) {
        if (!Objects.equals(cuentaOrigen, cuentaDestino)) {
            return true;
        } else {
            System.out.println("Error: el cbu de origen es igual al de destino.");
            return false;
        }
    }

    public boolean validarModenaDestino(TipoMoneda tipoMonedaOrigen, TipoMoneda tipoMonedaDestino) {
        return tipoMonedaOrigen == tipoMonedaDestino;
    }

    public boolean validarTipoPersona(String tipoPersona) {
        return tipoPersona.equals("F") || tipoPersona.equals("J");
    }
}
