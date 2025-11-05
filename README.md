# PSO Tutorial - Optimización por Enjambre de Partículas

## 📋 Objetivos

Este proyecto tiene como objetivos principales:

1. **Aprender PSO (Particle Swarm Optimization)**: Entender los fundamentos de este algoritmo de optimización bioinspirado.
2. **Implementación práctica**: Proporcionar un código Java limpio y modular que sea fácil de entender y modificar.
3. **Experimentación**: Permitir probar el algoritmo con múltiples funciones de prueba (benchmarks) para observar su comportamiento.
4. **Visualización**: Ofrecer herramientas gráficas para visualizar en tiempo real cómo las partículas exploran el espacio de búsqueda.
5. **Uso educativo**: Servir como material didáctico para clases de inteligencia artificial, optimización y metaheurísticas.

---

## 🧠 Introducción a PSO (Particle Swarm Optimization)

### ¿Qué es PSO?

PSO es un algoritmo de optimización metaheurístico inspirado en el comportamiento social de bandadas de aves o cardúmenes de peces. Fue desarrollado por Kennedy y Eberhart en 1995.

### Conceptos clave

- **Partícula**: Representa una solución candidata en el espacio de búsqueda. Cada partícula tiene:
  - **Posición** (x, y): Punto actual en el espacio de búsqueda
  - **Velocidad** (vx, vy): Dirección y magnitud del movimiento
  
- **Enjambre (Swarm)**: Conjunto de partículas que colaboran para encontrar el óptimo.

- **Memoria local (pbest)**: Cada partícula recuerda la mejor posición que ha visitado.

- **Memoria global (gbest)**: El enjambre mantiene la mejor posición encontrada por cualquier partícula.

### ¿Cómo funciona?

Las partículas se mueven por el espacio de búsqueda influenciadas por:
1. Su propia inercia (siguen moviéndose en su dirección actual)
2. Su mejor experiencia personal (atracción hacia pbest)
3. La mejor experiencia del enjambre (atracción hacia gbest)

Esta combinación permite **exploración** (buscar nuevas regiones) y **explotación** (refinar soluciones prometedoras).

### Parámetros del algoritmo

- **w (inercia)**: Controla cuánto influye la velocidad anterior. Valores típicos: 0.4-0.9
- **c1 (coeficiente cognitivo)**: Atracción hacia la mejor posición personal. Típico: 1.4-2.0
- **c2 (coeficiente social)**: Atracción hacia la mejor posición global. Típico: 1.4-2.0
- **vmax**: Velocidad máxima permitida (evita movimientos excesivos)
- **Número de partículas**: Entre 10-50 suele ser suficiente
- **Iteraciones**: Depende del problema (50-500 iteraciones típicamente)

---

## 📐 Algoritmo PSO (Pseudocódigo)

```
Algoritmo: Particle Swarm Optimization (PSO)

ENTRADA:
  - numPartículas: cantidad de partículas en el enjambre
  - numIteraciones: número de iteraciones máximas
  - w: coeficiente de inercia
  - c1: coeficiente cognitivo (componente local)
  - c2: coeficiente social (componente global)
  - vmax: velocidad máxima permitida
  - dominio: [xMin, xMax] × [yMin, yMax]
  - función_objetivo: función a optimizar

SALIDA:
  - gbest: mejor solución encontrada

INICIALIZACIÓN:
  Para cada partícula i = 1 hasta numPartículas:
    posición[i] ← aleatorio en [xMin, xMax] × [yMin, yMax]
    velocidad[i] ← aleatorio en [-vmax, vmax] × [-vmax, vmax]
    pbest[i] ← posición[i]
    pbest_valor[i] ← evaluar(posición[i])
  
  gbest ← mejor de todas las pbest
  gbest_valor ← pbest_valor[argmin(pbest_valor)]

BUCLE PRINCIPAL:
  Para iter = 1 hasta numIteraciones:
    Para cada partícula i = 1 hasta numPartículas:
      // Actualizar velocidad
      r1 ← aleatorio(0, 1)
      r2 ← aleatorio(0, 1)
      
      velocidad[i].x ← w × velocidad[i].x 
                       + c1 × r1 × (pbest[i].x - posición[i].x)
                       + c2 × r2 × (gbest.x - posición[i].x)
      
      velocidad[i].y ← w × velocidad[i].y 
                       + c1 × r1 × (pbest[i].y - posición[i].y)
                       + c2 × r2 × (gbest.y - posición[i].y)
      
      // Limitar velocidad
      velocidad[i] ← clamp(velocidad[i], -vmax, vmax)
      
      // Actualizar posición
      posición[i] ← posición[i] + velocidad[i]
      
      // Mantener dentro del dominio
      posición[i] ← clamp(posición[i], [xMin,yMin], [xMax,yMax])
      
      // Evaluar nueva posición
      valor ← evaluar(posición[i])
      
      // Actualizar mejor personal
      Si valor < pbest_valor[i]:
        pbest[i] ← posición[i]
        pbest_valor[i] ← valor
      
      // Actualizar mejor global
      Si valor < gbest_valor:
        gbest ← posición[i]
        gbest_valor ← valor
    
    Imprimir(iter, gbest, gbest_valor)
  
  Devolver gbest
```

