# Informe del proyecto de grafos

## 1. Introducción

Este proyecto resuelve un problema de rutas peatonales en el centro de Bogotá usando teoría de grafos y dos algoritmos de caminos mínimos:

- **Dijkstra**: calcula las rutas mínimas desde un lugar de origen hasta los demás lugares.
- **Floyd-Warshall**: calcula las rutas mínimas entre todos los pares de lugares.

El problema planteado es el desorden del espacio público causado por la ocupación indebida de los andenes. En el programa, cada arista tiene un peso que representa una estimación de congestión y desvío. Por lo tanto, minimizar el costo significa seleccionar un recorrido con menor dificultad peatonal.

## 2. Representación del problema

Los lugares se representan como vértices:

| Índice | Lugar |
|---|---|
| 0 | Plaza de Bolívar |
| 1 | Museo del Oro |
| 2 | Avenida Jiménez |
| 3 | San Victorino |

Las relaciones se representan como aristas dirigidas. Los pesos configurados son:

| Origen | Destino | Costo |
|---|---|---:|
| Plaza de Bolívar | Museo del Oro | 50 |
| Plaza de Bolívar | Avenida Jiménez | 80 |
| Museo del Oro | Avenida Jiménez | 20 |
| Museo del Oro | San Victorino | 90 |
| Avenida Jiménez | San Victorino | 40 |

La matriz inicial es:

```text
                  Plaza  Museo  Avenida  San
                  Bolívar Oro    Jiménez Victorino

Plaza de Bolívar     0     50      80       INF
Museo del Oro      INF      0      20        90
Avenida Jiménez    INF    INF       0        40
San Victorino      INF    INF     INF         0
```

`INF` se representa internamente mediante `Grafo.INFINITO = 1.000.000.000`. Indica que no existe una arista directa o una ruta alcanzable.

## 3. Explicación de las clases

### 3.1 `Grafo.java`

`Grafo` modela un grafo dirigido y ponderado mediante una matriz de costos.

Sus principales responsabilidades son:

- Recibir los nombres de los lugares.
- Crear una matriz cuadrada con `Grafo.INFINITO` como ausencia de arista.
- Asignar costo cero a las rutas de cada ciudad hacia sí misma.
- Permitir agregar o reemplazar aristas mediante `agregarArista`.
- Consultar vértices, nombres y costos.
- Validar índices y pesos negativos.

Fragmentos esenciales:

```java
this.costos = new int[ciudades.length][ciudades.length];
for (int i = 0; i < costos.length; i++) {
    Arrays.fill(costos[i], INFINITO);
    costos[i][i] = 0;
}
```

Esta inicialización representa las rutas posibles. Además:

```java
public void agregarArista(int origen, int destino, int costo)
```

establece el costo de una arista dirigida. La matriz no permite representar directamente una arista de costo negativo, y el método rechaza esos valores.

### 3.2 `Dijkstra.java`

`Dijkstra` calcula desde un único origen todas las distancias mínimas hacia los demás vértices.

El proceso es:

1. Inicializar todas las distancias en `INF`.
2. Inicializar la distancia del origen en cero.
3. Inicializar todos los predecesores en `-1`.
4. Insertar el origen en una cola de prioridad ordenada por distancia.
5. Extraer la ciudad con menor distancia conocida.
6. Revisar sus ciudades vecinas.
7. Si una nueva distancia es menor, actualizarla y su predecesor.
8. Repetir hasta que la cola quede vacía.

La cola de prioridad garantiza que siempre se procesa primero el vértice con menor costo acumulado:

```java
PriorityQueue<NodoDistancia> cola = new PriorityQueue<>(
    Comparator.comparingInt(nodo -> nodo.distancia)
);
```

El cálculo de una mejora se realiza con:

```java
int nuevaDistancia = distancias[ciudad] + costo;
if (nuevaDistancia < distancias[destino]) {
    distancias[destino] = nuevaDistancia;
    predecesores[destino] = ciudad;
    cola.add(new NodoDistancia(destino, nuevaDistancia));
}
```

Esto aplica la propiedad de relajación: la ruta a un destino mejora si se puede alcanzar el origen intermedio por una distancia menor y luego recorrer su arista.

También registra un `Estado` después de cada selección. Esa información permite presentar paso a paso las distancias y predecesores calculados.

Complejidad aproximada con matriz y cola de prioridad: `O(V²)` en esta implementación, donde `V` es el número de vértices.

### 3.3 `ResultadoDijkstra.java`

Esta clase encapsula el resultado de Dijkstra:

- `origen`: ciudad desde la cual comenzó el cálculo.
- `distancias`: costo mínimo a cada ciudad.
- `predecesores`: vértice previo usado para llegar a cada ciudad.
- `estados`: instantáneas de cada paso del algoritmo.

