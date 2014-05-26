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
    private final double k = 0.025;//50
    private final double g = 0.0025;//25
    private final double c = 0.5;//0.7
    private final double cR = 0.2;
    public final short mass;
    private final short charge;
    private Color col;
    public double vx = 0;
    public double vy = 0;
    private double x;
    private double y;
    public final int size;
    private double dy = 0;
    private double dx = 0;
    public int height = 0;
    public int width = 0;
    
    public Particle(int mass, int charge, Point p) {
        this.charge = (short) charge;
        this.mass = (short) mass;
        this.size = mass/5 + 1;
        this.setColor();
        this.x = p.x;
        this.y = p.y;
    }
    
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
    
    
    
    public void update(ArrayList<Particle> others, int deltaT, int width, int height) {
        double T = (1.0 / deltaT);
        this.height = height;
        this.width = width;
        for (Particle other : others) {
            if (other != this) {
                
                
            
                double d = Math.sqrt(
                    Math.pow(this.x - other.x, 2) +
                    Math.pow(this.y-other.y, 2));
                
                if (this.hasCollided(other, d)) {
                    dx = -(this.mass*this.vx + other.mass*other.vx + other.mass *cR * (other.vx - this.vx))/(this.mass + other.mass);
                    dy = -(this.mass*this.vy + other.mass*other.vy + other.mass *cR * (other.vy - this.vy))/(this.mass + other.mass);
                }
                else {
//                System.out.println(d);
                    double fy = this.k * (this.y-other.y) * ((this.charge * other.charge)
                            / Math.pow(d, 3));
                    double fx = this.k * (this.x-other.x) * ((this.charge * other.charge)
                            / Math.pow(d, 3));


                    this.vx += T * (fx/this.mass);
                    this.vy += T * (fy/this.mass);
                    dx = T * vx;
                    dy = T * vy;

    //                this.vx += T * (fx/this.mass);
    //                this.vy += T * (fy/this.mass);
    //                this.x += vx * T;
    //                this.y += vy * T;
                    doGravity(d, other, T);
                }
            }
        }
        
//        System.out.println(vx + " " + vy);
    }
    
    private boolean hasCollided (Particle other, double d){
        return (d <= (this.size + other.size)/2);
    } 
    
    private void doGravity(double d, Particle other, double T) {
        double fy = this.g * (this.y-other.y) * (-(this.mass * other.mass)
                / Math.pow(d, 3));
        double fx = this.g * (this.x-other.x) * (-(this.mass * other.mass)
                / Math.pow(d, 3));
        
        
        this.vx += T * (fx/this.mass);
        this.vy += T * (fy/this.mass);
        dx += T * vx;
        dy += T * vy;
        

//        this.vx += T * (fx/this.mass);
//        this.vy += T * (fy/this.mass);
//        this.x += vx * T;
//        this.y += vy * T;
    }
    
    public void move() {
        this.x += dx;
        this.y += dy;
        this.edgeCollide(width, height);
    }
    
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
    
    public void draw(Graphics g){
        g.setColor(this.col);
        g.fillOval((int)this.x-size/2, (int) this.y-size/2, size, size);
        
    }
    
    
}
