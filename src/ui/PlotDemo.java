package ui;

import functions.*;
import core.Particle;

import java.io.File;
import java.io.IOException;

public class PlotDemo {
    public static void main(String[] args) {
        var problem = new Function4();
        FunctionPlotter.Function2D f = (x, y) -> problem.evaluate(new Particle(x, y, 0, 0));

        // Usar dominio definido por el problema
        double xMin = problem.xMin(), xMax = problem.xMax(), yMin = problem.yMin(), yMax = problem.yMax();
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
