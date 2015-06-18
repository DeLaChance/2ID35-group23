/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package c1pGenerator;

/**
 *
 * @author huib
 */
class NonRandomDistribution implements Distribution
{
    private int v;
    
    public NonRandomDistribution(int i)
    {
        this.v= i;
    }

    @Override
    public double chanceForMore(int n)
    {
        if(n<v)
            return 1;
        else
            return 0;
    }
}
