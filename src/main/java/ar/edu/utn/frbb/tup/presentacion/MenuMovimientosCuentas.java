package ar.edu.utn.frbb.tup.presentacion;

import ar.edu.utn.frbb.tup.excepciones.ClientesVaciosException;
import ar.edu.utn.frbb.tup.modelo.Banco;
import ar.edu.utn.frbb.tup.modelo.Cliente;
import ar.edu.utn.frbb.tup.persistencia.ClienteDao;
import ar.edu.utn.frbb.tup.servicios.ServicioCuentas;
import ar.edu.utn.frbb.tup.servicios.ServicioMovimientos;

import java.util.List;
import java.util.Scanner;

public class MenuMovimientosCuentas {

    public void gestionMovimientos (Banco banco) throws ClientesVaciosException {

        ClienteDao clienteDao = new ClienteDao();
        Banco bancoProvincia = new Banco(); //creamos un objeto con el nombre del banco
        bancoProvincia.setClientes(clienteDao.findAllClientes());// traemos la lista del archivo y la cargamos en el banco

//        List<Cliente> clientes = banco.getClientes();// traemos la lista de clientes del banco
        Scanner entrada = new Scanner(System.in);
        int opcion;

        do{
            System.out.println("------------- Gestion Movimientos de Cuentas -------------");
            System.out.println("----------------1. Consultar Saldo------------------------");
            System.out.println("----------------2. Depositar dinero-----------------------");
            System.out.println("----------------3. Realizar extracción--------------------");
            System.out.println("----------------4. Realizar transferencia-----------------");
            System.out.println("----------------5. Volver al menu anterior----------------");
            System.out.println("----------------------------------------------------------");

            System.out.print("Ingrese una opcion: ");
            opcion = entrada.nextInt();

            switch (opcion){
                case 1:
                    ServicioMovimientos consultarSaldo = new ServicioMovimientos();
                    consultarSaldo.consultarSaldo(bancoProvincia.getClientes());
                    break;
                case 2:
                    ServicioMovimientos realizarDeposito = new ServicioMovimientos();
                    realizarDeposito.realizarDeposito(bancoProvincia.getClientes());
                    break;

                case 3:
                    ServicioMovimientos realizarExtraccion = new ServicioMovimientos();
                    realizarExtraccion.realizarExtraccion(bancoProvincia.getClientes());
                    break;
                case 4:
                    ServicioMovimientos realizarTransferencia = new ServicioMovimientos();
                    realizarTransferencia.realizarTransferencia(bancoProvincia.getClientes());
                    break;
                default:
                    System.out.println("Opcion no valida, debe ingresar un numero del 1 al 5.....");
            }
        }while(opcion != 5);

    }




}