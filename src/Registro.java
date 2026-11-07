import java.time.LocalDateTime;
import java.util.Objects;

/** Registro inmutable de una validación en el sistema de transporte. */
public record Registro(String idUsuario, String ruta, String estacion,
                       String accion, LocalDateTime timestamp) {
    public Registro {
        Objects.requireNonNull(idUsuario);
        Objects.requireNonNull(ruta);
        Objects.requireNonNull(estacion);
        Objects.requireNonNull(accion);
        Objects.requireNonNull(timestamp);
        if (!accion.equals("entrada") && !accion.equals("salida")) {
            throw new IllegalArgumentException("Accion invalida: " + accion);
        }
    }
}
