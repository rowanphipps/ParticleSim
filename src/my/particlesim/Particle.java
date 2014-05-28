/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package my.particlesim;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.util.ArrayList;
/**
 *
 * @author rowan
 */
public class Particle {
    private final double k = 0.025;     // coulomb's constant
    private final double g = 0.0005;    // universal gravitational constant
    private final double c = 0.5;   // coefficient of restitution for collisions with walls
    private final double cR = 0.999999;  // co. of r. for collisions with other particles
    private final short mass;
    private final short charge;
    private Color col;
    private double vx = 0;
    private double vy = 0;
    private double x;
    private double y;
    private final int size;
    private double dy = 0;
    private double dx = 0;
    private int id;
    private double dt;
//    public int height = 0;
//    public int width = 0;
    
    
    /**
     * Creates a new particle with a given mass and charge at Point p.
     * @param mass The mass of the particle
     * @param charge The charge of the particle
     * @param p The starting point of the particle
     */
    public Particle(int mass, int charge, Point p) {
        this.charge = (short) charge;
        this.mass = (short) mass;
        this.size = mass/5 + 1;
        this.setColor();
        this.x = p.x;
        this.y = p.y;
    }
    
    /**
     * Creates a new particle with a given mass and charge at the location (x, y).
     * @param mass The mass of the particle
     * @param charge The charge of the particle
     * @param x The initial x location of the particle
     * @param y The initial y location of the particle
     */
    public Particle(int mass, int charge, int x, int y) {
        this.charge = (short) charge;
        this.mass = (short) mass;
        this.size = mass/5 + 1;
        this.setColor();
        this.x = x;
        this.y = y;
    }
    
    /**
     * Sets the color of the particle.  White is no charge, red is positive,
     * and blue is negative.  The saturation is proportional to the charge of
     * the particle.
     */
    private void setColor(){
        if (charge == 0){
            this.col = Color.WHITE;
        }
        
        int temp = (int)(Math.abs(charge) * (255.0/100));
//        System.out.println(temp);
        if (charge < 0) {
            
            this.col = new Color(255-temp, 255-temp, 255);
        }
        else {
            this.col = new Color(255, 255-temp, 255 -temp);
        }
    }
    
    
    /**
     * Calculates physics for the particle
     * sets the dy and dx based on electrostatic attraction and the current velocity
     * @param others The other particles on the canvas
     * @param dt The physics delta t
     * @param width The current width of the world
     * @param height The current height of the world
     */
    public void update(ArrayList<Particle> others, int dt, int width, int height) {
        double T = (1.0 / dt);
        this.dt = T;
        for (int i = this.id+1; i < others.size(); i++) {
            Particle other = others.get(i);
            double d = (
                    Math.pow(this.x - other.x, 2) +
                            Math.pow(this.y-other.y, 2));
            
            if (false){//(this.hasCollided(other, d)) {
                
                /* 
                if two particles are inside each other then all this will do
                is to dampen the velocities of the particles.  It does not currently
                turn the particles into solid objects.  this is a problem.
                */
//                double nvx = -(this.mass*this.vx + other.mass*other.vx
//                        + other.getMass() * cR * (other.vx - this.vx))
//                        /(this.mass + other.getMass());
//                double nvy = -(this.mass*this.vy + other.mass*other.vy
//                        + other.getMass() * cR * (other.vy - this.vy))
//                        / (this.mass + other.getMass());
//                dx = (T * vx) * Math.signum(nvx);
//                dy = (T * vy) * Math.signum(nvy);
//                vy = nvy;
//                vx = nvx;
                return;
            }
            else {
//                System.out.println(d);
                double fy = this.k * (this.y-other.y) * ((this.charge * other.getCharge())
                        / Math.pow(d, 1.5));
                double fx = this.k * (this.x-other.x) * ((this.charge * other.getCharge())
                        / Math.pow(d, 1.5));
                
                this.vx += T * (fx/this.mass);
                this.vy += T * (fy/this.mass);
                dx += T * vx;
                dy += T * vy;
//                other.dx -= T * (fx/other.getMass()) * T;
//                other.dy -= T * (fy/other.getMass()) * T;
                other.applyForce(fx, fy, T);
                
                doGravity(d, other, T);
            }
        }
    }
    
