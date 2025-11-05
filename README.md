PSO-Tutorial
=============

Resumen
------
Proyecto de ejemplo que implementa un PSO (Particle Swarm Optimization) muy simple en Java.

Contenido del repositorio (directorio `src/`)
- `Main.java` — Programa principal que crea el problema y ejecuta el PSO.
- `Particle.java` — Representa una partícula (posición x,y y velocidades vx,vy). Implementado como `record` (Java 16+).
- `Problem.java` — Interfaz que define `double evaluate(Particle p)` para evaluar una partícula.
- `SimpleFunctionProblem.java` — Implementación concreta de `Problem` que define la función objetivo.
- `SimplePSO.java` — Implementación básica del algoritmo PSO.
- `Resultados_ejecucion_PSO.xlsx` — hoja con resultados (no usada por el código).

Requisitos
---------
- Java 16 o superior (el proyecto usa `record` que requiere Java 16+).
- Compilador `javac` y `java` en PATH.

Cómo compilar y ejecutar (Windows, cmd.exe)
----------------------------------------
En la raíz del proyecto (donde está `src`):

```
javac -d out src\*.java
java -cp out Main
```

Descripción básica del código
-----------------------------
- `Particle` es un objeto inmutable que guarda posición (x,y) y velocidad (vx,vy).
- `Problem.evaluate(Particle)` devuelve un valor numérico para la partícula. El PSO en `SimplePSO` está escrito tratándolo como un problema de minimización.
- `SimpleFunctionProblem.evaluate` implementa la función:
  f(x,y) = (x-3.14)^2 + (y-2.72)^2 + sin(3x + 1.41) + sin(4y - 1.73)
  Nota: en el código el comentario dice "Función a maximizar" pero el PSO la trata como minimización — es una inconsistencia en el comentario. La implementación actual hace minimización.

Cómo funciona el PSO en este código
-----------------------------------
Flujo general (en `SimplePSO.run`):
1. Inicializa `numParticles` partículas con posiciones aleatorias en [lowerBound, upperBound] (aquí [0,5]) y velocidades aleatorias en [0,1].
2. Inicializa `localBest[i]` como copia de cada partícula y determina `globalBest` con el menor valor de `evaluate` (minimización).
3. Repite `numIterations` veces:
   - Para cada partícula calcula la nueva velocidad usando la fórmula clásica velo = w*vel + c1*r1*(pbest - pos) + c2*r2*(gbest - pos).
   - Actualiza la posición sumando la velocidad.
   - Si la nueva posición tiene mejor valor que el `localBest`, actualiza `localBest`.
   - Si la nueva posición tiene mejor valor que `globalBest`, actualiza `globalBest`.
4. Devuelve `globalBest`.

Puntos a tener en cuenta / observaciones
---------------------------------------
1. Comentarios inconsistentes: en `SimpleFunctionProblem` el comentario indica "Función a maximizar" pero el algoritmo en `SimplePSO` la trata como minimización. Decida si quiere maximizar o minimizar y ajuste el comentario o la lógica (p. ej. multiplicando por -1 si desea maximizar).

2. Inicialización de velocidades: las velocidades iniciales se generan con `Math.random()` que da valores en [0,1]. Normalmente se inicializan en un rango simétrico (por ejemplo [-vmax,vmax]) para permitir movimientos en ambas direcciones desde el inicio.

3. Sin control de límites: no hay límites de posición ni tope de velocidad (vmax). Las partículas pueden salir del dominio esperado. Se recomienda:
   - Clampear la posición al rango [lowerBound, upperBound] o aplicar límites razonables.
   - Limitar la velocidad a `[-vmax, vmax]`.

4. Evaluaciones redundantes: el código llama repetidamente `problem.evaluate(particles[i])` y `problem.evaluate(localBest[i])`. Es mejor cachear el valor de la evaluación para evitar computación repetida.

5. Reproducibilidad: se usa `Math.random()` sin semilla. Si desea reproducir resultados use `java.util.Random` con semilla controlada.

6. Tipos y versión de Java: `record Particle` requiere Java 16+. Indíque en la documentación la versión mínima.

7. Nombrado / claridad: `creaEnjambreAleatorio` inicializa posiciones en [lowerBound, upperBound] pero las velocidades en [0,1]. Podría aceptar un parámetro `vmax` o un rango para velocidades.

