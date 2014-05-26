/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package my.particlesim;

import java.awt.Graphics;
import java.util.ArrayList;

/**
 *
 * @author rowan
 */
public class MyCanvas extends java.awt.Canvas {
    private ArrayList<Particle> particles = new ArrayList<Particle>();
    
//    public MyCanvas () {
////        super.Canvas();
//        this.createBufferStrategy(5);
//    }
    
    /**
     * Paints the particles to the screen
     */
    public void paint(Graphics g) {
        particles.stream().forEach((p) -> {
            p.draw(g);
        });
    }
    
    /**
     * Updates physics for all of the particles.
     * @param deltaT Physics delta t
     */
    public void updateP(int deltaT) {
        particles.parallelStream().forEach((p) -> {
            p.update(particles, deltaT, this.getWidth(), this.getHeight());
        });
        particles.parallelStream().forEach((p) -> {
            p.move(this.getWidth(), this.getHeight());
        });
    } 
    
    /**
     * Adds a particle to the canvas
     */
    public void add (Particle part) {
        this.particles.add(part);
    }
    
    /**
     * Removes all particles from the canvas
     */
    public void clear () {
        this.particles = new ArrayList<Particle>();
    }
}
