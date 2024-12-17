package ar.edu.utn.frbb.tup.servicios.operaciones;

import ar.edu.utn.frbb.tup.excepciones.CuentaNoEncontradaException;
import ar.edu.utn.frbb.tup.excepciones.CuentaSinDineroException;
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

public class TestServicioExtraccion {
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
    void extraccionExitosamente() throws CuentaSinDineroException, CuentaNoEncontradaException {
        // Preparo datos de entrada
        Long cbu = 22222222L;
        double monto = 500.0;

        Cuenta cuenta = new Cuenta();
        cuenta.setCbu(cbu);
        cuenta.setSaldo(1000.0);

        // Mockeo las validaciones
        doNothing().when(validacionesServicios).validarCuentaExistente(cbu);
        doNothing().when(validacionesServicios).validarSaldo(cuenta, monto);

        // Mockeo la búsqueda de la cuenta
        when(cuentaDao.findCuenta(cbu)).thenReturn(cuenta);

        // Mockeo el borrado y guardado de la cuenta
        doNothing().when(cuentaDao).deleteCuenta(anyLong());
        doNothing().when(cuentaDao).saveCuenta(any(Cuenta.class));

        // Mockeo el guardado del movimiento
        doNothing().when(movimientosDao).saveMovimiento(anyString(), anyDouble(), anyLong());

        // Ejecuto el método a testear
        Operacion resultado = servicioOperaciones.extraccion(cbu, monto);

        // Verifico el resultado
        assertNotNull(resultado);
        assertEquals(cbu, resultado.getCbu());
        assertEquals(500.0, resultado.getSaldoActual());
        assertEquals(monto, resultado.getMonto());
        assertEquals("Extraccion", resultado.getTipoOperacion());

        // Verifico las interacciones con los mocks
        verify(validacionesServicios, times(1)).validarCuentaExistente(cbu);
        verify(validacionesServicios, times(1)).validarSaldo(cuenta, monto);
        verify(cuentaDao, times(1)).findCuenta(cbu);
        verify(cuentaDao, times(1)).deleteCuenta(anyLong());
        verify(cuentaDao, times(1)).saveCuenta(any(Cuenta.class));
        verify(movimientosDao, times(1)).saveMovimiento(anyString(), anyDouble(), anyLong());
    }

    @Test
    void testExtraccionCuentaNoEncontrada() throws CuentaSinDineroException, CuentaNoEncontradaException {
        Long cbu = 22222222L;
        double monto = 500.0;

        // Mockeo la validación de cuenta existente
        doNothing().when(validacionesServicios).validarCuentaExistente(cbu);

        // Mockeo que la cuenta no existe
        when(cuentaDao.findCuenta(cbu)).thenReturn(null);

        // Llamo al método y espero la excepción
        CuentaNoEncontradaException exception = assertThrows(CuentaNoEncontradaException.class,
                () -> servicioOperaciones.extraccion(cbu, monto)
        );

        // Verifico el mensaje de la excepción
        assertEquals("La cuenta con el CBU especificado no existe.", exception.getMessage());

        // Verifico las interacciones con los mocks
        verify(validacionesServicios, times(1)).validarCuentaExistente(cbu);
        verify(cuentaDao, times(1)).findCuenta(cbu);
        verify(validacionesServicios, never()).validarSaldo(any(Cuenta.class), anyDouble());
        verify(cuentaDao, never()).deleteCuenta(anyLong());
        verify(cuentaDao, never()).saveCuenta(any(Cuenta.class));
        verify(movimientosDao, never()).saveMovimiento(anyString(), anyDouble(), anyLong());
    }

    @Test
    void testExtraccionCuentaSinDinero() throws CuentaNoEncontradaException, CuentaSinDineroException {
        Long cbu = 22222222L;
        double monto = 500.0;

        Cuenta cuenta = new Cuenta();
        cuenta.setCbu(cbu);
        cuenta.setSaldo(100.0); // Saldo insuficiente

        // Mockeo las validaciones
        doNothing().when(validacionesServicios).validarCuentaExistente(cbu);

        // Mockeo la búsqueda de la cuenta
        when(cuentaDao.findCuenta(cbu)).thenReturn(cuenta);

        // Mockeo la validación de saldo (lanza excepción)
        doThrow(new CuentaSinDineroException("No posee saldo suficiente para realizar la operacion, su saldo es de $" + cuenta.getSaldo()))
                .when(validacionesServicios).validarSaldo(cuenta, monto);

        // Llamo al método y espero la excepción
        CuentaSinDineroException exception = assertThrows(CuentaSinDineroException.class,
                () -> servicioOperaciones.extraccion(cbu, monto)
        );

        // Verifico el mensaje de la excepción
        assertEquals("No posee saldo suficiente para realizar la operacion, su saldo es de $100.0", exception.getMessage());

        // Verifico las interacciones con los mocks
        verify(validacionesServicios, times(1)).validarCuentaExistente(cbu);
        verify(validacionesServicios, times(1)).validarSaldo(cuenta, monto);
        verify(cuentaDao, times(1)).findCuenta(cbu);
        verify(cuentaDao, never()).deleteCuenta(anyLong());
        verify(cuentaDao, never()).saveCuenta(any(Cuenta.class));
        verify(movimientosDao, never()).saveMovimiento(anyString(), anyDouble(), anyLong());
    }

}
