package ar.edu.utn.frbb.tup.presentacion.inputs;

import ar.edu.utn.frbb.tup.excepciones.ClienteNoEncontradoException;
import ar.edu.utn.frbb.tup.excepciones.ClientesVaciosException;
import ar.edu.utn.frbb.tup.excepciones.CuentasVaciasException;
import ar.edu.utn.frbb.tup.modelo.Banco;
import ar.edu.utn.frbb.tup.persistencia.ClienteDao;
import ar.edu.utn.frbb.tup.presentacion.MenuClientes;
import ar.edu.utn.frbb.tup.presentacion.MenuCuentas;
import ar.edu.utn.frbb.tup.presentacion.MenuMovimientosCuentas;
import ar.edu.utn.frbb.tup.presentacion.ValidacionesPresentacion;
import ar.edu.utn.frbb.tup.servicios.ServicioClientes;
import ar.edu.utn.frbb.tup.servicios.ServicioCuentas;
import ar.edu.utn.frbb.tup.servicios.ServicioMovimientos;

public class MenuInputProcessor extends BaseInputProcessor{
//    ClienteInputProcessor clienteInputProcessor = new ClienteInputProcessor();
//    ServicioClientes servicioClientes = new ServicioClientes();
//    ServicioCuentas servicioCuentas = new ServicioCuentas();
//    ServicioMovimientos servicioMovimientos = new ServicioMovimientos();
//    ClienteDao clienteDao = new ClienteDao();
    boolean exit = false;

    public void renderMenu(Banco banco) throws ClienteNoEncontradoException, ClientesVaciosException, CuentasVaciasException {

        while (!exit) {
            ValidacionesPresentacion validar = new ValidacionesPresentacion();
            String opcionstr;
            System.out.println(" ------------------------------------ ");
            System.out.println("| Bienvenido a la aplicación de Banco |");
            System.out.println(" ------------------------------------ ");
            System.out.println("--------1. Gestion de Clientes--------");
            System.out.println("--------2. Gestion de Cuentas---------");
            System.out.println("--------3. Movimientos de Cuentas-----");
            System.out.println("--------4. Salir----------------------");
            System.out.println();

            do{
                System.out.print("Ingrese una opción: ");
                opcionstr = scanner.nextLine();

            }while(!validar.esEntero(opcionstr));


            int opcion = Integer.parseInt(opcionstr);

            switch (opcion) {
                case 1:
                    MenuClientes menuClientes = new MenuClientes();
                    menuClientes.gestionClientes(banco);
                    break;
                case 2:
                    MenuCuentas menuCuentas = new MenuCuentas();
                    menuCuentas.gestionCuentas(banco);
                    break;

                case 3:
                    MenuMovimientosCuentas menuMovimientosCuentas = new MenuMovimientosCuentas();
                    menuMovimientosCuentas.gestionMovimientos(banco);
                    break;
                case 4:
                    exit = true;
                    System. exit(0);
                    break;
                default:
                    System.out.println("Opción inválida. Por favor seleccione 1-4.");
            }
            clearScreen();
        }
    }
}
