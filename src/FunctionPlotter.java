import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 * Clase utilitaria para pintar funciones de dos variables f(x,y) en un mapa de calor.
 * Es genérica: recibe una implementación de {@link FunctionPlotter.Function2D} que
 * evalúa la función en coordenadas (x,y).
 *
 * Características:
 * - Renderiza un BufferedImage con la evaluación sobre una rejilla (width x height).
 * - Normaliza los valores para mapearlos a un gradiente de color.
 * - Puede mostrar el resultado en una ventana Swing o guardarlo como PNG.
 */
public class FunctionPlotter {

    /** Interfaz funcional para funciones f(x,y) -> double. */
    @FunctionalInterface
    public interface Function2D {
        double eval(double x, double y);
    }

    private final Function2D function;
    private final double xMin, xMax, yMin, yMax;
    private final int width, height;
    private double[][] values; // cache de evaluaciones
    private double vmin, vmax;
    private BufferedImage image;
    private int minI = -1, minJ = -1; // índice de la celda con valor mínimo
    // Margen en píxeles para dejar espacio a etiquetas/ticks
    private final int leftMargin = 70;
    private final int rightMargin = 20;
    private final int topMargin = 20;
    private final int bottomMargin = 50;

    // Overlay para visualización de partículas
    private Particle[] overlayParticles = new Particle[0];
    private Particle overlayGlobalBest = null;
    // Componentes Swing que se guardan al mostrar para permitir repintado externo
    private javax.swing.JFrame frame = null;
    private javax.swing.JPanel panel = null;

    /**
     * Construye un plotter para la función dada y dominio.
     * @param function la función a evaluar
     * @param xMin mínimo en X
     * @param xMax máximo en X
     * @param yMin mínimo en Y
     * @param yMax máximo en Y
     * @param width resolución horizontal (px)
     * @param height resolución vertical (px)
     */
    public FunctionPlotter(Function2D function, double xMin, double xMax, double yMin, double yMax, int width, int height) {
        this.function = function;
        this.xMin = xMin;
        this.xMax = xMax;
        this.yMin = yMin;
        this.yMax = yMax;
        this.width = Math.max(2, width);
        this.height = Math.max(2, height);
    }

    /** Evalúa la función en la rejilla y genera la imagen en memoria. */
    public void render() {
        values = new double[height][width];
        vmin = Double.POSITIVE_INFINITY;
        vmax = Double.NEGATIVE_INFINITY;
        minI = -1; minJ = -1;
        for (int j = 0; j < height; j++) {
            double y = yMax - j * (yMax - yMin) / (height - 1); // invertir Y para que origin esté abajo
            for (int i = 0; i < width; i++) {
                double x = xMin + i * (xMax - xMin) / (width - 1);
                double v;
                try {
                    v = function.eval(x, y);
                } catch (Exception e) {
                    v = Double.NaN; // si la función lanza, ponemos NaN
                }
                values[j][i] = v;
                if (!Double.isFinite(v)) continue;
                if (v < vmin) { vmin = v; minI = i; minJ = j; }
                if (v > vmax) vmax = v;
            }
        }

        // Si todos son NaN o iguales, ajustar rango
        if (vmin == Double.POSITIVE_INFINITY || vmax == Double.NEGATIVE_INFINITY) {
            vmin = 0;
            vmax = 1;
        } else if (vmin == vmax) {
            vmin -= 1.0;
            vmax += 1.0;
        }

        image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        for (int j = 0; j < height; j++) {
            for (int i = 0; i < width; i++) {
                double v = values[j][i];
                int rgb = valueToRGB(v);
                image.setRGB(i, j, rgb);
            }
        }
    }

    /** Actualiza la overlay de partículas (usado para visualización dinámica). */
    public synchronized void updateParticles(Particle[] particles, Particle globalBest) {
        if (particles == null) this.overlayParticles = new Particle[0];
        else this.overlayParticles = particles;
        this.overlayGlobalBest = globalBest;
    }

