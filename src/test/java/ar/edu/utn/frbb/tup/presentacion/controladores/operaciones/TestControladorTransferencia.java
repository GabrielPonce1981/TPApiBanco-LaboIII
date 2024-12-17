package ar.edu.utn.frbb.tup.presentacion.controladores.operaciones;

import ar.edu.utn.frbb.tup.excepciones.*;
import ar.edu.utn.frbb.tup.presentacion.DTOs.TransferenciaDto;
import ar.edu.utn.frbb.tup.presentacion.DTOs.TransferenciaResponse;
import ar.edu.utn.frbb.tup.presentacion.ValidacionesPresentacion;
import ar.edu.utn.frbb.tup.presentacion.controladores.ControladorOperaciones;
import ar.edu.utn.frbb.tup.servicios.ServicioOperaciones;
import ar.edu.utn.frbb.tup.servicios.ServicioTransferencias;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

//Test de la clase ControladorTransferencia para verificar el comportamiento del metodo transferencia en el controlador.
class TestControladorTransferencia {

    //inyecto los mock necesarios para el controlador
    @InjectMocks
    private ControladorOperaciones controladorOperaciones;
    //creo la instancias simuldas de las dependencias necesarias para el controlador
    @Mock
    private ServicioOperaciones servicioOperaciones;
    @Mock
    private ServicioTransferencias servicioTransferencias;
    @Mock
    private ValidacionesPresentacion validacionesPresentacion;

    @BeforeEach
    void setUp() {
        //inicializo los mocks antes de cada test
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testTransferenciaExitosa() throws CuentaDistintaMonedaException, CuentaNoEncontradaException, CuentaSinDineroException, TransferenciaFailException, TransferenciaBancoNoDisponibleException, ClienteNoEncontradoException {
        //preparo los datos de entrada
        Long cbuOrigen = 12345678L;
        Long cbuDestino = 87654321L;
        Double monto = 100.0;
        String tipoMoneda = "PESOS";

        TransferenciaDto transferenciaDto = new TransferenciaDto();
        transferenciaDto.setCbuOrigen(cbuOrigen);
        transferenciaDto.setCbuDestino(cbuDestino);
        transferenciaDto.setMonto(monto);
        transferenciaDto.setTipoMoneda(tipoMoneda);

        //mockeo las validaciones de entrada
        doNothing().when(validacionesPresentacion).validarTransferencia(transferenciaDto);
        doNothing().when(validacionesPresentacion).validarIngresosDeCbu(cbuOrigen, cbuDestino);
        doNothing().when(servicioTransferencias).realizarTransferencia(transferenciaDto);

        //ejecuto el metodo del controlador para testear
        ResponseEntity<Object> response = controladorOperaciones.realizarTransferencia(transferenciaDto);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        //verifico que el cuerpo de la respuesta sea del tipo TransferenciaResponse
        assertTrue(response.getBody() instanceof TransferenciaResponse);
        TransferenciaResponse transferenciaResponse = (TransferenciaResponse) response.getBody();
        //verifico que el estado y el mensaje de la respuesta sea el esperado
        assertEquals("EXITOSA", transferenciaResponse.getEstado());
        assertEquals("Transferencia realizada con exito", transferenciaResponse.getMensaje());

        //verifico que los metodos mockados se llamaron correctamente
        verify(validacionesPresentacion, times(1)).validarTransferencia(transferenciaDto);
        verify(validacionesPresentacion, times(1)).validarIngresosDeCbu(cbuOrigen, cbuDestino);
        verify(servicioTransferencias, times(1)).realizarTransferencia(transferenciaDto);

    }

    @Test
    void testTransferenciaCuentaDistintaMoneda() throws CuentaDistintaMonedaException, CuentaNoEncontradaException, CuentaSinDineroException, TransferenciaFailException, TransferenciaBancoNoDisponibleException, ClienteNoEncontradoException {
        //preparo los datos de entrada
        Long cbuOrigen = 12345678L;
        Long cbuDestino = 87654321L;
        Double monto = 100.0;
        String tipoMoneda = "DOLARES";

        TransferenciaDto transferenciaDto = new TransferenciaDto();
        transferenciaDto.setCbuOrigen(cbuOrigen);
        transferenciaDto.setCbuDestino(cbuDestino);
        transferenciaDto.setMonto(monto);
        transferenciaDto.setTipoMoneda(tipoMoneda);

        //mockeo validaciones de entrada y simulo que el metodo servicoTransferencias lance una excepcion
        doThrow(new CuentaDistintaMonedaException("Las monedas de origen y destino deben coincidir.")).when(servicioTransferencias).realizarTransferencia(transferenciaDto);

        //ejecuto el metodo del controlador para testear
        ResponseEntity<Object> response = controladorOperaciones.realizarTransferencia(transferenciaDto);

        //verifico que el estado y el mensaje de la respuesta sea el esperado
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());//400
        //verifico que el cuerpo de la respuesta sea del tipo TransferenciaResponse
        assertTrue(response.getBody() instanceof TransferenciaResponse);
        TransferenciaResponse transferenciaResponse = (TransferenciaResponse) response.getBody();
        assertEquals("FALLIDA", transferenciaResponse.getEstado());
        assertEquals("Las monedas de origen y destino deben coincidir.", transferenciaResponse.getMensaje());

        //verifico que el metodo mockado se llamo correctamente
        verify(validacionesPresentacion, times(1)).validarTransferencia(transferenciaDto);
        verify(validacionesPresentacion, times(1)).validarIngresosDeCbu(cbuOrigen, cbuDestino);
        verify(servicioTransferencias, times(1)).realizarTransferencia(transferenciaDto);
    }


