package functions;

import core.Particle;
import core.Problem;

/**
 * functions.Function6: Himmelblau's function (2D)
 * f(x,y) = (x^2 + y - 11)^2 + (x + y^2 - 7)^2
 * Multiple minima (e.g. (3.0,2.0), (-2.805118,3.131312), (-3.779310,-3.283186), (3.584428,-1.848126))
 * Domain typically: [-5, 5]
 */
public class Function6 implements Problem {
    @Override
    public double evaluate(Particle p) {
        double x = p.x();
        double y = p.y();
        double a = x*x + y - 11.0;
        double b = x + y*y - 7.0;
        return a*a + b*b;
    }

    @Override public double xMin() { return -5.0; }
    @Override public double xMax() { return 5.0; }
    @Override public double yMin() { return -5.0; }
    @Override public double yMax() { return 5.0; }
}
