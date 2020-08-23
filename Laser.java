//AJ Arnolie 6th 6/13/17
import java.util.*;
import java.awt.geom.*;
import java.awt.*;

//Extra game element recently added
//Laser restart player if the player intersects its path if it is on
public class Laser {
   public int[][] map;
   public int x, y, dir, time;
   public double delayOn, delayOff;
   public boolean isOn;
   public Line2D line;
   
   public Laser(int[][] mapp, int xx, int yy) {
     //Gets map for collision
     //This allows collision distance to be recalculated based on moving blocks
      map = mapp;
      x=xx;
      y=yy;
      //Has an on switch which can be flipped using delay method
      isOn = true;
      dir = 0;
      determineDirection();
      delayOn = 1;
      delayOff = 1; 
      time = 0;
   }

   //Returns the line of travel of the laser
   public Line2D getLine() {
      return line;
   }
   
   //Sets the time(approximately seconds) for how long the laser will be on and then off
   public void setDelay(double on, double off) {
      delayOn = on;
      delayOff = off;
   }
   
   //Updates status of isOn based on delay
   public void updateDelay() {
      time++;
      if (time == (int)(delayOn * 60) && isOn == true) {
         isOn = false;
         time = 0;
      }
      if (time == (int)(delayOff * 60) && isOn == false) {
         isOn = true;
         time = 0;
      }
   }

   //Method for determining the distance the laser should travel based on tile map
   //May need to change this function in the future to allow for more versatility
   private void determineDirection() {
      int ex = imageHandler.x;
      int ey = imageHandler.y;
      if (x >= 1 && y >= 1 && x < map.length - 1 && y < map[x].length - 1) {
      //Determines direction of laser based on surrounding map elements
         if (map[x+1][y] == 1 || map[x+1][y] == 4) {
            dir = 2;
         }
         else if (map[x][y-1] == 1 || map[x][y-1] == 4) {
            dir = 3;
         }
         else if (map[x][y+1] == 1 || map[x][y+1] == 4) {
            dir = 4;
         }
         else if (map[x-1][y] == 1 || map[x-1][y] == 4) {
            dir = 1;
         }

         //Based on direction, loops through checking each consecutive block until it hits a wall(solidBlock)
         int size = mapHandler.size;
         if (dir == 2) {
            int tempk = x - 1;
            int tempi = y;
            while(map[tempk][tempi] == 0 && tempk > 0) {
               tempk--;
            }
            Line2D l1 = new Line2D.Float(y*size-ex+size/2, x*size-ey, y*size-ex+size/2, tempk*size-ey+10);
            line = l1;
         }

         if (dir == 1) {
            int tempk = x + 1;
            int tempi = y;
            while(map[tempk][tempi] == 0 && tempk < map.length - 1) {
               tempk++;
            }
            Line2D l1 = new Line2D.Float(y*size-ex+size/2, x*size-ey+size, y*size-ex+size/2, tempk*size-ey+size-10);
            line = l1;
         }

         if (dir == 4) {
            int tempk = x;
            int tempi = y - 1;
            while(map[tempk][tempi] == 0 && tempi > 0) {
               tempi--;
            }
            Line2D l1 = new Line2D.Float(y*size-ex, x*size-ey+size/2, tempi*size-ex+10, x*size-ey+size/2);
            line = l1;
         }
            
         if (dir == 3) {
            int tempk = x;
            int tempi = y + 1;
            while(map[tempk][tempi] == 0 && tempi < map[0].length - 1) {
               tempi++;
            }
            Line2D l1 = new Line2D.Float(y*size-ex+size, x*size-ey+size/2, tempi*size-ex+size-10, x*size-ey+size/2);
            line = l1;
         }      
      }        
   }


   //Method to draw the laser based on the camera position and the tile map
   //Need to implement the determineDirection() method in here
   public void drawLaser(Graphics2D g2) {
      int ex = imageHandler.x;
      int ey = imageHandler.y;
      g2.setStroke(new BasicStroke(10));
      g2.setColor(Color.RED);
      int size = mapHandler.size;

      if (isOn) {
      //Determines the direction of the laser fire
         if (dir == 2) {
            int tempk = x - 1;
            int tempi = y;
            while(map[tempk][tempi] == 0 && tempk > 0) {
               tempk--;
            }
            Line2D l1 = new Line2D.Float(y*size-ex+size/2, x*size-ey, y*size-ex+size/2, tempk*size-ey+10);
            g2.draw(l1);
            line = l1;
            
         }

         if (dir == 1) {
            int tempk = x + 1;
            int tempi = y;
            while(map[tempk][tempi] == 0 && tempk < map.length - 1) {
               tempk++;
            }
            Line2D l1 = new Line2D.Float(y*size-ex+size/2, x*size-ey+size, y*size-ex+size/2, tempk*size-ey+size-10);
            line = l1;
            g2.draw(l1);
         }

         if (dir == 4) {
            int tempk = x;
            int tempi = y - 1;
            while(map[tempk][tempi] == 0 && tempi > 0) {
               tempi--;
            }
            Line2D l1 = new Line2D.Float(y*size-ex, x*size-ey+size/2, tempi*size-ex+10, x*size-ey+size/2);
            line = l1;
            g2.draw(l1);
         }
            
         if (dir == 3) {
            int tempk = x;
            int tempi = y + 1;
            while(map[tempk][tempi] == 0 && tempi < map[0].length - 1) {
               tempi++;
            }
            Line2D l1 = new Line2D.Float(y*size-ex+size, x*size-ey+size/2, tempi*size-ex+size-10, x*size-ey+size/2);
            line = l1;
            g2.draw(l1);
         }
      }
   }
}