public class SimplePSO {
    private final int numParticles;
    private final int numIterations;
    private final double w;
    private final double c1;
    private final double c2;
    private final Problem problem;

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
        // Inicializamos las partículas
        Particle[] particles = new Particle[numParticles];
        for (int i = 0; i < numParticles; i++) {
            particles[i] = new Particle(Math.random() * (upperBound - lowerBound) + lowerBound, Math.random() * (upperBound - lowerBound) + lowerBound, Math.random(), Math.random());
        }
        return particles;
    }


    public Particle run() {

        // INICIALIACIÓN -----------------------------------------------------

        // Inicializamos las partículas (posición y velocidad) usando umbrales
        Particle[] particles = creaEnjambreAleatorio(0,5);

        // Mejores partículas locales
        Particle[] localBest = new Particle[numParticles];
        Particle globalBest = null;
        // Es un problema de minimización, por lo que el valor inicial es el máximo
        double globalBestValue = Double.MAX_VALUE;
        for (int i = 0; i < numParticles; i++) {
            // Copia de valores. La asignación sería por referencia.
            localBest[i] = new Particle(particles[i].x(), particles[i].y(), particles[i].vx(), particles[i].vy());
            double value = problem.evaluate(particles[i]);
            if (value < globalBestValue) {
                globalBestValue = value;
                globalBest = new Particle(particles[i].x(), particles[i].y(), particles[i].vx(), particles[i].vy());
            }
//            System.out.println("Current solution: (" + particles[i].x() + ", " + particles[i].y() + ") -> " + value);
        }

        // BUCLE PRINCIPAL -----------------------------------------------------

        System.out.println("Iteración;Mejor solución;Valor");
        for (int iters = 1; iters <= numIterations; iters++) {
            // Iteramos sobre las partículas
            for (int i = 0; i < numParticles; i++) {
                // Calculamos nueva velocidad
                double vx = w * particles[i].vx() + c1 * Math.random() * (localBest[i].x() - particles[i].x()) + c2 * Math.random() * (globalBest.x() - particles[i].x());
                double vy = w * particles[i].vy() + c1 * Math.random() * (localBest[i].y() - particles[i].y()) + c2 * Math.random() * (globalBest.y() - particles[i].y());

                // Actualizamos la posición
                double x = particles[i].x() + vx;
                double y = particles[i].y() + vy;

                // Actualizamos valores de la partícula
                particles[i] = new Particle(x, y, vx, vy);

                // Actualizamos la mejor partícula local
                if (problem.evaluate(particles[i]) < problem.evaluate(localBest[i])) {
                    localBest[i] = new Particle(x, y, vx, vy);
                }

                // Actualizamos la mejor partícula global
                if (problem.evaluate(particles[i]) < globalBestValue) {
                    globalBestValue = problem.evaluate(particles[i]);
                    globalBest = new Particle(x, y, vx, vy);
                }
            }
            System.out.println(iters+";(" + globalBest.x() + ", " + globalBest.y() + ");" + globalBestValue);
        }

        return globalBest;
    }

}
