package ar.edu.utn.frbb.tup.modelos;

import ar.edu.utn.frbb.tup.presentacion.DTOs.CuentaDto;

import java.time.LocalDate;
import java.util.Random;


public class Cuenta {
    private Long dniTitular;
    private Long cbu;
    private TipoCuenta tipoCuenta;
    private TipoMoneda tipoMoneda;
    private String alias;
    private LocalDate fechaCreacion;
    private double saldo;

    public Cuenta(CuentaDto cuentaDto) {
        Random random = new Random();
        this.dniTitular = cuentaDto.getDniTitular();
        this.cbu = Math.abs(random.nextLong()%99999999) + 10000000;
        this.tipoCuenta = TipoCuenta.fromString(cuentaDto.getTipoCuenta());
        this.tipoMoneda = TipoMoneda.fromString(cuentaDto.getTipoMoneda());
        this.alias = cuentaDto.getAlias();
        this.fechaCreacion = LocalDate.now();
        this.saldo = 0;
    }

    public Cuenta() {
        Random random = new Random();
        this.cbu = Math.abs(random.nextLong()%99999999) + 10000000;
        this.fechaCreacion = LocalDate.now();
        this.saldo = 0;
    }

    public Long getDniTitular() {
        return dniTitular;
    }

    public void setDniTitular(Long dniTitular) {
        this.dniTitular = dniTitular;
    }

    public String getAlias() {
        return alias;
    }

    public Cuenta setAlias(String alias) {
        this.alias = alias;
        return this;
    }

    public LocalDate getFechaCreacion() {
        return fechaCreacion;
    }

    public Cuenta setFechaCreacion(LocalDate fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
        return this;
    }

    public double getSaldo() {
        return saldo;
    }

    public Cuenta setSaldo(double saldo) {
        this.saldo = saldo;
        return this;
    }

    public Long getCbu() {
        return cbu;
    }

    public Cuenta setCbu(Long cbu) {
        this.cbu = cbu;
        return this;
    }

    public TipoCuenta getTipoCuenta() {
        return tipoCuenta;
    }

    public void setTipoCuenta(TipoCuenta tipoCuenta) {
        this.tipoCuenta = tipoCuenta;
    }

    public TipoMoneda getTipoMoneda() {
        return tipoMoneda;
    }

    public void setTipoMoneda(TipoMoneda tipoMoneda) {
        this.tipoMoneda = tipoMoneda;
    }


}
