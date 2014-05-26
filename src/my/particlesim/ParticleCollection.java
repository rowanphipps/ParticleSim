/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package my.particlesim;

import java.awt.Canvas;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

/**
 *
 * @author rowan
 */
public class ParticleCollection implements ActionListener {
    public ArrayList<Particle> particles = new ArrayList<Particle>();
    public Graphics g;
    
//    public void update() {
//        for (Particle p: particles){
//            p.update(particles);
//        }
//    }
    
    public ParticleCollection (Canvas c) {
        this.g = c.getGraphics();
    }
    
    public void draw (){ //(Graphics g){
        for (Particle p: particles){
            p.draw(g);
        }
    }
    
    public void actionPerformed (ActionEvent evt){
        System.out.println(evt);
        
        this.draw();
        
    }
    public void add(Particle p) {
        particles.add(p);
    }
    public int size() {
        return this.particles.size();
    }
    
//    private void clicked(MouseEvent evt) {
//        System.out.println("click!");
//        
//    }
}
