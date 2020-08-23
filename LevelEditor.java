//AJ Arnolie 6th 6/13/17
import java.util.*;
import java.awt.*;

//Level Editor allows the user to create and save levels in game
//The program uses a text file to save the data for the new levels
public class LevelEditor{  

   public int selected,num;
   public Rectangle[] rects;
   public int[][] gridArray;

   public LevelEditor() {
      num = -1;
      rects = new Rectangle[11];
      gridArray = new int[45][100];

      //Clears/Resets grid before editing
      for (int i = 0; i < gridArray.length; i++) {
         for (int m = 0; m < gridArray[0].length; m++) {
            gridArray[i][m] = 0;
         }
      }
      for (int i = 0; i < rects.length; i++) {
         rects[i] = new Rectangle(50 + (i * 80), 80, 40, 40);
      } 
      selected = 5;
   }

   //Shows which tile is selected
   public void drawSelected(Graphics g) {
      if (selected != 0) {
         g.setColor(Color.YELLOW);
         g.drawRect(30+80*(selected-1),60,80,80);
      }
   }

   //Method that finds the specific grid location and changes the value of the position
   public void checkRects(int x, int y, Graphics g) {
      for (int i = 0;i <rects.length;i++) {
         if (rects[i].contains(x,y)) {
            selected = i+1;
         }
      }

      if (y > 150 && y < 600 && x > 0 && x < PlatDriver.FRAME1) {
         int ex = x / 10;
         int why = (y - 150) / 10;

         //List of tile selections
         if (selected == 1) {
            gridArray[why][ex] = 1;
         }
         if (selected == 2) {
            gridArray[why][ex] = 1;
         }
         if (selected == 3) {
            gridArray[why][ex] = 4;
         }
         if (selected == 4) {
            gridArray[why][ex] = 2;
         }
         if (selected == 5) {
            gridArray[why][ex] = 3;
         }
         if (selected == 6) {
            gridArray[why][ex] = 7;
         }
         if (selected == 7) {
            gridArray[why][ex] = 5;
         }
         if (selected == 8) {
            gridArray[why][ex] = 11;
         }
         if (selected == 9) {
            gridArray[why][ex] = 100;
         }
         if (selected == 10) {
            gridArray[why][ex] = 0;
         }
         if (selected == 11) {
            gridArray[why][ex] = 50;
         }
      }
   }

