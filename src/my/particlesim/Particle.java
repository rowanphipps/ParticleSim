/*
 * Copyright (C) 2014 Rowan Phipps
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/

package my.particlesim;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

/**
 * A charged particle
 * @author Rowan
 */
public class Particle {
    private final double k = 0.025;     // coulomb's constant
    private final double g = 0.0005;    // universal gravitational constant
    private final double c = 0.6;   // coefficient of restitution for collisions with walls
    private final double collisionElasticConstant = 0.001;  // co. of r. for collisions with other particles
    private final double coefRestitution = 0.6;
    private final double distanceExponent = 0.7;
    private final short mass;
    private final short charge;
    private Color col;
    private double vx = 0;
    private double vy = 0;
    private double x;
    private double y;
    private final int size; // diameter
    public double dy = 0;
    public double dx = 0;
    private int id;
    private double dt;
    private Image sprite;
//    private boolean collidedLast = false;
//    private boolean collidedThisFrame = false;
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
        this.buildSprite();
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
        this.buildSprite();
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
     * Creates the sprite that will be drawn for this particle base on the color and size
     */
    private void buildSprite() {
        this.sprite = new BufferedImage(this.size, this.size, BufferedImage.TYPE_4BYTE_ABGR);
        Graphics graph = this.sprite.getGraphics();
        graph.setColor(this.col);
        graph.fillOval(0, 0, size, size);
    }
    
    
    /**
     * Calculates physics for the particle
     * modifies vx and vy based on electrostatic attraction, gravity and collisions
     * @param others The other particles on the canvas
     * @param dt The physics delta t
     * @param width The current width of the world
     * @param height The current height of the world
     * @param gravity Sets whether or not to simulate gravity
     * @param electro Sets whether or not to simulate electrostatic forces
     * @param collision Sets whether or not particles collide
     */
    public void update(ArrayList<Particle> others, int dt, int width, int height,
            boolean gravity, boolean electro, boolean collision) {
        double T = (1.0 / dt);
        this.dt = T;
        boolean collided;
        for (int i = this.id+1; i < others.size(); i++) {
            Particle other = others.get(i);
            double d = Math.sqrt(
                    Math.pow(this.x - other.x, 2) +
                            Math.pow(this.y-other.y, 2));
            collided = this.hasCollided(other, d);
           
            
            if (collision && collided) {
//                this.vx += 0.00001;
//                this.vy += 0.00001;
                this.collide(other, d);
                other.collide(this, d);
            }
             
            if (gravity && !collided) {
                doGravity(d, other, T);
            }
            if (electro && !collided) {
                doElectro(d, other, T);
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
     * Simulates gravity and updates the values vx and vy appropriately
     * @param d The distance between the two particles
     * @param other The other particle
     * @param T The physics delta t
     */
    private void doGravity(double d, Particle other, double T) {
        double fy = this.g * (this.y-other.y) * (-(this.mass * other.getMass())
                / Math.pow(d, 2));
        double fx = this.g * (this.x-other.x) * (-(this.mass * other.getMass())
                / Math.pow(d, 2));
     
        this.applyForce(fx, fy, dt);
        other.applyForce(-fx, -fy, dt);
    }
    
    /**
     * Simulates electro static attraction
     * @param d The distance between the two particles
     * @param other The other particle
     * @param T The physics delta t
     */
    private void doElectro(double d, Particle other, double T) {
        double fy = this.k * (this.y-other.y) * ((this.charge * other.getCharge())
                / Math.pow(d, 2));
        double fx = this.k * (this.x-other.x) * ((this.charge * other.getCharge())
                / Math.pow(d, 2));

        this.vx += T * (fx/this.mass);
        this.vy += T * (fy/this.mass);
//        dx += T * vx;
//        dy += T * vy;
        
        other.applyForce(-fx, -fy, T);
    }
    
    /**
     * Handles collisions between two particles
     * @param other the other particle
     * @param d the distance between them
     */
    public void collide(Particle other, double d) {
        /*
        double nvx = (this.mass*this.vx + other.mass*other.getVx()
                + other.getMass() * coefRestitution * (other.getVx() - this.vx))
                /(this.mass + other.getMass());
                
                double nvy = (this.mass*this.vy + other.getMass()*other.getVy()
                + other.getMass() * coefRestitution * (other.getVy() - this.vy))
                / (this.mass + other.getMass());
                
                
                double otherX = (other.getMass()*other.getVx() + this.mass*this.vx
                + this.getMass() * coefRestitution * (this.getVx() - other.getVx()))
                /(other.getMass() + this.getMass());
                
                double otherY = (other.getMass()*other.getVy() + this.mass*this.vy
                + this.getMass() * coefRestitution * (this.vy - other.getVy()))
                / (other.getMass() + this.mass);
                
                if (this.vx < other.getVx()) {
                this.dx += (nvx/(nvx + otherX)) * (other.x - this.x);
                other.dx += (otherX/(nvx + otherX)) * (other.x - this.x);
                }
                else {
                this.dx += (nvx/(nvx + otherX)) * (this.x - other.x);
                other.dx += (otherX/(nvx + otherX)) * (this.x - other.x);
                }
                
                
                
                if (this.vy < other.getVy()) {
                this.dy += (nvy/(nvy + otherY)) * (other.y - this.y);
                other.dy += (otherY/(nvy + otherY)) * (other.y - this.y);
                }
                else {
                this.dy += (nvy/(nvy + otherY)) * (this.y - other.y);
                other.dy += (otherY/(nvy + otherY)) * (this.y - other.y);
                }
                
                
                this.setV(nvx, nvy);
                other.setV(otherX, otherY);
                */
                double nvx = -(this.mass*this.vx + other.mass*other.vx
                        + other.getMass() * collisionElasticConstant * (other.vx - this.vx))
                        /(this.mass + other.getMass());
                double nvy = -(this.mass*this.vy + other.mass*other.vy
                        + other.getMass() * collisionElasticConstant * (other.vy - this.vy))
                        / (this.mass + other.getMass());
//                dx = (dt * vx) * Math.signum(nvx);
//                dy = (dt * vy) * Math.signum(nvy);
                vy = nvy;
                vx = nvx;
                
          // wrong
                  
        double fy = this.collisionElasticConstant * (this.y-other.y) * ((this.mass * other.getMass())
                / Math.pow(d, distanceExponent));
        double fx = this.collisionElasticConstant * (this.x-other.x) * ((this.mass * other.getMass())
                / Math.pow(d, distanceExponent));
     
        this.applyForce(fx, fy, dt);
//        other.applyForce(-fx, -fy, dt);

    }
    
    /**
     * Updates the position of the particle
     * if this was done in the update method physics would be calculated incorrectly
     * and Third Law force pairs would not be equal
     * @param width the current width of the world
     * @param height the current height of the world
     */
    public void move(int width, int height) {
        this.x += vx*dt + dx;
        this.y += vy*dt + dy;
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
        if (x <= 0+r) {
            x = 0 + r;
            vx = - vx *c;
        }
        else if (x >= width-r) {
            x = width -r;
            vx = -vx *c;
        }
        
        if (y <= 0+r) {
            y = + r;
            vy = - vy *c;
        }
        else if (y >= height-r) {
            y = height - r;
            vy = -vy *c;
        }
    }
    
    /**
     * Draws this particle to a canvas
     * @param g the graphics object of the canvas
     */
    public void draw(Graphics g){
//        g.setColor(this.col);
//        g.fillOval((int)this.x-size/2, (int) this.y-size/2, size, size);
        g.drawImage(sprite, (int)this.x-size/2, (int) this.y-size/2, null);
        
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
     * @param fx fx
     * @param fy fy
     * @param dt change in time since last call
     */
    public void applyForce(double fx, double fy, double dt){
        this.vx += dt * (fx / this.mass);
        this.vy += dt * (fy / this.mass);
//        System.out.println(x + " " + y);
    } 
    
    /**
     * Sets the velocity of the particle
     * @param vx x component of velocity
     * @param vy y component of velocity
     */
    public void setV(double vx, double vy) {
        this.vx = vx;
        this.vy = vy;
    }  
    
    public double getVy() {
        return this.vy;
    }
    
    public double getVx() {
        return this.vx;
    }
    
    public double getX() {
        return this.x;
    }
    
    public double getY() {
        return this.y;
    }
}