    @Test
    void testTransferenciaCuentaNoEncontrada() throws CuentaDistintaMonedaException, CuentaNoEncontradaException, CuentaSinDineroException, TransferenciaFailException, TransferenciaBancoNoDisponibleException, ClienteNoEncontradoException {
        Long cbuOrigen = 12345678L;
        Long cbuDestino = 87654321L;
        Double monto = 100.0;
        String tipoMoneda = "PESOS";

        TransferenciaDto transferenciaDto = new TransferenciaDto();
        transferenciaDto.setCbuOrigen(cbuOrigen);
        transferenciaDto.setCbuDestino(cbuDestino);
        transferenciaDto.setMonto(monto);
        transferenciaDto.setTipoMoneda(tipoMoneda);

        //mockeo validaciones de entrada y simulo que el metodo servicioTransferencias lance una excepcion
        doThrow(new CuentaNoEncontradaException("Error: Una o ambas cuentas no existen.")).when(servicioTransferencias).realizarTransferencia(transferenciaDto);

        //ejecuto el metodo del controlador para testear
        ResponseEntity<Object> response = controladorOperaciones.realizarTransferencia(transferenciaDto);
        //verifico que el estado y el mensaje de la respuesta sea el esperado
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());//404
        //verifico que el cuerpo de la respuesta sea del tipo TransferenciaResponse
        assertTrue(response.getBody() instanceof TransferenciaResponse);
        TransferenciaResponse transferenciaResponse = (TransferenciaResponse) response.getBody();
        assertEquals("FALLIDA", transferenciaResponse.getEstado());
        assertEquals("Error: Una o ambas cuentas no existen.", transferenciaResponse.getMensaje());

