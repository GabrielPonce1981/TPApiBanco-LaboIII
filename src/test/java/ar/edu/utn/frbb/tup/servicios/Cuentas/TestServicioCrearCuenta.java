package ar.edu.utn.frbb.tup.servicios.Cuentas;

import ar.edu.utn.frbb.tup.excepciones.ClienteNoEncontradoException;
import ar.edu.utn.frbb.tup.excepciones.CuentaExistenteException;
import ar.edu.utn.frbb.tup.excepciones.TipoCuentaExistenteException;
import ar.edu.utn.frbb.tup.modelos.Cliente;
import ar.edu.utn.frbb.tup.modelos.Cuenta;
import ar.edu.utn.frbb.tup.persistencia.ClienteDao;
import ar.edu.utn.frbb.tup.persistencia.CuentaDao;
import ar.edu.utn.frbb.tup.persistencia.MovimientosDao;
import ar.edu.utn.frbb.tup.presentacion.DTOs.CuentaDto;
import ar.edu.utn.frbb.tup.servicios.ServicioCuentas;
import ar.edu.utn.frbb.tup.servicios.ValidacionesServicios;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

public class TestServicioCrearCuenta {
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
    private ServicioCuentas servicioCuentas;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void crearCuentaExitosamente() throws ClienteNoEncontradoException, CuentaExistenteException, TipoCuentaExistenteException {
        // Preparo datos de entrada
        CuentaDto cuentaDto = new CuentaDto();
        cuentaDto.setDniTitular(12345678L);
        cuentaDto.setTipoCuenta("CAJA_AHORRO");
        cuentaDto.setTipoMoneda("PESOS");
        cuentaDto.setAlias("Alias");

        // Mockeo la búsqueda del cliente
        Cliente cliente = new Cliente();
        cliente.setDni(cuentaDto.getDniTitular());
        when(clienteDao.findCliente(cuentaDto.getDniTitular())).thenReturn(cliente);

        // Mockeo la búsqueda de la cuenta (no existe)
        when(cuentaDao.findCuenta(anyLong())).thenReturn(null);

        // Mockeo la validación de cuenta con mismo tipo y moneda (no lanza excepción)
        doNothing().when(validacionesServicios).validarTipoMonedaCuenta(any(), any(), anyLong());

        // Mockeo el guardado de la cuenta (no lanza excepción)
        doNothing().when(cuentaDao).saveCuenta(any(Cuenta.class));

        // Ejecuto el método a testear
        Cuenta cuentaCreada = servicioCuentas.crearCuenta(cuentaDto);

        // Verifico el resultado
        assertNotNull(cuentaCreada);
        assertEquals(cuentaDto.getDniTitular(), cuentaCreada.getDniTitular());
        assertEquals(cuentaDto.getTipoCuenta(), cuentaCreada.getTipoCuenta().name());
        assertEquals(cuentaDto.getTipoMoneda(), cuentaCreada.getTipoMoneda().name());
        assertEquals(cuentaDto.getAlias(), cuentaCreada.getAlias());

        // Verifico que los metodos fueron llamados correctamente
        verify(clienteDao, times(1)).findCliente(cuentaDto.getDniTitular());
        verify(cuentaDao, times(1)).findCuenta(anyLong());
        verify(validacionesServicios, times(1)).validarTipoMonedaCuenta(any(), any(), anyLong());
        verify(cuentaDao, times(1)).saveCuenta(any(Cuenta.class));
    }

    @Test
    void crearCuentaClienteNoEncontrado() throws TipoCuentaExistenteException {
        CuentaDto cuentaDto = new CuentaDto();
        cuentaDto.setDniTitular(12345678L);
        cuentaDto.setTipoCuenta("CAJA_AHORRO");
        cuentaDto.setTipoMoneda("PESOS");
        cuentaDto.setAlias("Alias");

        // Mockeo la búsqueda del cliente (no existe)
        when(clienteDao.findCliente(cuentaDto.getDniTitular())).thenReturn(null);

        // Ejecuto el método a testear y verifico la excepción
        ClienteNoEncontradoException exception = assertThrows(
                ClienteNoEncontradoException.class,
                () -> servicioCuentas.crearCuenta(cuentaDto)
        );

        // Verifico el mensaje de la excepción
        assertEquals("No se encontro el cliente con el DNI: " + cuentaDto.getDniTitular(), exception.getMessage());

        // Verifico las interacciones con los mocks
        verify(clienteDao, times(1)).findCliente(cuentaDto.getDniTitular());
        verify(cuentaDao, never()).findCuenta(anyLong());
        verify(validacionesServicios, never()).validarTipoMonedaCuenta(any(), any(), anyLong());
        verify(cuentaDao, never()).saveCuenta(any(Cuenta.class));
    }

