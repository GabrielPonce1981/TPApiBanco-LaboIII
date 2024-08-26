package ar.edu.utn.frbb.tup.servicios;

import ar.edu.utn.frbb.tup.excepciones.ClienteExistenteException;
import ar.edu.utn.frbb.tup.excepciones.ClienteNoEncontradoException;
import ar.edu.utn.frbb.tup.excepciones.ClientesVaciosException;
import ar.edu.utn.frbb.tup.modelo.Cliente;
import ar.edu.utn.frbb.tup.presentacion.inputs.ClienteInputProcessor;
import ar.edu.utn.frbb.tup.persistencia.ClienteDao;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Scanner;

@Component
public class ServicioClientes {

    ClienteInputProcessor inputcliente = new ClienteInputProcessor();
    Scanner entrada = new Scanner(System.in);
    ValidacionesServicios validar = new ValidacionesServicios();
    ClienteInputProcessor modificar = new ClienteInputProcessor();

    ClienteDao clienteDao = new ClienteDao();

    public void findAllClientes() throws ClientesVaciosException {
        clienteDao.findAllClientes();
    }

    public void inicializarClientes() {
        clienteDao.inicializarClientes();
    }

    public void crearCliente(List<Cliente> clientes) {


//        Cliente cliente = inputcliente.ingresarCliente();

        long dni = inputcliente.ingreseDni();
        boolean encontrado = false;

        for (Cliente c : clientes) {
            if (c.getDni() == dni) {
                encontrado = true;
                throw new ClienteExistenteException("Ya existe un cliente con DNI " + dni);
            }
        }

        Cliente cliente = null;
        if (!encontrado) {
            cliente = inputcliente.ingresarCliente(dni);
            clientes.add(cliente);//agregamos cliente
            System.out.println("Cliente: " + cliente.getNombre());
        } else {

        }
        clienteDao.saveCliente(cliente);

    }
//    public void crearCliente(){
//
//        Cliente cliente = inputcliente.ingresarCliente();
//
//        if (clienteDao.findCliente(cliente.getDni()) != null) {
//            throw new ClienteExistenteException("Ya existe un cliente con DNI " + cliente.getDni());
//        }
//
//        clienteDao.saveCliente(cliente);
//    }

    public void elimimarCliente(List<Cliente> clientes) throws ClienteNoEncontradoException {
        String dniStr;
        do {
            System.out.print("Ingrese el dni del cliente: ");
            dniStr = entrada.nextLine();
        } while (!validar.validarDni(dniStr));
        long dni = Long.parseLong(dniStr);
        boolean encontrado = false;
        for (Cliente c : clientes) {
            if (c.getDni() == dni) {
                clienteDao.deleteCliente(c.getDni());
                System.out.println("Cliente eliminado");
                encontrado = true;
                break;
            }
        }
        if (!encontrado) {
            System.out.println("El cliente no se encuentra en el sistema");
        }
    }
//    public void elimimarCliente() throws ClienteNoEncontradoException {
//        String dniStr;
//        Cliente cliente = new Cliente();
//        do{
//            System.out.print("Ingrese el dni del cliente: ");
//            dniStr = entrada.nextLine();
//        } while (!validar.validarDni(dniStr));
//        long dni = Long.parseLong(dniStr);
//        cliente.setDni(dni);
//
//        if (clienteDao.findCliente(cliente.getDni()) == null) {
//            throw new ClienteNoEncontradoException("El cliente no se encuentra en el sistema");
//        } else {
//            clienteDao.deleteCliente(cliente.getDni());
//        }
//    }

public void modificarCliente(List<Cliente> clientes) throws ClienteNoEncontradoException {
    String dniStr;
    do {
        System.out.print("Ingrese el dni del cliente: ");
        dniStr = entrada.nextLine();
    } while (!validar.validarDni(dniStr));

    Cliente c = buscarCliente(clientes, dniStr);
    Cliente cTemp = c;

    if (c != null)
    {
        System.out.println("-----------Cliente encontrado---------------");
        System.out.println("Nombre: " + c.getNombre());
        System.out.println("Apellido: " + c.getApellido());
        System.out.println("Tipo de Persona: " + c.getTipoPersona());
        System.out.println("DNI: " + c.getDni());
        System.out.println("Numero de Cliente: " + c.getNumeroDeCliente());
        System.out.println("Fecha de Nacimiento: " + c.getFechaNacimiento());
        System.out.println("Domicilio: " + c.getDomicilio());
        System.out.println("Fecha de Alta: " + c.getFechaAlta());
        System.out.println("--------------------------------------------");
        cTemp.setNombre(modificar.ingreseNombre());
        cTemp.setApellido(modificar.ingreseApellido());
        cTemp.setTipoPersona(modificar.ingreseTipoPersona());
        cTemp.setDni(modificar.ingreseDni());
        cTemp.setFechaNacimiento(modificar.ingreseFechaNacimiento());
        cTemp.setDomicilio(modificar.ingreseDomicilio());
        clienteDao.deleteCliente(c.getDni());
        clienteDao.saveCliente(cTemp);
    }
    else
    {
        System.out.println("El cliente no se encuentra en el sistema");
    }

}
//    public void modificarCliente() throws ClientesVaciosException {
//        String dniStr;
//        do {
//            System.out.print("Ingrese el dni del cliente: ");
//            dniStr = entrada.nextLine();
//        } while (!validar.validarDni(dniStr));
//
//        Cliente c = buscarCliente(clienteDao.findAllClientes(), dniStr);
//
//        if (c != null)
//        {
//            System.out.println("-----------Cliente encontrado---------------");
//            System.out.println("Nombre: " + c.getNombre());
//            System.out.println("Apellido: " + c.getApellido());
//            System.out.println("Tipo de Persona: " + c.getTipoPersona());
//            System.out.println("DNI: " + c.getDni());
//            System.out.println("Numero de Cliente: " + c.getNumeroDeCliente());
//            System.out.println("Fecha de Nacimiento: " + c.getFechaNacimiento());
//            System.out.println("Domicilio: " + c.getDomicilio());
//            System.out.println("Fecha de Alta: " + c.getFechaAlta());
//            System.out.println("--------------------------------------------");
//
//            c.setNombre(modificar.ingreseNombre());
//            c.setApellido(modificar.ingreseApellido());
//            c.setTipoPersona(modificar.ingreseTipoPersona());
//            c.setDni(modificar.ingreseDni());
//            c.setFechaNacimiento(modificar.ingreseFechaNacimiento());
//
//        }
//        else
//        {
//            System.out.println("El cliente no se encuentra en el sistema");
//        }


    public Cliente buscarCliente(List<Cliente> clientes, String dni) {
        for (Cliente c : clientes) {
            if (c.getDni() == Long.parseLong(dni)) {
                return c;
            }
        }
        return null;
    }
}
