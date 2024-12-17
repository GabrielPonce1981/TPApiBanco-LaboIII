package ar.edu.utn.frbb.tup.servicios;

import ar.edu.utn.frbb.tup.excepciones.*;
import ar.edu.utn.frbb.tup.modelos.Cliente;
import ar.edu.utn.frbb.tup.persistencia.ClienteDao;
import ar.edu.utn.frbb.tup.persistencia.CuentaDao;
import ar.edu.utn.frbb.tup.persistencia.MovimientosDao;
import ar.edu.utn.frbb.tup.presentacion.DTOs.ClienteDto;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
public class ServicioClientes {

    private final ValidacionesServicios validacionesServicios;
    private final ClienteDao clienteDao;
    private final CuentaDao cuentaDao;
    private final MovimientosDao movimientosDao;


    public ServicioClientes(ValidacionesServicios validacionesServicios, ClienteDao clienteDao, CuentaDao cuentaDao, MovimientosDao movimientosDao) {
        this.validacionesServicios = validacionesServicios;
        this.clienteDao = clienteDao;
        this.cuentaDao = cuentaDao;
        this.movimientosDao = movimientosDao;
    }

    public List<Cliente> mostrarClientes() throws ClientesVaciosException {
        return clienteDao.findAllClientes();
    }

    public void inicializarClientes(){
        clienteDao.inicializarClientes();
    }

    public Cliente crearCliente(ClienteDto clienteDto) throws ClienteExistenteException, ClienteMenorDeEdadException {
        //realizo las validaciones antes de crear y guardar
        //validacionesPresentacion.validarDni(clienteDto.getDni());
        validacionesServicios.validarClienteExistente(clienteDto);
        validacionesServicios.esMayordeEdad(clienteDto.getFechaNacimiento());
        Cliente cliente = new Cliente(clienteDto);
        clienteDao.saveCliente(cliente);
        return cliente;
    }

    public Cliente eliminarCliente(long dni) throws ClienteNoEncontradoException, ClienteTieneCuentasException {
        Cliente clienteEliminado = buscarCliente(dni);

        //valido que el cliente a eliminar no tenga cuentas
        validacionesServicios.validarClienteSinCuentas(dni);

        //Elimino el cliente
        clienteDao.deleteCliente(dni);

        //Elimino las relaciones que tiene con las Cuentas y Movimientos
        List<Long> cbuEliminar = cuentaDao.getRelacionesDni(clienteEliminado.getDni());

        for (Long cbu : cbuEliminar){
            cuentaDao.deleteCuenta(cbu);
            movimientosDao.deleteMovimiento(cbu);
        }
        return clienteEliminado;
    }

    public Cliente buscarCliente(long dni) throws ClienteNoEncontradoException {
        //Funcion que devuelve el cliente encontrado o devuelve Null si no lo encontro
        Cliente cliente = clienteDao.findCliente(dni);
        if (cliente == null){
            throw new ClienteNoEncontradoException("No se encontro el cliente con el DNI: " + dni);
        }
        return cliente;
    }


}