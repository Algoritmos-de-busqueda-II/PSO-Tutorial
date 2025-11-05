package functions;

import core.Particle;
import core.Problem;

/**
 * functions.Function9: Bukin N.6 function (2D)
 * f(x,y) = 100*sqrt(|y - 0.01*x^2|) + 0.01*|x + 10|
 * Domain typically: x in [-15, -5], y in [-3, 3]
 * Global minimum near x = -10, y = 1 with f = 0
 */
public class Function9 implements Problem {
    @Override
    public double evaluate(Particle p) {
        double x = p.x();
        double y = p.y();
        double term1 = 100.0 * Math.sqrt(Math.abs(y - 0.01 * x * x));
        double term2 = 0.01 * Math.abs(x + 10.0);
        return term1 + term2;
    }

    @Override public double xMin() { return -15.0; }
    @Override public double xMax() { return -5.0; }
    @Override public double yMin() { return -3.0; }
    @Override public double yMax() { return 3.0; }
}