    @Test
    void testcrearCuentaExistente() throws TipoCuentaExistenteException {
        // Preparo datos de entrada
        CuentaDto cuentaDto = new CuentaDto();
        cuentaDto.setDniTitular(12345678L);
        cuentaDto.setTipoCuenta("CAJA_AHORRO");
        cuentaDto.setTipoMoneda("PESOS");
        cuentaDto.setAlias("Alias");

        // Mockeo la búsqueda del cliente
        Cliente cliente = new Cliente();
        cliente.setDni(cuentaDto.getDniTitular());
        when(clienteDao.findCliente(cuentaDto.getDniTitular())).thenReturn(cliente);

        // Mockeo que la cuenta ya existe
        Cuenta cuentaExistente = new Cuenta();
        when(cuentaDao.findCuenta(anyLong())).thenReturn(cuentaExistente);

        // Llamo al método y espero la excepción
        CuentaExistenteException exception = assertThrows(
                CuentaExistenteException.class,
                () -> servicioCuentas.crearCuenta(cuentaDto)
        );
        // Verifico el mensaje de la excepción
        assertEquals("Ya tiene una cuenta con ese CBU", exception.getMessage());

        // verifico que los metodos fueron llamados correctamente
        verify(clienteDao, times(1)).findCliente(cuentaDto.getDniTitular());
        verify(cuentaDao, times(1)).findCuenta(anyLong());
        verify(validacionesServicios, never()).validarTipoMonedaCuenta(any(), any(), anyLong());
        verify(cuentaDao, never()).saveCuenta(any(Cuenta.class));
    }

    @Test
    void crearCuentaTipoMonedaExistente() throws ClienteNoEncontradoException, TipoCuentaExistenteException {
        CuentaDto cuentaDto = new CuentaDto();
        cuentaDto.setDniTitular(12345678L);
        cuentaDto.setTipoCuenta("CAJA_AHORRO");
        cuentaDto.setTipoMoneda("PESOS");
        cuentaDto.setAlias("Alias");

        // Mockeo la búsqueda del cliente
        Cliente cliente = new Cliente();
        cliente.setDni(cuentaDto.getDniTitular());
        when(clienteDao.findCliente(cuentaDto.getDniTitular())).thenReturn(cliente);

        // Mockeo la búsqueda de la cuenta (no existe)
        when(cuentaDao.findCuenta(anyLong())).thenReturn(null);

        // Mockeo la validación de cuenta con mismo tipo y moneda (lanza excepción)
        doThrow(new TipoCuentaExistenteException("Ya tiene una cuenta con ese tipo de cuenta y tipo de moneda"))
                .when(validacionesServicios).validarTipoMonedaCuenta(any(), any(), anyLong());

        // Llamo al método y espero la excepción
        TipoCuentaExistenteException exception = assertThrows(
                TipoCuentaExistenteException.class,
                () -> servicioCuentas.crearCuenta(cuentaDto)
        );

        // Verifico el mensaje de la excepción
        assertEquals("Ya tiene una cuenta con ese tipo de cuenta y tipo de moneda", exception.getMessage());

        // Verifico las interacciones con los mocks
        verify(clienteDao, times(1)).findCliente(cuentaDto.getDniTitular());
        verify(cuentaDao, times(1)).findCuenta(anyLong());
        verify(validacionesServicios, times(1)).validarTipoMonedaCuenta(any(), any(), anyLong());
        verify(cuentaDao, never()).saveCuenta(any(Cuenta.class));
    }

}
