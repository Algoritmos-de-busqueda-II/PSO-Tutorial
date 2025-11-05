package functions;

import core.Particle;
import core.Problem;

/**
 * functions.Function8: Booth function (2D)
 * f(x,y) = (x + 2y - 7)^2 + (2x + y - 5)^2
 * Global minimum at (1,3) with f = 0.
 * Domain typically: [-10, 10]
 */
public class Function8 implements Problem {
    @Override
    public double evaluate(Particle p) {
        double x = p.x();
        double y = p.y();
        double t1 = x + 2.0*y - 7.0;
        double t2 = 2.0*x + y - 5.0;
        return t1*t1 + t2*t2;
    }

    @Override public double xMin() { return -100.0; }
    @Override public double xMax() { return 100.0; }
    @Override public double yMin() { return -100.0; }
    @Override public double yMax() { return 100.0; }
}
