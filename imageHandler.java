//AJ Arnolie 6th 6/13/17

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.awt.geom.*;
import java.util.*;

//Image Handler deals with most of the image and game drawing
//Helps to organize drawing different parts of the game
public class imageHandler{

   public bgImage[] images;
   public bgImage begin;
   public static final int FRAME1 = PlatDriver.FRAME1;
   public static final int FRAME2 = PlatDriver.FRAME2;
   public Character player;
   public static int x,  y;   
   public boolean backWallSwitch; 
   public static Image grassTile, dirtTile, RgrassTile, LgrassTile, DgrassTile,jumpTile,grass;
   
   public imageHandler() {
      //Switch to set if the "Back Wall" should appear or not
      backWallSwitch = false;

      //Images being used
      Image beginning = new ImageIcon("Art/beginning.png").getImage();
      Image background = new ImageIcon("Art/background.png").getImage();
      Image mario = new ImageIcon("Art/marioBackground.png").getImage();
      grassTile = new ImageIcon("Art/grassTile.png").getImage();
      RgrassTile = new ImageIcon("Art/RgrassTile.png").getImage();
      LgrassTile = new ImageIcon("Art/LgrassTile.png").getImage();
      DgrassTile = new ImageIcon("Art/DgrassTile.png").getImage();
      dirtTile = new ImageIcon("Art/dirtTile.png").getImage();
      jumpTile = new ImageIcon("Art/jumpTile.png").getImage();
      grass = new ImageIcon("Art/grass.png").getImage();
      
      //Sets the image for the Pause Menu
      begin = new bgImage(FRAME1, -200, -200, mario, 0);

      //This is a parrallax engine that allows you to set the movement of different backgrounds at different speeds
      images = new bgImage[1];
      images[0] = new bgImage(FRAME1, 0, 0, background, .2 * PlatPanel.speed);
      x = 0;
      y = 0;
   }

   //Draws the moving background of the title screen
   public void menuImageDraw(Graphics2D g, double x, double y) {
     //Draws image based on location of the mouse on the screen
      begin.x = (int)(-100 + (100 * (FRAME1 - x) / FRAME1));
      begin.y = (int)(-100 + (100 * (FRAME2 - y) / FRAME2));
      g.drawImage(begin.image, begin.x, begin.y, 1400, 800, null);
   }
   
   public void update(Graphics2D g2, Character p) {
      player = p;
      //Loops through and draws each image for the background
      if (images.length > 0) {
         drawLoopImages(g2, p);
      }
   }

   //Draws image based on x location and loops it around if necessary
   private void drawLoopImages(Graphics2D g, Character p) {
      for (int i = 0; i < images.length; i++) {
         int x = images[i].x;
         int y = images[i].y;
         double dx = images[i].dx;
         double dy = images[i].dx / 4;
         int width = images[i].width;
         x %= FRAME1;
         images[i].x = x;
         images[i].y = y;

         //Draws image in 1 or 2 pieces
         if (x == 0) {images[i].drawImg(g, 0, FRAME1, 0, FRAME1);} 
         else if (x >= FRAME1) {images[i].drawImg(g, 0, FRAME1, width - x, width - x + FRAME1);}
         else if ((x > 0) && (x < FRAME1)) {
            images[i].drawImg(g, 0, x, width - x, width);
            images[i].drawImg(g, x, FRAME1, 0, FRAME1 - x);} 
         else if (x < FRAME1 - width) {
            images[i].drawImg(g, 0, width + x, -x, width);
            images[i].drawImg(g, width + x, FRAME1, 0, FRAME1 - width - x);}
         else if ((x < 0) && (x >= FRAME1 - width)) {images[i].drawImg(g, 0, FRAME1, -x, FRAME1 - x);} 
      }  
   }
   
