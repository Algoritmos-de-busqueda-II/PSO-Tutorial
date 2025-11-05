package functions;

import core.Particle;
import core.Problem;

/**
 * Function5: Ackley function (2D)
 * f(x,y) = -20*exp(-0.2*sqrt(0.5*(x^2+y^2))) - exp(0.5*(cos(2*pi*x)+cos(2*pi*y))) + e + 20
 * Global minimum at (0,0) with f=0.
 * Domain typically: [-5, 5]
 */
public class Function5 implements Problem {
    @Override
    public double evaluate(Particle p) {
        double x = p.x();
        double y = p.y();
        double sumSq = 0.5*(x*x + y*y);
        double term1 = -20.0 * Math.exp(-0.2 * Math.sqrt(sumSq));
        double term2 = -Math.exp(0.5*(Math.cos(2*Math.PI*x) + Math.cos(2*Math.PI*y)));
        return term1 + term2 + Math.E + 20.0;
    }

    @Override public double xMin() { return -5.0; }
    @Override public double xMax() { return 5.0; }
    @Override public double yMin() { return -5.0; }
    @Override public double yMax() { return 5.0; }
}
