import java.util.Arrays;

/**
 * Representa un grafo dirigido y ponderado mediante una matriz de costos.
 * En este caso, los vertices representan lugares del centro de Bogota.
 */
public final class Grafo {
    /** Valor centinela que representa la ausencia de una arista. */
    public static final int INFINITO = 1_000_000_000;

    /** Nombres de las ciudades, ordenados por su indice en el grafo. */
    private final String[] ciudades;

    /** Costos de las aristas; las filas y columnas se mantienen encapsuladas. */
    private final int[][] costos;

    /**
     * Crea un grafo sin aristas y con los nombres de las ciudades indicados.
     *
     * @param ciudades nombres de los vertices, en el orden de sus indices
     * @throws IllegalArgumentException si no se proporcionan ciudades
     */
    public Grafo(String... ciudades) {
        if (ciudades == null || ciudades.length == 0) {
            throw new IllegalArgumentException("El grafo debe tener al menos una ciudad.");
        }

        this.ciudades = ciudades.clone();
        this.costos = new int[ciudades.length][ciudades.length];
        for (int i = 0; i < costos.length; i++) {
            Arrays.fill(costos[i], INFINITO);
            costos[i][i] = 0;
        }
    }

    /**
     * Agrega o reemplaza una arista dirigida entre dos ciudades.
     *
     * @param origen indice de la ciudad de salida
     * @param destino indice de la ciudad de llegada
     * @param costo peso no negativo de la arista
     * @throws IndexOutOfBoundsException si alguno de los indices no pertenece al grafo
     * @throws IllegalArgumentException si el costo es negativo
     */
    public void agregarArista(int origen, int destino, int costo) {
        validarIndice(origen);
        validarIndice(destino);
        if (costo < 0) {
            throw new IllegalArgumentException("El costo de una ruta no puede ser negativo.");
        }
        costos[origen][destino] = costo;
    }

    /**
     * Obtiene la cantidad de vertices del grafo.
     *
     * @return cantidad de ciudades
     */
    public int getCantidadCiudades() {
        return ciudades.length;
    }

    /**
     * Obtiene el nombre de una ciudad.
     *
     * @param indice indice de la ciudad
     * @return nombre de la ciudad indicada
     * @throws IndexOutOfBoundsException si el indice no pertenece al grafo
     */
    public String getNombreCiudad(int indice) {
        validarIndice(indice);
        return ciudades[indice];
    }

    /**
     * Obtiene el costo de una arista o {@link #INFINITO} si no existe.
     *
     * @param origen indice de la ciudad de salida
     * @param destino indice de la ciudad de llegada
     * @return costo de la arista o el valor centinela
     * @throws IndexOutOfBoundsException si alguno de los indices no pertenece al grafo
     */
    public int getCosto(int origen, int destino) {
        validarIndice(origen);
        validarIndice(destino);
        return costos[origen][destino];
    }

    /**
     * Indica si existe una arista directa entre dos ciudades distintas.
     *
     * @param origen indice de la ciudad de salida
     * @param destino indice de la ciudad de llegada
     * @return {@code true} si hay una arista dirigida entre las ciudades
     * @throws IndexOutOfBoundsException si alguno de los indices no pertenece al grafo
     */
    public boolean existeArista(int origen, int destino) {
        validarIndice(origen);
        validarIndice(destino);
        return origen != destino && getCosto(origen, destino) != INFINITO;
    }

    /**
     * Verifica que un indice corresponda a una ciudad del grafo.
     *
     * @param indice indice que se valida
     * @throws IndexOutOfBoundsException si el indice es invalido
     */
    private void validarIndice(int indice) {
        if (indice < 0 || indice >= ciudades.length) {
            throw new IndexOutOfBoundsException("Indice de ciudad fuera del grafo: " + indice);
        }
    }
}
