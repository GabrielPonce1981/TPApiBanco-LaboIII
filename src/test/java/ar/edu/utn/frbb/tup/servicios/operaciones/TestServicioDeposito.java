package ar.edu.utn.frbb.tup.servicios.operaciones;

import ar.edu.utn.frbb.tup.excepciones.CuentaNoEncontradaException;
import ar.edu.utn.frbb.tup.modelos.Cuenta;
import ar.edu.utn.frbb.tup.modelos.Operacion;
import ar.edu.utn.frbb.tup.persistencia.CuentaDao;
import ar.edu.utn.frbb.tup.persistencia.MovimientosDao;
import ar.edu.utn.frbb.tup.servicios.ServicioOperaciones;
import ar.edu.utn.frbb.tup.servicios.ValidacionesServicios;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

public class TestServicioDeposito {
    @Mock
    private CuentaDao cuentaDao;
    @Mock
    private MovimientosDao movimientosDao;
    @Mock
    private ValidacionesServicios validacionesServicios;

    @InjectMocks
    private ServicioOperaciones servicioOperaciones;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testDepositoExitoso() throws CuentaNoEncontradaException {
        // Preparo datos de entrada
        Long cbu = 11111111L;
        double monto = 500;

        Cuenta cuenta = new Cuenta();
        cuenta.setCbu(cbu);
        cuenta.setSaldo(1000);

        // Mockeo la validación de la cuenta existente
        doNothing().when(validacionesServicios).validarCuentaExistente(cbu);

        // Mockeo la búsqueda de la cuenta
        when(cuentaDao.findCuenta(cbu)).thenReturn(cuenta);

        // Mockeo el borrado y guardado de la cuenta
        doNothing().when(cuentaDao).deleteCuenta(cbu);
        doNothing().when(cuentaDao).saveCuenta(any(Cuenta.class));

        // Mockeo el guardado del movimiento
        doNothing().when(movimientosDao).saveMovimiento(anyString(), anyDouble(), anyLong());

        // Ejecuto el método a testear
        Operacion resultado = servicioOperaciones.deposito(cbu, monto);

        // Verifico el resultado
        assertNotNull(resultado);
        assertEquals(cbu, resultado.getCbu());
        assertEquals(1500, resultado.getSaldoActual());
        assertEquals(monto, resultado.getMonto());
        assertEquals("Deposito", resultado.getTipoOperacion());

        // Verifico las interacciones con los mocks
        verify(validacionesServicios, times(1)).validarCuentaExistente(cbu);
        verify(cuentaDao, times(1)).findCuenta(cbu);
        verify(cuentaDao, times(1)).deleteCuenta(cbu);
        verify(cuentaDao, times(1)).saveCuenta(any(Cuenta.class));
        verify(movimientosDao, times(1)).saveMovimiento(eq("Deposito"), eq(monto), eq(cbu));
    }

    @Test
    void TestDepositoCuentaNoEncontrada() throws CuentaNoEncontradaException {
        // Preparo datos de entrada
        Long cbu = 11111111L;
        double monto = 500;

        // Mockeo la validación de la cuenta existente
        doNothing().when(validacionesServicios).validarCuentaExistente(cbu);

        // Mockeo la búsqueda de la cuenta (no existe)
        when(cuentaDao.findCuenta(cbu)).thenReturn(null);

        // Llamo al método y espero la excepción
        CuentaNoEncontradaException exception = assertThrows(CuentaNoEncontradaException.class,
                () -> servicioOperaciones.deposito(cbu, monto)
        );

        // Verifico el mensaje de la excepción
        assertEquals("La cuenta con el CBU especificado no existe.", exception.getMessage());

        // Verifico las interacciones con los mocks
        verify(validacionesServicios, times(1)).validarCuentaExistente(cbu);
        verify(cuentaDao, times(1)).findCuenta(cbu);
        verify(cuentaDao, never()).deleteCuenta(anyLong());
        verify(cuentaDao, never()).saveCuenta(any(Cuenta.class));
        verify(movimientosDao, never()).saveMovimiento(anyString(), anyDouble(), anyLong());
    }

}
