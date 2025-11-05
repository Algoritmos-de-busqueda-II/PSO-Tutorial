import java.util.Random;

public class SimplePSO {
    private final int numParticles;
    private final int numIterations;
    private final double w;
    private final double c1;
    private final double c2;
    private final Problem problem;
    private final Random rand = new Random(); // para reproducibilidad opcional se puede pasar semilla

    // Constructor del algoritmo
    public SimplePSO(int numParticles, int numIterations, double w, double c1, double c2, Problem problem) {
        this.numParticles = numParticles;
        this.numIterations = numIterations;
        this.w = w;
        this.c1 = c1;
        this.c2 = c2;
        this.problem = problem;
    }

    private Particle[] creaEnjambreAleatorio(double lowerBound, double upperBound) {
        // Inicializamos las partículas (posición y velocidad) usando umbrales
        Particle[] particles = new Particle[numParticles];
        double range = upperBound - lowerBound;
        double vmax = range; // velocidad máxima inicial (se puede ajustar)
        for (int i = 0; i < numParticles; i++) {
            double x = lowerBound + rand.nextDouble() * range;
            double y = lowerBound + rand.nextDouble() * range;
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

        // Definimos un dominio para posiciones (antes estaba codificado como 0,5)
        double lowerBound = 0.0;
        double upperBound = 5.0;
        double range = upperBound - lowerBound;
        double vmax = range; // tope de velocidad

        // Inicializamos las partículas (posición y velocidad)
        Particle[] particles = creaEnjambreAleatorio(lowerBound, upperBound);

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

                // Clampear la posición al dominio
                x = Math.max(lowerBound, Math.min(upperBound, x));
                y = Math.max(lowerBound, Math.min(upperBound, y));

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
