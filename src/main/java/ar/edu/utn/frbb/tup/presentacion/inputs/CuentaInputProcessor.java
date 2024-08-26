package ar.edu.utn.frbb.tup.presentacion.inputs;

import ar.edu.utn.frbb.tup.modelo.Cuenta;
import ar.edu.utn.frbb.tup.modelo.TipoCuenta;
import ar.edu.utn.frbb.tup.modelo.TipoMoneda;

import java.time.LocalDate;
import java.util.Random;

public class CuentaInputProcessor extends BaseInputProcessor {

    Random random = new Random();


    public Cuenta ingresarCuenta() {

        Cuenta cuenta = new Cuenta();

        cuenta.setTipoCuenta(ingreseTipoCuenta());
        cuenta.setTipoMoneda(ingreseTipoMoneda());
        cuenta.setAlias(ingreseAliasCuenta());
        cuenta.setFechaCreacion(LocalDate.now());
        cuenta.setSaldo(0);
        cuenta.setCbu(ingreseCbuDeCuenta(random));

        clearScreen();
        return cuenta;
    }

    public String ingreseAliasCuenta() {
        System.out.print("Ingrese el alias de la cuenta: ");
        return scanner.nextLine();
    }

    public long ingreseCbuDeCuenta(Random randon){
        return randon.nextInt(1000000) + 20000000;
    }

    public TipoCuenta ingreseTipoCuenta() {
        System.out.print("Ingrese el tipo de cuenta: Caja de Ahorro(A) o Cuenta Corriente(C):");
        String tipoCuentaStr = scanner.nextLine().toUpperCase();
        while (!tipoCuentaStr.equals("A") && !tipoCuentaStr.equals("C")){
            System.out.println("Tipo de cuenta inválido.");
            System.out.print("Ingrese 'A' para Caja de Ahorro o 'C' para Cuenta Corriente: ");
            tipoCuentaStr = scanner.nextLine().toUpperCase();
        };
        TipoCuenta tipoCuenta = TipoCuenta.fromString(tipoCuentaStr);
        return tipoCuenta;
    }

    public TipoMoneda ingreseTipoMoneda() {
        System.out.print("Ingrese el tipo de moneda: Pesos(P) o Dolares(D):");
        String tipoMonedaStr = scanner.nextLine().toUpperCase();
        while (!tipoMonedaStr.equals("P") && !tipoMonedaStr.equals("D")){
            System.out.println("Tipo de moneda inválido.");
            System.out.print("Ingrese 'P' para Pesos o 'D' para Dolares: ");
            tipoMonedaStr = scanner.nextLine().toUpperCase();
        };
        TipoMoneda tipoMoneda = TipoMoneda.fromString(tipoMonedaStr);
        return tipoMoneda;
    }
}
