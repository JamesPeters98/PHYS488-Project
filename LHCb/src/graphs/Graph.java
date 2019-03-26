package graphs;
import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.JPanel;

import org.apache.commons.math3.linear.RealVector;

import com.orsoncharts.Chart3DPanel;
import com.orsoncharts.Colors;
import com.orsoncharts.Chart3D;
import com.orsoncharts.Chart3DFactory;
import com.orsoncharts.data.xyz.XYZDataset;
import com.orsoncharts.data.xyz.XYZSeries;
import com.orsoncharts.data.xyz.XYZSeriesCollection;
import com.orsoncharts.graphics3d.swing.DisplayPanel3D;
import com.orsoncharts.plot.XYZPlot;
import com.orsoncharts.renderer.xyz.ScatterXYZRenderer;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * A demo of a 3D line chart.
 */
@SuppressWarnings("serial")
public class Graph extends JFrame {
	
	RealVector[][] lines;
	RealVector[] a,d;

    /**
     * Creates a new test app.
     *
     * @param title  the frame title.
     */
    public Graph(String title, RealVector[][] lines, RealVector[] a, RealVector[] d) {
        super(title);
        this.lines = lines;
        this.a = a;
        this.d = d;
        addWindowListener(new ExitOnClose());
        getContentPane().add(createDemoPanel());
        
        pack();
        setVisible(true);
    }

    private XYZDataset<String> createRawDataset() {
        XYZSeriesCollection<String> dataset = new XYZSeriesCollection<String>();
        for(int i = 0; i < lines.length; i++) {
	        XYZSeries<String> series = new XYZSeries<String>("Series "+i);
	        for(RealVector p : lines[i]) {
	        	series.add(p.getEntry(0),p.getEntry(1),p.getEntry(2));
	        }
	        dataset.add(series);
        }
        
        if(a != null) {
        	for(int i = 0; i < a.length; i++) {
		        XYZSeries<String> series = new XYZSeries<String>("Line "+i);
		        int steps = 1000;
		        int range = 1;
		        for(int j = -steps/2; j <= steps/2; j++) {
		        	series.add(
			        		a[i].getEntry(0)+d[i].getEntry(0)*range*(2*j)/steps,
			        		a[i].getEntry(1)+d[i].getEntry(1)*range*(2*j)/steps,
			        		a[i].getEntry(2)+d[i].getEntry(2)*range*(2*j)/steps);
		        }
		        
		        dataset.add(series);
	        }
        }
        return dataset;
    }
    
    
    private Chart3D createScatterChart(XYZDataset<?> dataset) {
        Chart3D chart = Chart3DFactory.createScatterChart("Raw Data", "", dataset, "X", "Y", "Z");
        chart.setChartBoxColor(new Color(255, 255, 255, 128));
        XYZPlot plot = (XYZPlot) chart.getPlot();
        plot.getXAxis().setRange(-0.024, 0.024);
        plot.getYAxis().setRange(-0.024, 0.024);
        plot.getZAxis().setRange(-0.1, 0.6);
        
        ScatterXYZRenderer renderer = (ScatterXYZRenderer) plot.getRenderer();
        renderer.setSize(0.02);
        renderer.setColors(Colors.createIntenseColors());
        return chart;    
    }
    
  
    
    /**
     * Returns a panel containing the content for the demo.  This method is
     * used across all the individual demo applications to allow aggregation 
     * into a single "umbrella" demo (OrsonChartsDemo).
     * 
     * @return A panel containing the content for the demo.
     */
    public JPanel createDemoPanel() {
        Panel content = new Panel(new BorderLayout());
        Dimension size = new Dimension(1280, 720);
        content.setPreferredSize(size);
        
        //Raw Data
        XYZDataset dataset = createRawDataset();
        Chart3D chart = createScatterChart(dataset);
        Chart3DPanel chartPanel = new Chart3DPanel(chart);
        content.setChartPanel(chartPanel);
        chartPanel.zoomToFit(size);
        content.add(new DisplayPanel3D(chartPanel));
        return content;
    }

//    /**
//     * Starting point for the app.
//     *
//     * @param args  command line arguments (ignored).
//     */
//    public static void main(String[] args) {
//    	Graph app = new Graph(
//                "OrsonCharts: XYZLineChart3DDemo2.java");
//        app.pack();
//        app.setVisible(true);
//    }
    
    public class ExitOnClose extends WindowAdapter {

        @Override
        public void windowClosing(WindowEvent e) {
            System.exit(0);
        }
        
    }
}

