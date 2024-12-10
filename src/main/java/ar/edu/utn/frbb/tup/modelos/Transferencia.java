package ar.edu.utn.frbb.tup.modelos;

import ar.edu.utn.frbb.tup.presentacion.DTOs.TransferenciaDto;

import java.time.LocalDate;

public class Transferencia {
    private Long cbuOrigen;
    private Long cbuDestino;
    private double monto;
    private TipoMoneda tipoMoneda;
    private LocalDate fechaTransferencia;

public Transferencia (TransferenciaDto transferenciaDto) {
    this.cbuOrigen = transferenciaDto.getCbuOrigen();
    this.cbuDestino = transferenciaDto.getCbuDestino();
    this.monto = transferenciaDto.getMonto();
    this.tipoMoneda = TipoMoneda.fromString(transferenciaDto.getTipoMoneda());
    this.fechaTransferencia = LocalDate.now();
}

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

    public TipoMoneda getTipoMoneda() {
        return tipoMoneda;
    }

    public void setTipoMoneda(TipoMoneda tipoMoneda) {
        this.tipoMoneda = tipoMoneda;
    }

    public LocalDate getFechaTransferencia() {
        return fechaTransferencia;
    }

    public void setFechaTransferencia(LocalDate fechaTransferencia) {
        this.fechaTransferencia = fechaTransferencia;
    }
}

