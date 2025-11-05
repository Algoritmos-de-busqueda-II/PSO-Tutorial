package functions;

import core.Particle;
import core.Problem;

/**
 * Function3: Rosenbrock function (2D)
 * f(x,y) = (1 - x)^2 + 100*(y - x^2)^2
 * Global minimum at (1,1) with f=0.
 * Domain typically: [-2.048, 2.048] or [-5,5]
 */
public class Function3 implements Problem {
    @Override
    public double evaluate(Particle p) {
        double x = p.x();
        double y = p.y();
        double a = 1.0;
        double b = 100.0;
        return (a - x)*(a - x) + b * (y - x*x)*(y - x*x);
    }

    @Override public double xMin() { return -5; }
    @Override public double xMax() { return 5; }
    @Override public double yMin() { return -5; }
    @Override public double yMax() { return 5; }
}
