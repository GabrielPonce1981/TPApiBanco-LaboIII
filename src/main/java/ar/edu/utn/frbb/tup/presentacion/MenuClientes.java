package ar.edu.utn.frbb.tup.presentacion;

import ar.edu.utn.frbb.tup.excepciones.ClienteNoEncontradoException;
import ar.edu.utn.frbb.tup.excepciones.ClientesVaciosException;
import ar.edu.utn.frbb.tup.modelo.Banco;
import ar.edu.utn.frbb.tup.modelo.Cliente;
import ar.edu.utn.frbb.tup.persistencia.ClienteDao;
import ar.edu.utn.frbb.tup.servicios.ServicioClientes;

import java.util.List;
import java.util.Scanner;

public class MenuClientes {

    public void gestionClientes(Banco banco) throws ClienteNoEncontradoException, ClientesVaciosException {

        ClienteDao clienteDao = new ClienteDao();
        Banco bancoProvincia = new Banco(); //creamos un objeto con el nombre del banco
        bancoProvincia.setClientes(clienteDao.findAllClientes());// traemos la lista del archivo y la cargamos en el banco

        Scanner entrada = new Scanner(System.in);
        ValidacionesPresentacion validar = new ValidacionesPresentacion();

        String opcionstr;
        int opcion;


        do{
            System.out.println("------------- Gestion Clientes -------------");
            System.out.println("--------------1. Crear Cliente--------------");
            System.out.println("--------------2. Eliminar Cliente-----------");
            System.out.println("--------------3. Modificar Cliente----------");
            System.out.println("--------------4. Volver al menu anterior----");
            System.out.println("--------------------------------------------");


            do{
                System.out.print("Ingrese una opcion: ");
                opcionstr = entrada.nextLine();

            }while(!validar.esEntero(opcionstr));

            opcion = Integer.parseInt(opcionstr);

            switch (opcion){
                case 1:
                    ServicioClientes crear = new ServicioClientes();
                    crear.crearCliente(bancoProvincia.getClientes());
                    break;
                case 2:
                    ServicioClientes eliminar = new ServicioClientes();
                    eliminar.elimimarCliente(bancoProvincia.getClientes());
                    break;
                case 3:
                    ServicioClientes modificar = new ServicioClientes();
                    modificar.modificarCliente(bancoProvincia.getClientes());
                    break;
                case 4:
                    System.out.println("Saliendo del sistema");
                    break;
                default:
                    System.out.println("Opcion no valida, debe ingresar un numero del 1 al 4.....");
            }
        }while(opcion != 4);

    }


}

