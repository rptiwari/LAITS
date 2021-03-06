package laits.plot;

import laits.Main;
import laits.graph.*;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import javax.swing.JPanel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

/**
 * Class to create plots of the different variables of the model
 * THIS CLASS IS IN TESTING MODE, WE NEED TO DO THE ADJUSTMENTS TO THE REQUIRMENTS OF THE SW.
 *
 * @author Javier Gonzalez Sanchez
 * @version 20090612
 */
public class PlotPanel extends JPanel {
  // The string vertex is used to complete the label of the window including the name of the vertex
  // which is represented on this plot
  //private String vertex;

  private Vertex vertex;
  private String units;
  Graph graph;

  /**
   * Constructor
   *
   * Creates a new window with a plot inside of it
   * @param vertex is the name of the vertex that is being ploted
   * @param x0 is the initial value for the x-axis
   * @param xf is the final value for the x-axis
   * @param relation define the type of tokenList we are drawing
   */
  public PlotPanel(Vertex vertex, int x0, int xf, Graph g, String units) {
    this.vertex = vertex;
    this.units = units;
    this.graph = g;
    final XYDataset dataset = createDataset(x0, xf);
    final JFreeChart chart = createChart(dataset, this.vertex.label);
    final ChartPanel chartPanel = new ChartPanel(chart);

    if(Main.professorVersion == Main.VERSION2 || Main.professorVersion == Main.DEMO_VERSION2) {
      chart.getTitle().setFont(new Font("Arial", Font.BOLD, 14));
      chart.getXYPlot().getDomainAxis().setLabelFont(new Font("Ariel", Font.BOLD, 11));
      chart.getXYPlot().getRangeAxis().setLabelFont(new Font("Ariel", Font.BOLD, 11));
    }
    // The size of the panel depends on the size of the GraphDialog panel
    chartPanel.setPreferredSize(new java.awt.Dimension(550, 230));
    this.setLayout(new GridLayout(1, 1));
    add(chartPanel);
  }

  /**
   * Constructor
   *
   * Creates a new window with a plot inside of it
   * @param vertex is the name of the vertex that is being ploted
   * @param x0 is the initial value for the x-axis
   * @param xf is the final value for the x-axis
   * @param relation define the type of tokenList we are drawing
   */
  public PlotPanel(Vertex vertex, int x0, String taskName, String units) {
    this.vertex = vertex;
    this.units = units;
    final XYDataset dataset = createSolutionDataset(vertex, x0);
    System.out.println("DATASET "+dataset.getSeriesCount());
    
    if(dataset.getSeriesCount() > 0) {
      final JFreeChart chart = createChart(dataset, this.vertex.label);
      final ChartPanel chartPanel = new ChartPanel(chart);
      chart.getTitle().setFont(new Font("Arial", Font.BOLD, 14));
      chart.getXYPlot().getDomainAxis().setLabelFont(new Font("Ariel", Font.BOLD, 11));
      chart.getXYPlot().getRangeAxis().setLabelFont(new Font("Ariel", Font.BOLD, 11));
      // The size of the panel depends on the size of the GraphDialog panel
      chartPanel.setPreferredSize(new java.awt.Dimension(550, 230));
      this.setLayout(new GridLayout(1, 1));
      add(chartPanel);
    }      
  }

  /**
   * Constructor
   *
   * Creates a new window with a plot inside of it
   * @param vertex is the name of the vertex that is being ploted
   * @param x0 is the initial value for the x-axis
   * @param xf is the final value for the x-axis
   * @param relation define the type of tokenList we are drawing
   */
  public PlotPanel(int x0, int xf, GraphCanvas gc) {
    final XYDataset dataset = createScoreDataset(x0, xf, gc);
    final JFreeChart chart = createChart(dataset);
    final ChartPanel chartPanel = new ChartPanel(chart);

    chart.getTitle().setFont(new Font("Arial", Font.BOLD, 14));
    chart.getXYPlot().getDomainAxis().setLabelFont(new Font("Ariel", Font.BOLD, 12));
    chart.getXYPlot().getRangeAxis().setLabelFont(new Font("Ariel", Font.BOLD, 12));

    // The size of the panel depends on the size of the GraphDialog panel
    chartPanel.setPreferredSize(new java.awt.Dimension(550, 230));
    this.setLayout(new GridLayout(1, 1));
    add(chartPanel);
  }

