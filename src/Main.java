import functions.*;
import core.Particle;
import core.SimplePSO;
import ui.FunctionPlotter;

import javax.swing.SwingUtilities;

/**
 * Codigo de ejemplo para el bloque de inteligencia de enjambre.
 */
public class Main {
    public static void main(String[] args) {
        boolean visualize = true; // Cambia a false para ejecutar en modo no interactivo

        var problem = new Function4();
        SimplePSO pso = new SimplePSO(20, 100, 0.7, 1.4, 1.4, problem);

        if (visualize) {
            // Crear plotter y mostrar la función
            FunctionPlotter.Function2D f = (x, y) -> problem.evaluate(new Particle(x, y, 0, 0));
            FunctionPlotter plotter = new FunctionPlotter(f, problem.xMin(), problem.xMax(), problem.yMin(), problem.yMax(), 400, 400);
            plotter.render();
            // Mostrar GUI en EDT
            SwingUtilities.invokeLater(() -> plotter.show("PSO visualization"));

            // Listener que actualiza overlay y refresca la ventana
            SimplePSO.PSOListener listener = (iteration, particles, globalBest, value) -> {
                // Actualizar overlay y repintar (updateParticles es synchronized)
                plotter.updateParticles(particles, globalBest);
                plotter.refresh();
                System.out.println("Iter " + iteration + ": best=(" + globalBest.x() + "," + globalBest.y() + ") -> " + value);
            };

            Particle solution = pso.run(listener);
            System.out.println("\nBest solution: (" + solution.x() + ", " + solution.y() + ") -> " + problem.evaluate(solution));
        } else {
            Particle solution = pso.run();
            System.out.println("\nBest solution: (" + solution.x() + ", " + solution.y() + ") -> " + problem.evaluate(solution));
        }


    }
}