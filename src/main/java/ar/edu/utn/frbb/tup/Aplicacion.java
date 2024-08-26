package ar.edu.utn.frbb.tup;

import ar.edu.utn.frbb.tup.excepciones.ClienteNoEncontradoException;
import ar.edu.utn.frbb.tup.excepciones.ClientesVaciosException;
import ar.edu.utn.frbb.tup.excepciones.CuentasVaciasException;
import ar.edu.utn.frbb.tup.modelo.Banco;
import ar.edu.utn.frbb.tup.presentacion.inputs.MenuInputProcessor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Aplicacion {

    public static void main(String[] args) throws ClienteNoEncontradoException, ClientesVaciosException, CuentasVaciasException {
        SpringApplication.run(Aplicacion.class, args);

        Banco banco = new Banco();

        MenuInputProcessor menuInputProcessor = new MenuInputProcessor();
        menuInputProcessor.renderMenu(banco);

    }

}