    /** Mapea un valor numérico a un color (gradiente azul->cyan->green->yellow->red). */
    private int valueToRGB(double v) {
        if (!Double.isFinite(v)) return Color.BLACK.getRGB();
        double t = (v - vmin) / (vmax - vmin);
        t = Math.max(0.0, Math.min(1.0, t));
        // usar una rampa suave: azul(0) -> cyan -> green -> yellow -> red(1)
        Color c;
        if (t < 0.25) {
            double u = t / 0.25;
            c = lerpColor(new Color(0, 0, 128), new Color(0, 128, 255), u);
        } else if (t < 0.5) {
            double u = (t - 0.25) / 0.25;
            c = lerpColor(new Color(0, 128, 255), new Color(0, 255, 128), u);
        } else if (t < 0.75) {
            double u = (t - 0.5) / 0.25;
            c = lerpColor(new Color(0, 255, 128), new Color(255, 255, 0), u);
        } else {
            double u = (t - 0.75) / 0.25;
            c = lerpColor(new Color(255, 255, 0), new Color(200, 0, 0), u);
        }
        return c.getRGB();
    }

    private Color lerpColor(Color a, Color b, double t) {
        int r = (int) (a.getRed() + t * (b.getRed() - a.getRed()));
        int g = (int) (a.getGreen() + t * (b.getGreen() - a.getGreen()));
        int bl = (int) (a.getBlue() + t * (b.getBlue() - a.getBlue()));
        return new Color(clamp(r,0,255), clamp(g,0,255), clamp(bl,0,255));
    }

    private int clamp(int v, int lo, int hi) {
        if (v < lo) return lo;
        if (v > hi) return hi;
        return v;
    }

    /** Muestra la imagen en una ventana Swing (renderizará si no está hecha). */
    public void show(String title) {
        if (image == null) render();
        JFrame frame = new JFrame(title == null ? "Function Plot" : title);
        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics gg) {
                super.paintComponent(gg);
                Graphics g = gg.create();
                int w = getWidth();
                int h = getHeight();
                // Area donde se pinta la imagen (dejando márgenes para etiquetas)
                int ix = leftMargin;
                int iy = topMargin;
                int iw = Math.max(10, w - leftMargin - rightMargin);
                int ih = Math.max(10, h - topMargin - bottomMargin);
                // Dibujar fondo blanco
                g.setColor(Color.WHITE);
                g.fillRect(0, 0, w, h);
                // Dibujar la imagen escalada al interior
                if (image != null) {
                    g.drawImage(image, ix, iy, iw, ih, null);
                }

                // Dibujar rejilla y ejes
                g.setColor(new Color(0,0,0,180));
                // Ejes en coordenadas: si 0 está en dominio, colocarlos en la posición correcta
                double tx0 = (0 - xMin) / (xMax - xMin);
                double ty0 = (yMax - 0) / (yMax - yMin);
                int axisX = (xMin <= 0 && 0 <= xMax) ? ix + (int)Math.round(tx0 * (iw - 1)) : ix;
                int axisY = (yMin <= 0 && 0 <= yMax) ? iy + (int)Math.round(ty0 * (ih - 1)) : iy + ih - 1;
                g.drawLine(axisX, iy, axisX, iy + ih - 1);
                g.drawLine(ix, axisY, ix + iw - 1, axisY);

                // Grid
                g.setColor(new Color(0,0,0,60));
                int ticks = 8;
                for (int t = 0; t <= ticks; t++) {
                    int xi = ix + (int) Math.round((double) t / ticks * (iw - 1));
                    g.drawLine(xi, iy, xi, iy + ih - 1);
                }
                for (int t = 0; t <= ticks; t++) {
                    int yj = iy + (int) Math.round((double) t / ticks * (ih - 1));
                    g.drawLine(ix, yj, ix + iw - 1, yj);
                }

                // Ticks y etiquetas (más margen para etiquetas X)
                g.setColor(Color.BLACK);
                int labelTicks = 5;
                for (int t = 0; t <= labelTicks; t++) {
                    double tx = (double) t / labelTicks;
                    int xi = ix + (int) Math.round(tx * (iw - 1));
                    double xv = xMin + tx * (xMax - xMin);
                    g.drawLine(xi, axisY - 4, xi, axisY + 4);
                    String label = String.format("%.2f", xv);
                    // Centrar etiqueta
                    int strW = g.getFontMetrics().stringWidth(label);
                    g.drawString(label, xi - strW/2, iy + ih + 20);
                }
                for (int t = 0; t <= labelTicks; t++) {
                    double ty = (double) t / labelTicks;
                    int yj = iy + (int) Math.round(ty * (ih - 1));
                    double yv = yMax - ty * (yMax - yMin);
                    g.drawLine(axisX - 4, yj, axisX + 4, yj);
                    String label = String.format("%.2f", yv);
                    g.drawString(label, 2, yj + 4);
                }

                // Dibujar mínimo en coordenadas escala internas
                if (minI >= 0 && minJ >= 0) {
                    double minX = xMin + (double) minI * (xMax - xMin) / (width - 1);
                    double minY = yMax - (double) minJ * (yMax - yMin) / (height - 1);
                    int px = ix + (int) Math.round((minX - xMin) / (xMax - xMin) * (iw - 1));
                    int py = iy + (int) Math.round((yMax - minY) / (yMax - yMin) * (ih - 1));
                    g.setColor(Color.WHITE);
                    int r = Math.max(4, Math.min(iw, ih) / 100);
                    g.fillOval(px - r, py - r, 2*r, 2*r);
                    g.setColor(Color.RED);
                    g.drawOval(px - r, py - r, 2*r, 2*r);
                    String info = String.format("min: (%.3f, %.3f)=%.4f", minX, minY, vmin);
                    g.setColor(Color.BLACK);
                    g.drawString(info, Math.min(ix + iw - 4 - g.getFontMetrics().stringWidth(info), px + r + 4), Math.max(12, py - r - 4));
                }

                // Dibujar partículas overlay
                synchronized (FunctionPlotter.this) {
                    if (overlayParticles != null) {
                        for (Particle p : overlayParticles) {
                            double px = (p.x() - xMin) / (xMax - xMin);
                            double py = (yMax - p.y()) / (yMax - yMin);
                            int xi = ix + (int)Math.round(px * (iw - 1));
                            int yj = iy + (int)Math.round(py * (ih - 1));
                            g.setColor(new Color(0,0,0,180));
                            g.fillOval(xi-3, yj-3, 6, 6);
                        }
                    }
                    if (overlayGlobalBest != null) {
                        double px = (overlayGlobalBest.x() - xMin) / (xMax - xMin);
                        double py = (yMax - overlayGlobalBest.y()) / (yMax - yMin);
                        int xi = ix + (int)Math.round(px * (iw - 1));
                        int yj = iy + (int)Math.round(py * (ih - 1));
                        g.setColor(Color.MAGENTA);
                        int r = 6;
                        g.fillOval(xi-r, yj-r, 2*r, 2*r);
                    }
                }

                g.dispose();
            }

