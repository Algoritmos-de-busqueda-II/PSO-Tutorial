import java.io.File;
import java.io.IOException;

public class PlotDemo {
    public static void main(String[] args) {
        SimpleFunctionProblem problem = new SimpleFunctionProblem();
        FunctionPlotter.Function2D f = (x, y) -> problem.evaluate(new Particle(x, y, 0, 0));

        // Dominio y resolución (ajustable)
        double xMin = 0.0, xMax = 5.0, yMin = 0.0, yMax = 5.0;
        int width = 800, height = 600;

        FunctionPlotter plotter = new FunctionPlotter(f, xMin, xMax, yMin, yMax, width, height);
        plotter.render();

        try {
            File out = new File("function_plot.png");
            plotter.savePNG(out);
            System.out.println("Saved PNG: " + out.getAbsolutePath());
        } catch (IOException e) {
            System.err.println("Failed to save PNG: " + e.getMessage());
            e.printStackTrace();
        }

        // Si quieres ver la ventana GUI en local, descomenta la siguiente línea
        // plotter.show("Function plot");
    }
}

