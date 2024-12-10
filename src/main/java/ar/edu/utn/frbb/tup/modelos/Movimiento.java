package ar.edu.utn.frbb.tup.modelos;

import java.time.LocalDate;
import java.time.LocalTime;

public class Movimiento {
    private Long cbu;
    private LocalDate fechaOperacion;
    private LocalTime horaOperacion;
    private String tipoOperacion;
    private double monto;

    public Long getCbu() {
        return cbu;
    }

    public void setCbu(Long cbu) {
        this.cbu = cbu;
    }

    public LocalDate getFechaOperacion() {
        return fechaOperacion;
    }

    public Movimiento setFecha(LocalDate fecha){
        this.fechaOperacion = fecha;
        return this;
    }

    public LocalTime getHoraOperacion() {
        return horaOperacion;
    }

    public Movimiento setHora(LocalTime hora){
        this.horaOperacion = hora;
        return this;
    }

    public String getTipoOperacion() {
        return tipoOperacion;
    }

    public void setTipoOperacion(String tipoOperacion) {
        this.tipoOperacion = tipoOperacion;
    }

    public double getMonto() {
        return monto;
    }
    public void setMonto(double monto) {
        this.monto = monto;
    }

    public void setFechaOperacion(LocalDate fechaOperacion) {
        this.fechaOperacion = fechaOperacion;
    }

    public void setHoraOperacion(LocalTime horaOperacion) {
        this.horaOperacion = horaOperacion;
    }
}
