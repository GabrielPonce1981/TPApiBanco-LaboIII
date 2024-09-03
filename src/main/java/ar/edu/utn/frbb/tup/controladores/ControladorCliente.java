package ar.edu.utn.frbb.tup.controladores;

import ar.edu.utn.frbb.tup.servicios.ServicioClientes;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/clientes")
public class ControladorCliente {
    private ServicioClientes servicioClientes;

}
