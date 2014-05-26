/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package my.particlesim;

import java.awt.Graphics;
import java.util.ArrayList;
import java.util.Iterator;

/**
 *
 * @author rowan
 */
public class MyCanvas extends java.awt.Canvas {
    public ArrayList<Particle> particles = new ArrayList<Particle>();
    
//    public MyCanvas () {
////        super.Canvas();
//        this.createBufferStrategy(5);
//    }
    
    public void paint(Graphics g) {
        for (Particle p : particles) {
            p.draw(g);
        }
    }
    
    public void updateP(int deltaT) {
//        for (Particle p : particles) {
//            p.update(particles, deltaT, this.getWidth(), this.getHeight());
//        }
        
        for (Iterator<Particle> it = particles.iterator(); it.hasNext();) {
            Particle p = it.next();
            p.update(particles, deltaT, this.getWidth(), this.getHeight());
        }
        for (Iterator<Particle> it = particles.iterator(); it.hasNext();) {
            Particle p = it.next();
            p.move();
        }
    }
    
    
}
