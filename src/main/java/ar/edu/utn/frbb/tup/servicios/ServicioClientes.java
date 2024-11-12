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

    private List<Cliente> clientes;
    ClienteInputProcessor inputcliente = new ClienteInputProcessor();
    Scanner entrada = new Scanner(System.in);
    ValidacionesServicios validar = new ValidacionesServicios();
    ClienteInputProcessor modificar = new ClienteInputProcessor();

    ClienteDao clienteDao = new ClienteDao();

    public List<Cliente> findAllClientes() throws ClientesVaciosException {
        clienteDao.findAllClientes();
        return null;
    }

    public void inicializarClientes() throws ClientesVaciosException {
        this.clientes = clienteDao.findAllClientes();  // Cargar los clientes desde el DAO
    }

    public Cliente crearCliente(List<Cliente> clientes) {


//      Cliente cliente = inputcliente.ingresarCliente();

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

        return cliente;
    }


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


    public Cliente modificarCliente(List<Cliente> clientes) throws ClienteNoEncontradoException {
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

        return c;
    }

    public Cliente buscarCliente(List<Cliente> clientes, String dni) {
        for (Cliente c : clientes) {
            if (c.getDni() == Long.parseLong(dni)) {
                return c;
            }
        }
        return null;
    }


    // Método para buscar un cliente por DNI
    public Cliente buscarClientePorDni(long dni) throws ClienteNoEncontradoException {
        // Buscar el cliente
        Cliente cliente = clienteDao.findCliente(dni);
            if (cliente != null){
                return cliente;  // Si se encuentra, se retorna el cliente
            }
        // Si no se encuentra el cliente, lanzar la excepción
        throw new ClienteNoEncontradoException("Cliente con DNI " + dni + " no encontrado.");
    }

    // Mostrar todos los clientes
    public List<Cliente> mostrarClientes() throws ClientesVaciosException {

        List<Cliente> clientes = clienteDao.findAllClientes();

        if (clientes.isEmpty()){//Si la lista esta vacia significa que no hay clientes registrados
            throw new ClientesVaciosException("No hay clientes registrados");
        }

        //Leo toda la lista de clientes, si no hay clientes lanza una excepcion
        return clientes;

    }

    public Cliente borrarClientePorDni(long dni) throws ClienteNoEncontradoException {
        ServicioClientes servicioClientes = new ServicioClientes();
        Cliente clienteEncontrado = servicioClientes.buscarClientePorDni(dni);
        // Buscar el cliente en la lista de clientes

            if (clienteEncontrado.getDni() == dni) {
                clienteDao.deleteCliente(dni);
                return clienteEncontrado; // Si se encuentra, lo borramos de la lista y lo retornamos
            }
            // Si no se encuentra el cliente, lanzar la excepción
            else{
            throw new ClienteNoEncontradoException("Cliente con DNI " + dni + " no encontrado.");
            }
    }

}
