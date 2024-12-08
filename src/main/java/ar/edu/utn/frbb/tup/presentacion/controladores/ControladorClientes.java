package ar.edu.utn.frbb.tup.presentacion.controladores;

import ar.edu.utn.frbb.tup.excepciones.ClienteExistenteException;
import ar.edu.utn.frbb.tup.excepciones.ClienteMenorDeEdadException;
import ar.edu.utn.frbb.tup.excepciones.ClienteNoEncontradoException;
import ar.edu.utn.frbb.tup.excepciones.ClientesVaciosException;
import ar.edu.utn.frbb.tup.modelos.Cliente;
import ar.edu.utn.frbb.tup.presentacion.DTOs.ClienteDto;
import ar.edu.utn.frbb.tup.servicios.ServicioClientes;
import ar.edu.utn.frbb.tup.servicios.ValidacionesServicios;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/clientes")
public class ControladorClientes {
    private ServicioClientes servicioClientes;
    private ValidacionesServicios validacionesServicios;

    public ControladorClientes (ServicioClientes servicioClientes, ValidacionesServicios validacionesServicios){
        this.servicioClientes = servicioClientes;
        this.validacionesServicios = validacionesServicios;
        servicioClientes.inicializarClientes();
    }


    @GetMapping
    public ResponseEntity<List<Cliente>> getClientes() throws ClientesVaciosException {
        return new ResponseEntity<>(servicioClientes.mostrarClientes(), HttpStatus.OK);
    }

    @GetMapping("/{dni}")
    public ResponseEntity<Cliente> getClientePorDni(@PathVariable Long dni) throws ClienteNoEncontradoException {
        return new ResponseEntity<>(servicioClientes.buscarCliente(dni), HttpStatus.OK);
    }


    @PostMapping
    public ResponseEntity<Cliente> crearCliente(@RequestBody ClienteDto clienteDto) throws ClienteExistenteException, ClienteMenorDeEdadException {
        validacionesServicios.validarCampos(clienteDto);
        validacionesServicios.validarDni(clienteDto.getDni());
        validacionesServicios.validarClienteExistente(clienteDto);
        validacionesServicios.esMayordeEdad(clienteDto.getFechaNacimiento());
        return new ResponseEntity<>(servicioClientes.crearCliente(clienteDto), HttpStatus.CREATED);
    }

    @DeleteMapping("/{dni}")
    public ResponseEntity<Cliente> borrarCliente(@PathVariable Long dni) throws ClienteNoEncontradoException, ClientesVaciosException {
        return new ResponseEntity<>(servicioClientes.eliminarCliente(dni), HttpStatus.OK);
    }

}