Mejoras sugeridas (rápidas)
---------------------------
- Añadir control de límites y `vmax`.
- Inicializar velocidades en rango simétrico e introducir semilla reproducible.
- Reducir llamadas a `evaluate` guardando el valor actual de cada partícula.
- Añadir logging opcional o guardar resultados a CSV/Excel.
- Añadir tests unitarios sencillos para la función objetivo y la actualización de velocidad/posición.

Ejemplo de salida esperada
-------------------------
El `Main` ya imprime las iteraciones y la mejor solución encontrada. Por ejemplo:

Iteración;Mejor solución;Valor
1;(x, y);value
...

Al final imprime la mejor solución encontrada y el valor de la función en dicha solución.

Cobertura de requisitos
-----------------------
- Analizar el código: He revisado los ficheros fuente y descrito el funcionamiento y puntos débiles. — Hecho
- Señalar si hay algo mal: Comentarios inconsistentes y recomendaciones prácticas — Hecho
- Explicar PSO básicamente: Incluido en la sección "Cómo funciona el PSO" — Hecho
- Documentar el código en un README: He creado este `README.md`. — Hecho

Siguientes pasos (opcionales)
----------------------------
Si quieres, puedo:
- Aplicar las mejoras sugeridas directamente en el código (ej: límites de posición, `vmax`, semilla reproducible).
- Añadir tests unitarios y un script de ejecución.

Si quieres que implemente alguna de las mejoras, dime cuáles y lo hago.

Pseudocódigo del PSO (esquema general y correspondencia con esta implementación)
-------------------------------------------------------------------------------

A continuación hay un pseudocódigo en castellano que resume el algoritmo PSO y apunta a dónde se implementa cada paso en este proyecto.

Pseudocódigo (alto nivel):

1. Inicialización
   - Crear N partículas con posiciones (x,y) y velocidades (vx,vy) aleatorias en el dominio.
   - Evaluar cada partícula y guardar su mejor posición local (pbest).
   - Determinar la mejor posición global (gbest) entre todas las partículas.

   // En el código: `SimplePSO.creaEnjambreAleatorio(...)` crea las partículas.
   // `localBest[]` y `localBestValue[]` almacenan pbest; la variable `globalBest` y `globalBestValue` almacenan gbest.

2. Bucle principal (iteraciones)
   Para iter = 1 .. maxIteraciones:
     Para cada partícula i:
       - Generar r1, r2 ~ Uniform(0,1)
       - vx = w * vx + c1 * r1 * (pbest.x - x) + c2 * r2 * (gbest.x - x)
       - vy = w * vy + c1 * r1 * (pbest.y - y) + c2 * r2 * (gbest.y - y)
       - Limitar velocidad a [-vmax, vmax] (opcional)
       - x = x + vx
       - y = y + vy
       - Clampear posición al dominio [xmin,xmax]x[ymin,ymax] (opcional)
       - value = evaluate(x,y)
       - Si value < pbest_value: actualizar pbest y pbest_value
       - Si value < gbest_value: actualizar gbest y gbest_value
     Fin para
     (Opcional) Notificar observadores / actualizar GUI con las posiciones actuales
   Fin para

   // En el código: el bucle principal está en `SimplePSO.run(PSOListener)`.
   // Las constantes w, c1, c2 se pasan al constructor de `SimplePSO`.
   // Se aplican límites de velocidad y de posición en la actualización.
   // `PSOListener` permite una visualización paso a paso (usado en `Main.java`).

Notas de correspondencia y decisiones de implementación
- Minimización vs Maximización: El proyecto trata la función como problema de minimización (menor valor es mejor). Si quieres maximizar, invierte el signo en la evaluación.
- Evaluaciones: para evitar evaluaciones redundantes, se guarda el valor de cada partícula en `localBestValue[]` y se calcula `value` una sola vez tras mover la partícula.
- Reproducibilidad: el generador aleatorio usa `java.util.Random` sin semilla por defecto; puedes añadir una semilla en `SimplePSO` si necesitas reproducibilidad.
- Visualización: `FunctionPlotter` crea un mapa de calor de la función y soporta overlays de partículas (mediante `updateParticles(...)` y `refresh()`). `Main.java` muestra cómo activar la visualización usando `PSOListener`.

Consejos para extender o adaptar el algoritmo
- Añadir un parámetro `vmax` configurable en `SimplePSO` para ajustarlo sin depender del tamaño del dominio.
- Cambiar la inicialización de velocidades o la estrategia de inercia (p. ej. w decreciente).
- Implementar topologías de vecindad (PSO local) guardando vecinos en vez del único `globalBest`.
- Guardar trayectorias o resultados en CSV para análisis posterior.
