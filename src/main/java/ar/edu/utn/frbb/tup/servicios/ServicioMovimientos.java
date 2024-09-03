package ar.edu.utn.frbb.tup.servicios;

import ar.edu.utn.frbb.tup.modelo.Cliente;
import ar.edu.utn.frbb.tup.modelo.Cuenta;
import ar.edu.utn.frbb.tup.modelo.Movimiento;
import ar.edu.utn.frbb.tup.modelo.Prestamo;
import ar.edu.utn.frbb.tup.persistencia.CuentaDao;
import ar.edu.utn.frbb.tup.persistencia.MovimientosDao;
import ar.edu.utn.frbb.tup.presentacion.ValidacionesPresentacion;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Scanner;

@Component
public class ServicioMovimientos {
    Scanner entrada = new Scanner(System.in);
    ValidacionesServicios validar = new ValidacionesServicios();
    ServicioClientes buscar = new ServicioClientes();
    ValidacionesPresentacion validarEntrada = new ValidacionesPresentacion();
    ServicioCuentas servicioCuentas = new ServicioCuentas();
    CuentaDao cuentaDao = new CuentaDao();
    MovimientosDao movimientosDao = new MovimientosDao();

    public void inicializarMovimientos() {
        movimientosDao.inicializarMovimientos();
    }

