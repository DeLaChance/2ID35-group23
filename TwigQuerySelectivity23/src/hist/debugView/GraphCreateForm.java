/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package hist.debugView;

import java.awt.Container;
import java.awt.GridLayout;
import javax.swing.JLabel;
import javax.swing.JTextField;

/**
 *
 * @author huib
 */
class GraphCreateForm extends Container
{
    private newGraphListener listener;
    private final JTextField numLeafs;
    
    public GraphCreateForm(newGraphListener l)
    {
        listener = l;
        
        this.setLayout(new GridLayout(5,1));
        
        this.add(new JLabel("Generate new graph"));
        
        this.numLeafs = new JTextField();
        
        Container leafs = new Container();
        leafs.setLayout(new GridLayout(1,3));
        leafs.add(new JLabel("Leafs: "));
        leafs.add(this.numLeafs);
        leafs.add(new JLabel(">0"));
        this.add(leafs);
    }
}
