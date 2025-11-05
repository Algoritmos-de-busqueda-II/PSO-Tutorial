package functions;

import core.Particle;
import core.Problem;

/**
 * Function2: Sphere function (2D)
 * f(x,y) = x^2 + y^2
 * Global minimum at (0,0) with f=0.
 * Domain typically: [-5.12, 5.12]
 */
public class Function2 implements Problem {
    @Override
    public double evaluate(Particle p) {
        double x = p.x();
        double y = p.y();
        return x*x + y*y;
    }

    @Override public double xMin() { return -5.12; }
    @Override public double xMax() { return 5.12; }
    @Override public double yMin() { return -5.12; }
    @Override public double yMax() { return 5.12; }
}