            @Override
            public Dimension getPreferredSize() {
                return new Dimension(width + leftMargin + rightMargin, height + topMargin + bottomMargin);
            }
        };
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.getContentPane().add(panel, BorderLayout.CENTER);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        // Guardar referencias para uso externo
        this.frame = frame;
        this.panel = panel;
    }

    /** Fuerza un repintado del panel de visualización (seguro desde cualquier hilo). */
    public void refresh() {
        if (panel == null) return;
        javax.swing.SwingUtilities.invokeLater(() -> panel.repaint());
    }

    /** Guarda la imagen renderizada como PNG en el fichero dado. */
    public void savePNG(File outFile) throws IOException {
        if (image == null) render();
        // Para guardar con ejes y overlays, render temporal en un panel
        // Simplest approach: render and then draw axes and overlays on copy
        BufferedImage outImg = new BufferedImage(width + leftMargin + rightMargin, height + topMargin + bottomMargin, BufferedImage.TYPE_INT_RGB);
        Graphics g = outImg.getGraphics();
        // paint using same logic as in show()
        render();
        // Use show() painting logic via a temporary panel would be complex; instead, place image with margins and draw simple axes and markers
        g.setColor(Color.WHITE);
        g.fillRect(0,0,outImg.getWidth(), outImg.getHeight());
        g.drawImage(image, leftMargin, topMargin, width, height, null);
        g.dispose();
        ImageIO.write(outImg, "png", outFile);
    }

    /** Devuelve el buffered image renderizado (null si no se ha renderizado). */
    public BufferedImage getImage() {
        return image;
    }

}
