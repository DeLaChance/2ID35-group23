/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package hist.debugView;

import hist.Histogram;
import hist.Position;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import javax.swing.JPanel;

/**
 *
 * @author huib
 */
class HistogramView extends JPanel
{
    protected Histogram histogram;
    
    public HistogramView(Histogram hist)
    {
        this.histogram = hist;
    }
    
    public int[] toScreenPosition(int x, int y)
    {
        int[] r = new int[2];
        r[0] = 10+(int)((x/(double)this.histogram.getMaxY())*(this.getWidth()-20));
        r[1] = 10+(int)((1-y/(double)this.histogram.getMaxY())*(this.getHeight()-20));
        
        return r;
    }
    
    @Override
    public void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        
        Graphics2D g2d = (Graphics2D)g;
        
        g2d.setColor(Color.WHITE);
        g2d.fillRect(10, 10, this.getWidth()-20, this.getHeight()-20);
        g2d.setColor(Color.GRAY);
        g2d.drawLine(10, this.getHeight()-10, this.getWidth()-10, 10);
        
        g2d.setColor(Color.BLACK);
        
        for(Position p : this.histogram.getDatapoints())
        {
            int[] screenpos = this.toScreenPosition(p.getX(), p.getY());
            
            g2d.drawOval(screenpos[0]-1, screenpos[1]-1, 3, 3);
        }
    }
}