    public void consultarSaldo(List<Cliente> clientes) {
        String dni;
        do{
            System.out.print("Ingrese el dni del cliente: ");
            dni = entrada.nextLine();
        } while (!validar.validarDni(dni));

        Cliente cliente = buscar.buscarCliente(clientes, dni);
        if (cliente == null) {
            System.out.println("Cliente no encontrado, primero debe crearlo");
        } else {
            String cbu;
            do {
                System.out.print("Ingrese el CBU de la cuenta: ");
                cbu = entrada.nextLine();
            } while (!validarEntrada.esLong(cbu));
//            Cuenta cuenta = servicioCuentas.buscarCuentas(cliente.getCuentas(), cbu);

            Cuenta cuenta = cuentaDao.findCuenta(Long.parseLong(cbu));

            if (cuenta == null) {
                System.out.println("Cuenta no encontrada");
            } else {
                System.out.println("Saldo de cuenta: " + cuenta.getSaldo());
                Movimiento movimiento = new Movimiento();
                movimiento.setCbu(Long.parseLong(cbu));
                movimiento.setFecha(LocalDate.now());
                movimiento.setHora(LocalTime.parse(LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm"))));
                movimiento.setTipoOperacion("consulta");
                movimiento.setMonto(cuenta.getSaldo());
                movimientosDao.saveMovimiento(movimiento);
            }
        }
    }

    public void realizarDeposito(List<Cliente> clientes) {
        String dni;
        do{
            System.out.print("Ingrese el dni del cliente: ");
            dni = entrada.nextLine();
        } while (!validar.validarDni(dni));

        Cliente cliente = buscar.buscarCliente(clientes, dni);
        if (cliente == null) {
            System.out.println("Cliente no encontrado, primero debe crearlo");
        } else {
            String cbu;
            do {
                System.out.print("Ingrese el CBU de la cuenta: ");
                cbu = entrada.nextLine();
            } while (!validarEntrada.esLong(cbu));

//           Cuenta cuenta = servicioCuentas.buscarCuentas(cliente.getCuentas(), cbu);
            Cuenta cuenta = cuentaDao.findCuenta(Long.parseLong(cbu));
            if (cuenta == null) {
                System.out.println("Cuenta no encontrada");
            } else {
                System.out.print("Ingrese el monto a depositar: ");
                double monto = entrada.nextDouble();
                cuenta.setSaldo(cuenta.getSaldo() + monto);
                cuentaDao.deleteCuenta(cuenta.getCbu());
                cuentaDao.saveCuenta(cuenta);
                System.out.println("Deposito realizado con exito");
                System.out.println("Saldo de cuenta: " + cuenta.getSaldo());
                Movimiento movimiento = new Movimiento();
                movimiento.setCbu(Long.parseLong(cbu));
                movimiento.setFecha(LocalDate.now());
                movimiento.setHora(LocalTime.parse(LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm"))));
                movimiento.setTipoOperacion("deposito");
                movimiento.setMonto(monto);
                movimientosDao.saveMovimiento(movimiento);

            }
        }
    }

    public void realizarExtraccion(List<Cliente> clientes) {
        String dni;
        do{
            System.out.print("Ingrese el DNI del cliente: ");
            dni = entrada.nextLine();
        } while (!validar.validarDni(dni));

        Cliente cliente = buscar.buscarCliente(clientes, dni);
        if (cliente == null) {
            System.out.println("Cliente no encontrado, primero debe crearlo");
        } else {
            String cbu;
            do {
                System.out.print("Ingrese el CBU de la cuenta: ");
                cbu = entrada.nextLine();
            } while (!validarEntrada.esLong(cbu));
//            Cuenta cuenta = servicioCuentas.buscarCuentas(cliente.getCuentas(), cbu);
            Cuenta cuenta = cuentaDao.findCuenta(Long.parseLong(cbu));
            if (cuenta == null) {
                System.out.println("Cuenta no encontrada");
            } else {
                String montoStr;
                double monto;
                do {
                    System.out.print("Ingrese el monto a extraer: ");
                    montoStr = entrada.nextLine();
                } while (!validarEntrada.esDouble(montoStr));
                monto = Double.parseDouble(montoStr);
                if (cuenta.getSaldo() < monto) {
                    System.out.println("Fondos insuficientes");
                } else {
                    cuenta.setSaldo(cuenta.getSaldo() - monto);
                    cuentaDao.deleteCuenta(cuenta.getCbu());
                    cuentaDao.saveCuenta(cuenta);
                    System.out.println("Extraccion realizada con exito");
                    System.out.println("Saldo de cuenta: " + cuenta.getSaldo());
                    Movimiento movimiento = new Movimiento();
                    movimiento.setCbu(Long.parseLong(cbu));
                    movimiento.setFecha(LocalDate.now());
                    movimiento.setHora(LocalTime.parse(LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm"))));
                    movimiento.setTipoOperacion("extraccion");
                    movimiento.setMonto(monto);
                    movimientosDao.saveMovimiento(movimiento);
                }
            }
        }
    }

    public void realizarTransferencia(List<Cliente> clientes) {
        String dniOrigen;
        String dniDestino;
        String cbuOrigen;
        String cbuDestino;
        String montoStr;

        do{
            System.out.print("Ingrese el DNI del cliente origen:  ");
            dniOrigen = entrada.nextLine();
        } while (!validar.validarDni(dniOrigen));

        Cliente clienteOrigen = buscar.buscarCliente(clientes, dniOrigen);
        if (clienteOrigen == null) {
            System.out.println("Cliente origen no encontrado, primero debe crearlo");
        }
        else {

            do {
                System.out.print("Ingrese el CBU de la cuenta origen: ");
                cbuOrigen = entrada.nextLine();
            } while (!validarEntrada.esLong(cbuOrigen));
//            Cuenta cuentaOrigen = servicioCuentas.buscarCuentas(clienteOrigen.getCuentas(), cbuOrigen);
            Cuenta cuentaOrigen = cuentaDao.findCuenta(Long.parseLong(cbuOrigen));
            if (cuentaOrigen == null) {
                System.out.println("Cuenta origen no encontrada");
            } else {
                do{
                    System.out.print("Ingrese el DNI del cliente destino: ");
                    dniDestino = entrada.nextLine();
                } while (!validar.validarDni(dniDestino));
                Cliente clienteDestino = buscar.buscarCliente(clientes, dniDestino);
                if (clienteDestino == null) {
                    System.out.println("Cliente destino no encontrado, primero debe crearlo");
                } else {
                    do {
                        System.out.print("Ingrese el CBU de la cuenta destino: ");
                        cbuDestino = entrada.nextLine();
                    } while (!validarEntrada.esLong(cbuDestino) || !validar.validarCuentaDestino(cbuOrigen, cbuDestino));

//                    Cuenta cuentaDestino = servicioCuentas.buscarCuentas(clienteDestino.getCuentas(), cbuDestino);
                    Cuenta cuentaDestino = cuentaDao.findCuenta(Long.parseLong(cbuDestino));
                    if (cuentaDestino == null) {
                        System.out.println("Cuenta destino no encontrada");
                    } else if (validar.validarModenaDestino(cuentaOrigen.getTipoMoneda(),cuentaDestino.getTipoMoneda())) {
                        double monto;
                        do {
                            System.out.print("Ingrese el monto a transferir: ");
                            montoStr = entrada.nextLine();
                        } while (!validarEntrada.esDouble(montoStr));
                        monto = Double.parseDouble(montoStr);
                        if (cuentaOrigen.getSaldo() < monto) {
                            System.out.println("Fondos insuficientes en la cuenta origen");
                        } else {
                            cuentaOrigen.setSaldo(cuentaOrigen.getSaldo() - monto);
                            cuentaDao.deleteCuenta(cuentaOrigen.getCbu());
                            cuentaDao.saveCuenta(cuentaOrigen);
                            cuentaDestino.setSaldo(cuentaDestino.getSaldo() + monto);
                            cuentaDao.deleteCuenta(cuentaDestino.getCbu());
                            cuentaDao.saveCuenta(cuentaDestino);
                            System.out.println("Transferencia realizada con exito");
                            System.out.println("Saldo cuenta origen: " + cuentaOrigen.getSaldo());
                            Movimiento movimiento = new Movimiento();
                            movimiento.setCbu(Long.parseLong(cbuOrigen));
                            movimiento.setFecha(LocalDate.now());
                            movimiento.setHora(LocalTime.parse(LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm"))));
                            movimiento.setTipoOperacion("transferencia");
                            movimiento.setMonto(monto);
                            movimientosDao.saveMovimiento(movimiento);
                        }
                    } else {
                        System.out.println("Error: la moneda de origen es diferente a la del destino.");
                        }
                }
            }
        }
    }

    public void solicitarPrestamo (Cliente cliente, Prestamo prestamo){

    }

}
