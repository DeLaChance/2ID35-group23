/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package hist.debugView;

import hist.Cell;
import hist.Histogram;
import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.GridLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

/**
 *
 * @author huib
 */
public class MainView extends JFrame implements CellObserver
{
    private Histogram histogram;
    
    public MainView(Histogram hist)
    {
        super("Histogram Inspector");
        this.histogram = hist;
        
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(1200, 800);
        
        HistogramView hv = new HistogramView(hist);
        hv.addCellObserver(this);
        
        Container cp = this.getContentPane();
        cp.setLayout(new BorderLayout());
        cp.add(hv, BorderLayout.CENTER);
        cp.add(this.getDetailView(), BorderLayout.EAST);
        
        this.setVisible(true);
    }
    
    private JPanel getDetailView()
    {
        JPanel d = new JPanel();
        d.setLayout(new GridLayout(2,1));
        d.add(this.getDetailTextArea());
        d.add(this.getControlPanel());
        return d;
    }
    
    private JTextArea detailTextArea;
    private Container getDetailTextArea()
    {
        if(this.detailTextArea == null)
            this.detailTextArea = new JTextArea();
        
        this.detailTextArea.setText(this.getDetailText());
        this.detailTextArea.setEditable(false);
        this.detailTextArea.setColumns(20);
        
        Container details = new JScrollPane(this.detailTextArea);
        Container c = new Container();
        c.setLayout(new BorderLayout());
        
        c.add(new JLabel("Details"), BorderLayout.NORTH);
        c.add(details, BorderLayout.CENTER);
        
        return c;
    }
    
    private Container controlPanel;
    private Container getControlPanel()
    {
        if(controlPanel == null)
            controlPanel = new ControlPanel(this.histogram);
        
        return controlPanel;
    }
    
    private String cellInfo = "No cell selected";
    private String getDetailText()
    {
        return "Histogram\n"+
                this.histogram.getDatapoints().size()+" datapoints\n"+
                "\n"+
                "Cell\n"+
                cellInfo;
    }
    private void updateCellInfo()
    {
        cellInfo = selectedCell.toString();
        
        this.detailTextArea.setText(this.getDetailText());
    }

    private Cell selectedCell;
    @Override
    public void setCell(Cell c)
    {
        this.selectedCell = c;
        updateCellInfo();
    }
}
