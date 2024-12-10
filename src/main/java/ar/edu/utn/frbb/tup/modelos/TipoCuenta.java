package ar.edu.utn.frbb.tup.modelos;

public enum TipoCuenta {
    CUENTA_CORRIENTE("CUENTA_CORRIENTE"),
    CAJA_AHORRO("CAJA_AHORRO");

    private final String descripcion;

    TipoCuenta(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public static TipoCuenta fromString(String text) throws IllegalArgumentException {
        for (TipoCuenta tipo : TipoCuenta.values()) {
            if (tipo.descripcion.equalsIgnoreCase(text)) {
                return tipo;
            }
        }
        throw new IllegalArgumentException("No se pudo encontrar un TipoCuenta con la descripcion: " + text + ", debe ser 'CUENTA_CORRIENTE' o 'CAJA_AHORRO'");
    }
}
