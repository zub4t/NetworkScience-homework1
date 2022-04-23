package models;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.RenderingHints;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import lombok.Builder;

/**
 * Main class of the simple function plotter. Contains the
 * main method and creates the GUI
 */
@Builder
public class SimplePlotMain {
    Function function;

    public static void main(String[] args) {
        // Create the GUI on the Event-Dispatch-Thread
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                createAndShowGUI(new Function() {
                    @Override
                    public double compute(double argument) {
                        // System.out.ln(argument);
                        return Math.sin((int) argument);
                    }
                }, 10, 10, 10, 10);
            }
        });
    }

    /**
     * Creates the frame containing the simple plotter
     */
    public static void createAndShowGUI(Function function, double minX, double maxX, double minY, double maxY) {
        // Create the main frame
        JFrame frame = new JFrame("Network Science visualizer");
        frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        frame.getContentPane().setLayout(new BorderLayout());
        frame.setSize(800, 600);

        // Create the SimplePlotPanel and add it to the frame
        SimplePlotPanel plotPanel = new SimplePlotPanel();
        frame.getContentPane().add(plotPanel, BorderLayout.CENTER);

        plotPanel.setFunction(function);

        // Create a simple control panel and add it to the frame
        JComponent controlPanel = createControlPanel(plotPanel, minX, maxX, minY, maxY);
        frame.getContentPane().add(controlPanel, BorderLayout.EAST);

        // As the last action: Make the frame visible
        frame.setVisible(true);
    }

    /**
     * Creates a panel containing some Spinners that allow defining
     * the area in which the function should be shown
     *
     * @param plotPanel The SimplePlotPanel, to which the settings
     *                  will be transferred
     * @return The control-panel
     */
    private static JComponent createControlPanel(
            final SimplePlotPanel plotPanel, double minX, double maxX, double minY, double maxY) {
        JPanel controlPanel = new JPanel(new BorderLayout());
        JPanel panel = new JPanel(new GridLayout(0, 2));
        controlPanel.add(panel, BorderLayout.NORTH);

        // Create spinners for the minimum and maximum
        // X- and Y-values
        final JSpinner minXSpinner = new JSpinner(
                new SpinnerNumberModel(-1.0, -1000.0, 1000.0, 0.1));
        final JSpinner maxXSpinner = new JSpinner(
                new SpinnerNumberModel(1.0, -1000.0, 1000.0, 0.1));
        final JSpinner minYSpinner = new JSpinner(
                new SpinnerNumberModel(-1.0, -1000.0, 1000.0, 0.1));
        final JSpinner maxYSpinner = new JSpinner(
                new SpinnerNumberModel(1.0, -1000.0, 1000.0, 0.1));

        final JSpinner alphaSpinner = new JSpinner(
                new SpinnerNumberModel(SimplePlotPanel.alpha, 1, 3, 0.1));
        final JSpinner nSpinner = new JSpinner(
                new SpinnerNumberModel(SimplePlotPanel.n, 2000, 60000, 10));
        // Add the spinners and some labels to the panel
        panel.add(new JLabel("minX"));
        panel.add(minXSpinner);
        panel.add(new JLabel("maxX"));
        panel.add(maxXSpinner);
        panel.add(new JLabel("minY"));
        panel.add(minYSpinner);
        panel.add(new JLabel("maxY"));
        panel.add(maxYSpinner);
        panel.add(new JLabel("α"));
        panel.add(alphaSpinner);
        panel.add(new JLabel("Number of nodes"));
        panel.add(nSpinner);
        // Create a ChangeListener that will be added to all spinners,
        // and which transfers the settings to the SimplePlotPanel
        ChangeListener changeListener = new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent event) {
                double minX = ((Double) minXSpinner.getValue()).doubleValue();
                double maxX = ((Double) maxXSpinner.getValue()).doubleValue();
                double minY = ((Double) minYSpinner.getValue()).doubleValue();
                double maxY = ((Double) maxYSpinner.getValue()).doubleValue();
                SimplePlotPanel.alpha = ((Double) alphaSpinner.getValue()).doubleValue();
                SimplePlotPanel.n = ((Integer) nSpinner.getValue());

                plotPanel.setRangeX(minX, maxX);
                plotPanel.setRangeY(minY, maxY);
            }
        };
        minXSpinner.addChangeListener(changeListener);
        maxXSpinner.addChangeListener(changeListener);
        minYSpinner.addChangeListener(changeListener);
        maxYSpinner.addChangeListener(changeListener);
        alphaSpinner.addChangeListener(changeListener);
        nSpinner.addChangeListener(changeListener);

        // Set some default values for the Spinners
        minXSpinner.setValue(minX);
        maxXSpinner.setValue(maxX);
        minYSpinner.setValue(minY);
        maxYSpinner.setValue(maxY);

        return controlPanel;
    }
}

/**
 * Interface for a general function that may be plotted with
 * the SimplePlotPanel
 */
interface Function {
    /**
     * Compute the value of the function for the given argument
     *
     * @param argument The function argument
     * @return The function value
     */
    double compute(double argument);
}

/**
 * The panel in which the function will be plotted
 */
class SimplePlotPanel extends JPanel {
    public static boolean barabasi = false;
    public static double trueAlpha = 0.0;
    public static int size = 10;
    public static int n = 2000;
    public static double alpha = 2.5;

    private static final long serialVersionUID = -6588061082489436970L;

    /**
     * The function that will be plotted
     */
    private Function function;

    /**
     * The minimal x value that is shown
     */
    private double minX = -1.0f;