---

## 🗂️ Estructura del Código

El proyecto está organizado en paquetes modulares:

```
PSO-Tutorial/
├── README.md
├── src/
│   ├── Main.java                    # Programa principal (punto de entrada)
│   ├── core/                        # Núcleo del algoritmo PSO
│   │   ├── Particle.java            # Record que representa una partícula
│   │   ├── Problem.java             # Interfaz para funciones objetivo
│   │   └── SimplePSO.java           # Implementación del algoritmo PSO
│   ├── functions/                   # Funciones de prueba (benchmarks)
│   │   ├── Function1.java           # Paraboloide perturbado
│   │   ├── Function2.java           # Sphere
│   │   ├── Function3.java           # Rosenbrock
│   │   ├── Function4.java           # Rastrigin
│   │   ├── Function5.java           # Ackley
│   │   ├── Function6.java           # Himmelblau
│   │   ├── Function7.java           # Beale
│   │   ├── Function8.java           # Booth
│   │   ├── Function9.java           # Bukin N.6
│   │   └── Function10.java          # Six-hump Camelback
│   └── ui/                          # Utilidades de visualización
│       ├── FunctionPlotter.java     # Renderiza mapas de calor y partículas
│       └── PlotDemo.java            # Demo para generar imágenes PNG
└── out/                             # Clases compiladas (generado)
```

### Descripción de componentes clave

#### 📦 Paquete `core`

**`Particle.java`** - Representa una partícula:
```java
public record Particle(double x, double y, double vx, double vy)
```
- `x, y`: Posición en el espacio 2D
- `vx, vy`: Componentes de velocidad

**`Problem.java`** - Interfaz para funciones objetivo:
```java
public interface Problem {
    double evaluate(Particle p);  // Evalúa la función
    double xMin();                // Límite inferior en X
    double xMax();                // Límite superior en X
    double yMin();                // Límite inferior en Y
    double yMax();                // Límite superior en Y
}
```

**`SimplePSO.java`** - Motor del algoritmo:
- Inicializa el enjambre aleatoriamente
- Ejecuta el bucle principal del PSO
- Actualiza velocidades y posiciones
- Gestiona pbest y gbest
- Soporta visualización mediante `PSOListener`

#### 🎯 Paquete `functions`

Cada función implementa `Problem` y define su dominio óptimo. Incluye:

