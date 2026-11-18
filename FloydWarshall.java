import java.util.ArrayList;
import java.util.List;

/**
 * Calcula los costos minimos entre todos los pares de lugares del centro.
 */
public final class FloydWarshall {
    /**
     * Crea el calculador de rutas de Floyd-Warshall.
     */
    public FloydWarshall() {
    }

    /**
     * Ejecuta Floyd-Warshall y conserva las mejoras realizadas durante el calculo.
     *
     * @param grafo grafo dirigido que se analiza
     * @return distancias minimas, rutas y actualizaciones encontradas
     */
    public ResultadoFloydWarshall calcular(Grafo grafo) {
        int cantidad = grafo.getCantidadCiudades();
        int[][] distancias = new int[cantidad][cantidad];
        int[][] siguiente = new int[cantidad][cantidad];
        List<ResultadoFloydWarshall.Actualizacion> actualizaciones = new ArrayList<>();

        for (int origen = 0; origen < cantidad; origen++) {
            for (int destino = 0; destino < cantidad; destino++) {
                distancias[origen][destino] = grafo.getCosto(origen, destino);
                siguiente[origen][destino] =
                    grafo.getCosto(origen, destino) == Grafo.INFINITO ? -1 : destino;
            }
        }

        for (int intermedia = 0; intermedia < cantidad; intermedia++) {
            for (int origen = 0; origen < cantidad; origen++) {
                if (distancias[origen][intermedia] == Grafo.INFINITO) {
                    continue;
                }
                for (int destino = 0; destino < cantidad; destino++) {
                    if (distancias[intermedia][destino] == Grafo.INFINITO) {
                        continue;
                    }

                    int alternativa =
                        distancias[origen][intermedia] + distancias[intermedia][destino];
                    if (alternativa < distancias[origen][destino]) {
                        actualizaciones.add(new ResultadoFloydWarshall.Actualizacion(
                            intermedia,
                            origen,
                            destino,
                            distancias[origen][destino],
                            alternativa
                        ));
                        distancias[origen][destino] = alternativa;
                        siguiente[origen][destino] = siguiente[origen][intermedia];
                    }
                }
            }
        }

        return new ResultadoFloydWarshall(distancias, siguiente, actualizaciones);
    }
}
