//AJ Arnolie 6th 6/13/17
import java.util.*;
import java.awt.*;
//Class for bullet fired from player
//Bullet use not yet implemented
//Allows for easier collision detection and organization
public class Bullet{
   public double x,y,dx,dy,xPos,yPos;
         
   public Bullet(double xx, double yx, double dxx, double dyx){
   //Gets start location and direction
      xPos=xx;
      yPos=yx;
      dx=dxx;
      dy=dyx;
      x = xPos-PlatPanel.camerax;
      y = yPos-PlatPanel.cameray;
   } 
   private void move(){
   //Moves based on original direction
      xPos+=dx;
      yPos+=dy;
      x = xPos-PlatPanel.camerax;
      y = yPos-PlatPanel.cameray;
      
   }
   public void draw(Graphics2D g){
   //draw a rect in the location of the Bullet
      move();
      g.setColor(Color.RED);
      g.fillOval((int)x+2,(int)y+2,10,10);
   }
}