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
public class CustomBranchingDistribution implements Distribution
{
    @Override
    public double chanceForMore(int n)
    {
        switch(n)
        {
            case 0:
                return 1.00;
            case 1:
                return 0.56;
            case 2:
                return 0.76;
            case 3:
                return 0.61;
            case 4:
                return 0.61;
            case 5:
                return 0.73;
            case 6:
                return 0.84;
            case 7:
                return 0.36;
            case 8:
                return 0.32;
            case 9:
                return 0.64;
            case 10:
                return 0.56;
            case 11:
                return 0.64;
            case 12:
                return 0.79;
            case 13:
                return 0.72;
            case 14:
                return 0.79;
            case 15:
                return 0.62;
            default:
                return 0.9;
        }
    }
}