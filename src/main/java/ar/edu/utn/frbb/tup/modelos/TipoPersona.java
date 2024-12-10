package ar.edu.utn.frbb.tup.modelos;

public enum TipoPersona {
    PERSONA_FISICA("Fisica"),
    PERSONA_JURIDICA("Juridica");

    private final String descripcion;

    TipoPersona(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public static TipoPersona fromString(String text) {
        for (TipoPersona tipo : TipoPersona.values()) {
            if (tipo.descripcion.equalsIgnoreCase(text) || tipo.name().equalsIgnoreCase(text)) {
                return tipo;
            }
        }
        throw new IllegalArgumentException(" Error: No se pudo encontrar un TipoPersona con la descripción: " + text + ", ingresar 'PERSONA_FISICA', 'PERSONA_JURIDICA', 'Fisica' o 'Juridica'");
    }
}
