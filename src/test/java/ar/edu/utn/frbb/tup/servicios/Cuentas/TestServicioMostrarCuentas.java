package ar.edu.utn.frbb.tup.servicios.Cuentas;

import ar.edu.utn.frbb.tup.excepciones.ClienteNoEncontradoException;
import ar.edu.utn.frbb.tup.excepciones.CuentaNoEncontradaException;
import ar.edu.utn.frbb.tup.modelos.Cliente;
import ar.edu.utn.frbb.tup.modelos.Cuenta;
import ar.edu.utn.frbb.tup.persistencia.ClienteDao;
import ar.edu.utn.frbb.tup.persistencia.CuentaDao;
import ar.edu.utn.frbb.tup.servicios.ServicioCuentas;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class TestServicioMostrarCuentas {
    @Mock
    private CuentaDao cuentaDao;
    @Mock
    private ClienteDao clienteDao;

    @InjectMocks
    private ServicioCuentas servicioCuentas;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testMostrarCuentasConExito() throws ClienteNoEncontradoException, CuentaNoEncontradaException {
        // Preparo datos de entrada
        long dni = 12345678L;
        Cliente cliente = new Cliente();
        cliente.setDni(dni);

        Set<Cuenta> cuentas = new HashSet<>();
        Cuenta cuenta1 = new Cuenta();
        cuenta1.setCbu(12121212L);
        Cuenta cuenta2 = new Cuenta();
        cuenta2.setCbu(34343434L);
        cuentas.add(cuenta1);
        cuentas.add(cuenta2);

        // Mockeo el metodo findCliente del clienteDao y devuelve el cliente
        when(clienteDao.findCliente(dni)).thenReturn(cliente);

        // Mockeo metodo findAllCuentasDelCliente del cuentaDao y devuelve las cuentas
        when(cuentaDao.findAllCuentasDelCliente(dni)).thenReturn(cuentas);

        // Ejecuto el método a testear
        Set<Cuenta> cuentasMostradas = servicioCuentas.mostrarCuentas(dni);

        // verifico que el resultado sea el esperado
        assertNotNull(cuentasMostradas);
        assertEquals(2, cuentasMostradas.size());
        assertTrue(cuentasMostradas.contains(cuenta1));
        assertTrue(cuentasMostradas.contains(cuenta2));

        //verivico que se llamaron los metodos correctamente
        verify(clienteDao, times(1)).findCliente(dni);
        verify(cuentaDao, times(1)).findAllCuentasDelCliente(dni);
    }

    @Test
    void testMostrarCuentasClienteNoEncontrado() throws ClienteNoEncontradoException {
        // Preparo datos de entrada
        long dni = 12345678L;

        // Mockeo metodo findCliente del clienteDao y devuelve null
        when(clienteDao.findCliente(dni)).thenReturn(null);

        // Llamo al método y espero la excepción
        ClienteNoEncontradoException exception = assertThrows(ClienteNoEncontradoException.class,
                () -> servicioCuentas.mostrarCuentas(dni));

        assertEquals("No se encontro el cliente con el DNI: " + dni, exception.getMessage());

        // Verifico las interacciones con los mocks
        verify(clienteDao, times(1)).findCliente(dni);
        verify(cuentaDao, never()).findAllCuentasDelCliente(anyLong());
    }

    @Test
    void testMmostrarCuentasNoEncontradas() throws ClienteNoEncontradoException {
        long dni = 12345678L;
        Cliente cliente = new Cliente();
        cliente.setDni(dni);

        // Mockeo la búsqueda del cliente y que retorne el cliente
        when(clienteDao.findCliente(dni)).thenReturn(cliente);

        // Mockeo que no hay cuentas para el cliente
        when(cuentaDao.findAllCuentasDelCliente(dni)).thenReturn(new HashSet<>());

        // Llamo al método y espero la excepción
        CuentaNoEncontradaException exception = assertThrows(
                CuentaNoEncontradaException.class,
                () -> servicioCuentas.mostrarCuentas(dni)
        );

        assertEquals("No hay cuentas asociadas al cliente con DNI: " + dni, exception.getMessage());

        // Verifico las interacciones con los mocks
        verify(clienteDao, times(1)).findCliente(dni);
        verify(cuentaDao, times(1)).findAllCuentasDelCliente(dni);
    }

}
