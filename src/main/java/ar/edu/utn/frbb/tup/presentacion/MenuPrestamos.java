package ar.edu.utn.frbb.tup.presentacion;

import ar.edu.utn.frbb.tup.excepciones.ClientesVaciosException;
import ar.edu.utn.frbb.tup.modelo.Banco;
import ar.edu.utn.frbb.tup.persistencia.ClienteDao;

import java.util.Scanner;

public class MenuPrestamos {
    public void gestionPrestamos (Banco banco) throws ClientesVaciosException {

        ClienteDao clienteDao = new ClienteDao();
        Banco bancoProvincia = new Banco(); //creamos un objeto con el nombre del banco
        bancoProvincia.setClientes(clienteDao.findAllClientes());// traemos la lista del archivo y la cargamos en el banco

        Scanner entrada = new Scanner(System.in);
        int opcion;

        do{
            System.out.println("----------------- Gestion Prestamos ----------------------");
            System.out.println("----------------1. Solicitar prestamo---------------------");
            System.out.println("----------------2. Consultar prestamo---------------------");
            System.out.println("----------------3. Volver al menu anterior----------------");
            System.out.println("----------------------------------------------------------");

            System.out.print("Ingrese una opcion: ");
            opcion = entrada.nextInt();

            switch (opcion){
                case 1:

                    break;
                case 2:

                    break;

                default:
                    System.out.println("Opcion no valida, debe ingresar un numero del 1 al 3.....");
            }
        }while(opcion != 3);

    }
}
