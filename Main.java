import java.util.List;

/**
 * Punto de entrada para analizar rutas peatonales en el centro de Bogota.
 */
public class Main {
    /** Indice asignado a la Plaza de Bolivar dentro del grafo. */
    private static final int PLAZA_DE_BOLIVAR = 0;

    /** Indice asignado a San Victorino dentro del grafo. */
    private static final int SAN_VICTORINO = 3;

    /**
     * Evita crear instancias de la clase de presentacion.
     */
    private Main() {
    }

    /**
     * Construye el grafo, ejecuta ambos algoritmos y presenta sus resultados.
     *
     * @param args argumentos de linea de comandos; no se utilizan
     */
    public static void main(String[] args) {
        Grafo grafo = crearGrafo();
        System.out.println("Caso de estudio: desorden del espacio publico por "
            + "ocupacion indebida de andenes en el centro de Bogota.");
        System.out.println("Los pesos representan el costo estimado de congestion "
            + "y desvio en cada recorrido.");
        imprimirLugares(grafo);
        imprimirMatrizInicial(grafo);
        imprimirRutasDirectas(grafo);

        System.out.println("\nDijkstra desde Plaza de Bolivar");
        ResultadoDijkstra resultadoDijkstra =
            new Dijkstra().calcular(grafo, PLAZA_DE_BOLIVAR);
        imprimirEstadosDijkstra(grafo, resultadoDijkstra);
        imprimirRutaYDistancia(
            "Dijkstra",
            grafo,
            resultadoDijkstra.getRutaHasta(SAN_VICTORINO),
            resultadoDijkstra.getDistanciaHasta(SAN_VICTORINO)
        );

        System.out.println("\nFloyd-Warshall");
        ResultadoFloydWarshall resultadoFloyd =
            new FloydWarshall().calcular(grafo);
        imprimirActualizacionesFloyd(grafo, resultadoFloyd);
        imprimirMatrizFloyd(grafo, resultadoFloyd);
        imprimirRutaYDistancia(
            "Floyd-Warshall",
            grafo,
            resultadoFloyd.getRuta(PLAZA_DE_BOLIVAR, SAN_VICTORINO),
            resultadoFloyd.getDistancia(PLAZA_DE_BOLIVAR, SAN_VICTORINO)
        );

        System.out.println("\nComparacion: "
            + (resultadoDijkstra.getDistanciaHasta(SAN_VICTORINO)
                == resultadoFloyd.getDistancia(
                    PLAZA_DE_BOLIVAR, SAN_VICTORINO
                )
                ? "ambos algoritmos encuentran el mismo costo minimo de recorrido."
                : "los algoritmos encontraron costos distintos."));
    }

    /**
     * Crea el grafo dirigido con lugares y niveles de congestion del centro.
     *
     * @return grafo configurado para la actividad
     */
    private static Grafo crearGrafo() {
        Grafo grafo = new Grafo(
            "Plaza de Bolivar",
            "Museo del Oro",
            "Avenida Jimenez",
            "San Victorino"
        );
        grafo.agregarArista(0, 1, 50);
        grafo.agregarArista(0, 2, 80);
        grafo.agregarArista(1, 2, 20);
        grafo.agregarArista(1, 3, 90);
        grafo.agregarArista(2, 3, 40);
        return grafo;
    }

    /**
     * Imprime la correspondencia entre los indices y los lugares.
     *
     * @param grafo grafo cuyos lugares se muestran
     */
    private static void imprimirLugares(Grafo grafo) {
        System.out.println("Lugares:");
        for (int i = 0; i < grafo.getCantidadCiudades(); i++) {
            System.out.printf("%d = %s%n", i, grafo.getNombreCiudad(i));
        }
    }

    /**
     * Imprime los costos directos entre todas las parejas de lugares.
     *
     * @param grafo grafo del cual se obtiene la matriz inicial
     */
    private static void imprimirMatrizInicial(Grafo grafo) {
        System.out.println("\nMatriz de costos inicial:");
        imprimirEncabezado(grafo);
        for (int origen = 0; origen < grafo.getCantidadCiudades(); origen++) {
            System.out.printf("%-16s", grafo.getNombreCiudad(origen));
            for (int destino = 0; destino < grafo.getCantidadCiudades(); destino++) {
                System.out.printf("%-16s", formatearCosto(grafo.getCosto(origen, destino)));
            }
            System.out.println();
        }
    }

    /**
     * Lista las aristas dirigidas existentes y sus costos.
     *
     * @param grafo grafo cuyas rutas directas se muestran
     */
    private static void imprimirRutasDirectas(Grafo grafo) {
        System.out.println("\nRutas directas existentes:");
        for (int origen = 0; origen < grafo.getCantidadCiudades(); origen++) {
            for (int destino = 0; destino < grafo.getCantidadCiudades(); destino++) {
                if (grafo.existeArista(origen, destino)) {
                    System.out.printf(
                        "%s -> %s: %d%n",
                        grafo.getNombreCiudad(origen),
                        grafo.getNombreCiudad(destino),
                        grafo.getCosto(origen, destino)
                    );
                }
            }
        }
    }