| Función | Tipo | Características | Dominio | Fórmula |
|---------|------|----------------|---------|---------|
| Function1 | Sintética | Paraboloide con perturbaciones sinusoidales | [0,5]² | $f(x,y) = (x-3.14)^2 + (y-2.72)^2 + \sin(3x+1.41) + \sin(4y-1.73)$ |
| Function2 | Sphere | Convexa, 1 mínimo global | [-5.12,5.12]² | $f(x,y) = x^2 + y^2$ |
| Function3 | Rosenbrock | Valle estrecho, difícil convergencia | [-2.048,2.048]² | $f(x,y) = (1-x)^2 + 100(y-x^2)^2$ |
| Function4 | Rastrigin | Altamente multimodal (muchos mínimos locales) | [-5.12,5.12]² | $f(x,y) = 20 + x^2 + y^2 - 10[\cos(2\pi x) + \cos(2\pi y)]$ |
| Function5 | Ackley | Multimodal, casi plana lejos del óptimo | [-5,5]² | $f(x,y) = -20e^{-0.2\sqrt{0.5(x^2+y^2)}} - e^{0.5[\cos(2\pi x)+\cos(2\pi y)]} + e + 20$ |
| Function6 | Himmelblau | 4 mínimos globales idénticos | [-5,5]² | $f(x,y) = (x^2+y-11)^2 + (x+y^2-7)^2$ |
| Function7 | Beale | Valle, mínimo en (3, 0.5) | [-4.5,4.5]² | $f(x,y) = (1.5-x+xy)^2 + (2.25-x+xy^2)^2 + (2.625-x+xy^3)^2$ |
| Function8 | Booth | Convexa, mínimo en (1, 3) | [-10,10]² | $f(x,y) = (x+2y-7)^2 + (2x+y-5)^2$ |
| Function9 | Bukin N.6 | Valle muy estrecho, difícil | x∈[-15,-5], y∈[-3,3] | $f(x,y) = 100\sqrt{\|y-0.01x^2\|} + 0.01\|x+10\|$ |
| Function10 | Six-hump Camelback | 6 mínimos locales, 2 globales | x∈[-3,3], y∈[-2,2] | $f(x,y) = \left(4-2.1x^2+\frac{x^4}{3}\right)x^2 + xy + (-4+4y^2)y^2$ |

#### 🎨 Paquete `ui`

**`FunctionPlotter.java`** - Visualizador avanzado:
- Genera mapas de calor de la función objetivo
- Dibuja ejes cartesianos y rejilla
- Marca el mínimo encontrado
- Superpone las partículas del enjambre en tiempo real
- Exporta imágenes PNG

**`PlotDemo.java`** - Ejemplo para generar visualizaciones estáticas.

---

## 🚀 Cómo Ejecutar

### Requisitos previos

- **Java 16 o superior** (el proyecto usa `record`)
- `javac` y `java` en el PATH del sistema

### Compilación

Abre una terminal (cmd en Windows) en la raíz del proyecto y ejecuta:

```cmd
javac -d out src\*.java src\core\*.java src\functions\*.java src\ui\*.java
```

Esto compilará todos los archivos `.java` y colocará las clases en el directorio `out/`.

### Ejecución del programa principal

```cmd
java -cp out Main
```

Esto ejecutará el PSO con visualización en tiempo real (si `visualize = true` en `Main.java`).

### Generar una imagen PNG de una función

```cmd
java -cp out ui.PlotDemo
```

Esto generará un archivo `function_plot.png` con el mapa de calor de la función.

### Cambiar la función a optimizar

Edita `Main.java` y cambia la línea:
```java
var problem = new Function4();  // Cambia por Function1, Function2, etc.
```

### Ajustar parámetros del PSO

En `Main.java`, modifica los parámetros del constructor:
```java
SimplePSO pso = new SimplePSO(
    20,      // número de partículas
    100,     // iteraciones
    0.7,     // w (inercia)
    1.4,     // c1 (cognitivo)
    1.4,     // c2 (social)
    problem  // función objetivo
);
```

Para especificar un `vmax` personalizado:
```java
SimplePSO pso = new SimplePSO(20, 100, 0.7, 1.4, 1.4, 2.5, problem);
//                                                      ↑ vmax
```

---

## 🎬 Ejemplos y Visualizaciones

### Ejemplo 1: Ejecutar PSO con visualización

**Código** (`Main.java` con `visualize = true`):
```java
boolean visualize = true;
var problem = new Function4();  // Rastrigin (multimodal)
SimplePSO pso = new SimplePSO(20, 100, 0.7, 1.4, 1.4, problem);
```

**Resultado esperado**:
- Se abre una ventana con el mapa de calor de Rastrigin
- Puntos negros = partículas del enjambre
- Punto magenta = mejor solución global (gbest)
- Marca roja = mínimo real de la función
- Las partículas se mueven en cada iteración
- En la consola se imprime: `Iter X: best=(x,y) -> valor`

**Salida de consola**:
```
Iteración;Mejor solución;Valor
1;(-2.341, 1.892);15.2341
2;(-1.932, 1.234);8.5632
...
100;(0.0012, -0.0034);0.0023

Best solution: (0.0012, -0.0034) -> 0.0023
```