Las matrices y listas se copian para evitar que el estado interno pueda ser modificado externamente.

La ruta se reconstruye en sentido contrario:

```java
LinkedList<Integer> ruta = new LinkedList<>();
for (int actual = destino; actual != -1; actual = predecesores[actual]) {
    ruta.addFirst(actual);
    if (actual == origen) {
        return Collections.unmodifiableList(ruta);
    }
}
```

Comenzando en el destino, el método busca su predecesor, luego el predecesor de ese predecesor, y así hasta alcanzar el origen. `addFirst` invierte el recorrido para obtener el camino desde el inicio hasta el destino.

Si no existe un predecesor alcanzable hasta el origen, devuelve una lista vacía.

### 3.4 `FloydWarshall.java`

`FloydWarshall` encuentra las rutas mínimas entre todas las parejas de vértices.

Inicialmente copia la matriz del grafo:

```java
distancias[origen][destino] = grafo.getCosto(origen, destino);
```

También crea una matriz `siguiente`. Esta matriz indica cuál es el próximo vértice que debe visitarse para reconstruir una ruta.

Los tres ciclos permiten considerar cada vértice como posible intermediario:

```java
for (int intermedia = 0; intermedia < cantidad; intermedia++) {
    for (int origen = 0; origen < cantidad; origen++) {
        for (int destino = 0; destino < cantidad; destino++) {
            // comparar ruta directa con ruta pasando por la ciudad intermedia
        }
    }
}
```

La relajación se expresa mediante:

```java
int alternativa =
    distancias[origen][intermedia] +
    distancias[intermedia][destino];
```

Si la alternativa es menor que el camino existente:

- Se reemplaza la distancia.
- Se actualiza la matriz `siguiente`.
- Se registra la mejora en una actualización.

Complejidad del algoritmo: `O(V³)`, donde `V` es la cantidad de vértices.

### 3.5 `ResultadoFloydWarshall.java`

Esta clase conserva:

- La matriz final de distancias mínimas.
- La matriz `siguiente`, usada para reconstruir caminos.
- Todas las mejoras encontradas durante la ejecución.

La ruta se reconstruye así:

```java
int actual = origen;
ruta.add(actual);
while (actual != destino) {
    actual = siguiente[actual][destino];
    ruta.add(actual);
}
```

Cada paso consulta cuál debe ser la ciudad siguiente. Si aparece `-1`, significa que no existe una ruta completa.

La clase `Actualizacion` registra el vértice intermedio, origen, destino, costo anterior y costo nuevo de cada mejora.

### 3.6 `Main.java`

`Main` es el punto de entrada del programa y coordina todos los componentes.

Sus responsabilidades son:

1. Crear el grafo del caso de estudio.
2. Mostrar lugares e índices.
3. Mostrar la matriz inicial.
4. Mostrar las rutas directas.
5. Ejecutar Dijkstra desde la Plaza de Bolívar.
6. Mostrar las etapas y el predecesor de cada vértice.
7. Ejecutar Floyd-Warshall.
8. Mostrar las mejoras, la matriz final y las rutas.
9. Comparar las distancias obtenidas.

El grafo se construye en `crearGrafo`:

```java
Grafo grafo = new Grafo(
    "Plaza de Bolivar",
    "Museo del Oro",
    "Avenida Jimenez",
    "San Victorino"
);
```

Luego se agregan las cinco aristas dirigidas. El programa toma como origen el índice `0` y como destino el índice `3`.

Las constantes `PLAZA_DE_BOLIVAR` y `SAN_VICTORINO` hacen explícito el significado de los índices y reducen errores al llamar los algoritmos.

## 4. Ejecución de Dijkstra

El recorrido paso a paso desde la Plaza de Bolívar es:

### Paso 1: Plaza de Bolívar

| Lugar | Costo | Predecesor |
|---|---:|---|
| Plaza de Bolívar | 0 | - |
| Museo del Oro | 50 | Plaza de Bolívar |
| Avenida Jiménez | 80 | Plaza de Bolívar |
| San Victorino | INF | - |

### Paso 2: Museo del Oro

El nuevo camino hacia Avenida Jiménez cuesta:

```text
50 + 20 = 70
```

Como `70 < 80`, Avenida Jiménez mejora su costo y cambia su predecesor.

También se encuentra un camino temporal hacia San Victorino:

```text
50 + 90 = 140
```

### Paso 3: Avenida Jiménez

Desde Avenida Jiménez se obtiene:

```text
70 + 40 = 110
```

Como `110 < 140`, San Victorino cambia a una ruta menos costosa.

### Paso 4: San Victorino

