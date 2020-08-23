//AJ Arnolie 6th 6/13/17

import java.util.*;
import java.awt.geom.*;
import java.awt.*;

//This class creates a turret that follows the player and fires bullets
public class Turret{
   public int[][] map;
   public Character player;
   public int x, y, x2, y2, angle, time;
   public boolean isOn,isFollowing,laserBeam;
   public ArrayList<Bullet> bulls;
   public ArrayList<Integer> solidBlocks;
   
   public Turret(int[][] mapp,int xx, int yy) {
   //Array tells the bullets when to stop moving and remove
      solidBlocks = new ArrayList<Integer>();
      solidBlocks.add(1);
      solidBlocks.add(4);
      solidBlocks.add(5);
      solidBlocks.add(8);

      map = mapp;
      x = yy;
      y = xx; 
      angle = 270;
      isOn = true;
      isFollowing = true;
      laserBeam = false;
      bulls = new ArrayList<Bullet>();
      time = 0;
   }

   //Adds another bullet and sets it on a dy and dx based on the angle of the turret
   public void fire() {
      int size = mapHandler.size;
      bulls.add(new Bullet(x*size+size/2,y*size+size/2,Math.cos(Math.toRadians(angle))*8,-Math.sin(Math.toRadians(angle))*8));
   }

   //Calculates the direction that the turret needs to rotate based on the position of the player
   public void checkAngle(Character p) {
      player = p;
      int size = mapHandler.size;
      double width = p.myXPos+p.width/2-(x*size+size/2);
      double height = p.myYPos+p.height/2-(y*size+size/2);
      double ang = Math.atan2(-height, width);
      ang = Math.toDegrees(ang);

      if (ang < 0) {
         ang += 360;
      }
      if (ang > 180) {
         if (ang < angle) {
            angle -= 4;
         }
         else if (ang >= angle) {
            angle += 4;
         }
      }
      else{
         if (angle < 270 && angle > ang) {
            angle -= 4;
         }
         if (angle < 270 && angle <= ang) {
            angle += 4;
         }
         if (angle > 270) {
            angle += 4;
         }
      }
      angle = angle % 360;
   }

   //Returns the current angle of the turret
   public int getAngle() {
      return angle;
   }

   //Changes the angle of the turret, fires new bullets, and draws everything
   public void drawTurret(Graphics2D g2, Character p) {
      checkAngle(p);
      time++;
      if (time % 10 == 0) {
         fire();
      }
      
      for (int i = 0; i < bulls.size(); i++) {
         boolean test = false;
         for (int v = 0; v < solidBlocks.size(); v++) {
            if (blockType(bulls.get(i).xPos, bulls.get(i).yPos) == solidBlocks.get(v)) {
               test = true;
            } 
         }
         if (test == true || bulls.get(i).xPos > 4000) {
            bulls.remove(i);
         }
      }

      int ex = imageHandler.x;
      int ey = imageHandler.y;
      g2.setColor(Color.RED);
      for (int i = 0; i < bulls.size(); i++) {
         bulls.get(i).draw(g2);
      }
   }

   //General function that determines the tile type of a certain x and y value based on a map
   public int blockType(double i, double j) {
      int X = (int)(i) / mapHandler.size;
      int Y = (int)(j) / mapHandler.size;
      
      if (Y > mapHandler.getMap().length + 5) {
         mapHandler.changeLevel(mapHandler.levelNum,player);   
      }
      if (X > mapHandler.getMap()[0].length - 1 || Y > mapHandler.getMap().length - 1 || X < 0 || Y < 0) {
         return 0;
      }
      return mapHandler.getMap()[Y][X];
   }
}