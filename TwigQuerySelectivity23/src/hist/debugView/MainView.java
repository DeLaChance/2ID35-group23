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
        d.add(new JLabel("Histogram\n"+this.histogram.getDatapoints().size()+" points"));
        d.add(this.getCellInfo());
        return d;
    }
    
    private JLabel cellInfo = new JLabel("Cell");
    private Container getCellInfo()
    {
        return cellInfo;
    }
    private void updateCellInfo()
    {
        cellInfo.setText("Cell\n"+selectedCell.getDatapoints().size()+" points");
    }

    private Cell selectedCell;
    @Override
    public void setCell(Cell c)
    {
        this.selectedCell = c;
        updateCellInfo();
    }
}
