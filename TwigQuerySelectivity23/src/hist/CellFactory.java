/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package hist;

/**
 *
 * @author huib
 */
interface CellFactory<T>
{
    public T create(boolean isOnDiagonal);
}