  /**
   * Creates the data to draw the plot. The data set includes x's and y's values
   * that would be used to draw the plot.
   *
   * @param x0 is the initial value for the x-axis
   * @param xf is the final value for the x-axis
   * @param relation define the type of tokenList we are drawing
   * @return the data set to draw the plot
   */
  private XYDataset createDataset(int x0, int xf) {
    final XYSeries series = new XYSeries("");
    for (int i = x0; i <= xf; i++) {
      if (vertex.getNonNegative() == true && vertex.equation.value.get(i - x0) < 0) {
        series.add(i, 0.0);
      } else {
        try {
          series.add(i, vertex.equation.value.get(i - x0));
        } catch(Exception e) {
          // Catch Exception
        };
      }
    }
    final XYSeriesCollection dataset = new XYSeriesCollection();
    dataset.addSeries(series);
    return dataset;
  }

  /**
   * This method creates a solution dataset used to create the solution graphs
   * @param taskName
   * @return
   */
  private XYDataset createSolutionDataset(Vertex vertex, int x0) {
    final XYSeries series = new XYSeries("");
    System.out.println("vertex.correctValues.size()::::"+vertex.correctValues.size());
    for(int i = 0; i < vertex.correctValues.size(); i++) {
      series.add(x0, vertex.correctValues.get(i));
      x0++;
    }
    final XYSeriesCollection dataset = new XYSeriesCollection();
    if(vertex.correctValues.size()>0)
      dataset.addSeries(series);    
    return dataset;
  }

  /**
   * This method creates a solution dataset used to create the score graph
   * @return
   */
  private XYDataset createScoreDataset(int x0, int xf, GraphCanvas gc) {
    final XYSeries series = new XYSeries("");
    for(int i = 0; i < gc.getScoreList().size(); i++) {
      series.add(x0, gc.getScoreList().get(i));
      x0++;
    }

    final XYSeriesCollection dataset = new XYSeriesCollection();
    dataset.addSeries(series);
    return dataset;
  }

  /**
   * Creates the chart with the plot on it
   * @param dataset is the data (x and y coordinates to be ploted)
   * @param vertex is the name of the vertex being ploted
   * @return the chart with the plot on it
   */
  private JFreeChart createChart(final XYDataset dataset, String vertex) {
    final JFreeChart chart = ChartFactory.createXYLineChart(
            vertex, // chart title
            "Time (in " + units + ")", // x axis label
            /*"# "*/vertex, // y axis label
            dataset, // data
            PlotOrientation.VERTICAL,
            false, // include legend
            true, // tooltips
            false // urls
            );
    chart.setBackgroundPaint(Color.white);
    // final StandardLegend legend = (StandardLegend) chart.getLegend();
    // legend.setDisplaySeriesShapes(true);
    final XYPlot plot = chart.getXYPlot();
    plot.setBackgroundPaint(Color.lightGray);
    plot.setDomainGridlinePaint(Color.white);
    plot.setRangeGridlinePaint(Color.white);
    final XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
    //renderer.setSeriesLinesVisible(0, true);
    renderer.setSeriesShapesVisible(0, false);
    //renderer.setSeriesShapesVisible(2, true);
    plot.setRenderer(renderer);
    final NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
    rangeAxis.setStandardTickUnits(NumberAxis.createStandardTickUnits());
    return chart;
  }

   /**
   * Creates the chart with the plot on it
   * @param dataset is the data (x and y coordinates to be ploted)
   * @param vertex is the name of the vertex being ploted
   * @return the chart with the plot on it
   */
  private JFreeChart createChart(final XYDataset dataset) {
    final JFreeChart chart = ChartFactory.createXYLineChart(
            "Here are your scores so far:", // chart title
            "Level", // x axis label
            "% Correct", // y axis label
            dataset, // data
            PlotOrientation.VERTICAL,
            false, // include legend
            true, // tooltips
            false // urls
            );
    chart.setBackgroundPaint(Color.white);
    // final StandardLegend legend = (StandardLegend) chart.getLegend();
    // legend.setDisplaySeriesShapes(true);
    final XYPlot plot = chart.getXYPlot();
    plot.setBackgroundPaint(Color.lightGray);
    plot.setDomainGridlinePaint(Color.white);
    plot.setRangeGridlinePaint(Color.white);
    final XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
    //renderer.setSeriesLinesVisible(0, true);
    renderer.setSeriesShapesVisible(0, false);
    //renderer.setSeriesShapesVisible(2, true);
    plot.setRenderer(renderer);
    final NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
    rangeAxis.setStandardTickUnits(NumberAxis.createStandardTickUnits());
    return chart;
  }
}
