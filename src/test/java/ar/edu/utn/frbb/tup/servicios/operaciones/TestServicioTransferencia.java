package ar.edu.utn.frbb.tup.servicios.operaciones;

import ar.edu.utn.frbb.tup.excepciones.*;
import ar.edu.utn.frbb.tup.modelos.Cliente;
import ar.edu.utn.frbb.tup.modelos.Cuenta;
import ar.edu.utn.frbb.tup.modelos.TipoMoneda;
import ar.edu.utn.frbb.tup.persistencia.ClienteDao;
import ar.edu.utn.frbb.tup.persistencia.CuentaDao;
import ar.edu.utn.frbb.tup.persistencia.MovimientosDao;
import ar.edu.utn.frbb.tup.presentacion.DTOs.TransferenciaDto;
import ar.edu.utn.frbb.tup.servicios.ServicioTransferencias;
import ar.edu.utn.frbb.tup.servicios.ValidacionesServicios;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class TestServicioTransferencia {
    @Mock
    private CuentaDao cuentaDao;
    @Mock
    private ClienteDao clienteDao;
    @Mock
    private MovimientosDao movimientosDao;
    @Mock
    private ValidacionesServicios validacionesServicios;

    @InjectMocks
    private ServicioTransferencias servicioTransferencias;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        // Spy de ServicioTransferencias
        servicioTransferencias = spy(new ServicioTransferencias(cuentaDao, movimientosDao, clienteDao, validacionesServicios));
    }

    @Test
    void testRealizarTransferenciaExitosa() throws Exception {
        // Configurar datos de entrada
        TransferenciaDto transferenciaDto = new TransferenciaDto();
        transferenciaDto.setCbuOrigen(12345678L);
        transferenciaDto.setCbuDestino(87654321L);
        transferenciaDto.setMonto(1000.0);
        transferenciaDto.setTipoMoneda("PESOS");

        Cuenta cuentaOrigen = new Cuenta();
        cuentaOrigen.setCbu(12345678L);
        cuentaOrigen.setSaldo(2000.0);
        cuentaOrigen.setTipoMoneda(TipoMoneda.PESOS);
        cuentaOrigen.setDniTitular(44444444L);

        Cuenta cuentaDestino = new Cuenta();
        cuentaDestino.setCbu(87654321L);
        cuentaDestino.setSaldo(500.0);
        cuentaDestino.setTipoMoneda(TipoMoneda.PESOS);
        cuentaDestino.setDniTitular(55555555L);

        Cliente clienteOrigen = new Cliente();
        clienteOrigen.setDni(44444444L);
        clienteOrigen.setBanco("Nacion");

        Cliente clienteDestino = new Cliente();
        clienteDestino.setDni(55555555L);
        clienteDestino.setBanco("Nacion");

        // Configurar mocks
        when(cuentaDao.findCuenta(12345678L)).thenReturn(cuentaOrigen);
        when(cuentaDao.findCuenta(87654321L)).thenReturn(cuentaDestino);
        when(clienteDao.findCliente(44444444L)).thenReturn(clienteOrigen);
        when(clienteDao.findCliente(55555555L)).thenReturn(clienteDestino);

        doNothing().when(validacionesServicios).validarCuentasOrigenDestino(cuentaOrigen, cuentaDestino);
        doNothing().when(validacionesServicios).validarSaldo(cuentaOrigen, transferenciaDto.getMonto());

        // Ejecutar método
        servicioTransferencias.realizarTransferencia(transferenciaDto);

        // Verificaciones
        verify(validacionesServicios, times(1)).validarSaldo(cuentaOrigen, transferenciaDto.getMonto());
        verify(movimientosDao, times(1)).saveMovimiento("Transferencia enviada", transferenciaDto.getMonto(), cuentaOrigen.getCbu());
        verify(movimientosDao, times(1)).saveMovimiento("Transferencia recibida", transferenciaDto.getMonto(), cuentaDestino.getCbu());
        verify(cuentaDao, times(1)).saveCuenta(cuentaOrigen);
        verify(cuentaDao, times(1)).saveCuenta(cuentaDestino);
    }

    @Test
    void testTransferenciaConCuentaDistintaMoneda() throws CuentaDistintaMonedaException, CuentaNoEncontradaException, CuentaSinDineroException, TransferenciaFailException, TransferenciaBancoNoDisponibleException {
        TransferenciaDto transferenciaDto = new TransferenciaDto();
        transferenciaDto.setCbuOrigen(12345678L);
        transferenciaDto.setCbuDestino(87654321L);
        transferenciaDto.setMonto(1000.0);
        transferenciaDto.setTipoMoneda("PESOS");

        Cuenta cuentaOrigen = new Cuenta();
        cuentaOrigen.setCbu(12345678L);
        cuentaOrigen.setTipoMoneda(TipoMoneda.PESOS);

        Cuenta cuentaDestino = new Cuenta();
        cuentaDestino.setCbu(87654321L);
        cuentaDestino.setTipoMoneda(TipoMoneda.DOLARES);

        when(cuentaDao.findCuenta(12345678L)).thenReturn(cuentaOrigen);
        when(cuentaDao.findCuenta(87654321L)).thenReturn(cuentaDestino);

        doThrow(new CuentaDistintaMonedaException("Las cuentas deben ser de la misma moneda")).when(validacionesServicios).validarCuentasOrigenDestino(cuentaOrigen, cuentaDestino);

        CuentaDistintaMonedaException exception = assertThrows(CuentaDistintaMonedaException.class,
                () -> servicioTransferencias.realizarTransferencia(transferenciaDto));

        assertEquals("Las cuentas deben ser de la misma moneda", exception.getMessage());

        verify(cuentaDao, times(1)).findCuenta(12345678L);
        verify(cuentaDao, times(1)).findCuenta(87654321L);
        verify(validacionesServicios, times(1)).validarCuentasOrigenDestino(cuentaOrigen, cuentaDestino);
        verify(movimientosDao, never()).saveMovimiento(anyString(), anyDouble(), anyLong());
        verify(cuentaDao, never()).saveCuenta(any(Cuenta.class));
    }

    @Test
    void testTransferenciaCuentaNoEncontrada() throws CuentaNoEncontradaException, CuentaDistintaMonedaException, CuentaSinDineroException, TransferenciaFailException, TransferenciaBancoNoDisponibleException {
        TransferenciaDto transferenciaDto = new TransferenciaDto();
        transferenciaDto.setCbuOrigen(12345678L);
        transferenciaDto.setCbuDestino(87654321L);
        transferenciaDto.setMonto(1000.0);
        transferenciaDto.setTipoMoneda("PESOS");

        when(cuentaDao.findCuenta(12345678L)).thenReturn(null);

        CuentaNoEncontradaException exception = assertThrows(CuentaNoEncontradaException.class,
                () -> servicioTransferencias.realizarTransferencia(transferenciaDto));

        assertEquals("Error: Una o ambas cuentas no existen.", exception.getMessage());

        verify(cuentaDao, times(1)).findCuenta(12345678L);
        verify(movimientosDao, never()).saveMovimiento(anyString(), anyDouble(), anyLong());
        verify(cuentaDao, never()).saveCuenta(any(Cuenta.class));
    }

    @Test
    void testTransferenciaCuentaSinDinero() throws Exception {
        TransferenciaDto transferenciaDto = new TransferenciaDto();
        transferenciaDto.setCbuOrigen(12345678L);
        transferenciaDto.setCbuDestino(87654321L);
        transferenciaDto.setMonto(1000.0);

        Cuenta cuentaOrigen = new Cuenta();
        cuentaOrigen.setCbu(12345678L);
        cuentaOrigen.setSaldo(500.0);

        Cuenta cuentaDestino = new Cuenta();
        cuentaDestino.setCbu(87654321L);

        when(cuentaDao.findCuenta(12345678L)).thenReturn(cuentaOrigen);
        when(cuentaDao.findCuenta(87654321L)).thenReturn(cuentaDestino);

        doThrow(new CuentaSinDineroException("No posee saldo suficiente para realizar la operacion, su saldo es de $500.0"))
                .when(validacionesServicios).validarSaldo(cuentaOrigen, 1000.0);

        CuentaSinDineroException exception = assertThrows(CuentaSinDineroException.class, () -> {
            servicioTransferencias.realizarTransferencia(transferenciaDto);
        });

        assertEquals("No posee saldo suficiente para realizar la operacion, su saldo es de $500.0", exception.getMessage());

        verify(validacionesServicios, times(1)).validarSaldo(cuentaOrigen, 1000.0);
    }

    @Test
    void testEsBancoDisponibleConBancoValido() {
        // Pruebas con bancos válidos
        assertTrue(servicioTransferencias.esBancoDisponible("Nacion"), "Nacion debería ser un banco disponible");
        assertTrue(servicioTransferencias.esBancoDisponible("Provincia"), "Provincia debería ser un banco disponible");
        assertTrue(servicioTransferencias.esBancoDisponible("HSBC"), "HSBC debería ser un banco disponible");
        assertTrue(servicioTransferencias.esBancoDisponible("BBVA"), "BBVA debería ser un banco disponible");
        assertTrue(servicioTransferencias.esBancoDisponible("Santander"), "Santander debería ser un banco disponible");
    }

    @Test
    void testEsBancoDisponibleConBancoInvalido() {
        // Pruebas con bancos inválidos
        assertFalse(servicioTransferencias.esBancoDisponible("Galicia"), "Galicia no debería ser un banco disponible");
        assertFalse(servicioTransferencias.esBancoDisponible("Macro"), "Macro no debería ser un banco disponible");
        assertFalse(servicioTransferencias.esBancoDisponible("ICBC"), "ICBC no debería ser un banco disponible");
        assertFalse(servicioTransferencias.esBancoDisponible("Brubank"), "Brubank no debería ser un banco disponible");
    }
}