    /**
     * The maximal x value that is shown
     */
    private double maxX = 1.0f;

    /**
     * The minimal y value that is shown
     */
    private double minY = -1.0f;

    /**
     * The maximal y value that is shown
     */
    private double maxY = 1.0f;

    /**
     * Set the Function that should be plotted
     *
     * @param function The Function that should be plotted
     */
    public void setFunction(Function function) {
        this.function = function;
        repaint();
    }

    /**
     * Set the x-range that should be plotted
     *
     * @param minX The minimum x-value
     * @param maxX The maximum y-value
     */
    public void setRangeX(double minX, double maxX) {
        this.minX = minX;
        this.maxX = maxX;
        repaint();
    }

    /**
     * Set the y-range that should be plotted
     *
     * @param minY The minimum y-value
     * @param maxY The maximum y-value
     */
    public void setRangeY(double minY, double maxY) {
        this.minY = minY;
        this.maxY = maxY;
        repaint();
    }

    /**
     * Overridden method from JComponent: Paints this panel - that
     * is, paints the function into the given graphics object
     */
    @Override
    protected void paintComponent(Graphics gr) {
        super.paintComponent(gr);
        Graphics2D g = (Graphics2D) gr;
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, getWidth(), getHeight());
        g.setRenderingHint(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        paintAxes(g);
        paintFunction(g);
    }

    /**
     * Converts an x-coordinate of the function into an x-value of this panel
     *
     * @param x The x-coordinate of the function
     * @return The x-coordinate on this panel
     */
    private int toScreenX(double x) {
        double relativeX = (x - minX) / (maxX - minX);
        int screenX = (int) (getWidth() * relativeX);
        return screenX;
    }

    /**
     * Converts an y-coordinate of the function into an y-value of this panel
     *
     * @param y The y-coordinate of the function
     * @return The y-coordinate on this panel
     */
    private int toScreenY(double y) {
        double relativeY = (y - minY) / (maxY - minY);
        int screenY = getHeight() - 1 - (int) (getHeight() * relativeY);
        return screenY;
    }

    /**
     * Converts an x-coordinate on this panel into an x-coordinate
     * for the function
     *
     * @param x The x-coordinate on the panel
     * @return The x-coordinate for the function
     */
    private double toFunctionX(int x) {
        double relativeX = (double) x / getWidth();
        double functionX = minX + relativeX * (maxX - minX);
        return functionX;
    }

    /**
     * Paints some coordinate axes into the given Graphics
     *
     * @param g The graphics
     */
    private void paintAxes(Graphics2D g) {
        int x0 = toScreenX(0);
        int y0 = toScreenY(0);
        g.setColor(Color.BLACK);
        g.drawLine(0, y0, getWidth(), y0);
        g.drawLine(x0, 0, x0, getHeight());
    }

    /**
     * Paints the function into the given Graphics
     *
     * @param g The graphics
     */
    private void paintFunction(Graphics2D g) {

        for (int i = 1; i < App.currentMap.size(); i++) {
            trueAlpha += Math.log(App.currentMap.get(i) / App.m);
        }
        System.out.println("mapa" + App.currentMap);
        trueAlpha = (App.currentMap.size() / trueAlpha) + 1;

        int previousScreenX = 0;
        double previousFunctionX = toFunctionX(previousScreenX);
        double previousFunctionY = function.compute(previousFunctionX);
        int previousScreenY = toScreenY(previousFunctionY);

        int previuousBrabasi = -1;
        double previuousErdos = -1;

        for (int screenX = 1; screenX < getWidth(); screenX++) {
            double functionX = toFunctionX(screenX);
            if ((previuousBrabasi != (int) Math.pow(10, (functionX)) && SimplePlotPanel.barabasi)
                    || !SimplePlotPanel.barabasi) {
                g.setColor(Color.BLUE);
                double functionY = function.compute(functionX);

                if (SimplePlotPanel.barabasi || (functionY != previuousErdos && !SimplePlotPanel.barabasi)) {
                    int screenY = toScreenY(functionY);
                    g.drawArc(screenX, screenY, SimplePlotPanel.size, SimplePlotPanel.size, 0, 360);
                    previousScreenX = screenX;
                    previousScreenY = screenY;
                }
                previuousErdos = functionY;
            }
            if (SimplePlotPanel.barabasi) {
                if (screenX > 1) {
                    g.setColor(Color.RED);
                    double y = SimplePlotPanel.n
                            * Math.pow(new Double(Math.pow(10, (toFunctionX(screenX - 1)))), -1 * alpha);
                    double yy = SimplePlotPanel.n
                            * Math.pow(new Double(Math.pow(10, (toFunctionX(screenX)))), -1 * alpha);
                    // System.out.ln((Math.log10(y)) + " " + (Math.log10(yy)));
                    g.drawLine(screenX - 1, toScreenY(Math.log10(y)), screenX, toScreenY(Math.log10(yy)));

                    g.setColor(Color.GREEN);
                    y = SimplePlotPanel.n
                            * Math.pow(new Double(Math.pow(10, (toFunctionX(screenX - 1)))), -1 * trueAlpha);
                    yy = SimplePlotPanel.n
                            * Math.pow(new Double(Math.pow(10, (toFunctionX(screenX)))), -1 * trueAlpha);
                    // System.out.ln((Math.log10(y)) + " " + (Math.log10(yy)));
                    g.drawLine(screenX - 1, toScreenY(Math.log10(y)), screenX, toScreenY(Math.log10(yy)));

                }

            }
            previuousBrabasi = (int) Math.pow(10, (functionX));

        }
    }

}