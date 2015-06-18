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
class CustomBranchingDistribution implements Distribution
{
    @Override
    public double chanceForMore(int n)
    {
        if(n==0)
            return 1.00;
        else if(n==1)
            return 0.50;
        else if(n<4)
            return 0.90;
        else if(n<15)
            return 0.80;
        else
            return 12/(double)n;
    }
}