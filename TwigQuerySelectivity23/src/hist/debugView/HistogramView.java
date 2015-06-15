/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package hist.debugView;

import hist.Equidepth;
import hist.Histogram;
import hist.Position;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.HashSet;
import java.util.Set;
import javax.swing.JPanel;

/**
 *
 * @author huib
 */
class HistogramView extends JPanel implements MouseListener, MouseMotionListener
{
    protected Histogram histogram;
    private Set<CellObserver> observers = new HashSet<>();
    
    public HistogramView(Histogram hist)
    {
        super();
        this.histogram = hist;
        
        this.addMouseListener(this);
        this.addMouseMotionListener(this);
    }
    
    public void addCellObserver(CellObserver co)
    {
        this.observers.add(co);
    }
    
    public int[] toScreenPosition(int x, int y)
    {
        int[] r = new int[2];
        r[0] = 10+(int)((x/(double)this.histogram.getMaxY())*(this.getWidth()-20));
        r[1] = 10+(int)((1-y/(double)this.histogram.getMaxY())*(this.getHeight()-20));
        
        return r;
    }
    
    public int[] toScreenPosition_cell(int x, int y)
    {
        int[] r = new int[2];
        
        int[] p1 = this.toScreenPosition((int)(0.55*histogram.getMaxY()), (int)(0.45*histogram.getMaxY()));
        int[] p2 = this.toScreenPosition(histogram.getMaxY(), 0);
        
        int s = this.histogram.getCellSize();
        double normY = (y/(double)s%1);
        if(normY == 0)
            normY=1;
        
        r[0] = p1[0]+(int)((   x/(double)s%1 )*(p2[0]-p1[0]-10));
        r[1] = p1[1]+(int)((1-  normY        )*(p2[1]-p1[1]-10));
        
        return r;
    }
    
    public int[] fromScreenPosition(int x, int y)
    {
        int[] p = new int[2];
        p[0] = (int)(this.histogram.getMaxY()*((x-10)/(double)(this.getWidth()-20)));
        p[1] = (int)(this.histogram.getMaxY()*(1-(y-10)/(double)(this.getHeight()-20)));
        
        return p;
    }
    
    @Override
    public void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        
        Graphics2D g2d = (Graphics2D)g;
        RenderingHints rh = new RenderingHints(
             RenderingHints.KEY_TEXT_ANTIALIASING,
             RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2d.setRenderingHints(rh);
        
        g2d.setColor(Color.WHITE);
        g2d.fillRect(10, 10, this.getWidth()-20, this.getHeight()-20);
        g2d.setColor(Color.GRAY);
        g2d.drawLine(10, this.getHeight()-10, this.getWidth()-10, 10);
        
        this.drawCell(g2d);
        
        g2d.setColor(new Color(224, 224, 224));
        for(int h=0; h<histogram.getMaxY(); h+=histogram.getCellSize())
        {
            int[] p1 = toScreenPosition(0, h);
            int[] p2 = toScreenPosition(h+histogram.getCellSize(), h);
            g2d.drawLine(p1[0], p1[1], p2[0], p2[1]);
        }
        for(int v=0; v<histogram.getMaxY(); v+=histogram.getCellSize())
        {
            int[] p1 = toScreenPosition(v, v-histogram.getCellSize());
            int[] p2 = toScreenPosition(v, histogram.getMaxY());
            g2d.drawLine(p1[0], p1[1], p2[0], p2[1]);
        }
        
        g2d.setColor(Color.BLACK);
        
        for(Position p : this.histogram.getDatapoints())
        {
            int[] screenpos = this.toScreenPosition(p.getX(), p.getY());
            
            g2d.drawOval(screenpos[0]-1, screenpos[1]-1, 2, 2);
        }
    }
    
    private void drawCell(Graphics2D g)
    {
        if(!this.histogram.isInGrid(selectX, selectY))
            return;
        
        g.setColor(Color.GREEN.brighter());
        for(Equidepth.Bar bar : this.histogram.getCell(selectX, selectY).getBars())
        {
            int[] q1 = this.toScreenPosition_cell(bar.minX, bar.maxY);
            int[] q2 = this.toScreenPosition_cell(bar.maxX, bar.minY);
            g.drawRect(q1[0], q1[1], q2[0]-q1[0], q2[1]-q1[1]);
        }
        
        g.setColor(Color.BLACK);
        
        int[] p1 = this.toScreenPosition((int)(0.55*histogram.getMaxY()), (int)(0.45*histogram.getMaxY()));
        int[] p2 = this.toScreenPosition(histogram.getMaxY(), 0);
        g.drawRect(p1[0], p1[1], p2[0]-p1[0]-10, p2[1]-p1[1]-10);
        
        g.setColor(Color.BLACK);
        for(Position p : this.histogram.getCell(selectX, selectY).getDatapoints())
        {
            int[] screenpos = this.toScreenPosition_cell(p.getX(), p.getY());
            
            g.drawOval(screenpos[0]-1, screenpos[1]-1, 2, 2);
        }
        
        g.setColor(Color.LIGHT_GRAY);
        int s = histogram.getCellSize();
        p1 = this.toScreenPosition(s*(selectX/s  ), s*(selectY/s+1));
        p2 = this.toScreenPosition(s*(selectX/s+1), s*(selectY/s  ));
        g.fillRect(p1[0], p1[1], p2[0]-p1[0], p2[1]-p1[1]);
    }
    
    private int selectX=0, selectY=0;
    private void selectCell(int x, int y)
    {
        if(this.histogram.isInGrid(x, y))
        {
            this.selectX = x;
            this.selectY = y;
        }
        
        for(CellObserver co : this.observers)
            co.setCell(this.histogram.getCell(selectX, selectY));
    }

    @Override
    public void mouseClicked(MouseEvent e)
    {
        int[] p = this.fromScreenPosition(e.getX(), e.getY());
        this.selectCell(p[0], p[1]);
        
        this.repaint();
    }

    @Override
    public void mousePressed(MouseEvent e)
    {
        
    }

    @Override
    public void mouseReleased(MouseEvent e)
    {
        
    }

    @Override
    public void mouseEntered(MouseEvent e)
    {
        
    }

    @Override
    public void mouseExited(MouseEvent e)
    {
        
    }

    @Override
    public void mouseDragged(MouseEvent e)
    {
        
    }

    @Override
    public void mouseMoved(MouseEvent e)
    {
        
    }

    void reset(Histogram histogram)
    {
        this.histogram = histogram;
        this.selectX=0;
        this.selectY=0;
        this.repaint();
    }
}
