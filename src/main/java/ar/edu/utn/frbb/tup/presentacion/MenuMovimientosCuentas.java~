package ar.edu.utn.frbb.tup.presentacion;

import ar.edu.utn.frbb.tup.modelo.Banco;
import ar.edu.utn.frbb.tup.modelo.Cliente;
import ar.edu.utn.frbb.tup.servicios.ServicioCuentas;

import java.util.List;
import java.util.Scanner;

public class MenuMovimientosCuentas {

    public void gestionMovimientos (Banco banco){
        List<Cliente> clientes = banco.getClientes();// traemos la lista de clientes del banco
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
                    ServicioCuentas consultarSaldo = new ServicioCuentas();
                    consultarSaldo.consultarSaldo(clientes);
                    break;
                case 2:
                    ServicioCuentas realizarDeposito = new ServicioCuentas();
                    realizarDeposito.realizarDeposito(clientes);
                    break;

                case 3:
                    ServicioCuentas realizarExtraccion = new ServicioCuentas();
                    realizarExtraccion.realizarExtraccion(clientes);
                    break;
                case 4:
                    ServicioCuentas realizarTransferencia = new ServicioCuentas();
                    realizarTransferencia.realizarTransferencia(clientes);
                    break;
                default:
                    System.out.println("Opcion no valida, debe ingresar un numero del 1 al 5.....");
            }
        }while(opcion != 5);

    }




}