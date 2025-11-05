package core;

public interface Problem {
    /** Evalúa la función objetivo para la partícula dada. */
    double evaluate(Particle p);

    /** Dominio obligatorio: límites en X e Y. */
    double xMin();
    double xMax();
    double yMin();
    double yMax();
}
