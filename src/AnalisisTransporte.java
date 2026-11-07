import java.time.Duration;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/** Operaciones puras y declarativas. No altera los datos de entrada. */
public final class AnalisisTransporte {
    private AnalisisTransporte() { }

    // Funcion de orden superior: recibe un predicado y devuelve registros filtrados.
    public static List<Registro> filtrar(List<Registro> datos, Predicate<Registro> criterio) {
        return datos.stream().filter(criterio).toList();
    }

    // Funcion de orden superior: recibe una funcion que extrae una clave de agrupacion.
    public static <K> Map<K, Long> contarPor(List<Registro> datos,
                                              Function<Registro, K> clave) {
        return Map.copyOf(datos.stream().collect(
                Collectors.groupingBy(clave, Collectors.counting())));
    }

    /** Afluencia = numero de eventos de entrada por estacion en el dia. */
    public static Map<String, Long> afluenciaPorEstacion(List<Registro> datos, LocalDate dia) {
        return contarPor(filtrar(datos, r -> r.accion().equals("entrada")
                && r.timestamp().toLocalDate().equals(dia)), Registro::estacion);
    }

    /** Conteo de entradas por hora (0 a 23); no incluye eventos de salida. */
    public static Map<Integer, Long> entradasPorHora(List<Registro> datos, LocalDate dia) {
        return contarPor(filtrar(datos, r -> r.accion().equals("entrada")
                && r.timestamp().toLocalDate().equals(dia)), r -> r.timestamp().getHour());
    }

    /** Devuelve todas las horas empatadas con el valor maximo, en orden ascendente. */
    public static List<Integer> horasPico(List<Registro> datos, LocalDate dia) {
        Map<Integer, Long> porHora = entradasPorHora(datos, dia);
        long max = porHora.values().stream().mapToLong(Long::longValue).max().orElse(0);
        if (max == 0) return List.of();
        return porHora.entrySet().stream().filter(e -> e.getValue() == max)
                .map(Map.Entry::getKey).sorted().toList();
    }

    /** Rutas ordenadas de mayor a menor numero de entradas; desempate alfabetico. */
    public static List<Map.Entry<String, Long>> rutasMasUsadas(List<Registro> datos,
                                                                LocalDate dia) {
        Map<String, Long> conteos = contarPor(filtrar(datos, r ->
                r.accion().equals("entrada") && r.timestamp().toLocalDate().equals(dia)),
                Registro::ruta);
        return conteos.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed()
                        .thenComparing(Map.Entry.comparingByKey())).toList();
    }

    /** Secuencia cronologica de estaciones visitadas por usuario. */
    public static Map<String, List<String>> patronesPorUsuario(List<Registro> datos) {
        Map<String, List<String>> resultado = datos.stream()
                .collect(Collectors.groupingBy(Registro::idUsuario,
                        Collectors.collectingAndThen(Collectors.toList(), grupo ->
                                grupo.stream().sorted(Comparator.comparing(Registro::timestamp))
                                        .map(Registro::estacion).toList())));
        return Map.copyOf(resultado);
    }

    /**
     * Minutos promedio entre eventos consecutivos de un mismo usuario.
     * Solo se emparejan registros de la misma ruta y estaciones distintas;
     * son tiempos estimados entre validaciones, no tiempos GPS.
     */
    public static double minutosPromedioEntreEstaciones(List<Registro> datos) {
        Map<String, List<Registro>> grupos = datos.stream()
                .collect(Collectors.groupingBy(Registro::idUsuario));
        List<Long> minutos = grupos.values().stream().flatMap(grupo -> {
            List<Registro> ordenados = grupo.stream()
                    .sorted(Comparator.comparing(Registro::timestamp)).toList();
            return java.util.stream.IntStream.range(1, ordenados.size())
                    .mapToObj(i -> List.of(ordenados.get(i - 1), ordenados.get(i)))
                    .filter(par -> par.get(0).ruta().equals(par.get(1).ruta())
                            && !par.get(0).estacion().equals(par.get(1).estacion()))
                    .map(par -> Duration.between(par.get(0).timestamp(),
                            par.get(1).timestamp()).toMinutes())
                    .filter(m -> m >= 0);
        }).toList();
        return minutos.stream().mapToLong(Long::longValue).average().orElse(0.0);
    }

    /**
     * Proxy simulado de ocupacion: entradas por ruta durante el dia.
     * No equivale a ocupacion instantanea de un bus.
     */
    public static Set<String> rutasCriticas(List<Registro> datos, LocalDate dia,
                                            long umbralEntradas) {
        if (umbralEntradas < 0) throw new IllegalArgumentException("Umbral negativo");
        return Set.copyOf(rutasMasUsadas(datos, dia).stream()
                .filter(e -> e.getValue() > umbralEntradas)
                .map(Map.Entry::getKey).toList());
    }
}
