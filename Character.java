 //AJ Arnolie 6th 6/13/17

import java.awt.*;
import java.util.*;
import java.io.*;
import javax.sound.sampled.*;

//The character class for the player in the game
//Uses collision detection and calculates velocity based on acceleration speed and walls
public class Character {

   public double x, y, dx, dy, width, height, mapx, mapy, gravity ,jump;
   public double maxGravity, currCol, currRow, speed, maxSpeed, stoppingSpeed, wallJump;
   public static double myXPos, myYPos;
   private Color myColor;
   public Clip newClip;
   public boolean falling, left, right, topLeft, topRight, bottomLeft, bottomRight;
   //Allows for save data
   public int deathCounter;
   public ArrayList<Integer> solidBlocks, liquidBlocks, emptyBlocks;
  
   public Character() {
   //gets the starting location of the player and sets it
      myXPos = mapHandler.levels.get(mapHandler.levelNum).playerLocation.x;
      myYPos = mapHandler.levels.get(mapHandler.levelNum).playerLocation.y;
      
      //Array of blocks that the player should collide with
      solidBlocks = new ArrayList<Integer>();
      solidBlocks.add(1);
      solidBlocks.add(4);
      solidBlocks.add(5);
      solidBlocks.add(8);
      solidBlocks.add(11);

      //Array of blocks where the players movement should changes to more water like qualities
      liquidBlocks = new ArrayList<Integer>();
      liquidBlocks.add(2);
      liquidBlocks.add(3);
      liquidBlocks.add(27);

      //Array of blocks where the player can travel through
      emptyBlocks = new ArrayList<Integer>();
      emptyBlocks.add(0);

      //Dimensions and qualities of player    
      width = 35;
      height = 35;
      falling = false;
      left = false;
      right = false;
      deathCounter = 0;
      dx = 0;
      dy = 0;
      myColor = Color.MAGENTA;
      mapx = 0;
      mapy = 0;
      gravity = .3;
      jump = -10;
      maxGravity = 17;
      speed = .4;
      maxSpeed = PlatPanel.speed;
      stoppingSpeed = .75;
      wallJump = maxSpeed-2;
   }

   //Modifier Methods
   public void setdx(double x) {dx = x;}
   public void setdy(double y) {dy = y;}
   public void setX(double xx) {x = xx;}
   public void setY(double yy) {y = yy;}
   public void setColor(Color c) {myColor = c;}

   //Accessor Methods
   public double getdx() {return dx;}
   public double getdy() {return dy;}
   public double getX() {return x;}
   public double getY() {return y;}
   public Color getColor() {return myColor;}


   //Draws player and changes color of player based on block type
   public void draw(Graphics myBuffer,PlatPanel p) {
      if (blockType(myXPos+width/2,myYPos+height/2) == 2 || blockType(myXPos+width/2,myYPos+height/2) == 27) {
         myBuffer.setColor(new Color(135,206,250));
         myBuffer.fillRect((int)(x), (int)(y), (int)width, (int)height);
      }
      else if (blockType(myXPos+width/2,myYPos+height/2) == 3) {
         myBuffer.setColor(new Color(255,99,71));
         myBuffer.fillRect((int)(x), (int)(y), (int)width, (int)height);
      } 
      else { 
         //myBuffer.setColor(myColor);
         p.drawColor((int)(x), (int)(y), (int)width, (int)height,myBuffer);
      }
   }

