/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package my.particlesim;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

/**
 *
 * @author rowan
 */
public class MyCanvas extends java.awt.Canvas {
    private ArrayList<Particle> particles = new ArrayList<>();
    private Image buffer = null;
    private int width = 0;
    private int height = 0;
    private Graphics buffG;
    
//    public MyCanvas () {
//        
//    }
    
    /**
     * Paints the particles to the screen
     */
    @Override
    public void paint(Graphics g) {
//        this.buffer.getGraphics();
        this.buffG.setColor(Color.BLACK);
        this.buffG.fillRect(0, 0, width, height);
        particles.stream().forEach((p) -> {
            p.draw(g);
        });
    }
    
    /**
     * Updates physics for all of the particles.
     * @param deltaT Physics delta t
     * @param gravity Sets whether or not to simulate gravity
     * @param electro Sets whether or not to simulate electrostatic forces 
     * @param collision Sets whether or not particles collide
     */
    public void updateP(int deltaT, boolean gravity, boolean electro, boolean collision) {
        particles.parallelStream().forEach((p) -> {
            p.update(particles, deltaT, this.getWidth(), this.getHeight(),
                    gravity, electro, collision);
        });
        particles.stream().forEach((p) -> {
            p.move(this.getWidth(), this.getHeight());
        });
    } 
    
    /**
     * Updates graphics
     */
    @Override
    public void update(Graphics g) {
        if (this.width != this.getWidth() || this.height != this.getHeight()) {
            this.width = this.getWidth();
            this.height = this.getHeight();
//            this.buffer = createImage(width, height);
            this.buffer = new BufferedImage(this.getWidth(), this.getHeight(), BufferedImage.TYPE_INT_RGB);
            this.buffG = this.buffer.getGraphics();
        }
        
        this.paint(buffG);
        g.drawImage(buffer, 0, 0, this);
//        Graphics b = buffer.getGraphics();
//        b.setColor(Color.BLACK);
//        b.fillRect(0, 0, this.getWidth(), this.getHeight());
        
    }
    
    /**
     * Adds a particle to the canvas
     */
    public void add (Particle part) {
        part.setId(this.particles.size());
        this.particles.add(part);
    }
    
    /**
     * Removes all particles from the canvas
     */
    public void clear () {
        this.particles = new ArrayList<>();
    }
    
    
    public void log() {
        System.out.println(particles.size());
    }
}
