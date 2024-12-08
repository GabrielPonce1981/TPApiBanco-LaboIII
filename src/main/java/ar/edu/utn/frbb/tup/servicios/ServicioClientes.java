package ar.edu.utn.frbb.tup.servicios;

import ar.edu.utn.frbb.tup.excepciones.ClienteExistenteException;
import ar.edu.utn.frbb.tup.excepciones.ClienteMenorDeEdadException;
import ar.edu.utn.frbb.tup.excepciones.ClienteNoEncontradoException;
import ar.edu.utn.frbb.tup.excepciones.ClientesVaciosException;
import ar.edu.utn.frbb.tup.modelos.Cliente;
import ar.edu.utn.frbb.tup.persistencia.ClienteDao;
import ar.edu.utn.frbb.tup.persistencia.CuentaDao;
import ar.edu.utn.frbb.tup.persistencia.MovimientosDao;
import ar.edu.utn.frbb.tup.presentacion.DTOs.ClienteDto;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
public class ServicioClientes {

    //private List<Cliente> clientes;
    ValidacionesServicios validar = new ValidacionesServicios();
    ClienteDao clienteDao = new ClienteDao();
    CuentaDao cuentaDao = new CuentaDao();
    MovimientosDao movimientosDao = new MovimientosDao();


    public List<Cliente> mostrarClientes() throws ClientesVaciosException {
        List<Cliente> clientes = clienteDao.findAllClientes();
        clienteDao.findAllClientes();
        return clientes;
    }

    public void inicializarClientes(){
        clienteDao.inicializarClientes();
    }

    public Cliente crearCliente(ClienteDto clienteDto) throws ClienteExistenteException, ClienteMenorDeEdadException {
        Cliente cliente = new Cliente(clienteDto);
        clienteDao.saveCliente(cliente);
        return cliente;
    }

    public Cliente eliminarCliente(long dni) throws ClienteNoEncontradoException {
        Cliente clienteEliminado = buscarCliente(dni);

        //Elimino el cliente
        clienteDao.deleteCliente(dni);

        //Elimino las relaciones que tiene con las Cuentas y Movimientos
        List<Long> cbuEliminar = cuentaDao.getRelacionesDni(clienteEliminado.getDni());
        //Obtengo lista de todos los CBUs a eliminar

        for (Long cbu : cbuEliminar){
            cuentaDao.deleteCuenta(cbu);
            movimientosDao.deleteMovimiento(cbu);
        }
        return clienteEliminado;
    }

    public Cliente buscarCliente(long dni) throws ClienteNoEncontradoException {
        //Funcion que devuelve el cliente encontrado o vuelve Null si no lo encontro
        Cliente cliente = clienteDao.findCliente(dni);
        if (cliente == null){
            throw new ClienteNoEncontradoException("No se encontro el cliente con el DNI: " + dni);
        }
        return cliente;
    }


}