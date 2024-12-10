package ar.edu.utn.frbb.tup.presentacion.DTOs;

public class TransferenciaDto {
    private Long cbuOrigen;
    private Long cbuDestino;
    private double monto;
    private String tipoMoneda;


    public Long getCbuOrigen() {
        return cbuOrigen;
    }

    public void setCbuOrigen(Long cbuOrigen) {
        this.cbuOrigen = cbuOrigen;
    }

    public Long getCbuDestino() {
        return cbuDestino;
    }

    public void setCbuDestino(Long cbuDestino) {
        this.cbuDestino = cbuDestino;
    }

    public double getMonto() {
        return monto;
    }

    public void setMonto(double monto) {
        this.monto = monto;
    }

    public String getTipoMoneda() {
        return tipoMoneda;
    }

    public void setTipoMoneda(String tipoMoneda) {
        this.tipoMoneda = tipoMoneda;
    }

}

