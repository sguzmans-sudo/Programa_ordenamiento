import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Contiene el resultado y los estados intermedios de una ejecucion de Dijkstra.
 */
public final class ResultadoDijkstra {
    /** Indice del vertice desde el cual se calcularon las rutas. */
    private final int origen;

    /** Distancia minima conocida desde el origen a cada ciudad. */
    private final int[] distancias;

    /** Predecesor utilizado para alcanzar cada ciudad. */
    private final int[] predecesores;

    /** Estados inmutables registrados despues de seleccionar cada ciudad. */
    private final List<Estado> estados;

    /**
     * Crea el resultado y copia los datos mutables para mantener la encapsulacion.
     *
     * @param origen indice del vertice inicial
     * @param distancias distancias calculadas
     * @param predecesores predecesores de cada ciudad
     * @param estados estados registrados durante el algoritmo
     */
    ResultadoDijkstra(
        int origen, int[] distancias, int[] predecesores, List<Estado> estados
    ) {
        this.origen = origen;
        this.distancias = distancias.clone();
        this.predecesores = predecesores.clone();
        this.estados = Collections.unmodifiableList(new ArrayList<>(estados));
    }

    /**
     * Obtiene la distancia minima hasta una ciudad.
     *
     * @param destino indice de la ciudad destino
     * @return distancia o {@link Grafo#INFINITO} si no es alcanzable
     * @throws IndexOutOfBoundsException si el destino no pertenece al resultado
     */
    public int getDistanciaHasta(int destino) {
        validarIndice(destino);
        return distancias[destino];
    }

    /**
     * Obtiene el predecesor de una ciudad en el arbol de rutas minimas.
     *
     * @param ciudad indice de la ciudad consultada
     * @return indice del predecesor o {@code -1} si no tiene uno
     * @throws IndexOutOfBoundsException si la ciudad no pertenece al resultado
     */
    public int getPredecesorDe(int ciudad) {
        validarIndice(ciudad);
        return predecesores[ciudad];
    }

    /**
     * Reconstruye la ruta desde el origen hasta una ciudad.
     *
     * @param destino indice de la ciudad destino
     * @return indices de las ciudades de la ruta, o una lista vacia si no es alcanzable
     * @throws IndexOutOfBoundsException si el destino no pertenece al resultado
     */
    public List<Integer> getRutaHasta(int destino) {
        validarIndice(destino);
        LinkedList<Integer> ruta = new LinkedList<>();
        for (int actual = destino; actual != -1; actual = predecesores[actual]) {
            ruta.addFirst(actual);
            if (actual == origen) {
                return Collections.unmodifiableList(ruta);
            }
        }
        return Collections.emptyList();
    }

    /**
     * Obtiene los estados registrados durante el algoritmo.
     *
     * @return lista inmutable de estados
     */
    public List<Estado> getEstados() {
        return estados;
    }

    /**
     * Verifica que un indice pertenezca a los arreglos del resultado.
     *
     * @param indice indice que se valida
     * @throws IndexOutOfBoundsException si el indice no es valido
     */
    private void validarIndice(int indice) {
        if (indice < 0 || indice >= distancias.length) {
            throw new IndexOutOfBoundsException("Indice de ciudad fuera del resultado: " + indice);
        }
    }

    /**
     * Instantanea inmutable de distancias y predecesores tras una seleccion.
     */
    public static final class Estado {
        /** Ciudad que se selecciono en este paso. */
        private final int ciudadSeleccionada;

        /** Copia de las distancias al finalizar el paso. */
        private final int[] distancias;

        /** Copia de los predecesores al finalizar el paso. */
        private final int[] predecesores;

        /**
         * Crea una copia del estado actual del algoritmo.
         *
         * @param ciudadSeleccionada indice de la ciudad seleccionada
         * @param distancias distancias actuales
         * @param predecesores predecesores actuales
         */
        Estado(int ciudadSeleccionada, int[] distancias, int[] predecesores) {
            this.ciudadSeleccionada = ciudadSeleccionada;
            this.distancias = distancias.clone();
            this.predecesores = predecesores.clone();
        }

        /**
         * Obtiene la ciudad seleccionada en este paso.
         *
         * @return indice de la ciudad
         */
        public int getCiudadSeleccionada() {
            return ciudadSeleccionada;
        }

        /**
         * Obtiene la distancia hasta una ciudad en este paso.
         *
         * @param ciudad indice de la ciudad
         * @return distancia registrada o {@link Grafo#INFINITO} si no es alcanzable
         * @throws IndexOutOfBoundsException si la ciudad no pertenece al estado
         */
        public int getDistancia(int ciudad) {
            validarIndice(ciudad);
            return distancias[ciudad];
        }

        /**
         * Obtiene el predecesor de una ciudad en este paso.
         *
         * @param ciudad indice de la ciudad
         * @return indice del predecesor o {@code -1} si no tiene uno
         * @throws IndexOutOfBoundsException si la ciudad no pertenece al estado
         */
        public int getPredecesor(int ciudad) {
            validarIndice(ciudad);
            return predecesores[ciudad];
        }

        /**
         * Verifica que el indice pertenezca a las instantaneas del estado.
         *
         * @param indice indice que se valida
         * @throws IndexOutOfBoundsException si el indice no es valido
         */
        private void validarIndice(int indice) {
            if (indice < 0 || indice >= distancias.length) {
                throw new IndexOutOfBoundsException("Indice de ciudad fuera del estado: " + indice);
            }
        }
    }
}
