package ar.edu.utn.frbb.tup.servicios.operaciones;

import ar.edu.utn.frbb.tup.excepciones.CuentaNoEncontradaException;
import ar.edu.utn.frbb.tup.excepciones.MovimientosVaciosException;
import ar.edu.utn.frbb.tup.modelos.Cuenta;
import ar.edu.utn.frbb.tup.modelos.Movimiento;
import ar.edu.utn.frbb.tup.persistencia.ClienteDao;
import ar.edu.utn.frbb.tup.persistencia.CuentaDao;
import ar.edu.utn.frbb.tup.persistencia.MovimientosDao;
import ar.edu.utn.frbb.tup.servicios.ServicioOperaciones;
import ar.edu.utn.frbb.tup.servicios.ValidacionesServicios;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class TestServicioMovimientos {
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
    private ServicioOperaciones servicioOperaciones;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testMostrarMovimientosExitosamente() throws CuentaNoEncontradaException, MovimientosVaciosException {
        // Preparo datos de entrada
        Long cbu = 11111111L;

        Cuenta cuenta = new Cuenta();
        cuenta.setCbu(cbu);

        Movimiento movimiento1 = new Movimiento();
        movimiento1.setCbu(cbu);
        movimiento1.setFechaOperacion(LocalDate.now());
        movimiento1.setHoraOperacion(LocalTime.now().withNano(0));
        movimiento1.setTipoOperacion("Deposito");
        movimiento1.setMonto(3000.0);

        Movimiento movimiento2 = new Movimiento();
        movimiento2.setCbu(cbu);
        movimiento2.setFechaOperacion(LocalDate.now().minusDays(1));
        movimiento2.setHoraOperacion(LocalTime.now().withNano(0));
        movimiento2.setTipoOperacion("Extraccion");
        movimiento2.setMonto(1000.0);

        List<Movimiento> movimientos = new ArrayList<>();
        movimientos.add(movimiento1);
        movimientos.add(movimiento2);

        // Mockeo la validación de cuenta existente
        doNothing().when(validacionesServicios).validarCuentaExistente(cbu);

        // Mockeo la búsqueda de la cuenta
        when(cuentaDao.findCuenta(cbu)).thenReturn(cuenta);

        // Mockeo la búsqueda de movimientos
        when(movimientosDao.findMovimientos(cbu)).thenReturn(movimientos);

        // Ejecuto el método a testear
        List<Movimiento> resultado = servicioOperaciones.mostrarMovimientos(cbu);

        // Verifico el resultado
        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        assertTrue(resultado.contains(movimiento1));
        assertTrue(resultado.contains(movimiento2));

        // Verifico las interacciones con los mocks
        verify(validacionesServicios, times(1)).validarCuentaExistente(cbu);
        verify(cuentaDao, times(1)).findCuenta(cbu);
        verify(movimientosDao, times(1)).findMovimientos(cbu);
    }

    @Test
    void testMostrarMovimientosCuentaNoEncontrada() throws CuentaNoEncontradaException {
        // Preparo datos de entrada
        Long cbu = 11111111L;

        // Mockeo la validación de cuenta existente
        doNothing().when(validacionesServicios).validarCuentaExistente(cbu);

        // Mockeo que la cuenta no existe
        when(cuentaDao.findCuenta(cbu)).thenReturn(null);

        // Llamo al método y espero la excepción
        CuentaNoEncontradaException exception = assertThrows(CuentaNoEncontradaException.class,
                () -> servicioOperaciones.mostrarMovimientos(cbu)
        );

        // Verifico el mensaje de la excepción
        assertEquals("La cuenta con el CBU especificado no existe.", exception.getMessage());

        // Verifico las interacciones con los mocks
        verify(validacionesServicios, times(1)).validarCuentaExistente(cbu);
        verify(cuentaDao, times(1)).findCuenta(cbu);
        verify(movimientosDao, never()).findMovimientos(anyLong());
    }

    @Test
    void testMostrarMovimientosVacios() throws CuentaNoEncontradaException {
        // Preparo datos de entrada
        Long cbu = 11111111L;

        Cuenta cuenta = new Cuenta();
        cuenta.setCbu(cbu);

        List<Movimiento> movimientosVacios = new ArrayList<>();

        // Mockeo la validación de cuenta existente
        doNothing().when(validacionesServicios).validarCuentaExistente(cbu);

        // Mockeo la búsqueda de la cuenta
        when(cuentaDao.findCuenta(cbu)).thenReturn(cuenta);

        // Mockeo la búsqueda de movimientos (sin movimientos)
        when(movimientosDao.findMovimientos(cbu)).thenReturn(movimientosVacios);

        // Llamo al método y espero la excepción
        MovimientosVaciosException exception = assertThrows(MovimientosVaciosException.class,
                () -> servicioOperaciones.mostrarMovimientos(cbu)
        );

        // Verifico el mensaje de la excepción
        assertEquals("La cuenta no tiene movimientos", exception.getMessage());

        // Verifico las interacciones con los mocks
        verify(validacionesServicios, times(1)).validarCuentaExistente(cbu);
        verify(cuentaDao, times(1)).findCuenta(cbu);
        verify(movimientosDao, times(1)).findMovimientos(cbu);
    }
}