   //Updates movement using collision detection
   public void update() {   
      double tempx = myXPos + dx;
      double tempy = myYPos + dy;
      checkPosition(myXPos, myYPos, width, height);
      currCol = (int)myXPos / mapHandler.size;
      currRow = (int)myYPos / mapHandler.size;
      right = PlatPanel.right;
      left = PlatPanel.left;

      //Movement
      if (right) {
         dx += speed;
         if (dx > maxSpeed) {
            dx = maxSpeed;
         }
      }
      if (left) {
         dx -= speed;
         if (dx < -maxSpeed) {
            dx = -maxSpeed;
         }
      }
      if (left && right) {
         if (dx > 0) {
            dx -= stoppingSpeed;
         }
         if (dx < 0) {
            dx += stoppingSpeed;
         }
         if (Math.abs(dx) < stoppingSpeed) {
            dx = 0;
         }
      }

      if (!left && !right) {
         if (dx > 0) {
            if (dy != 0) {
               dx -= stoppingSpeed/5;
            }
            else {
               dx -= stoppingSpeed;
            }
         }
         if (dx < 0) {
            if (dy != 0) {
               dx += stoppingSpeed/5;
            }
            else {
               dx += stoppingSpeed;
            }
         }
         if (Math.abs(dx) < stoppingSpeed) {
            dx = 0;
         }
      
      }

      //Checks bottom collision
      checkPosition(myXPos + 2, myYPos, width - 4, height + 1);
      if (!falling) {
         if (!bottomLeft && !bottomRight) {
            falling = true;
         }
      }

      if (falling) {
         boolean test = false;
         for (int i = 0; i < liquidBlocks.size(); i++) {
            if (blockType(myXPos+width/2,myYPos+height/2) == liquidBlocks.get(i)) {
               test = true;
            } 
         }
         if (test) {
            if (dy > maxGravity / 3) {
               dy = maxGravity / 3;
            }
            dy += gravity / 3;
         }
         else{
            if (dy > maxGravity) {
               dy = maxGravity;
            }
            dy += gravity;
         }
      }

      //Top collision
      checkPosition(myXPos + 2, myYPos - 1, width - 4, height + 1);
      if (dy < 0) {
         if (topLeft||topRight) {
            dy = 0;
            myYPos = (currRow+1) * mapHandler.size;
         }
         else {
            myYPos += dy;
         }
      }

      //Bottom collision
      checkPosition(myXPos + 2, myYPos, width - 4, height + 1);
      if (dy > 0) {
         if (bottomLeft || bottomRight) {
            dy = 0;
            falling = false;
            myYPos = (currRow) * mapHandler.size - height + mapHandler.size;
         }
         else {
            myYPos += dy;
         }
      }

      //Left collision
      checkPosition(myXPos - 1, myYPos + 2, width + 1, height - 4);
      if (dx < 0) {
         if ((topLeft || bottomLeft)) {
            dx = 0;
            myXPos = (currCol + 1) * mapHandler.size;
         }
         else {
            myXPos += dx;
         }
      }
      //Right collision
      checkPosition(myXPos, myYPos - 2, width + 1, height - 4);
      if (dx > 0) {
         if ((topRight || bottomRight)) {
            dx = 0;
            myXPos = (currCol + 1) * mapHandler.size - width - 1;
         }
         else {
            myXPos += dx;
         }
      } 

      //Success
      if (blockType(myXPos + width / 2, myYPos + height / 2) == 100) {
         mapHandler.randomLevel(this); 
         resetTurrets();
      }

      //Death by spikes
      if (blockType(myXPos+width/2,myYPos+height/2) == 7 || blockType(myXPos+width/2,myYPos+height/2) == 27) {
         mapHandler.levels.get(mapHandler.levelNum).incrementDeaths();
         mapHandler.changeLevel(mapHandler.levelNum,this);
         myXPos /= PlatPanel.scale;
         myYPos /= PlatPanel.scale;
         resetTurrets();
         deathCounter++;
      }

      //Sets the camera to follow player
      //The camera can follow any object
      cameraFollow();
      x = myXPos - PlatPanel.camerax;
      y = myYPos - PlatPanel.cameray;

      //Laser collision
      for (int i = 0; i < mapHandler.levels.get(mapHandler.levelNum).lasers.size(); i++) {
         Rectangle r1 = new Rectangle((int)x,(int)y,(int)width,(int)height);
         if (r1.intersectsLine(mapHandler.levels.get(mapHandler.levelNum).lasers.get(i).getLine())) {
            mapHandler.levels.get(mapHandler.levelNum).incrementDeaths();
            mapHandler.changeLevel(mapHandler.levelNum,this);
            resetTurrets();
            deathCounter++;
         }
      }

      //Tests to see if the bullets and the player collide
      for (int k = 0; k < mapHandler.levels.get(mapHandler.levelNum).turrets.size(); k++) {
         for (int i = 0; i < mapHandler.levels.get(mapHandler.levelNum).turrets.get(k).bulls.size(); i++) {
            double c = mapHandler.levels.get(mapHandler.levelNum).turrets.get(k).bulls.get(i).xPos;
            double d = mapHandler.levels.get(mapHandler.levelNum).turrets.get(k).bulls.get(i).yPos;
            if (distance(myXPos + height / 2, myYPos + height / 2, c + 5, d + 5) < height / 2 + 5) {
               mapHandler.levels.get(mapHandler.levelNum).turrets.get(k).bulls.remove(i);
               i--;
               mapHandler.levels.get(mapHandler.levelNum).incrementDeaths();
               mapHandler.changeLevel(mapHandler.levelNum,this);
               resetTurrets();
               deathCounter++;
            } 
         }
      }
   }  