   //Loops through and draws tileMap
   public void drawMap(int[][] map, Graphics2D g, int X, int Y, int sz) {
      x = X;
      y = Y;
      int size = sz;

      //Draws background if there is a backWall
      if (backWallSwitch) {
         for (int i = 0; i < map[0].length; i++) {
            for (int k = 0; k < map.length; k++) {
               if ((map[k][i] == 0 || map[k][i] == 7 || map[k][i] == 27) 
                  && Math.abs(x+FRAME1/2-i*size) < FRAME1 / 2 + size 
                  && Math.abs(y+FRAME2/2-k*size) < FRAME2 / 2 + size) {
                  g.setColor(new Color(60,60,60));
                  g.fillRect(i * size - x, k * size - y, size, size);
               }
            
            }
         }
      }

      //Updates all the lasers in the level
      if (sz == mapHandler.size) {
         for (int i = 0; i < mapHandler.levels.get(mapHandler.levelNum).lasers.size(); i++) {
            mapHandler.levels.get(mapHandler.levelNum).lasers.get(i).updateDelay();
            mapHandler.levels.get(mapHandler.levelNum).lasers.get(i).drawLaser(g);
         }
         for (int i = 0; i < mapHandler.levels.get(mapHandler.levelNum).turrets.size(); i++) {
            mapHandler.levels.get(mapHandler.levelNum).turrets.get(i).drawTurret(g,player);
         }
      }
      
      //Checks each block for an int and sets color/shape based on this
      for (int i = 0; i < map[0].length; i++) {
         for (int k = 0; k < map.length; k++) {

            //Normal Wall Block
            if (map[k][i] == 1 && Math.abs(x+FRAME1/2-i*size) < FRAME1 / 2 + size 
               && Math.abs(y+FRAME2/2-k*size) < FRAME2 / 2 + size) {
               if (k == 0 || map[k-1][i] == 0 || map[k-1][i] == 7 || map[k-1][i] == 100) {
                  if (PlatPanel.imageswitch) {
                     g.drawImage(grassTile,i*size-x,k*size-y-2,size,size+2,null);
                  }
                  else {
                     g.setColor(Color.BLACK);
                     g.fillRect(i*size-x,k*size-y,size,size);
                  }
               }
               else if (k == map.length - 1 || map[k+1][i] == 0 || map[k+1][i] == 7 || map[k+1][i] == 100) {
                  if (PlatPanel.imageswitch) {
                     g.drawImage(DgrassTile,i*size-x,k*size-y-2,size,size+2,null);
                  }
                  else {
                     g.setColor(Color.BLACK);
                     g.fillRect(i*size-x,k*size-y,size,size);
                  }
               }
               else {
                  if (PlatPanel.imageswitch) {
                     g.drawImage(dirtTile,i*size-x,k*size-y,size,size,null);
                  }
                  else {
                     g.setColor(Color.BLACK);
                     g.fillRect(i*size-x,k*size-y,size,size);
                  }
               }
            }

            //Water Block
            if (map[k][i] == 2 && Math.abs(x+FRAME1/2-i*size) < FRAME1 / 2 + size && Math.abs(y+FRAME2/2-k*size) < FRAME2 / 2 + size) {
               g.setColor(new Color(0,0,255,200));
               g.fillRect(i*size-x,k*size-y,size,size);
            }

            //Lava Block
            if (map[k][i] == 3 && Math.abs(x+FRAME1/2-i*size) < FRAME1 / 2 + size && Math.abs(y+FRAME2/2-k*size) < FRAME2 / 2 + size) {
               g.setColor(new Color(207,48,16,240));
               g.fillRect(i*size-x,k*size-y,size,size);
            }
            
            //Jump Block
            if (map[k][i] == 4 && Math.abs(x+FRAME1/2-i*size) < FRAME1 / 2 + size && Math.abs(y+FRAME2/2-k*size) < FRAME2 / 2 + size) {
               if (PlatPanel.imageswitch) {
                  g.drawImage(jumpTile,i*size-x,k*size-y,size,size,null);
               }
               else {
                  g.setColor(Color.MAGENTA);
                  g.fillRect(i*size-x,k*size-y,size,size);
               }
            }

            //Laser Block
            if (map[k][i] == 5) {               
               g.setColor(Color.BLACK);
               g.fillRect(i*size-x,k*size-y,size,size);
            }

            //Falling Block (Not Yet Implemented)
            if (map[k][i] == 8&&Math.abs(x+FRAME1/2-i*size)<FRAME1/2+size && Math.abs(y+FRAME2/2-k*size)<FRAME2/2+size) {
               g.setColor(Color.BLACK);
               g.fillRect(i*size-x,k*size-y,size,size);
            }

            //Falling Block (Not Yet Implemented)
            if (map[k][i] == 9&&Math.abs(x+FRAME1/2-i*size)<FRAME1/2+size && Math.abs(y+FRAME2/2-k*size)<FRAME2/2+size) {
               g.setColor(Color.BLACK);
               g.fillRect(i*size-x,k*size-y,size,size);
            }

            //Turret
            if (map[k][i] == 11&&Math.abs(x+FRAME1/2-i*size)<FRAME1/2+size && Math.abs(y+FRAME2/2-k*size)<FRAME2/2+size) {
               g.setColor(Color.GRAY);
               g.fillRect(i*size-x,k*size-y,size,size);
            }

            //Spike
            if (map[k][i] == 7&&Math.abs(x+FRAME1/2-i*size)<FRAME1/2+size && Math.abs(y+FRAME2/2-k*size)<FRAME2/2+size) {
               g.setColor(Color.GRAY);
               //Finds direction based on surrounding tiles
               if (k>=1&&i>=1&&k<map.length-1&&i<map[k].length-1) {
                  if (map[k+1][i]==1 || map[k+1][i]==4) {
                     int[] q ={i*size-x,i*size-x+size/2,i*size-x+size};
                     int[] w ={k*size-y+size,k*size-y,k*size-y+size};
                     g.fillPolygon(q,w,3);
                  }
                  else if (map[k][i-1]==1 || map[k][i-1]==4) {
                     int[] q ={i*size-x,i*size-x+size,i*size-x};
                     int[] w ={k*size-y,k*size-y+size/2,k*size-y+size};
                     g.fillPolygon(q,w,3);
                  }
                  else if (map[k][i+1]==1 || map[k][i+1]==4) {
                     int[] q ={i*size-x,i*size-x+size,i*size-x+size};
                     int[] w ={k*size-y+size/2,k*size-y,k*size-y+size};
                     g.fillPolygon(q,w,3);
                  }
                  else if (map[k-1][i]==1 || map[k-1][i]==4) {
                     int[] q ={i*size-x,i*size-x+size,i*size-x+size/2};
                     int[] w ={k*size-y,k*size-y,k*size-y+size};
                     g.fillPolygon(q,w,3);
                  }
               }
            }

            //Water Spike Block
            if (map[k][i] == 27&&Math.abs(x+FRAME1/2-i*size)<FRAME1/2+size && Math.abs(y+FRAME2/2-k*size)<FRAME2/2+size) {
               g.setColor(Color.GRAY);
               //Finds direction based on surrounding blocks
               if (k>=1&&i>=1&&k<map.length-1&&i<map[k].length-1) {
                  if (map[k+1][i]==1 || map[k+1][i]==4) {
                     int[] q ={i*size-x,i*size-x+size/2,i*size-x+size};
                     int[] w ={k*size-y+size,k*size-y,k*size-y+size};
                     g.fillPolygon(q,w,3);
                  }
                  else if (map[k][i-1]==1 || map[k][i-1]==4) {
                     int[] q ={i*size-x,i*size-x+size,i*size-x};
                     int[] w ={k*size-y,k*size-y+size/2,k*size-y+size};
                     g.fillPolygon(q,w,3);
                  }
                  else if (map[k][i+1]==1 || map[k][i+1]==4) {
                     int[] q ={i*size-x,i*size-x+size,i*size-x+size};
                     int[] w ={k*size-y+size/2,k*size-y,k*size-y+size};
                     g.fillPolygon(q,w,3);
                  }
                  else if (map[k-1][i]==1 || map[k-1][i]==4) {
                     int[] q ={i*size-x,i*size-x+size,i*size-x+size/2};
                     int[] w ={k*size-y,k*size-y,k*size-y+size};
                     g.fillPolygon(q,w,3);
                  }
               }
               g.setColor(new Color(0,0,255,200));
               g.fillRect(i*size-x,k*size-y,size,size);
            
            }
            
            //Finish Line Block
            if (map[k][i] == 100&&Math.abs(x+FRAME1/2-i*size)<FRAME1/2+size && Math.abs(y+FRAME2/2-k*size)<FRAME2/2+size) {
               int colSwitch = 0;
               for (int a = 0;a<4;a++) {
                  for (int b = 0;b<4;b++) {
                     if (colSwitch==0) {
                        g.setColor(Color.WHITE);
                        colSwitch = 1;
                     }
                     else if (colSwitch==1) {
                        g.setColor(Color.BLACK);
                        colSwitch = 0;
                     }
                     g.fillRect(i*size-x+(b*size/4),k*size-y+(a*size/4),size/4,size/4);
                  }
                  colSwitch = 1-colSwitch;
               }  
            }
         }
      }
   }
}