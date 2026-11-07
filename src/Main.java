import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public final class Main {
    private static Registro r(String usuario, String ruta, String estacion,
                              String accion, String fechaHora) {
        return new Registro(usuario, ruta, estacion, accion,
                LocalDateTime.parse(fechaHora));
    }

    public static void main(String[] args) {
        LocalDate dia = LocalDate.of(2026, 11, 5);
        List<Registro> datos = List.of(
            r("U1", "R1", "Central", "entrada", "2026-11-05T07:00"),
            r("U1", "R1", "Norte", "salida", "2026-11-05T07:20"),
            r("U2", "R1", "Central", "entrada", "2026-11-05T07:15"),
            r("U2", "R1", "Norte", "salida", "2026-11-05T07:40"),
            r("U3", "R2", "Sur", "entrada", "2026-11-05T08:00"),
            r("U3", "R2", "Central", "salida", "2026-11-05T08:30"),
            r("U4", "R1", "Central", "entrada", "2026-11-05T07:45"),
            r("U5", "R2", "Sur", "entrada", "2026-11-05T09:10"),
            r("U6", "R2", "Sur", "entrada", "2026-11-04T07:00")
        );
        System.out.println("=== TECNOMOVIL DATA ===");
        System.out.println("Registros simulados: " + datos.size());
        System.out.println("Dia analizado: " + dia);
        System.out.println("Afluencia por estacion: " +
                new java.util.TreeMap<>(AnalisisTransporte.afluenciaPorEstacion(datos, dia)));
        System.out.println("Entradas por hora: " +
                new java.util.TreeMap<>(AnalisisTransporte.entradasPorHora(datos, dia)));
        System.out.println("Horas pico: " + AnalisisTransporte.horasPico(datos, dia));
        System.out.println("Rutas mas utilizadas: " + AnalisisTransporte.rutasMasUsadas(datos, dia));
        System.out.println("Patrones de U1: " + AnalisisTransporte.patronesPorUsuario(datos).get("U1"));
        System.out.printf(java.util.Locale.US, "Promedio entre estaciones: %.2f minutos%n",
                AnalisisTransporte.minutosPromedioEntreEstaciones(datos));
        System.out.println("Rutas criticas (umbral > 2 entradas): " +
                new java.util.TreeSet<>(AnalisisTransporte.rutasCriticas(datos, dia, 2)));

        // Pruebas reproducibles sin bibliotecas de terceros.
        assertEquals(3L, AnalisisTransporte.afluenciaPorEstacion(datos, dia).get("Central"));
        assertEquals(List.of(7), AnalisisTransporte.horasPico(datos, dia));
        assertEquals("R1", AnalisisTransporte.rutasMasUsadas(datos, dia).get(0).getKey());
        assertEquals(List.of("Central", "Norte"),
                AnalisisTransporte.patronesPorUsuario(datos).get("U1"));
        assertEquals(25.0, AnalisisTransporte.minutosPromedioEntreEstaciones(datos));
        assertEquals(java.util.Set.of("R1"), AnalisisTransporte.rutasCriticas(datos, dia, 2));
        assertEquals(9, datos.size()); // entrada conservada
        System.out.println("PRUEBAS: 7/7 correctas");
    }

    private static void assertEquals(Object esperado, Object actual) {
        if (!esperado.equals(actual))
            throw new AssertionError("Esperado " + esperado + ", obtenido " + actual);
    }
}