    /**
     * Checks to see if this particle has collided with another.
     * This works best at very low speeds and still needs improvement
     * Does not do any ray tracing
     * @param other The other particle
     * @param d The distance between the two particles
     * @return True if the particles are overlapping
     */
    private boolean hasCollided (Particle other, double d){
        return (d <= (this.size + other.getSize())/2);
    } 
    
    /**
     * Simulates gravity and updates the values dx and dy appropriately
     * @param d The distance between the two particles
     * @param other The other particle
     * @param T The physics delta t
     */
    private void doGravity(double d, Particle other, double T) {
        double fy = this.g * (this.y-other.y) * (-(this.mass * other.getMass())
                / Math.pow(d, 1.5));
        double fx = this.g * (this.x-other.x) * (-(this.mass * other.getMass())
                / Math.pow(d, 1.5));
        
        
        this.vx += T * (fx/this.mass);
        this.vy += T * (fy/this.mass);
        dx += T * vx;
        dy += T * vy;
        other.applyForce(-fx, -fy, T);
//        other.dx -= T * (fx/other.getMass()) * T;
//        other.dy -= T * (fy/other.getMass()) * T;
        

//        this.vx += T * (fx/this.mass);
//        this.vy += T * (fy/this.mass);
//        this.x += vx * T;
//        this.y += vy * T;
    }
    
    /**
     * Updates the position of the particle
     * if this was done in the update method physics would be calculated incorrectly
     * and Third Law force pairs would not be equal
     * @param width the current width of the world
     * @param height the current height of the world
     */
    public void move(int width, int height) {
        this.x += dx;
        this.y += dy;
        this.edgeCollide(width, height);
        dx = 0;
        dy = 0; 
    }
    
    /**
     * Handles collisions with the edges of the world
     * this mostly works but particles can accumulate in the corners over time
     * 
     * @param width the current width of the world
     * @param height the current height of the world
     */
    private void edgeCollide(int width, int height){
        int r = size/2;
        if (x <= 0) {
            x = -x + r;
            vx = - vx *c;
        }
        else if (x >= width) {
            x = width - (x - width) -r;
            vx = -vx *c;
        }
        
        if (y <= 0) {
            y = -y + r;
            vy = - vy *c;
        }
        else if (y >= height) {
            y = height - r - (y - height);
            vy = -vy *c;
        }
    }
    
    /**
     * Draws this particle to a canvas
     * @param g the graphics object of the canvas
     */
    public void draw(Graphics g){
        g.setColor(this.col);
        g.fillOval((int)this.x-size/2, (int) this.y-size/2, size, size);
        
    }
    
    /**
     * Returns the mass of this particle 
     * @return mass
     */
    public int getMass(){
        return this.mass;
    }
    
    /**
     * Returns the charge of the particle.
     * @return charge
     */
    public int getCharge(){
        return this.charge;
    }
    
    /**
     * Returns the diameter of the particle
     * @return size
     */
    public int getSize() {
        return this.size;
    }
    
    /**
     * Sets the id of the particle
     */
    public void setId(int id){
        this.id = id;
    }
    
    /**
     * Applies a displacement to the particle 
     * @param fx dx
     * @param fy dy
     * @param dt change in time since last call
     */
    public void applyForce(double fx, double fy, double dt){
        this.vx += dt * (fx / this.mass);
        this.vy += dt * (fy / this.mass);
        this.dx += dt * vx;
        this.dy += dt * vy;
//        System.out.println(x + " " + y);
    }
    
    
}
