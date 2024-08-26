package ar.edu.utn.frbb.tup.presentacion;

import ar.edu.utn.frbb.tup.excepciones.ClientesVaciosException;
import ar.edu.utn.frbb.tup.excepciones.CuentasVaciasException;
import ar.edu.utn.frbb.tup.modelo.Banco;
import ar.edu.utn.frbb.tup.modelo.Cliente;
import ar.edu.utn.frbb.tup.persistencia.ClienteDao;
import ar.edu.utn.frbb.tup.servicios.ServicioCuentas;

import java.util.List;
import java.util.Scanner;

public class MenuCuentas {

    ValidacionesPresentacion validar = new ValidacionesPresentacion();

    public void gestionCuentas(Banco banco) throws ClientesVaciosException, CuentasVaciasException {

        ClienteDao clienteDao = new ClienteDao();
        Banco bancoProvincia = new Banco(); //creamos un objeto con el nombre del banco
        bancoProvincia.setClientes(clienteDao.findAllClientes());// traemos la lista del archivo y la cargamos en el banco

//      List<Cliente> clientes = banco.getClientes();// traemos la lista de clientes del banco
        Scanner entrada = new Scanner(System.in);

        String opcionstr;
        int opcion;

        do{
            System.out.println("------------- Gestion Cuentas ----------------");
            System.out.println("-------------1. Crear Cuenta------------------");
            System.out.println("-------------2. Eliminar Cuenta---------------");
            System.out.println("-------------3. Mostrar Cuentas---------------");
            System.out.println("-------------4. Volver al menu anterior-------");
            System.out.println("----------------------------------------------");

            do{
                System.out.print("Ingrese una opcion: ");
                opcionstr = entrada.nextLine();

            }while(!validar.esEntero(opcionstr));

            opcion = Integer.parseInt(opcionstr);

            switch (opcion){
                case 1:
                    ServicioCuentas crear = new ServicioCuentas();
                    crear.crearCuenta(bancoProvincia.getClientes());
                    break;
                case 2:
                    ServicioCuentas eliminar = new ServicioCuentas();
                    eliminar.eliminarCuenta(bancoProvincia.getClientes());
                    break;

                case 3:
                    ServicioCuentas mostrar = new ServicioCuentas();
                    mostrar.mostrarCuentas(bancoProvincia.getClientes());
                    break;

                case 4:
                    break;
                default:
                    System.out.println("Opcion no valida, debe ingresar un numero del 1 al 4.....");
            }
        }while(opcion != 4);

    }

}
