/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package c1pGenerator;

/**
 * Models a randomly distributed integer, X.
 * 
 * @author huib
 */
public interface Distribution
{
    // return chance X>n given X>=n
    public double chanceForMore(int n);
}