   //Transfers the gridArray to the screen
   //Based on number draws a color/image
   public void drawGrid(Graphics g) {
      for (int i = 0; i < 100; i++) {
         for (int k = 0; k < 45; k++) {
            if (gridArray[k][i] == 0) {
               g.setColor(Color.WHITE);
               g.drawRect(i*10,150+k*10,10,10);
            }
            if (gridArray[k][i] == 1) {
               g.drawImage(imageHandler.grassTile,i*10,150+k*10,10,10,null);
            }
            if (gridArray[k][i] == 2) {
               g.setColor(new Color(0,0,255,200));
               g.fillRect(i*10,k*10+150,10,10);
            }
            if (gridArray[k][i] == 3) {
               g.setColor(new Color(207,48,16,240));
               g.fillRect(i*10,k*10+150,10,10);
            }
            if (gridArray[k][i] == 4) {
               g.drawImage(imageHandler.jumpTile,i*10,k*10+150,10,10,null);
            }
            if (gridArray[k][i] == 5) {               
               g.setColor(Color.BLACK);
               g.fillRect(i*10,k*10+150,10,10);
            }
            if (gridArray[k][i] == 11) {               
               g.setColor(Color.GRAY);
               g.fillRect(i*10,k*10+150,10,10);
            }
            if (gridArray[k][i] == 50) {               
               g.setColor(Color.YELLOW);
               g.fillRect(i*10,k*10+150,10,10);
            }
            if (gridArray[k][i] == 7) {
               g.setColor(Color.BLACK);
               if (k>=1&&i>=1&&k <gridArray.length-1&&i <gridArray[k].length-1) {
                  if (gridArray[k+1][i]==1 || gridArray[k+1][i]==4) {
                     int[] q ={i*10-0,i*10-0+10/2,i*10-0+10};
                     int[] w ={k*10+150+10,k*10+150,k*10+150+10};
                     g.fillPolygon(q,w,3);
                  }
                  else if (gridArray[k][i-1]==1 || gridArray[k][i-1]==4) {
                     int[] q ={i*10-0,i*10-0+10,i*10-0};
                     int[] w ={k*10+150,k*10+150+10/2,k*10+150+10};
                     g.fillPolygon(q,w,3);
                  }
                  else if (gridArray[k][i+1]==1 || gridArray[k][i+1]==4) {
                     int[] q ={i*10-0,i*10-0+10,i*10-0+10};
                     int[] w ={k*10+150+10/2,k*10+150,k*10+150+10};
                     g.fillPolygon(q,w,3);
                  }
                  else if (gridArray[k-1][i]==1 || gridArray[k-1][i]==4) {
                     int[] q ={i*10-0,i*10-0+10,i*10-0+10/2};
                     int[] w ={k*10+150,k*10+150,k*10+150+10};
                     g.fillPolygon(q,w,3);
                  }
               }
            }
         
          //Draws finish line
            if (gridArray[k][i] == 100) {
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
                     g.fillRect((b*10/4)+i*10,(a*10/4)+k*10+150,10/4,10/4);
                  }
                  colSwitch = 1-colSwitch;
               }
            }       
         }
      }
   }

   //Draws the outsides of the Level Editor
   //Creates the selection options
   public void drawLevelEditor(Graphics g) {
      g.setColor(new Color(35,38,47));
      g.fillRect(0,0,PlatDriver.FRAME1,150);
      g.fillRect(0,600,PlatDriver.FRAME1,100);
      g.drawImage(imageHandler.grassTile,50,80,40,40,null);
      g.drawImage(imageHandler.dirtTile,130,80,40,40,null);
      g.drawImage(imageHandler.jumpTile,210,80,40,40,null);
      g.setColor(new Color(0,0,255,200));
      g.fillRect(290,80,40,40);
      g.setColor(new Color(207,48,16,240));
      g.fillRect(370,80,40,40);
      g.setColor(Color.GRAY);
      int[] q ={450,490,470};
      int[] w ={120,120,80};
      g.fillPolygon(q,w,3);
      g.setColor(Color.BLACK);
      g.fillRect(530,80,40,40);
      g.setColor(Color.RED);
      g.fillRect(545,85,10,30);
      g.setColor(Color.GRAY);
      g.fillRect(610,80,40,40);
      g.setColor(Color.RED);
      g.fillOval(625,85,10,10);
      g.fillOval(625,105,10,10);

      int colSwitch = 0;
      for (int a = 0; a < 4; a++) {
         for (int b = 0; b < 4; b++) {
            if (colSwitch == 0) {
               g.setColor(Color.WHITE);
               colSwitch = 1;
            }
            else if (colSwitch == 1) {
               g.setColor(Color.BLACK);
               colSwitch = 0;
            }
            g.fillRect((b*40/4)+690,(a*40/4)+80,40/4,40/4);
         }
         colSwitch = 1 - colSwitch;
      } 

      g.setColor(Color.WHITE);
      g.drawRect(770,80,40,40);
      g.setColor(Color.YELLOW);
      g.fillRect(850,80,40,40);
   }

   //Sets all the values in gridArray to 0
   public void clearLevel() {
      for (int a = 0; a < gridArray.length; a++) {
         for (int b = 0; b < gridArray[0].length; b++) {
            gridArray[a][b] = 0;    
         }
      }
   }

   //Cuts down the level to remove the extra outer empty space
   //Creates a new level object with the new array
   public void saveLevel(PlatPanel p) {
      int minDist = 101;
      for (int i = 0;i < gridArray.length; i++) {
         int dist = 0;
         for (int k = 0; k < gridArray[0].length && gridArray[i][k] == 0; k++) {
            dist++;
         }
         if (dist < minDist) {
            minDist = dist;
         }
      }
      
      int xMin = minDist;
      minDist = 46;
      for (int i = 0; i < gridArray[0].length; i++) {
         int dist = 0;
         for (int k = 0; k < gridArray.length && gridArray[k][i] == 0; k++) {
            dist++;
         }
         if (dist < minDist) {
            minDist = dist;
         }
      }

      int yMin = minDist;
      int[][] nextArray = new int[45 - yMin][100 - xMin];
      for (int i = 0;i < nextArray.length; i++) {
         for (int d = 0;d < nextArray[0].length; d++) {
            nextArray[i][d] = gridArray[i + yMin][d + xMin];
         }
      }

      int i = mapHandler.levels.size();
      mapHandler.levels.add(new Level(nextArray, "Level " + (i + 1)));
      p.rects.add(new Rectangle(60+(i/10)*300,140+((i%10)*50),240,50));
      clearLevel();
   }

   //Allows the user to set the level grid to a certain array
   public void setLevelGrid(int[][] grid, int in) {
      for (int i = 0;i <grid.length;i++) {
         for (int d = 0;d<grid[i].length;d++) {
            gridArray[i][d] = grid[i][d];
            num = in;
         }
      
      }
   }
}