        //verifico que el metodo mockado se llamo correctamente
        verify(validacionesPresentacion, times(1)).validarTransferencia(transferenciaDto);
        verify(validacionesPresentacion, times(1)).validarIngresosDeCbu(cbuOrigen, cbuDestino);
        verify(servicioTransferencias, times(1)).realizarTransferencia(transferenciaDto);
    }

    @Test
    void testTransferenciaCuentaSinDinero() throws CuentaDistintaMonedaException, CuentaNoEncontradaException, CuentaSinDineroException, TransferenciaFailException, TransferenciaBancoNoDisponibleException, ClienteNoEncontradoException {
        Long cbuOrigen = 12345678L;
        Long cbuDestino = 87654321L;
        Double monto = 100.0;
        String tipoMoneda = "PESOS";

        TransferenciaDto transferenciaDto = new TransferenciaDto();
        transferenciaDto.setCbuOrigen(cbuOrigen);
        transferenciaDto.setCbuDestino(cbuDestino);
        transferenciaDto.setMonto(monto);
        transferenciaDto.setTipoMoneda(tipoMoneda);

        //mockeo validaciones de entrada y simulo que el metodo servicioTransferencias lance una excepcion
        doThrow(new CuentaSinDineroException("No posee saldo suficiente para realizar la operacion ")).when(servicioTransferencias).realizarTransferencia(transferenciaDto);

        //ejecuto el metodo del controlador para testear
        ResponseEntity<Object> response = controladorOperaciones.realizarTransferencia(transferenciaDto);
        //verifico que el estado y el mensaje de la respuesta sea el esperado
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());//400
        //verifico que el cuerpo de la respuesta sea del tipo TransferenciaResponse
        assertTrue(response.getBody() instanceof TransferenciaResponse);
        TransferenciaResponse transferenciaResponse = (TransferenciaResponse) response.getBody();
        assertEquals("FALLIDA", transferenciaResponse.getEstado());
        assertEquals("No posee saldo suficiente para realizar la operacion ", transferenciaResponse.getMensaje());


        //verifico que el metodo mockado se llamo correctamente
        verify(validacionesPresentacion, times(1)).validarTransferencia(transferenciaDto);
        verify(validacionesPresentacion, times(1)).validarIngresosDeCbu(cbuOrigen, cbuDestino);
        verify(servicioTransferencias, times(1)).realizarTransferencia(transferenciaDto);
    }

    @Test
    void testTransferenciaMontoSuperaLimite() throws CuentaDistintaMonedaException, CuentaNoEncontradaException, CuentaSinDineroException, TransferenciaFailException, TransferenciaBancoNoDisponibleException, ClienteNoEncontradoException {
        TransferenciaDto transferenciaDto = new TransferenciaDto();
        transferenciaDto.setCbuOrigen(12345678L);
        transferenciaDto.setCbuDestino(87654321L);
        transferenciaDto.setMonto(100.0);
        transferenciaDto.setTipoMoneda("PESOS");


        //mockeo validaciones de entrada y simulo que el metodo servicioTransferencias lance una excepcion
        doThrow(new TransferenciaFailException("El monto supera el límite permitido para transferencias Banelco.")).when(servicioTransferencias).realizarTransferencia(transferenciaDto);

        //ejecuto el metodo del controlador para testear
        ResponseEntity<Object> response = controladorOperaciones.realizarTransferencia(transferenciaDto);
        //verifico que el estado y el mensaje de la respuesta sea el esperado
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());//400
        //verifico que el cuerpo de la respuesta sea del tipo TransferenciaResponse
        assertTrue(response.getBody() instanceof TransferenciaResponse);
        TransferenciaResponse transferenciaResponse = (TransferenciaResponse) response.getBody();
        assertEquals("FALLIDA", transferenciaResponse.getEstado());
        assertEquals("El monto supera el límite permitido para transferencias Banelco.", transferenciaResponse.getMensaje());


        //verifico que el metodo mockado se llamo correctamente
        verify(validacionesPresentacion, times(1)).validarTransferencia(transferenciaDto);
        verify(validacionesPresentacion, times(1)).validarIngresosDeCbu(12345678L, 87654321L);
        verify(servicioTransferencias, times(1)).realizarTransferencia(transferenciaDto);

    }

    @Test
    void testTransferenciaBancoNoDispobible() throws CuentaDistintaMonedaException, CuentaNoEncontradaException, CuentaSinDineroException, TransferenciaFailException, TransferenciaBancoNoDisponibleException, ClienteNoEncontradoException {
        TransferenciaDto transferenciaDto = new TransferenciaDto();
        transferenciaDto.setCbuOrigen(12345678L);
        transferenciaDto.setCbuDestino(87654321L);
        transferenciaDto.setMonto(100.0);
        transferenciaDto.setTipoMoneda("PESOS");


        //mockeo validaciones de entrada y simulo que el metodo servicioTransferencias lance una excepcion
        doThrow(new TransferenciaBancoNoDisponibleException("La transferencia no se pudo realizar, el banco destino no esta disponible para el servico solicitado")).when(servicioTransferencias).realizarTransferencia(transferenciaDto);

        ResponseEntity<Object> response = controladorOperaciones.realizarTransferencia(transferenciaDto);

        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());//404
        //verifico que el cuerpo de la respuesta sea del tipo TransferenciaResponse
        assertTrue(response.getBody() instanceof TransferenciaResponse);
        TransferenciaResponse transferenciaResponse = (TransferenciaResponse) response.getBody();
        assertEquals("FALLIDA", transferenciaResponse.getEstado());
        assertEquals("La transferencia no se pudo realizar, el banco destino no esta disponible para el servico solicitado", transferenciaResponse.getMensaje());

        //verifico que el metodo mockado se llamo correctamente
        verify(validacionesPresentacion, times(1)).validarTransferencia(transferenciaDto);
        verify(validacionesPresentacion, times(1)).validarIngresosDeCbu(12345678L, 87654321L);
        verify(servicioTransferencias, times(1)).realizarTransferencia(transferenciaDto);
    }

    @Test
    void testTransferenciaDatosNulos() throws CuentaDistintaMonedaException, CuentaNoEncontradaException, CuentaSinDineroException, TransferenciaFailException, TransferenciaBancoNoDisponibleException, IllegalArgumentException {
        TransferenciaDto transferenciaDto = new TransferenciaDto();
        transferenciaDto.setCbuOrigen(null);
        transferenciaDto.setCbuDestino(null);
        transferenciaDto.setMonto(0.0);
        transferenciaDto.setTipoMoneda(null);

        doThrow(new IllegalArgumentException("Error: datos erroneos o vacios")).when(validacionesPresentacion).validarTransferencia(transferenciaDto);

        ResponseEntity<Object> response = controladorOperaciones.realizarTransferencia(transferenciaDto);
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());//400
        //verifico que el cuerpo de la respuesta sea del tipo TransferenciaResponse
        assertTrue(response.getBody() instanceof TransferenciaResponse);
        TransferenciaResponse transferenciaResponse = (TransferenciaResponse) response.getBody();
        assertEquals("FALLIDA", transferenciaResponse.getEstado());
        assertEquals("Error: datos erroneos o vacios", transferenciaResponse.getMensaje());
        //verifico que el metodo mockado se llamo correctamente
        verify(validacionesPresentacion, times(1)).validarTransferencia(transferenciaDto);
    }
}
