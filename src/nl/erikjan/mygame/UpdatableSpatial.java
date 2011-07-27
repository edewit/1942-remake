/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package nl.erikjan.mygame;

/**
 *
 * @author edewit
 */
public interface UpdatableSpatial {
    void update(float interpolation);
    boolean isAlive();
}
