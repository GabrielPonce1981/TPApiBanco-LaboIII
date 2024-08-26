package ar.edu.utn.frbb.tup.excepciones;

public class ClienteExistenteException extends RuntimeException {
    public ClienteExistenteException(String message) {
        super(message);
    }
}
