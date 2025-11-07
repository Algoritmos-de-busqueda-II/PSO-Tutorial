package functions;

import core.Particle;
import core.Problem;

public class Function1 implements Problem {

    @Override
    public double evaluate(Particle p) {
        // Función a minimizar: f(x,y) = (x-3.14)^2 + (y-2.72)^2 + sin(3x+1.41) + sin(4y-1.73)
        return (Math.pow((p.x()-3.14),2)+Math.pow((p.y()-2.72),2)+Math.sin(3*p.x()+1.41)+Math.sin(4*p.y()-1.73));
    }

    // Dominio: por defecto [0,5]
    @Override
    public double xMin() { return 0.0; }
    @Override
    public double xMax() { return 5.0; }
    @Override
    public double yMin() { return 0.0; }
    @Override
    public double yMax() { return 5.0; }
}
