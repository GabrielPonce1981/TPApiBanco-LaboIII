package ar.edu.utn.frbb.tup.servicios.Clientes;

import ar.edu.utn.frbb.tup.excepciones.ClienteExistenteException;
import ar.edu.utn.frbb.tup.excepciones.ClienteMenorDeEdadException;
import ar.edu.utn.frbb.tup.modelos.Cliente;
import ar.edu.utn.frbb.tup.persistencia.ClienteDao;
import ar.edu.utn.frbb.tup.persistencia.CuentaDao;
import ar.edu.utn.frbb.tup.persistencia.MovimientosDao;
import ar.edu.utn.frbb.tup.presentacion.DTOs.ClienteDto;
import ar.edu.utn.frbb.tup.servicios.ServicioClientes;
import ar.edu.utn.frbb.tup.servicios.ValidacionesServicios;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class TestServicioCrearCliente {
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
    void crearClienteConExito() throws ClienteExistenteException, ClienteMenorDeEdadException {
        //preparo los datos de entrada
        ClienteDto clienteDto = new ClienteDto();
        clienteDto.setDni(12345678L);
        clienteDto.setNombre("Gabriel");
        clienteDto.setApellido("Ponce");
        clienteDto.setFechaNacimiento(LocalDate.of(1981, 02, 03));
        clienteDto.setDomicilio("Luis Vera 3264");
        clienteDto.setBanco("Nacion");
        clienteDto.setTipoPersona("PERSONA_FISICA");

        //mockeo validacion para que no lance excepciones
        doNothing().when(validacionesServicios).validarClienteExistente(clienteDto);
        doNothing().when(validacionesServicios).esMayordeEdad(clienteDto.getFechaNacimiento());
        //mock para salvar el cliente
        doNothing().when(clienteDao).saveCliente(any(Cliente.class));

        //llamo al metodo crearCliente
        Cliente clienteCreado = servicioClientes.crearCliente(clienteDto);

        //verifico que el cliente creado sea el mismo que se esperaba
        assertNotNull(clienteCreado);
        assertEquals(clienteDto.getDni(), clienteCreado.getDni());
        assertEquals(clienteDto.getNombre(), clienteCreado.getNombre());
        assertEquals(clienteDto.getApellido(), clienteCreado.getApellido());
        assertEquals(clienteDto.getFechaNacimiento(), clienteCreado.getFechaNacimiento());
        assertEquals(clienteDto.getDomicilio(), clienteCreado.getDomicilio());
        assertEquals(clienteCreado.getTipoPersona().toString(), clienteDto.getTipoPersona());
        assertEquals(clienteDto.getBanco(), clienteCreado.getBanco());

        //verifico que las validaciones se llamaron una vez
        verify(validacionesServicios, times(1)).validarClienteExistente(clienteDto);
        verify(validacionesServicios, times(1)).esMayordeEdad(clienteDto.getFechaNacimiento());
        //verifico que se haya llamado al metodo saveCliente al menos una vez
        verify(clienteDao, times(1)).saveCliente(any(Cliente.class));
    }

    @Test
    void testCrearClienteExistente() throws ClienteExistenteException, ClienteMenorDeEdadException {
        //preparo los datos de entrada
        ClienteDto clienteDto = new ClienteDto();
        clienteDto.setDni(12345678L);

        //mockeo validacion para que lance excepcion
        doThrow(new ClienteExistenteException("Ya existe un cliente con el dni ingresado")).when(validacionesServicios).validarClienteExistente(clienteDto);

        //verifico que se lance la excepcion esperada
        ClienteExistenteException exception = assertThrows(ClienteExistenteException.class,
                () -> servicioClientes.crearCliente(clienteDto));

        assertEquals("Ya existe un cliente con el dni ingresado", exception.getMessage());

        verify(validacionesServicios, times(1)).validarClienteExistente(clienteDto);
        verify(clienteDao, never()).saveCliente(any(Cliente.class));
    }

    @Test
    void testCrearClienteMenorDeEdad() throws ClienteExistenteException, ClienteMenorDeEdadException {
        //preparo los datos de entrada
        ClienteDto clienteDto = new ClienteDto();
        clienteDto.setDni(12345678L);
        clienteDto.setFechaNacimiento(LocalDate.of(2009, 04, 15));

        //mockeo validacion dni y que no lance excepciones
        doNothing().when(validacionesServicios).validarClienteExistente(clienteDto);
        //mokeo que la validacion de edad lance excepcion
        doThrow(new ClienteMenorDeEdadException("El cliente debe ser mayor de edad")).when(validacionesServicios).esMayordeEdad(clienteDto.getFechaNacimiento());
        //verifico que se lance la excepcion esperada
        ClienteMenorDeEdadException exception = assertThrows(ClienteMenorDeEdadException.class,
                () -> servicioClientes.crearCliente(clienteDto));

        assertEquals("El cliente debe ser mayor de edad", exception.getMessage());
        verify(validacionesServicios, times(1)).validarClienteExistente(clienteDto);
        verify(validacionesServicios, times(1)).esMayordeEdad(clienteDto.getFechaNacimiento());
        verify(clienteDao, never()).saveCliente(any(Cliente.class));
    }
}