    /**
     * Muestra distancias y predecesores despues de cada seleccion de Dijkstra.
     *
     * @param grafo grafo que aporta los nombres de los lugares
     * @param resultado resultado de la ejecucion de Dijkstra
     */
    private static void imprimirEstadosDijkstra(
        Grafo grafo, ResultadoDijkstra resultado
    ) {
        for (ResultadoDijkstra.Estado estado : resultado.getEstados()) {
            System.out.println(
                "\nSeleccion: " + grafo.getNombreCiudad(estado.getCiudadSeleccionada())
            );
            System.out.printf("%-16s %-16s %-16s%n", "Lugar", "Costo", "Predecesor");
            for (int ciudad = 0; ciudad < grafo.getCantidadCiudades(); ciudad++) {
                int distancia = estado.getDistancia(ciudad);
                int predecesor = estado.getPredecesor(ciudad);
                System.out.printf(
                    "%-16s %-16s %-16s%n",
                    grafo.getNombreCiudad(ciudad),
                    formatearCosto(distancia),
                    predecesor == -1 ? "-" : grafo.getNombreCiudad(predecesor)
                );
            }
        }
    }

    /**
     * Muestra cada mejora de distancia registrada por Floyd-Warshall.
     *
     * @param grafo grafo que aporta los nombres de los lugares
     * @param resultado resultado de la ejecucion de Floyd-Warshall
     */
    private static void imprimirActualizacionesFloyd(
        Grafo grafo, ResultadoFloydWarshall resultado
    ) {
        for (ResultadoFloydWarshall.Actualizacion actualizacion
            : resultado.getActualizaciones()) {
            System.out.printf(
                "Via %s: %s -> %s mejora de %s a %d%n",
                grafo.getNombreCiudad(actualizacion.getCiudadIntermedia()),
                grafo.getNombreCiudad(actualizacion.getOrigen()),
                grafo.getNombreCiudad(actualizacion.getDestino()),
                formatearCosto(actualizacion.getDistanciaAnterior()),
                actualizacion.getDistanciaNueva()
            );
        }
    }

    /**
     * Imprime la matriz final de distancias minimas de Floyd-Warshall.
     *
     * @param grafo grafo que aporta los nombres de los lugares
     * @param resultado resultado con las distancias calculadas
     */
    private static void imprimirMatrizFloyd(
        Grafo grafo, ResultadoFloydWarshall resultado
    ) {
        System.out.println("\nMatriz de distancias minimas:");
        imprimirEncabezado(grafo);
        for (int origen = 0; origen < grafo.getCantidadCiudades(); origen++) {
            System.out.printf("%-16s", grafo.getNombreCiudad(origen));
            for (int destino = 0; destino < grafo.getCantidadCiudades(); destino++) {
                System.out.printf(
                    "%-16s",
                    formatearCosto(resultado.getDistancia(origen, destino))
                );
            }
            System.out.println();
        }
    }

    /**
     * Imprime los nombres de los lugares que encabezan una matriz.
     *
     * @param grafo grafo cuyos lugares se imprimen
     */
    private static void imprimirEncabezado(Grafo grafo) {
        System.out.printf("%-16s", "");
        for (int ciudad = 0; ciudad < grafo.getCantidadCiudades(); ciudad++) {
            System.out.printf("%-16s", grafo.getNombreCiudad(ciudad));
        }
        System.out.println();
    }

    /**
     * Imprime una ruta y su costo total, o informa que no existe.
     *
     * @param algoritmo nombre del algoritmo que obtuvo el resultado
     * @param grafo grafo que aporta los nombres de los lugares
     * @param ruta indices de lugares que forman el recorrido
     * @param distancia costo total del recorrido
     */
    private static void imprimirRutaYDistancia(
        String algoritmo, Grafo grafo, List<Integer> ruta, int distancia
    ) {
        if (ruta.isEmpty() || distancia == Grafo.INFINITO) {
            System.out.println(algoritmo + ": no existe una ruta peatonal entre los lugares.");
            return;
        }

        StringBuilder textoRuta = new StringBuilder();
        for (int i = 0; i < ruta.size(); i++) {
            if (i > 0) {
                textoRuta.append(" -> ");
            }
            textoRuta.append(grafo.getNombreCiudad(ruta.get(i)));
        }
        System.out.println(algoritmo + ": " + textoRuta);
        System.out.println("Costo total de congestión y desvío: " + distancia);
    }

    /**
     * Convierte el valor infinito del grafo a una etiqueta legible.
     *
     * @param costo costo que se desea presentar
     * @return "INF" si no existe ruta directa; de lo contrario, el costo
     */
    private static String formatearCosto(int costo) {
        return costo == Grafo.INFINITO ? "INF" : Integer.toString(costo);
    }
}
