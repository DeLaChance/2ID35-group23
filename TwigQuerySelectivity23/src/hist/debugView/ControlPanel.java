/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package hist.debugView;

import c1pGenerator.C1PMatrixGenerator;
import estimation.QueryPoint;
import hist.Histogram;
import hist.PositionList;
import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Timer;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

/**
 *
 * @author huib
 */
class ControlPanel extends JPanel implements ActionListener
{
    private Histogram histogram;
    private JTextArea results;
    private int queryCount=100;
    
    public ControlPanel(Histogram histogram)
    {
        super();
        this.histogram = histogram;
        
        this.setLayout(new BorderLayout());
        this.add(new JLabel("Query stuff"), BorderLayout.NORTH);
        
        results = new JTextArea();
        results.setColumns(20);
        this.add(new JScrollPane(results), BorderLayout.CENTER);
        
        JButton runQ = new JButton("Run queries");
        runQ.addActionListener(this);
        
        JTextField queryCount = new JTextField();
        queryCount.setText(""+this.queryCount);
        queryCount.addKeyListener(new KeyListener()
                {
                    final JTextField count = queryCount;

                    @Override
                    public void keyTyped(KeyEvent e){}

                    @Override
                    public void keyPressed(KeyEvent e){}

                    @Override
                    public void keyReleased(KeyEvent e)
                    {
                        try
                        {
                            updateQueryCount(Integer.parseInt(this.count.getText()));
                        }
                        catch(NumberFormatException ex)
                        {
                            updateQueryCount(1);
                            System.out.println("NumberFormatException, queryCount assumed to be 1, as long as the number doesn't make sense");
                        }
                    }
                });
        
        Container queryControl = new Container();
        queryControl.setLayout(new GridLayout(1,2));
        queryControl.add(queryCount);
        queryControl.add(runQ);
        
        this.add(queryControl, BorderLayout.SOUTH);
    }
    
    private void updateQueryCount(int count)
    {
        System.out.println("Updated queryCount to "+count);
        this.queryCount = count;
    }

    @Override
    public void actionPerformed(ActionEvent e)
    {
        double avgEst=0, avgDet=0, avgEstTime=0, avgDetTime=0;
        int fails = 0;
        
        for(int i=0; i<queryCount; i++)
        {
            try
            {
                int[] q = C1PMatrixGenerator.randPosition(this.histogram.getMaxY());
                
                QueryPoint qp = new QueryPoint(q[0], q[1]);
                PositionList<QueryPoint> query = new PositionList<>();
                query.add(qp);

                long time1 = System.nanoTime();
                int est = this.histogram.estimateCount(query);
                long time2 = System.nanoTime();
                int det = this.histogram.determineCount(query);
                long time3 = System.nanoTime();

                String queryString = "{";
                for(QueryPoint qpoint : query)
                    queryString += "("+qpoint.getX()+","+qpoint.getY()+")";
                queryString += "}";

                this.results.setText(this.results.getText()+"\nQuery: "+queryString+"\n"
                        + " Estimate:\t"+est+" ("+((time2-time1)/1000f)+" us)\n"
                        + " Exact:\t"   +det+" ("+((time3-time2)/1000f)+" us)\n");

                avgEst += est;
                avgDet += det;
                avgEstTime += (time2-time1)/1000f;
                avgDetTime += (time3-time2)/1000f;
            }
            catch(Exception ex)
            {
                System.err.println(ex.getMessage());
                fails++;
            }
        }
        
        if(queryCount > 0)
        {
            avgEst     /=queryCount-fails;
            avgDet     /=queryCount-fails;
            avgEstTime /=queryCount-fails;
            avgDetTime /=queryCount-fails;
        }
        
        
        this.results.setText(this.results.getText()+"\nAverages over "+queryCount+" queries:\n"
                + " Estimate:\t"+Math.round(avgEst*100)/100f+" ("+Math.round(avgEstTime*1000)/1000f+" us)\n"
                + " Exact:\t"   +Math.round(avgDet*100)/100f+" ("+Math.round(avgDetTime*1000)/1000f+" us)\n"
                + (fails>0? " Fails:\t"   +fails +"\n" : ""));
    }
}
