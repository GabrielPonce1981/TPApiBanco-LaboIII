package ar.edu.utn.frbb.tup.presentacion.DTOs;

//creo clase para devolver un mensaje de respuesta a la transferencia
public class TransferenciaResponse {
    private String estado;
    private String mensaje;


    public TransferenciaResponse( String estado, String mensaje) {
        this.estado = estado;
        this.mensaje = mensaje;

    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }
}



