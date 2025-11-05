package core;

import java.util.Random;

public class SimplePSO {
    private final int numParticles;
    private final int numIterations;
    private final double w;
    private final double c1;
    private final double c2;
    private final Problem problem;
    private final Random rand = new Random(); // para reproducibilidad opcional se puede pasar semilla
    // Límite máximo absoluto de la velocidad (vmax). Si es NaN, se deriva del dominio en tiempo de ejecución.
    private final double vmaxLimit;

    // Constructor del algoritmo (mantener compatibilidad: sin parámetro vmax)
    public SimplePSO(int numParticles, int numIterations, double w, double c1, double c2, Problem problem) {
        this.numParticles = numParticles;
        this.numIterations = numIterations;
        this.w = w;
        this.c1 = c1;
        this.c2 = c2;
        this.problem = problem;
        this.vmaxLimit = Double.NaN; // indica comportamiento por defecto (derivado del dominio)
    }

    /**
     * Nuevo constructor que permite definir un `vmax` explicito (límite de velocidad).
     *
     * @param vmax máximo de velocidad absoluta que se aplicará a vx y vy (>=0)
     */
    public SimplePSO(int numParticles, int numIterations, double w, double c1, double c2, double vmax, Problem problem) {
        this.numParticles = numParticles;
        this.numIterations = numIterations;
        this.w = w;
        this.c1 = c1;
        this.c2 = c2;
        this.problem = problem;
        this.vmaxLimit = Math.max(0.0, vmax);
    }

    private Particle[] creaEnjambreAleatorio(double xMin, double xMax, double yMin, double yMax) {
        // Inicializamos las partículas (posición y velocidad) usando umbrales
        Particle[] particles = new Particle[numParticles];
        double xrange = xMax - xMin;
        double yrange = yMax - yMin;
        // Si el usuario definió un vmaxLimit en el constructor, úsalo; si no, por defecto usamos el mayor rango
        double vmax = Double.isNaN(this.vmaxLimit) ? Math.max(xrange, yrange) : this.vmaxLimit;
        for (int i = 0; i < numParticles; i++) {
            double x = xMin + rand.nextDouble() * xrange;
            double y = yMin + rand.nextDouble() * yrange;
            double vx = (rand.nextDouble() * 2.0 - 1.0) * vmax; // en [-vmax, vmax]
            double vy = (rand.nextDouble() * 2.0 - 1.0) * vmax;
            particles[i] = new Particle(x, y, vx, vy);
        }
        return particles;
    }

    // New listener interface for visualization / callbacks
    public interface PSOListener {
        /**
         * Called after each iteration with current particles and current global best.
         * Implementors should not block the caller for long; use SwingUtilities.invokeLater
         * if updating GUI components.
         */
        void onIteration(int iteration, Particle[] particles, Particle globalBest, double globalBestValue);
    }

    public Particle run(PSOListener listener) {

        // INICIALIZACIÓN -----------------------------------------------------

        // Usar el dominio provisto por el problema
        double xMin = problem.xMin();
        double xMax = problem.xMax();
        double yMin = problem.yMin();
        double yMax = problem.yMax();
        double xrange = xMax - xMin;
        double yrange = yMax - yMin;
        // Determinar el tope de velocidad efectivo: si el usuario pasó un vmaxLimit lo usamos, si no lo derivamos del domain range
        double vmax = Double.isNaN(this.vmaxLimit) ? Math.max(xrange, yrange) : this.vmaxLimit; // tope de velocidad

        // Inicializamos las partículas (posicion y velocidad)
        Particle[] particles = creaEnjambreAleatorio(xMin, xMax, yMin, yMax);

        // Mejores partículas locales (y sus valores)
        Particle[] localBest = new Particle[numParticles];
        double[] localBestValue = new double[numParticles];

        // Inicializamos globalBest mediante un único bucle para evitar NPE y ramas especiales
        if (numParticles == 0) {
            return null; // caso extremo: no hay partículas
        }

        // Inicializamos tomando la primera partícula para evitar que el analizador piense
        // que `globalBest` puede ser null cuando se usa más adelante.
        localBest[0] = new Particle(particles[0].x(), particles[0].y(), particles[0].vx(), particles[0].vy());
        localBestValue[0] = problem.evaluate(particles[0]);
        Particle globalBest = new Particle(particles[0].x(), particles[0].y(), particles[0].vx(), particles[0].vy());
        double globalBestValue = localBestValue[0];

        for (int i = 1; i < numParticles; i++) {
            localBest[i] = new Particle(particles[i].x(), particles[i].y(), particles[i].vx(), particles[i].vy());
            localBestValue[i] = problem.evaluate(particles[i]);
            if (localBestValue[i] < globalBestValue) {
                globalBestValue = localBestValue[i];
                globalBest = new Particle(particles[i].x(), particles[i].y(), particles[i].vx(), particles[i].vy());
            }
        }

        // BUCLE PRINCIPAL -----------------------------------------------------

        System.out.println("Iteración;Mejor solución;Valor");
        for (int iters = 1; iters <= numIterations; iters++) {
            // Iteramos sobre las partículas
            for (int i = 0; i < numParticles; i++) {
                // Calculamos nueva velocidad (usar rand en lugar de Math.random)
                double r1 = rand.nextDouble();
                double r2 = rand.nextDouble();
                double vx = w * particles[i].vx() + c1 * r1 * (localBest[i].x() - particles[i].x()) + c2 * r2 * (globalBest.x() - particles[i].x());
                double vy = w * particles[i].vy() + c1 * r1 * (localBest[i].y() - particles[i].y()) + c2 * r2 * (globalBest.y() - particles[i].y());

                // Limitar velocidad
                vx = Math.max(-vmax, Math.min(vmax, vx));
                vy = Math.max(-vmax, Math.min(vmax, vy));

                // Actualizamos la posición
                double x = particles[i].x() + vx;
                double y = particles[i].y() + vy;

                // Clampear la posición al dominio (por ejes)
                x = Math.max(xMin, Math.min(xMax, x));
                y = Math.max(yMin, Math.min(yMax, y));

                // Actualizamos valores de la partícula
                Particle newParticle = new Particle(x, y, vx, vy);
                particles[i] = newParticle;

                // Evaluar una sola vez y reusar el valor
                double value = problem.evaluate(newParticle);

                // Actualizamos la mejor partícula local
                if (value < localBestValue[i]) {
                    localBest[i] = new Particle(x, y, vx, vy);
                    localBestValue[i] = value;
                }

                // Actualizamos la mejor partícula global
                if (value < globalBestValue) {
                    globalBestValue = value;
                    globalBest = new Particle(x, y, vx, vy);
                }
            }
            System.out.println(iters+";(" + globalBest.x() + ", " + globalBest.y() + ");" + globalBestValue);

            // Notificar listener con una copia de las partículas para visualización
            if (listener != null) {
                Particle[] copy = new Particle[numParticles];
                for (int k = 0; k < numParticles; k++) {
                    copy[k] = new Particle(particles[k].x(), particles[k].y(), particles[k].vx(), particles[k].vy());
                }
                listener.onIteration(iters, copy, new Particle(globalBest.x(), globalBest.y(), globalBest.vx(), globalBest.vy()), globalBestValue);
                // Small pause so visualization can update (non-blocking for GUI if listener uses invokeLater)
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }

        return globalBest;
    }

    // Compatibilidad: run() delega a run(null)
    public Particle run() {
        return run(null);
    }

}