San Victorino queda seleccionado con costo final `110`.

Ruta final:

```text
Plaza de Bolívar -> Museo del Oro -> Avenida Jiménez -> San Victorino
```

## 5. Ejecución de Floyd-Warshall

Las mejoras registradas por Floyd-Warshall son:

| Intermedio | Ruta | Mejora |
|---|---|---|
| Museo del Oro | Plaza de Bolívar → Avenida Jiménez | 80 → 70 |
| Museo del Oro | Plaza de Bolívar → San Victorino | INF → 140 |
| Avenida Jiménez | Plaza de Bolívar → San Victorino | 140 → 110 |
| Avenida Jiménez | Museo del Oro → San Victorino | 90 → 60 |

La matriz final de distancias mínimas es:

```text
                  Plaza  Museo  Avenida  San
                  Bolívar Oro    Jiménez Victorino

Plaza de Bolívar     0     50      70       110
Museo del Oro      INF      0      20        60
Avenida Jiménez    INF    INF       0        40
San Victorino      INF    INF     INF         0
```

La matriz muestra que el costo desde la Plaza de Bolívar hasta San Victorino es `110`, y que existen rutas mínimas para todos los demás pares alcanzables.

## 6. Solución al problema planteado

El programa transforma el caso social en un problema cuantificable:

- Los lugares problemáticos son vértices.
- Los recorridos posibles son aristas.
- La congestion y los desvíos son costos.
- El camino mínimo representa la opción con menor dificultad acumulada.

El proyecto no cambia físicamente el espacio público, pero proporciona una herramienta para:

- Identificar recorridos alternativos.
- Comparar el costo de desplazamiento entre lugares.
- Elegir rutas menos congestionadas.
- Servir como base para reubicar puestos informales o diseñar intervenciones urbanas.

En el ejemplo, dos rutas se comparan para llegar a San Victorino:

### Ruta directa

```text
Plaza de Bolívar -> Avenida Jiménez -> San Victorino
80 + 40 = 120
```

### Ruta calculada

```text
Plaza de Bolívar -> Museo del Oro -> Avenida Jiménez -> San Victorino
50 + 20 + 40 = 110
```

Aunque pasa por un lugar adicional, la segunda ruta tiene menor costo total.

## 7. Comparación de algoritmos

| Característica | Dijkstra | Floyd-Warshall |
|---|---|---|
| Objetivo | Un origen hacia todos los destinos | Todos los pares de lugares |
| Resultado principal | Distancias y predecesores | Matriz de distancias y matriz `siguiente` |
| Necesidad de cola | Sí | No |
| Complejidad | `O(V²)` en esta implementación | `O(V³)` |
| Mejor uso | Una ruta desde un punto específico | Análisis completo de todas las combinaciones |
| En el proyecto | Ruta desde Plaza de Bolívar | Ruta entre todos los lugares |

Ambos algoritmos producen el mismo costo para el recorrido solicitado:

```text
Dijkstra:      110
Floyd-Warshall: 110
```

La coincidencia sirve como comprobación cruzada de que el resultado es consistente.

## 8. Uso del programa

Para compilar:

```powershell
javac *.java
```

Para ejecutar:

```powershell
java Main
```

La salida muestra:

1. Los lugares e índices.
2. La matriz inicial.
3. Las rutas directas.
4. Las etapas de Dijkstra.
5. Las mejoras de Floyd-Warshall.
6. La matriz final.
7. Las rutas y costos finales.
8. La comparación entre ambos resultados.

## 9. Consideraciones y posibles mejoras

El proyecto es adecuado como demostración académica, pero puede ampliarse:

- Agregar rutas bidireccionales si corresponde al caso real.
- Sustituir costos estimados por datos medidos de congestión.
- Permitir que el usuario ingrese origen y destino.
- Leer lugares y pesos desde un archivo.
- Agregar una interfaz gráfica o un mapa.
- Usar coordenadas reales para calcular distancias.
- Incorporar restricciones como horarios o cierre de vías.
- Validar rutas negativas si se generaliza el modelo.
- Mostrar todas las rutas mínimas posibles, no solo una.

## 10. Conclusión

El programa representa correctamente el centro de Bogotá como un grafo dirigido y utiliza dos algoritmos independientes para encontrar recorridos de menor costo.

Para la configuración incluida, el mejor recorrido entre la Plaza de Bolívar y San Victorino es:

```text
Plaza de Bolívar -> Museo del Oro -> Avenida Jiménez -> San Victorino
```

Su costo total de congestión y desvío es `110`. Dijkstra obtiene esta ruta desde un origen específico, mientras que Floyd-Warshall confirma el mismo costo al calcular todas las rutas posibles del grafo.
