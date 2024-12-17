package ar.edu.utn.frbb.tup.presentacion.controladores.Clientes;

import ar.edu.utn.frbb.tup.excepciones.ClienteNoEncontradoException;
import ar.edu.utn.frbb.tup.excepciones.ClienteTieneCuentasException;
import ar.edu.utn.frbb.tup.excepciones.ClientesVaciosException;
import ar.edu.utn.frbb.tup.modelos.Cliente;
import ar.edu.utn.frbb.tup.presentacion.ValidacionesPresentacion;
import ar.edu.utn.frbb.tup.presentacion.controladores.ControladorClientes;
import ar.edu.utn.frbb.tup.servicios.ServicioClientes;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class TestControladorEliminarCliente {

    @InjectMocks
    private ControladorClientes controladorClientes;

    @Mock
    private ValidacionesPresentacion validacionesPresentacion;

    @Mock
    private ServicioClientes servicioClientes;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void eliminarClienteExitosamente() throws ClienteNoEncontradoException, ClienteTieneCuentasException, ClientesVaciosException {
        // Cargo los datos de entrada del cliente que se va a eliminar
        Long dni = 12345678L;

        // Simulo el cliente a retornar después de ser eliminado, este sera el valor esperado
        Cliente clienteEliminado = new Cliente();
        clienteEliminado.setDni(dni);
        clienteEliminado.setNombre("Gabriel");
        clienteEliminado.setApellido("Ponce");

        // Configuro el mock para que no lance excepciones y retorne el cliente esperado
        doNothing().when(validacionesPresentacion).validarDni(dni);//El metodo no lanza excepcion
        when(servicioClientes.eliminarCliente(dni)).thenReturn(clienteEliminado);//retorna el cliente esperado

        //llamo al método del controlador y guardo la respuesta
        ResponseEntity<Cliente> response = controladorClientes.borrarCliente(dni);


        // Verifico que el código de estado HTTP sea 200 (OK) y el cliente devuelto sea el esperado
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(clienteEliminado, response.getBody());

        // Verifico las interacciones con los mocks
        verify(validacionesPresentacion, times(1)).validarDni(dni);
        verify(servicioClientes, times(1)).eliminarCliente(dni);
    }

    @Test
    void eliminarClienteNoExiste() throws ClienteNoEncontradoException, ClienteTieneCuentasException {
        // Cargo los datos de entrada
        Long dni = 12345678L;

        // Configuro el mock para lanzar la excepción ClienteNoEncontradoException
        doNothing().when(validacionesPresentacion).validarDni(dni);//configuro el mock para que no lance excepcion
        // Configuro el mock para lanzar la excepción ClienteNoEncontradoException
        doThrow(new ClienteNoEncontradoException("El cliente no existe"))
                .when(servicioClientes).eliminarCliente(dni);

        // Verifico que la excepción esperada se lance al llamar al controlador.
        ClienteNoEncontradoException exception = assertThrows(
                ClienteNoEncontradoException.class,
                () -> controladorClientes.borrarCliente(dni)
        );

        // Verifico que el mensaje de la excepción sea el esperado
        assertEquals("El cliente no existe", exception.getMessage());

        // Verifico las interacciones con los mocks
        verify(validacionesPresentacion, times(1)).validarDni(dni);
        verify(servicioClientes, times(1)).eliminarCliente(dni);
    }

    @Test
    void eliminarClienteConCuentas() throws ClienteNoEncontradoException, ClienteTieneCuentasException {
        // Cargo los datos de entrada
        Long dni = 12345678L;

        // Simulo que el cliente tiene cuentas asociadas
        doNothing().when(validacionesPresentacion).validarDni(dni);
        doThrow(new ClienteTieneCuentasException("El cliente tiene cuentas asociadas"))
                .when(servicioClientes).eliminarCliente(dni);

        // Llamo al controlador y espero que se lance la excepción
        ClienteTieneCuentasException exception = assertThrows(
                ClienteTieneCuentasException.class,
                () -> controladorClientes.borrarCliente(dni)
        );

        // Verifico que el mensaje de la excepción sea el esperado
        assertEquals("El cliente tiene cuentas asociadas", exception.getMessage());

        // Verifico las interacciones con los mocks
        verify(validacionesPresentacion, times(1)).validarDni(dni);
        verify(servicioClientes, times(1)).eliminarCliente(dni);
    }

}