### Ejemplo 2: Comparar funciones fáciles vs difíciles

**Función fácil** (Sphere - Function2):
```java
var problem = new Function2();  // Convexa, convergencia rápida
SimplePSO pso = new SimplePSO(10, 50, 0.5, 1.5, 1.5, problem);
```
→ PSO encuentra el óptimo (0,0) en pocas iteraciones.

**Función difícil** (Rastrigin - Function4):
```java
var problem = new Function4();  // Multimodal, muchos mínimos locales
SimplePSO pso = new SimplePSO(30, 200, 0.7, 1.4, 1.4, problem);
```
→ Necesita más partículas e iteraciones. Puede quedar atrapado en mínimos locales.

### Ejemplo 3: Efecto de los parámetros

**Alta inercia (w = 0.9)**: Mayor exploración, convergencia más lenta
```java
SimplePSO pso = new SimplePSO(20, 100, 0.9, 1.4, 1.4, problem);
```

**Baja inercia (w = 0.4)**: Mayor explotación, convergencia rápida (riesgo de mínimos locales)
```java
SimplePSO pso = new SimplePSO(20, 100, 0.4, 1.4, 1.4, problem);
```

**Alto componente social (c2 = 2.0)**: Las partículas convergen rápidamente hacia gbest
```java
SimplePSO pso = new SimplePSO(20, 100, 0.7, 1.4, 2.0, problem);
```

### Ejemplo 4: Generar imagen PNG sin GUI

Ejecuta `PlotDemo` para obtener una visualización estática:
```cmd
java -cp out ui.PlotDemo
```

Modifica `PlotDemo.java` para cambiar la función:
```java
var problem = new Function6();  // Himmelblau (4 mínimos)
```

Resultado: archivo `function_plot.png` con:
- Mapa de calor (azul=bajo, rojo=alto)
- Ejes con etiquetas
- Rejilla
- Marca del mínimo encontrado

### Ejemplo 5: Desactivar visualización (modo batch)

En `Main.java`:
```java
boolean visualize = false;
```

Útil para:
- Ejecutar múltiples pruebas automáticas
- Medir tiempos de ejecución sin overhead gráfico
- Integrar con scripts de experimentación

---

## 🔬 Actividades Sugeridas para Clase

1. **Exploración de parámetros**: Variar w, c1, c2 y observar el efecto en la convergencia.

2. **Comparación de funciones**: Ejecutar PSO en Function2 (fácil) vs Function4 (difícil).

3. **Análisis de convergencia**: Graficar la evolución de gbest_valor vs iteraciones.

4. **Efecto del tamaño del enjambre**: Probar con 5, 10, 20, 50 partículas.

5. **Límite de velocidad**: Comparar comportamiento con y sin vmax.

6. **Modificación del algoritmo**: Implementar PSO con topología de vecindario (ring, star).

7. **Nuevas funciones**: Añadir ``FunctionXX`` con tu propia función de prueba.

---

## 📚 Referencias y Recursos

- Kennedy, J., & Eberhart, R. (1995). Particle swarm optimization.
- Shi, Y., & Eberhart, R. (1998). A modified particle swarm optimizer.
- [Virtual Library of Simulation Experiments - Test Functions](https://www.sfu.ca/~ssurjano/optimization.html)

---

## 📝 Notas Técnicas

- **Reproducibilidad**: El generador aleatorio no usa semilla fija. Para reproducir resultados, modifica `SimplePSO` para aceptar una semilla.
- **Dominio automático**: Cada `Problem` define su propio dominio. No es necesario hardcodear límites.
- **Visualización en tiempo real**: La actualización usa `SwingUtilities.invokeLater` para evitar bloquear el EDT.
- **Pausa entre iteraciones**: Hay un `Thread.sleep(100)` en modo visualización para que la animación sea visible.

---

## 🛠️ Extensiones Posibles

- Añadir PSO con inercia decreciente linealmente
- Implementar PSO con constricción (clerc)
- Agregar topologías de vecindario (lbest)
- Exportar trayectorias a CSV para análisis
- Añadir más dimensiones (3D, N-D)
- Implementar variantes (APSO, BBPSO, etc.)

---

