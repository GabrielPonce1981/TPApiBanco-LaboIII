package ar.edu.utn.frbb.tup.servicios.Cuentas;

import ar.edu.utn.frbb.tup.excepciones.ClienteNoEncontradoException;
import ar.edu.utn.frbb.tup.excepciones.CuentaNoEncontradaException;
import ar.edu.utn.frbb.tup.excepciones.CuentaTieneSaldoException;
import ar.edu.utn.frbb.tup.excepciones.CuentasVaciasException;
import ar.edu.utn.frbb.tup.modelos.Cuenta;
import ar.edu.utn.frbb.tup.persistencia.CuentaDao;
import ar.edu.utn.frbb.tup.persistencia.MovimientosDao;
import ar.edu.utn.frbb.tup.servicios.ServicioCuentas;
import ar.edu.utn.frbb.tup.servicios.ValidacionesServicios;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class TestServicioEliminarCuenta {
    @Mock
    private CuentaDao cuentaDao;
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
    void testEliminarCuentaExitosamente() throws ClienteNoEncontradoException, CuentasVaciasException, CuentaNoEncontradaException, CuentaTieneSaldoException {
        // Preparo datos de entrada
        long dni = 12345678L;
        long cbu = 87654321L;

        Cuenta cuenta = new Cuenta();
        cuenta.setCbu(cbu);
        cuenta.setDniTitular(dni);
        cuenta.setSaldo(0);

        // mockeo validaciones
        doNothing().when(validacionesServicios).validarClienteNoExistente(dni);
        doNothing().when(validacionesServicios).validarCuentasAsociadasCLiente(dni);
        doNothing().when(validacionesServicios).validarSaldoDisponible(cbu);

        // Mockeo la búsqueda de la cuenta del cliente
        when(cuentaDao.findCuentaDelCliente(cbu, dni)).thenReturn(cuenta);

        // Mockeo el borrado de la cuenta y movimientos
        doNothing().when(cuentaDao).deleteCuenta(cbu);
        doNothing().when(movimientosDao).deleteMovimiento(cbu);

        // LLamo al método a testear
        Cuenta cuentaEliminada = servicioCuentas.eliminarCuenta(dni, cbu);

        // Verifico el resultado
        assertNotNull(cuentaEliminada);
        assertEquals(cuenta.getCbu(), cuentaEliminada.getCbu());
        assertEquals(cuenta.getDniTitular(), cuentaEliminada.getDniTitular());

        // Verifico las interacciones con los mocks
        verify(validacionesServicios, times(1)).validarClienteNoExistente(dni);
        verify(validacionesServicios, times(1)).validarCuentasAsociadasCLiente(dni);
        verify(validacionesServicios, times(1)).validarSaldoDisponible(cbu);
        verify(cuentaDao, times(1)).findCuentaDelCliente(cbu, dni);
        verify(cuentaDao, times(1)).deleteCuenta(cbu);
        verify(movimientosDao, times(1)).deleteMovimiento(cbu);
    }

        @Test
        void testEliminarCuentaClienteNoEncontrado() throws ClienteNoEncontradoException, CuentaTieneSaldoException, CuentasVaciasException {
        // Preparo datos de entrada
        long dni = 12345678L;
        long cbu = 87654321L;

        // Mockeo que el cliente no existe
        doThrow(new ClienteNoEncontradoException("No se encontro el cliente con el DNI: " + dni))
                .when(validacionesServicios).validarClienteNoExistente(dni);

        // Llamo al método y espero la excepción
        ClienteNoEncontradoException exception = assertThrows(ClienteNoEncontradoException.class,
                () -> servicioCuentas.eliminarCuenta(dni, cbu)
        );

        // Verifico el mensaje de la excepción
        assertEquals("No se encontro el cliente con el DNI: " + dni, exception.getMessage());

        // Verifico las interacciones con los mocks
        verify(validacionesServicios, times(1)).validarClienteNoExistente(dni);
        verify(validacionesServicios, never()).validarCuentasAsociadasCLiente(anyLong());
        verify(validacionesServicios, never()).validarSaldoDisponible(anyLong());
        verify(cuentaDao, never()).findCuentaDelCliente(anyLong(), anyLong());
        verify(cuentaDao, never()).deleteCuenta(anyLong());
        verify(movimientosDao, never()).deleteMovimiento(anyLong());
    }

    @Test
    void testEliminarCuentaCuentasVacias() throws ClienteNoEncontradoException, CuentasVaciasException, CuentaTieneSaldoException {
        // Preparo datos de entrada
        long dni = 12345678L;
        long cbu = 87654321L;

        // Mockeo las validaciones
        doNothing().when(validacionesServicios).validarClienteNoExistente(dni);
        doThrow(new CuentasVaciasException("No hay cuentas asociadas al cliente con DNI: " + dni))
                .when(validacionesServicios).validarCuentasAsociadasCLiente(dni);

        // Llamo al método y espero la excepción
        CuentasVaciasException exception = assertThrows(
                CuentasVaciasException.class,
                () -> servicioCuentas.eliminarCuenta(dni, cbu)
        );

        // Verifico el mensaje de la excepción
        assertEquals("No hay cuentas asociadas al cliente con DNI: " + dni, exception.getMessage());

        // Verifico las interacciones con los mocks
        verify(validacionesServicios, times(1)).validarClienteNoExistente(dni);
        verify(validacionesServicios, times(1)).validarCuentasAsociadasCLiente(dni);
        verify(validacionesServicios, never()).validarSaldoDisponible(anyLong());
        verify(cuentaDao, never()).findCuentaDelCliente(anyLong(), anyLong());
        verify(cuentaDao, never()).deleteCuenta(anyLong());
        verify(movimientosDao, never()).deleteMovimiento(anyLong());
    }

        @Test
        void testEliminarCuentaNoEncontrada() throws ClienteNoEncontradoException, CuentasVaciasException, CuentaTieneSaldoException {
        // Preparo datos de entrada
        long dni = 12345678L;
        long cbu = 87654321L;

        // Mockeo las validaciones
        doNothing().when(validacionesServicios).validarClienteNoExistente(dni);
        doNothing().when(validacionesServicios).validarCuentasAsociadasCLiente(dni);

        // Mockeo el metodo findCuentaDelCliente para que devuelva null
        when(cuentaDao.findCuentaDelCliente(cbu, dni)).thenReturn(null);

        // Llamo al método y espero la excepción
        CuentaNoEncontradaException exception = assertThrows(CuentaNoEncontradaException.class,
                () -> servicioCuentas.eliminarCuenta(dni, cbu)
        );

        // Verifico el mensaje de la excepción
        assertEquals("El cliente no tiene ninguna cuenta con el CBU: " + cbu, exception.getMessage());

        // Verifico las interacciones con los mocks
        verify(validacionesServicios, times(1)).validarClienteNoExistente(dni);
        verify(validacionesServicios, times(1)).validarCuentasAsociadasCLiente(dni);
        verify(validacionesServicios, never()).validarSaldoDisponible(anyLong());
        verify(cuentaDao, times(1)).findCuentaDelCliente(cbu, dni);
        verify(cuentaDao, never()).deleteCuenta(anyLong());
        verify(movimientosDao, never()).deleteMovimiento(anyLong());
    }


    @Test
    void testEliminarCuentaConSaldo() throws ClienteNoEncontradoException, CuentasVaciasException, CuentaNoEncontradaException, CuentaTieneSaldoException {
        // Preparo datos de entrada
        long dni = 12345678L;
        long cbu = 87654321L;

        Cuenta cuenta = new Cuenta();
        cuenta.setCbu(cbu);
        cuenta.setDniTitular(dni);
        // Agrego saldo a la cuenta
        cuenta.setSaldo(100);

        //mockeo las validaciones
        doNothing().when(validacionesServicios).validarClienteNoExistente(dni);
        doNothing().when(validacionesServicios).validarCuentasAsociadasCLiente(dni);

        // Mockeo la búsqueda de la cuenta del cliente
        when(cuentaDao.findCuentaDelCliente(cbu, dni)).thenReturn(cuenta);

        // Mockeo la validación de saldo (lanza excepción)
        doThrow(new CuentaTieneSaldoException("La cuenta tiene saldo disponible, no puede ser eliminada"))
                .when(validacionesServicios).validarSaldoDisponible(cbu);

        // Llamo al método y espero la excepción
        CuentaTieneSaldoException exception = assertThrows(CuentaTieneSaldoException.class,
                () -> servicioCuentas.eliminarCuenta(dni, cbu)
        );

        // Verifico el mensaje de la excepción
        assertEquals("La cuenta tiene saldo disponible, no puede ser eliminada", exception.getMessage());

        // Verifico las interacciones con los mocks
        verify(validacionesServicios, times(1)).validarClienteNoExistente(dni);
        verify(validacionesServicios, times(1)).validarCuentasAsociadasCLiente(dni);
        verify(validacionesServicios, times(1)).validarSaldoDisponible(cbu);
        verify(cuentaDao, times(1)).findCuentaDelCliente(cbu, dni);
        verify(cuentaDao, never()).deleteCuenta(anyLong());
        verify(movimientosDao, never()).deleteMovimiento(anyLong());
    }


}





