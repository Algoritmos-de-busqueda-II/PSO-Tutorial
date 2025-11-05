package core;

/**
 * Un registro de Java proporciona automáticamente un constructor, métodos de acceso y métodos de igualdad.
 *
 * Unimos en un mismo objeto las coordenadas y la velocidad de una partícula.
 */
public record Particle(double x, double y, double vx, double vy) { }
