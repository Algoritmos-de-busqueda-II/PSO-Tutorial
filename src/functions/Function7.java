package functions;

import core.Particle;
import core.Problem;

/**
 * functions.Function7: Beale function (2D)
 * f(x,y) = (1.5 - x + x*y)^2 + (2.25 - x + x*y^2)^2 + (2.625 - x + x*y^3)^2
 * Global minimum at (x,y) = (3, 0.5) with f = 0.
 * Domain typically: [-4.5, 4.5]
 */
public class Function7 implements Problem {
    @Override
    public double evaluate(Particle p) {
        double x = p.x();
        double y = p.y();
        double t1 = 1.5 - x + x*y;
        double t2 = 2.25 - x + x*y*y;
        double t3 = 2.625 - x + x*y*y*y;
        return t1*t1 + t2*t2 + t3*t3;
    }

    @Override public double xMin() { return -4.5; }
    @Override public double xMax() { return 4.5; }
    @Override public double yMin() { return -4.5; }
    @Override public double yMax() { return 4.5; }
}
