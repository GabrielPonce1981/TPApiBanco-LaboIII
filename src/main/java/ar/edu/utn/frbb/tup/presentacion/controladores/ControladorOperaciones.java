package ar.edu.utn.frbb.tup.presentacion.controladores;

import ar.edu.utn.frbb.tup.excepciones.*;
import ar.edu.utn.frbb.tup.modelos.Movimiento;
import ar.edu.utn.frbb.tup.modelos.Operacion;
import ar.edu.utn.frbb.tup.presentacion.DTOs.TransferenciaDto;
import ar.edu.utn.frbb.tup.presentacion.DTOs.TransferenciaResponse;
import ar.edu.utn.frbb.tup.presentacion.ValidacionesPresentacion;
import ar.edu.utn.frbb.tup.servicios.ServicioOperaciones;
import ar.edu.utn.frbb.tup.servicios.ServicioTransferencias;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.List;

@RestController
@RequestMapping("api/operaciones")

public class ControladorOperaciones {
    private final ServicioOperaciones servicioOperaciones;
    private final ServicioTransferencias servicioTransferencias;
    private final ValidacionesPresentacion validacionesPresentacion;

    public ControladorOperaciones(ServicioOperaciones servicioOperaciones, ServicioTransferencias servicioTransferencias,ValidacionesPresentacion validacionesPresentacion) {
        this.servicioOperaciones = servicioOperaciones;
        this.servicioTransferencias = servicioTransferencias;
        this.validacionesPresentacion = validacionesPresentacion;
        servicioOperaciones.inicializarMovimientos();
    }

    //Consulta de saldo
    @GetMapping("/consultarsaldo/{cbu}")
    public ResponseEntity<Operacion> getConsultarSaldo(@PathVariable Long cbu) throws CuentaNoEncontradaException {
        return new ResponseEntity<>(servicioOperaciones.consultarSaldo(cbu), HttpStatus.OK);
    }

    //Deposito
    @PutMapping("/deposito/{cbu}")
    public ResponseEntity<Operacion> getDeposito(@PathVariable Long cbu, @RequestParam double monto) throws CuentaNoEncontradaException {
        return new ResponseEntity<>(servicioOperaciones.deposito(cbu, monto), HttpStatus.OK);
    }

    //Mostrar movimientos
    @GetMapping("/movimientos/{cbu}")
    public ResponseEntity<List<Movimiento>> getMostrarMovimientos(@PathVariable Long cbu) throws CuentaNoEncontradaException, MovimientosVaciosException {
        return new ResponseEntity<>(servicioOperaciones.mostrarMovimientos(cbu), HttpStatus.OK);
    }

    //Retiro
    @PutMapping("/extraccion/{cbu}")
    public ResponseEntity<Operacion> getRetiro(@PathVariable Long cbu, @RequestParam double monto) throws CuentaNoEncontradaException, CuentaSinDineroException {
        return new ResponseEntity<>(servicioOperaciones.extraccion(cbu, monto), HttpStatus.OK);
    }

    //Transferencia
    @PostMapping("/transferencia")
    public ResponseEntity<Object> realizarTransferencia(@RequestBody TransferenciaDto transferenciaDto) throws CuentaDistintaMonedaException, CuentaNoEncontradaException, CuentaSinDineroException, TransferenciaFailException {
        try {
            validacionesPresentacion.validarTransferencia(transferenciaDto);
            validacionesPresentacion.validarIngresosDeCbu(transferenciaDto.getCbuOrigen(), transferenciaDto.getCbuDestino());
            servicioTransferencias.realizarTransferencia(transferenciaDto);
            return new ResponseEntity<>(new TransferenciaResponse("EXITOSA", "Transferencia realizada con éxito"), HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            // Validaciones de presentación fallidas
            return new ResponseEntity<>(new TransferenciaResponse("FALLIDA", e.getMessage()), HttpStatus.BAD_REQUEST);
        } catch (CuentaDistintaMonedaException | CuentaNoEncontradaException | CuentaSinDineroException | TransferenciaFailException e) {
            // Excepciones específicas del servicio
            return new ResponseEntity<>(new TransferenciaResponse("FALLIDA", e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }
}