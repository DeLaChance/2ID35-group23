/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package hist.debugView;

import hist.Histogram;
import java.awt.BorderLayout;
import java.awt.Container;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 *
 * @author huib
 */
public class MainView extends JFrame
{
    public MainView(Histogram hist)
    {
        super("Histogram Inspector");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(1200, 800);
        
        Container cp = this.getContentPane();
        cp.setLayout(new BorderLayout());
        cp.add(new HistogramView(hist), BorderLayout.CENTER);
        cp.add(this.getDetailView(), BorderLayout.EAST);
        
        this.setVisible(true);
    }
    
    private JPanel getDetailView()
    {
        JPanel d = new JPanel();
        d.add(new JLabel("Details"));
        return d;
    }
}
