package ar.edu.utn.frbb.tup.servicios.Clientes;

import ar.edu.utn.frbb.tup.excepciones.ClienteNoEncontradoException;
import ar.edu.utn.frbb.tup.excepciones.ClienteTieneCuentasException;
import ar.edu.utn.frbb.tup.modelos.Cliente;
import ar.edu.utn.frbb.tup.persistencia.ClienteDao;
import ar.edu.utn.frbb.tup.persistencia.CuentaDao;
import ar.edu.utn.frbb.tup.persistencia.MovimientosDao;
import ar.edu.utn.frbb.tup.servicios.ServicioClientes;
import ar.edu.utn.frbb.tup.servicios.ValidacionesServicios;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

public class TestServicioEliminarCliente {
    @Mock
    private CuentaDao cuentaDao;
    @Mock
    private ClienteDao clienteDao;
    @Mock
    private MovimientosDao movimientosDao;
    @Mock
    private ValidacionesServicios validacionesServicios;
    //InjectMocks para inyectar los mocks en el servicio a testear
    @InjectMocks
    private ServicioClientes servicioClientes;

    @BeforeEach
    void setUp() {
        //inicializo los mocks antes de cada test
        MockitoAnnotations.openMocks(this);
    }


    @Test
    void testEliminarClienteConExito () throws ClienteNoEncontradoException, ClienteTieneCuentasException  {
            Long dni = 12345678L;
            Cliente cliente = new Cliente();
            cliente.setDni(dni);
            //creo una lista de cbu para simular las cuentas del cliente
            List<Long> cbuList = Arrays.asList(11111111L, 22222222L);

            //mock de metodos
            //mockeo metodo para que devuelva el cliente
            when(clienteDao.findCliente(dni)).thenReturn(cliente);
            //mockeo validaciones para que no haga nada
            doNothing().when(validacionesServicios).validarClienteSinCuentas(dni);
            //mockeo metodo para que devuelva la lista de cbu
            when(cuentaDao.getRelacionesDni(dni)).thenReturn(cbuList);
            //mockeo metodos para que no haga nada
            doNothing().when(clienteDao).deleteCliente(dni);
            doNothing().when(cuentaDao).deleteCuenta(anyLong());
            doNothing().when(movimientosDao).deleteMovimiento(anyLong());

            //llamo al metodo eliminarCliente
            Cliente clienteEliminado = servicioClientes.eliminarCliente(dni);

            //verifico que el cliente eliminado sea el mismo que se esperaba
            assertNotNull(clienteEliminado);
            assertEquals(cliente.getDni(), clienteEliminado.getDni());

            //verifico que se hayan llamado los metodos de los mocks
            verify(clienteDao, times(1)).findCliente(dni);
            verify(validacionesServicios, times(1)).validarClienteSinCuentas(dni);
            verify(clienteDao, times(1)).deleteCliente(dni);
            verify(cuentaDao, times(cbuList.size())).deleteCuenta(anyLong());
            verify(movimientosDao, times(cbuList.size())).deleteMovimiento(anyLong());

        }

        @Test
        void testEliminarClienteNoEncontrado() throws ClienteNoEncontradoException, ClienteTieneCuentasException {
            Long dni = 12345678L;
            //mockeo metodo para que devuelva null
            when(clienteDao.findCliente(dni)).thenReturn(null);
            //llamo al metodo eliminarCliente y espero que se lance la excepcion
            ClienteNoEncontradoException Exception = assertThrows(ClienteNoEncontradoException.class,
                    () -> servicioClientes.eliminarCliente(dni));
            //verifico que se haya lanzado la excepcion
            assertEquals("No se encontro el cliente con el DNI: " + dni, Exception.getMessage());

            verify(clienteDao, times(1)).findCliente(dni);
            verify(validacionesServicios, never()).validarClienteSinCuentas(dni);
            verify(clienteDao, never()).deleteCliente(dni);

        }

        @Test
        void testEliminarClienteConCuentas() throws ClienteNoEncontradoException, ClienteTieneCuentasException {
            Long dni = 12345678L;
            Cliente cliente = new Cliente();
            cliente.setDni(dni);
            //mockeo metodo para que devuelva el cliente
            when(clienteDao.findCliente(dni)).thenReturn(cliente);

            //configuto mock para lanzar la excepcion
            doThrow(new ClienteTieneCuentasException("El cliente con DNI " + dni + " tiene cuentas asociadas y no puede ser eliminado.")).when(validacionesServicios).validarClienteSinCuentas(dni);
            //verifico que se lance excepcion
            ClienteTieneCuentasException Exception = assertThrows(ClienteTieneCuentasException.class,
                    () -> servicioClientes.eliminarCliente(dni));
            assertEquals("El cliente con DNI " + dni + " tiene cuentas asociadas y no puede ser eliminado.", Exception.getMessage());


            verify(clienteDao, times(1)).findCliente(dni);
            verify(validacionesServicios, times(1)).validarClienteSinCuentas(dni);
            verify(clienteDao, never()).deleteCliente(dni);


    }

}
