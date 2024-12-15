package ar.edu.utn.frbb.tup.presentacion.controladores;

import ar.edu.utn.frbb.tup.excepciones.*;
import ar.edu.utn.frbb.tup.modelos.Cliente;
import ar.edu.utn.frbb.tup.presentacion.DTOs.ClienteDto;
import ar.edu.utn.frbb.tup.presentacion.ValidacionesPresentacion;
import ar.edu.utn.frbb.tup.servicios.ServicioClientes;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("api/clientes")
public class ControladorClientes {
    private ServicioClientes servicioClientes;
    private ValidacionesPresentacion validacionesPresentacion;

    public ControladorClientes (ServicioClientes servicioClientes, ValidacionesPresentacion validacionesPresentacion){
        this.servicioClientes = servicioClientes;
        this.validacionesPresentacion = validacionesPresentacion;
        servicioClientes.inicializarClientes();
    }


    @GetMapping
    public ResponseEntity<List<Cliente>> getClientes() throws ClientesVaciosException{
        List<Cliente> clientes = servicioClientes.mostrarClientes();
        return new ResponseEntity<>(clientes, HttpStatus.OK);
    }

    @GetMapping("/{dni}")
    public ResponseEntity<Cliente> getClientePorDni(@PathVariable Long dni) throws ClienteNoEncontradoException {
        validacionesPresentacion.validarDni(dni);
        return new ResponseEntity<>(servicioClientes.buscarCliente(dni), HttpStatus.OK);
    }


    @PostMapping
    public ResponseEntity<Cliente> crearCliente(@RequestBody ClienteDto clienteDto) throws ClienteExistenteException, ClienteMenorDeEdadException {
        validacionesPresentacion.validarCampos(clienteDto);
        return new ResponseEntity<>(servicioClientes.crearCliente(clienteDto), HttpStatus.CREATED);
    }

    @DeleteMapping("/{dni}")
    public ResponseEntity<Cliente> borrarCliente(@PathVariable Long dni) throws ClienteNoEncontradoException, ClientesVaciosException, ClienteTieneCuentasException {
        validacionesPresentacion.validarDni(dni);
        return new ResponseEntity<>(servicioClientes.eliminarCliente(dni), HttpStatus.OK);
    }

}
