import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

/**
 * Ejecuta el algoritmo de Dijkstra para encontrar rutas peatonales de menor costo.
 */
public final class Dijkstra {
    /**
     * Crea el calculador de rutas de Dijkstra.
     */
    public Dijkstra() {
    }

    /**
     * Calcula las distancias minimas desde una ciudad de origen.
     *
     * @param grafo grafo que se analiza
     * @param origen indice de la ciudad inicial
     * @return distancias, predecesores y estados posteriores a cada seleccion
     * @throws IndexOutOfBoundsException si el origen no pertenece al grafo
     */
    public ResultadoDijkstra calcular(Grafo grafo, int origen) {
        int cantidad = grafo.getCantidadCiudades();
        validarIndice(origen, cantidad);

        int[] distancias = new int[cantidad];
        int[] predecesores = new int[cantidad];
        boolean[] seleccionados = new boolean[cantidad];
        Arrays.fill(distancias, Grafo.INFINITO);
        Arrays.fill(predecesores, -1);
        distancias[origen] = 0;

        PriorityQueue<NodoDistancia> cola = new PriorityQueue<>(
            Comparator.comparingInt(nodo -> nodo.distancia)
        );
        List<ResultadoDijkstra.Estado> estados = new ArrayList<>();
        cola.add(new NodoDistancia(origen, 0));

        while (!cola.isEmpty()) {
            NodoDistancia actual = cola.poll();
            int ciudad = actual.ciudad;
            if (actual.distancia != distancias[ciudad] || seleccionados[ciudad]) {
                continue;
            }
            seleccionados[ciudad] = true;

            for (int destino = 0; destino < cantidad; destino++) {
                int costo = grafo.getCosto(ciudad, destino);
                if (costo == Grafo.INFINITO || seleccionados[destino]) {
                    continue;
                }

                int nuevaDistancia = distancias[ciudad] + costo;
                if (nuevaDistancia < distancias[destino]) {
                    distancias[destino] = nuevaDistancia;
                    predecesores[destino] = ciudad;
                    cola.add(new NodoDistancia(destino, nuevaDistancia));
                }
            }

            estados.add(new ResultadoDijkstra.Estado(ciudad, distancias, predecesores));
        }

        return new ResultadoDijkstra(origen, distancias, predecesores, estados);
    }

    /**
     * Comprueba que un indice este dentro del rango de vertices.
     *
     * @param indice indice que se valida
     * @param cantidad cantidad de vertices disponibles
     * @throws IndexOutOfBoundsException si el indice no es valido
     */
    private void validarIndice(int indice, int cantidad) {
        if (indice < 0 || indice >= cantidad) {
            throw new IndexOutOfBoundsException("Indice de origen fuera del grafo: " + indice);
        }
    }

    /**
     * Elemento de la cola de prioridad que asocia una ciudad con su distancia.
     */
    private static final class NodoDistancia {
        /** Indice de la ciudad representada. */
        private final int ciudad;

        /** Distancia tentativa desde el origen. */
        private final int distancia;

        /**
         * Crea una entrada de la cola de prioridad.
         *
         * @param ciudad indice de la ciudad
         * @param distancia distancia tentativa
         */
        private NodoDistancia(int ciudad, int distancia) {
            this.ciudad = ciudad;
            this.distancia = distancia;
        }
    }
}
