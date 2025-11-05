/**
 * Codigo de ejemplo para el bloque de inteligencia de enjambre.
 */
public class Main {
    public static void main(String[] args) {
        SimpleFunctionProblem problem = new SimpleFunctionProblem();
        SimplePSO pso = new SimplePSO(10, 100, 0.5, 0.5, 0.5, problem);
        Particle solution = pso.run();
        System.out.println("\nBest solution: (" + solution.x() + ", " + solution.y() + ") -> " + problem.evaluate(solution));

        // Mejor solución es (3.182,3.131). Se muestra por pantalla:
        Particle bestSolution = new Particle(3.182, 3.131, 0, 0);
        System.out.println("\nOptimal solution: (" + bestSolution.x() + ", " + bestSolution.y() + ") -> " + problem.evaluate(bestSolution));
    }
}