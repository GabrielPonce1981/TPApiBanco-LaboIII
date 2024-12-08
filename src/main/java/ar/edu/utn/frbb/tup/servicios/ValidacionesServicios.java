package ar.edu.utn.frbb.tup.servicios;
import ar.edu.utn.frbb.tup.excepciones.ClienteExistenteException;
import ar.edu.utn.frbb.tup.excepciones.ClienteMenorDeEdadException;
import ar.edu.utn.frbb.tup.modelos.Cliente;
import ar.edu.utn.frbb.tup.modelos.TipoMoneda;
import ar.edu.utn.frbb.tup.modelos.TipoPersona;
import ar.edu.utn.frbb.tup.persistencia.ClienteDao;
import ar.edu.utn.frbb.tup.presentacion.DTOs.ClienteDto;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.Objects;

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
        if (clienteDto.getTipoPersona() == null) throw new IllegalArgumentException("Error: Ingrese un tipo de persona");
    }


    public void validarTipoPersona(TipoPersona tipoPersona) {
        if (!Objects.equals(tipoPersona, "F") && !Objects.equals(tipoPersona, "J")) {
            throw new IllegalArgumentException("Error: El tipo de persona debe ser 'F' para Fisica o 'J' para Juridica");
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