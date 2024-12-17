package ar.edu.utn.frbb.tup.servicios.Clientes;

import ar.edu.utn.frbb.tup.excepciones.ClienteNoEncontradoException;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class TestServicioBuscarCliente {
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
    void testBuscarClientePorDniConExito() throws ClienteNoEncontradoException {
        //preparo los datos de entrada
        Long dni = 12345678L;
        Cliente cliente = new Cliente();
        cliente.setDni(dni);
        cliente.setNombre("Gabriel");
        cliente.setApellido("Ponce");

        //mock de cliente, defino el comportamiento del mismo, cuando se llama a findCliente con el dni, devuelve el cliente
        when(clienteDao.findCliente(dni)).thenReturn(cliente);
        //llamo al metodo buscarCliente
        Cliente clienteEncontrado = servicioClientes.buscarCliente(dni);
        //verifico que el cliente encontrado sea el mismo que se esperaba
        assertNotNull(clienteEncontrado);
        assertEquals(cliente.getDni(), clienteEncontrado.getDni());
        assertEquals(cliente.getNombre(), clienteEncontrado.getNombre());
        assertEquals(cliente.getApellido(), clienteEncontrado.getApellido());

        //verifico que se haya llamado al metodo findCliente al menos una vez
        verify(clienteDao, times(1)).findCliente(dni);

    }

    @Test
    void testBuscarClietePorDniNoExiste() throws ClienteNoEncontradoException {
        //preparo los datos de entrada
        Long dni = 12345678L;
        //mock de cliente, defino el comportamiento del mismo, cuando se llama a findCliente con el dni, devuelva null
        when(clienteDao.findCliente(dni)).thenReturn(null);
        //llamo al metodo buscarCliente
        ClienteNoEncontradoException Exception = assertThrows(ClienteNoEncontradoException.class,
                () -> servicioClientes.buscarCliente(dni)
        );

        assertEquals("No se encontro el cliente con el DNI: " + dni, Exception.getMessage());
        //verifico que se haya llamado al metodo findCliente al menos una vez
        verify(clienteDao, times(1)).findCliente(dni);
    }

}
