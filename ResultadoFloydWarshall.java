import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Conserva distancias minimas, rutas y actualizaciones de Floyd-Warshall.
 */
public final class ResultadoFloydWarshall {
    /** Matriz final con las distancias minimas entre cada par de ciudades. */
    private final int[][] distancias;

    /** Siguiente ciudad que se debe visitar para reconstruir cada ruta. */
    private final int[][] siguiente;

    /** Mejoras de distancia realizadas durante la ejecucion. */
    private final List<Actualizacion> actualizaciones;

    /**
     * Crea el resultado con copias defensivas de las matrices y la lista.
     *
     * @param distancias matriz de distancias finales
     * @param siguiente matriz para reconstruccion de rutas
     * @param actualizaciones cambios de distancia registrados
     */
    ResultadoFloydWarshall(
        int[][] distancias,
        int[][] siguiente,
        List<Actualizacion> actualizaciones
    ) {
        this.distancias = copiarMatriz(distancias);
        this.siguiente = copiarMatriz(siguiente);
        this.actualizaciones = Collections.unmodifiableList(
            new ArrayList<>(actualizaciones)
        );
    }

    /**
     * Obtiene la cantidad de ciudades incluidas en las matrices.
     *
     * @return cantidad de ciudades
     */
    public int getCantidadCiudades() {
        return distancias.length;
    }

    /**
     * Obtiene la distancia minima entre dos ciudades.
     *
     * @param origen indice de la ciudad inicial
     * @param destino indice de la ciudad final
     * @return distancia minima o {@link Grafo#INFINITO} si no hay ruta
     * @throws IndexOutOfBoundsException si algun indice no pertenece al resultado
     */
    public int getDistancia(int origen, int destino) {
        validarIndice(origen);
        validarIndice(destino);
        return distancias[origen][destino];
    }

    /**
     * Reconstruye la ruta minima entre dos ciudades.
     *
     * @param origen indice de la ciudad inicial
     * @param destino indice de la ciudad final
     * @return indices de las ciudades de la ruta, o una lista vacia si no hay ruta
     * @throws IndexOutOfBoundsException si algun indice no pertenece al resultado
     */
    public List<Integer> getRuta(int origen, int destino) {
        validarIndice(origen);
        validarIndice(destino);
        if (siguiente[origen][destino] == -1) {
            return Collections.emptyList();
        }

        List<Integer> ruta = new ArrayList<>();
        int actual = origen;
        ruta.add(actual);
        while (actual != destino) {
            actual = siguiente[actual][destino];
            if (actual == -1) {
                return Collections.emptyList();
            }
            ruta.add(actual);
        }
        return Collections.unmodifiableList(ruta);
    }

    /**
     * Obtiene las mejoras de distancia encontradas por el algoritmo.
     *
     * @return lista inmutable de actualizaciones
     */
    public List<Actualizacion> getActualizaciones() {
        return actualizaciones;
    }

    /**
     * Comprueba que un indice pertenezca a las matrices del resultado.
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
     * Copia cada fila de una matriz para evitar compartir su estado mutable.
     *
     * @param matriz matriz que se copiara
     * @return copia independiente de la matriz
     */
    private static int[][] copiarMatriz(int[][] matriz) {
        int[][] copia = new int[matriz.length][];
        for (int i = 0; i < matriz.length; i++) {
            copia[i] = matriz[i].clone();
        }
        return copia;
    }

    /**
     * Describe una mejora de distancia realizada durante Floyd-Warshall.
     */
    public static final class Actualizacion {
        /** Ciudad usada como intermediaria en el nuevo recorrido. */
        private final int ciudadIntermedia;

        /** Indice de la ciudad de origen de la ruta mejorada. */
        private final int origen;

        /** Indice de la ciudad destino de la ruta mejorada. */
        private final int destino;

        /** Distancia que tenia la ruta antes de la mejora. */
        private final int distanciaAnterior;

        /** Nueva distancia minima encontrada. */
        private final int distanciaNueva;

        /**
         * Crea el registro de una mejora de distancia.
         *
         * @param ciudadIntermedia indice de la ciudad usada como intermedia
         * @param origen indice de la ciudad de origen
         * @param destino indice de la ciudad destino
         * @param distanciaAnterior distancia antes de la mejora
         * @param distanciaNueva distancia obtenida
         */
        Actualizacion(
            int ciudadIntermedia,
            int origen,
            int destino,
            int distanciaAnterior,
            int distanciaNueva
        ) {
            this.ciudadIntermedia = ciudadIntermedia;
            this.origen = origen;
            this.destino = destino;
            this.distanciaAnterior = distanciaAnterior;
            this.distanciaNueva = distanciaNueva;
        }

        /**
         * Obtiene el indice de la ciudad intermedia.
         *
         * @return indice de la ciudad intermedia
         */
        public int getCiudadIntermedia() {
            return ciudadIntermedia;
        }

        /**
         * Obtiene el indice de origen de la ruta mejorada.
         *
         * @return indice de origen
         */
        public int getOrigen() {
            return origen;
        }

        /**
         * Obtiene el indice de destino de la ruta mejorada.
         *
         * @return indice de destino
         */
        public int getDestino() {
            return destino;
        }

        /**
         * Obtiene la distancia anterior a la mejora.
         *
         * @return distancia anterior
         */
        public int getDistanciaAnterior() {
            return distanciaAnterior;
        }

        /**
         * Obtiene la nueva distancia minima.
         *
         * @return distancia nueva
         */
        public int getDistanciaNueva() {
            return distanciaNueva;
        }
    }
}
