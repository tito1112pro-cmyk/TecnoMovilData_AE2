# TecnoMovil Data - Unidad 2

Proyecto académico de procesamiento funcional de datos del transporte urbano, basado en el caso de estudio de IU Digital de Antioquia.

## Requisitos
- JDK 17 o posterior (verificado con OpenJDK 21).
- Sin frameworks ni dependencias externas.

## Ejecución
```bash
mkdir -p out
javac -encoding UTF-8 -d out src/*.java
java -cp out Main
```

El programa imprime las seis operaciones y ejecuta siete verificaciones deterministas. Los eventos y el umbral son simulados. El indicador de sobrecarga es **un proxy de entradas por ruta**, no ocupación real en tiempo real; el tiempo promedio es entre validaciones consecutivas del mismo usuario y ruta.

## Publicación en GitHub
Crear un repositorio personal, subir `src/`, `README.md`, `evidencias/` y documentar la URL en el informe. No se ha publicado un repositorio externo automáticamente.
