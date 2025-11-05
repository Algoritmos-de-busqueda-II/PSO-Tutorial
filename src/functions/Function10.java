package functions;

import core.Particle;
import core.Problem;

/**
 * functions.Function10: Six-hump Camelback function (2D)
 * f(x,y) = (4 - 2.1 x^2 + x^4/3) x^2 + x*y + (-4 + 4 y^2) y^2
 * There are multiple minima; two global minima approximately at
 * (x, y) = (0.0898, -0.7126) and (-0.0898, 0.7126) with f ≈ -1.0316
 * Domain typically: x in [-3, 3], y in [-2, 2]
 */
public class Function10 implements Problem {
    @Override
    public double evaluate(Particle p) {
        double x = p.x();
        double y = p.y();
        double term1 = (4.0 - 2.1 * x * x + (x * x * x * x) / 3.0) * x * x;
        double term2 = x * y;
        double term3 = (-4.0 + 4.0 * y * y) * y * y;
        return term1 + term2 + term3;
    }

    @Override public double xMin() { return -3.0; }
    @Override public double xMax() { return 3.0; }
    @Override public double yMin() { return -2.0; }
    @Override public double yMax() { return 2.0; }
}
