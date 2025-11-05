package functions;

import core.Particle;
import core.Problem;

/**
 * Function4: Rastrigin function (2D)
 * f(x,y) = 20 + x^2 + y^2 - 10*(cos(2*pi*x) + cos(2*pi*y))
 * Global minimum at (0,0) with f=0.
 * Domain typically: [-5.12, 5.12]
 */
public class Function4 implements Problem {
    @Override
    public double evaluate(Particle p) {
        double x = p.x();
        double y = p.y();
        double A = 10.0;
        return 2*A + (x*x - A * Math.cos(2*Math.PI*x)) + (y*y - A * Math.cos(2*Math.PI*y));
    }

    @Override public double xMin() { return -5.12; }
    @Override public double xMax() { return 5.12; }
    @Override public double yMin() { return -5.12; }
    @Override public double yMax() { return 5.12; }
}
