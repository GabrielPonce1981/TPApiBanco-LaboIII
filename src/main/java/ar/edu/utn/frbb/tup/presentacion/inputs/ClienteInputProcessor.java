package ar.edu.utn.frbb.tup.presentacion.inputs;

import ar.edu.utn.frbb.tup.modelo.Cliente;
import ar.edu.utn.frbb.tup.modelo.TipoPersona;
import ar.edu.utn.frbb.tup.presentacion.ValidacionesPresentacion;
import ar.edu.utn.frbb.tup.servicios.ValidacionesServicios;

import java.time.LocalDate;

public class ClienteInputProcessor extends BaseInputProcessor{
    ValidacionesPresentacion validarEntrada = new ValidacionesPresentacion();
    ValidacionesServicios validar = new ValidacionesServicios();
    //private static List<Cliente> clientes = new ArrayList<>();

    public Cliente ingresarCliente(long dni) {

        // Ingreso de datos del Cliente
        Cliente cliente = new Cliente();
        clearScreen();

        cliente.setDni(dni);
        cliente.setNombre(ingreseNombre());
        cliente.setApellido(ingreseApellido());
        cliente.setFechaNacimiento(ingreseFechaNacimiento());
        cliente.setTipoPersona(ingreseTipoPersona());
        cliente.setFechaAlta(fechaAlta());
        cliente.setNumeroDeCliente(numeroDeCliente());
        cliente.setDomicilio(ingreseDomicilio());
        cliente.setBanco(ingreseBanco());

        clearScreen();
        return cliente;
    }
    String dni;
    public long ingreseDni() {
        do {
            System.out.print("Ingrese el DNI del cliente:");
            dni = scanner.nextLine();
        }while(!validar.validarDni(dni));
        return Long.parseLong(dni);
    }

    public String ingreseNombre(){
        String nombre;
        do{
            System.out.print("Ingrese el nombre del cliente:");
            nombre = scanner.nextLine();
        } while (validarEntrada.estaVacio(nombre) || !validarEntrada.esLetra(nombre));
        return nombre;
    }

    public String ingreseApellido(){
        String apellido;
        do{
            System.out.print("Ingrese el apellido del cliente:");
            apellido = scanner.nextLine();
        } while (validarEntrada.estaVacio(apellido) || !validarEntrada.esLetra(apellido));
        return apellido;
    }

    public LocalDate ingreseFechaNacimiento() {
        String fechaNacimientoStr;
        do{
            System.out.print("Ingrese la fecha de nacimiento (Formato: YYYY-MM-DD):");
            fechaNacimientoStr = scanner.nextLine();
        } while (!validarEntrada.validarFecha(fechaNacimientoStr) || !validar.esMayordeEdad(fechaNacimientoStr));
        return LocalDate.parse(fechaNacimientoStr);
    }
    public TipoPersona ingreseTipoPersona(){
        System.out.print("Ingrese el tipo de persona Física(F) o Jurídica(J):");
        String tipoPersonaStr = scanner.nextLine().toUpperCase();
        while (!validar.validarTipoPersona(tipoPersonaStr)){
            System.out.println("Tipo de persona inválido.");
            System.out.print("Ingrese F para FISICA o J para JURIDICA: ");
            tipoPersonaStr = scanner.nextLine().toUpperCase();
        };
        TipoPersona tipoPersona = TipoPersona.fromString(tipoPersonaStr);
        return tipoPersona;
    }

    public LocalDate fechaAlta() {
        LocalDate fechaAlta = LocalDate.now();
        return fechaAlta;

    }

    public long numeroDeCliente() {
        long numeroDeCliente = Long.parseLong(dni);
        return numeroDeCliente;
    }

    public String ingreseDomicilio(){
        String domicilio;
        do{
            System.out.print("Ingrese el domicilio del cliente:");
            domicilio = scanner.nextLine();
        } while (validarEntrada.estaVacio(domicilio));
        return domicilio;
    }

    public String ingreseBanco() {
        String banco;
        do {
            System.out.print("Ingrese el banco del cliente:");
            banco = scanner.nextLine();
        } while (validarEntrada.estaVacio(banco) || !validarEntrada.esLetra(banco));
        return banco;
    }
}