   //Method to have camera follow player
   public void cameraFollow() {
      PlatPanel.camerax = (int)(myXPos - 400);
      PlatPanel.cameray = (int)(myYPos - 350);
   }

   //Jump method sets dy if jump is possible
   public void jump() {
      boolean test = false;
      for (int i = 0; i < liquidBlocks.size(); i++) {
         if (blockType(myXPos+width/2,myYPos+height/2) == liquidBlocks.get(i)) {
            test = true;
         } 
      }

      if (test) {
         dy = jump / 2;
      }
      else {
         if (blockType(myXPos-1,myYPos+1) == 1 || blockType(myXPos-1,myYPos+height-1) == 1) {
            dy = jump;
            dx = wallJump;
         }
         if (blockType(myXPos-1,myYPos+1) == 4 || blockType(myXPos-1,myYPos+height-1) == 4) {
            dy = jump * 2;
            dx = wallJump * 2;
         }
         if (blockType(myXPos+width+1,myYPos+1) == 1 || blockType(myXPos+width+1,myYPos+height-1) == 1) {
            dy = jump;
            dx =- wallJump;
         }
         if (blockType(myXPos+width+1,myYPos+1) == 4 || blockType(myXPos+width+1,myYPos+height-1) == 4) {
            dy = jump * 2;
            dx =- wallJump * 2;
         }
         if (dy == 0) { 
            if (blockType(myXPos,myYPos+height+5) == 4 || blockType(myXPos+width,myYPos+height+5) == 4) {
               dy = jump * 2;
            }
            else{
               dy = jump;
            }
         }
      }
   }

   //Easy to use method that plays any wav file by name
   public void playNote(String note) {
      try {
         /*InputStream in = new FileInputStream(note + ".wav");
         AudioStream audioStream = new AudioStream(in);
         AudioPlayer.player.start(audioStream);*/
         AudioInputStream stream = AudioSystem.getAudioInputStream(new File(note + ".wav"));
         AudioFormat format = stream.getFormat();
         DataLine.Info info = new DataLine.Info(Clip.class, format);
         Clip clip = (Clip) AudioSystem.getLine(info);
         newClip = clip;
         clip.open(stream);
         clip.loop(Clip.LOOP_CONTINUOUSLY);
      } 
      catch (Exception b) {
         System.out.println(b);
      }
   }  

   public void stopClip() {
      newClip.stop();
   }

   //sets the corner boolean values based on a x,y,width,and height
   public void checkPosition(double w, double k, double wth, double hgt) {
      boolean test = false;
      for (int i = 0; i < solidBlocks.size(); i++) {
         if (blockType(w, k) == solidBlocks.get(i)) {
            test = true;
         } 
      }

      topLeft = test;
      test = false;
      for (int i = 0; i < solidBlocks.size(); i++) {
         if (blockType(w + wth, k) == solidBlocks.get(i)) {
            test = true;
         } 
      }

      topRight = test;
      test = false;
      for (int i = 0; i < solidBlocks.size(); i++) {
         if (blockType(w, k + hgt) == solidBlocks.get(i)) {
            test = true;
         } 
      }

      bottomLeft = test;
      test = false;
      for (int i = 0; i < solidBlocks.size(); i++) {
         if (blockType(w + wth, k + hgt) == solidBlocks.get(i)) {
            test = true;
         } 
      }

      bottomRight = test;
      currCol = (int)w / mapHandler.size;
      currRow = (int)k / mapHandler.size;
   
   }

   //Gets the block type at a specific x and y point
   public void resetTurrets() {
      for (int i = 0; i < mapHandler.levels.get(mapHandler.levelNum).turrets.size(); i++) {
         mapHandler.levels.get(mapHandler.levelNum).turrets.get(i).angle = 270;
      }
   }

   //Method that determines the tile type of a point based on an array map
   public int blockType(double i, double j) {
      int X = (int)(i) / mapHandler.size;
      int Y = (int)(j) / mapHandler.size;
      if (Y > mapHandler.getMap().length + 5) {
         mapHandler.changeLevel(mapHandler.levelNum,this);   
      }
      if (X > mapHandler.getMap()[0].length - 1 || Y > mapHandler.getMap().length - 1 || X < 0 || Y < 0) {
         return 0;
      }
      return mapHandler.getMap()[Y][X];
   }
   
   //Calculates distance using distance formula
   private double distance(double x1, double y1, double x2, double y2) {
      return Math.sqrt(Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2));
   }
}