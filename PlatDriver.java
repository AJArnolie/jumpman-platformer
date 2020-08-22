/* AJ Arnolie 6th 6/13/17
 * Jumpman Final Project
 *
 * For my final project, I made a platformer game with
 * a level editor and long-term save mechanics. The user
 * can play one of the premade levels or design their own
 * and add it to the list. This new level is then saved so
 * that when the user opens the game again, the level and
 * the stats for that level are still available. There are
 * additional features for the user to explore.
 */
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.util.ArrayList;
import java.awt.geom.*;
import java.io.*;
import java.util.Scanner;
import javax.swing.JFrame;

public class PlatDriver {
   public static final int FRAME1 = 1000;
   public static final int FRAME2 = 700;
   public static void main(String[] args) { 
      JFrame frame = new JFrame("Platformer");
      frame.setSize(FRAME1, FRAME2);
      frame.setLocation(150, 30);
      frame.setUndecorated(true);
      frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      frame.setContentPane(new PlatPanel(frame));
      frame.setResizable(false);
      frame.setVisible(true);
   }
}

//This panel handles most that occurs during the game
//Handles the FRAMESTATES in the game
class PlatPanel extends JPanel {

   //Sets the variables used in the class
   private static final int FRAME1 = PlatDriver.FRAME1;
   private static final int FRAME2 = PlatDriver.FRAME2;
   public static boolean left, right, wallswitch, imageswitch, coolswitch, musicswitch;
   public static boolean mouseDown, isDragging, removeClick, editClick, scrollClick;
   public static double cameraWidth, dragx, dragy, scrollx, mouseposx, mouseposy, scale, speed;
   public static mapHandler mpHandler;
   public static int camerax, cameray, w;
   public static int camerasize = FRAME1;
   public Image rightArrow, leftArrow, square, goomba, potato, title, restart, resume, level;
   public int overallTime, totalDeaths, characterSelect, zoom, time, zoomswitch, creditScrollTimer;
   public Rectangle r1,r2,r3,credits,statistics,exit,quit,settings,more,newLevel,leftA,rightA,
   clear,save,savePlay,drag,remove,back,scrollBar,wallSwitch,imageSwitch,musicSwitch,zoomSwitch;
   public ArrayList<Rectangle> rects;
   public imageHandler imgHandler;
   public Character player;
   public LevelEditor le;
   public Scanner sc;
   public JFrame mframe;
   public PlatPanel p;
   public long startTime;
   private Color BACKGROUND = new Color(30,30,30);
   private BufferedImage myImage;
   private Graphics myBuffer;
   private Timer t; 

   public static int FRAMESTATE = 0;
   //Different FRAMESTATES used in the game
   //FRAMESTATE 0 = Pause Menu
   //FRAMESTATE 1 = Game
   //FRAMESTATE 3 = Level Info
   //FRAMESTATE 4 = Credits
   //FRAMESTATE 5 = Level Editor
   //FRAMESTATE 6 = Statistics
   //FRAMESTATE 7 = Settings
   //FRAMESTATE 8 = More Levels
   

   public PlatPanel(JFrame frame) {
      mframe = frame;
      p = this;
      mouseDown = false;
      isDragging = false;
      removeClick = false;
      editClick = false;
      scrollClick = false;
      wallswitch = false;
      imageswitch = true;
      coolswitch = false;
      musicswitch = true;
      zoomswitch = 0;
      time = 0;
      characterSelect = 0;
      left = false;
      right = false;
      //Movement speed for character based on this number
      speed = 8;
      //Location of camera at the start
      camerax = 0;
      cameray = 0;
      creditScrollTimer = 50;

      rects = new ArrayList<Rectangle>();
      //Rectangles for FRAMESTATE button presses
      r1 = new Rectangle(380,400,240,35);
      r2 = new Rectangle(400,400,200,150);
      r3 = new Rectangle(750,400,200,150);
      exit = new Rectangle(810,30,130,35);
      quit = new Rectangle(980,0,20,15);
      drag = new Rectangle(940,0,40,15);
      settings = new Rectangle(380,550,240,35);
      back = new Rectangle(380,600,240,35);
      more = new Rectangle(660,555,110,35);
      newLevel = new Rectangle(790,555,110,35);
      leftA = new Rectangle(110,330,40,40);
      rightA = new Rectangle(290,330,40,40);
      clear = new Rectangle(100,630,130,35);
      save = new Rectangle(300,630,130,35);
      savePlay = new Rectangle(500,630,130,35);
      remove = new Rectangle(435,660,130,25);
      scrollBar = new Rectangle(300,180,15,30);
      credits = new Rectangle(380,450,240,35);
      statistics = new Rectangle(380,500,240,35);
      wallSwitch = new Rectangle(300,170,50,50);
      imageSwitch = new Rectangle(300,320,50,50);
      musicSwitch = new Rectangle(300,470,50,50);
      zoomSwitch = new Rectangle(700,170,50,50);      

      //Images used in the game
      title = new ImageIcon("Art/title.png").getImage();
      resume = new ImageIcon("Art/resume.png").getImage();
      restart = new ImageIcon("Art/restart.png").getImage();
      level = new ImageIcon("Art/levels.png").getImage();
      leftArrow = new ImageIcon("Art/larrow.png").getImage();
      rightArrow = new ImageIcon("Art/rarrow.png").getImage();
      square = new ImageIcon("Art/square.png").getImage();
      goomba = new ImageIcon("Art/goomba.png").getImage();
      potato = new ImageIcon("Art/Potato.png").getImage();

      imgHandler = new imageHandler();
      mpHandler = new mapHandler();
      myImage =  new BufferedImage(FRAME1, FRAME2, BufferedImage.TYPE_INT_RGB);
      myBuffer = myImage.getGraphics();
      t = new Timer(15, new Listener());
      setLayout(new FlowLayout());
      setFocusable(true);
      addKeyListener(new Key());
      addMouseListener(new MouseClickyThing());

      //Handles Background scrolling and sprite and image drawing for game
      //Main Character in the game 
      player = new Character();
      player.playNote("Audio/chibiNinja");    
             
      //Sets the view width of the screen
      //Width of frame will not change, but the speed and size will change accordingly
      cameraWidth = FRAME1;
      scale = cameraWidth/FRAME1;
      mapHandler.size/=scale;
      player.width/=scale;
      player.height/=scale;
      player.gravity/=scale;
      player.jump/=scale;
      player.maxGravity/=scale;
      player.speed/=scale;
      player.maxSpeed/=scale;
      player.stoppingSpeed/=scale;
      player.wallJump/=scale;
      player.myXPos/=scale;
      player.myYPos/=scale;
      
      //Handles reading previous information and levels from a file
      for (int i = 0; i < mapHandler.levels.size(); i++) {
         rects.add(new Rectangle(60 + (i / 10) * 300, 140 + ((i % 10) * 50), 240, 50));
      }

      //Creates level editor
      le = new LevelEditor();
      File file = new File("data.txt");
      try { sc = new Scanner(file);}
      catch (FileNotFoundException e) {}

      if (sc.hasNextInt()) {
         overallTime = sc.nextInt();
      }
      if (sc.hasNextInt()) {
         totalDeaths = sc.nextInt();
      }
      int amount = 0;
      if (sc.hasNextInt()) {
         amount = sc.nextInt();
      }
      int[] deaths = new int[amount];
      for (int v = 0; v < amount; v++) {
         if (sc.hasNextLine()) {
            deaths[v] = sc.nextInt();
         }
      }

      while (sc.hasNextInt()) {
         int mapw = 0;
         int maph = 0;
         if (sc.hasNextInt()) {
            maph = sc.nextInt();
         }
         if (sc.hasNextInt()) {
            mapw = sc.nextInt();
         }
         int[][] newArray = new int[maph][mapw];
         for (int i = 0; i < newArray.length; i++) {
            for (int m = 0; m < newArray[0].length; m++) {
               if (sc.hasNextInt()) {
                  newArray[i][m] = sc.nextInt();
               }
            }
         }
         if (mapw != 0 && maph != 0) {
            int s = mapHandler.levels.size();
            int ni = mapHandler.levels.size()+1;
            mapHandler.levels.add(new Level(newArray,"Level " + ni));
            p.rects.add(new Rectangle(60 + (s / 10) * 300, 140 + ((s % 10) * 50), 240, 50));
         }
      }
      for (int y = 0; y < deaths.length; y++) {
         mapHandler.levels.get(y).totalDeaths = deaths[y];
      }

      // Starts timer/game loop
      t.start();
      startTime = System.nanoTime();
   }
   //Used for all the button presses in the game
   //Allowed for increased ability to set specific position on screen
   class MouseClickyThing extends MouseAdapter {   
      public void mouseReleased(MouseEvent e) {
         if (isDragging) {
            isDragging = false;
         }
         if (FRAMESTATE == 5) {
            mouseDown = false;
         }
         if (scrollClick) {
            scrollClick = false;
         }
      }
      public void mousePressed(MouseEvent e) {
         //Mouse location on Press
         int x1 = e.getX();
         int y1 = e.getY();
         if (e.getButton() == MouseEvent.BUTTON1) {
            if (quit.contains(x1, y1)) {
               try {
                  PrintWriter pw = new PrintWriter("data.txt");
                  int seconds = (int)((System.nanoTime() - startTime) / 1000000000);
                  int num = overallTime + seconds;
                  pw.write("" + num + "\r\n");
                  int deaths = player.deathCounter + totalDeaths;
                  pw.write("" + deaths + "\r\n");
                  pw.write("" + mapHandler.levels.size() + "\r\n");

                  for (int i = 0; i < mapHandler.levels.size(); i++) {
                     pw.write("" + mapHandler.levels.get(i).totalDeaths + "\r\n");
                  }
                  for (int i = 10; i < mapHandler.levels.size(); i++) {
                     mapHandler.levels.get(i).printToFile(pw);
                  }
                  pw.close();
               }
               catch (FileNotFoundException r) {
               }
               System.exit(0);
            }

            if (drag.contains(x1, y1)) {
               isDragging = true;
               dragx = mouseposx;
               dragy = mouseposy;
            }

            if (FRAMESTATE == 0) {
            //Mouse Listener in Pause Menu
               if (r1.contains(x1, y1)) {
                  FRAMESTATE = 1;
               }
               if (credits.contains(x1, y1)) {
                  FRAMESTATE = 4;
               } 
               if (statistics.contains(x1, y1)) {
                  FRAMESTATE = 6;
               }    
               if (settings.contains(x1, y1)) {
                  FRAMESTATE = 7;
               }     
               if (more.contains(x1, y1)) {
                  FRAMESTATE = 8;
               } 
               if (newLevel.contains(x1, y1)) {
                  FRAMESTATE = 5;
               }  
               if (leftA.contains(x1, y1)) {
                  characterSelect--;
                  if (characterSelect < 0) {
                     characterSelect = mapHandler.characters.size() - 1;
                  }
               } 
               if (rightA.contains(x1, y1)) {
                  characterSelect++;
                  characterSelect %= (mapHandler.characters.size());
               }
            }
            else if (FRAMESTATE == 1) {
            //Mouse Listener in Game
            }
            else if (FRAMESTATE == 3) {
            //Mouse Listener in Level Info
               if (settings.contains(x1, y1)) {
                  FRAMESTATE = 1;
               }
               if (back.contains(x1, y1)) {
                  FRAMESTATE = 8;
               }
            }
            else if (FRAMESTATE == 4) {
               if (exit.contains(x1, y1)) {
                  FRAMESTATE = 0;
               }
            }
            else if (FRAMESTATE == 5) {
               if (exit.contains(x1, y1)) {
                  FRAMESTATE = 0;
               }
               if (clear.contains(x1, y1)) {
                  le.clearLevel();
               }
               if (save.contains(x1, y1)) {
                  le.saveLevel(p);
               }
               if (savePlay.contains(x1, y1)) {
                  le.saveLevel(p);
                  mapHandler.changeLevel(mapHandler.levels.size() - 1, player);
                  FRAMESTATE = 1;
               }
               mouseDown = true;
            }
            else if (FRAMESTATE == 6) {
               if (exit.contains(x1, y1)) {
                  FRAMESTATE = 0;
               }
            }
            else if (FRAMESTATE == 7) {
               if (exit.contains(x1, y1)) {
                  FRAMESTATE = 0;
               }
               if (wallSwitch.contains(x1, y1)) {
                  wallswitch = !wallswitch;
               }
               if (imageSwitch.contains(x1, y1)) {
                  imageswitch = !imageswitch;
               }
               if (musicSwitch.contains(x1, y1)) {
                  musicswitch = !musicswitch;
                  if (musicswitch == true) {
                     player.playNote("Audio/chibiNinja"); 
                  }
                  if (musicswitch == false) {
                     player.stopClip();
                  }
               }
               if (zoomSwitch.contains(x1, y1)) {
                  zoomswitch++;
                  zoomswitch %= 4;
                  w = FRAME1;
                  if (zoomswitch == 0) {
                     w=FRAME1;
                  }  
                  if (zoomswitch == 1) {
                     w=FRAME1*3/2;
                  }
                  if (zoomswitch == 2) {
                     w=FRAME1*2;
                  }
                  if (zoomswitch == 3) {
                     w=FRAME1*3;
                  }   
                  scale = w/FRAME1*scale;
                  mapHandler.size /= scale;
                  player.width /= scale;
                  player.height /= scale;
                  player.gravity /= scale;
                  player.jump /= scale;
                  player.maxGravity /= scale;
                  player.speed /= scale;
                  player.maxSpeed /= scale;
                  player.stoppingSpeed /= scale;
                  player.wallJump /= scale;
                  player.myXPos /= scale;
                  player.myYPos /= scale;
               
               }
            }
            else if (FRAMESTATE == 8) {
               if (exit.contains(x1, y1)) {
                  FRAMESTATE = 0;
                  removeClick = false;
               }
               if (remove.contains(x1, y1)) {
                  removeClick = true;
               }
               for (int i = 0; i < mapHandler.levels.size(); i++) {
                  if (rects.get(i).contains(x1, y1)) {
                     if (removeClick && i > 9) {
                        mapHandler.levels.remove(i);
                        removeClick = false;
                     }
                     else{
                        mapHandler.changeLevel(i, player);
                        FRAMESTATE = 3;
                     }
                  }
               }
            
            }
         }
      }
   }
 
   //Paints the screen based on which FRAMESTATE is selected
   public void paintComponent(Graphics g) {
      time++;
      Graphics2D g3 = (Graphics2D)g;
      g3.setStroke(new BasicStroke(10));
      //Most accurate mouse position based on location in frame
      mouseposx = MouseInfo.getPointerInfo().getLocation().getX()-mframe.getLocation().getX();
      mouseposy = MouseInfo.getPointerInfo().getLocation().getY()-mframe.getLocation().getY();
      
      if (isDragging) {
         mframe.setLocation((int)(mframe.getLocation().getX()+(mouseposx-dragx)),(int)(mframe.getLocation().getY()+(mouseposy-dragy)));
      }
      if (scrollClick) {
         scrollBar.setLocation((int)scrollBar.getX()+(int)(mouseposx-scrollx),180);
      }

      //Draws Game
      if (FRAMESTATE == 1) {
         myBuffer.setColor(BACKGROUND);
         myBuffer.fillRect(0,0,FRAME1,FRAME2);
         Graphics2D g2 = (Graphics2D) myBuffer;
         //Updates background images
         imgHandler.update(g2,player);
         //Draws tile map
         imgHandler.drawMap(mapHandler.levels.get(mapHandler.levelNum).map,g2,camerax,cameray,mapHandler.size);
         //Moves player
         player.update();
         //Draws player
         player.draw(myBuffer,this); 
         myBuffer.setColor(Color.RED);
         myBuffer.fillRect(980,0,20,15);  
         myBuffer.setColor(new Color(249, 179, 129));
         myBuffer.fillRect(940,0,40,15);  
         g.drawImage(myImage, 0, 0, FRAME1, FRAME2, null);
      }

      //Draws Pause Menu
      if (FRAMESTATE == 0) {
         if (mouseposx < 0) {mouseposx = 0;}
         if (mouseposy < 0) {mouseposy = 0;}
         imgHandler.menuImageDraw((Graphics2D)myBuffer, mouseposx, mouseposy);
         myBuffer.setColor(new Color(70,72,84,200));
         myBuffer.fillRoundRect(90,250,260,350,15,15);
         myBuffer.fillRoundRect(370,250,260,350,15,15);
         myBuffer.fillRoundRect(650,250,260,350,15,15);
         myBuffer.setColor(new Color(35,38,47));
         myBuffer.fillRoundRect(650,250,260,40,15,15);
         myBuffer.fillRoundRect(90,250,260,40,15,15);
         myBuffer.setColor(new Color(33,34,39));
         myBuffer.fillRoundRect(380,400,240,35,15,15);
         myBuffer.fillRoundRect(380,450,240,35,15,15);
         myBuffer.fillRoundRect(380,500,240,35,15,15);
         myBuffer.fillRoundRect(380,550,240,35,15,15);
         myBuffer.setColor(Color.WHITE);
         myBuffer.setFont(new Font("Arial Black", Font.BOLD, 20));
         myBuffer.drawString("Level Select", 710, 280);
         myBuffer.drawString("Player Select", 140, 280);
         myBuffer.setFont(new Font("Arial Black", Font.BOLD, 15));
         myBuffer.setColor(new Color(255, 140, 0));
         myBuffer.drawString("Play", 485, 424);
         myBuffer.setColor(Color.WHITE);
         myBuffer.drawString("Credits", 470, 474);
         myBuffer.drawString("Statistics", 460, 524);
         myBuffer.drawString("Settings", 465, 574);
         drawColor(190, 320, 60, 60, myBuffer);
         
         /////////////LEVEL DRAW//////////////
         Graphics2D gs = (Graphics2D)myBuffer;
         gs.setStroke(new BasicStroke(1));
         for (int i = 0; i < 5; i++) {
            myBuffer.setColor(Color.WHITE);
            myBuffer.setFont(new Font("Arial Black", Font.BOLD, 15)); 
            myBuffer.drawString(mapHandler.levels.get(i).name, 660,310+(i*50));
            myBuffer.setColor(new Color(220,220,220)); 
            myBuffer.setFont(new Font("Arial Black", Font.BOLD, 12));          
            myBuffer.drawString("Deaths: " + mapHandler.levels.get(i).totalDeaths, 660,330+(i*50));
            myBuffer.drawLine(660, 340+(i*50), 900, 340+(i*50));
         }
         myBuffer.setColor(new Color(33,34,39));
         myBuffer.fillRoundRect(660,555,110,35,15,15);
         myBuffer.fillRoundRect(790,555,110,35,15,15);
         myBuffer.setColor(Color.WHITE);
         myBuffer.setFont(new Font("Arial Black", Font.BOLD, 12)); 
         myBuffer.drawString("More Levels",670,575);
         myBuffer.drawString("Create New",800,575);
         myBuffer.drawImage(leftArrow,110,330,40,40,null);
         myBuffer.drawImage(rightArrow,290,330,40,40,null);
         /////////////////////////////////////
         myBuffer.setColor(Color.RED);
         myBuffer.fillRect(980,0,20,15);
         myBuffer.setColor(new Color(249, 179, 129));
         myBuffer.fillRect(940,0,40,15);  
         Graphics2D g2 = (Graphics2D) myBuffer;
         //Draws menu image background
         //Draws other images
         myBuffer.drawImage(title,200,80,600,150,null);
         g.drawImage(myImage, 0, 0, FRAME1, FRAME2, null);
      }

      //Draws Level info for level based on the one selected in Level Select
      if (FRAMESTATE == 3) {
         myBuffer.setColor(new Color(46,49,57));
         myBuffer.fillRect(0,0,FRAME1,FRAME2);
         Graphics2D g2 = (Graphics2D)myBuffer;
         //Draws mini version of map for player to see
         imgHandler.drawMap(mapHandler.getMap(),g2,0,-100,10);
         myBuffer.setColor(new Color(33,34,39));
         myBuffer.fillRoundRect(380,550,240,35,15,15);
         myBuffer.fillRoundRect(380,600,240,35,15,15);
         myBuffer.setColor(Color.WHITE);
         drawYCentered(myBuffer,"Play",settings,new Font("Arial Black", Font.BOLD, 15));
         drawYCentered(myBuffer,"Back",back,new Font("Arial Black", Font.BOLD, 15));
         //Play Button
         myBuffer.setColor(Color.RED);
         myBuffer.fillRect(980,0,20,15);
         myBuffer.setColor(new Color(249, 179, 129));
         myBuffer.fillRect(940,0,40,15);  
         g.drawImage(myImage, 0, 0, FRAME1, FRAME2, null);
      }

      //Credits
      if (FRAMESTATE == 4) {
         creditScrollTimer--;
         int x = creditScrollTimer;
         myBuffer.setColor(new Color(46,49,57));
         myBuffer.fillRect(0,0,FRAME1,FRAME2);
         myBuffer.setColor(Color.WHITE);
         myBuffer.setFont(new Font("Arial Black", Font.BOLD, 20));
         Rectangle rect = new Rectangle(0,0,FRAME1,FRAME2);
         Font f = new Font("Arial Black", Font.BOLD, 20);
         Font f2 = new Font("Arial Black", Font.BOLD, 50);
         drawCentered(myBuffer,"Credits",(80+x)%FRAME2+700,rect,f2);
         drawCentered(myBuffer,"Game Designer: AJ Arnolie",(140+x)%FRAME2+700,rect,f);
         drawCentered(myBuffer,"Programmer: AJ Arnolie",(220+x)%FRAME2+700,rect,f);
         drawCentered(myBuffer,"Graphic Designer: AJ Arnolie",(300+x)%FRAME2+700,rect,f);
         drawCentered(myBuffer,"Music: Eric Skiff",(380+x)%FRAME2+700,rect,f);
         drawCentered(myBuffer,"Chibi Ninja",(420+x)%FRAME2+700,rect,f);
         drawCentered(myBuffer,"Jumpshot",(460+x)%FRAME2+700,rect,f);
         drawCentered(myBuffer,"A Night of Dizzy Spells",(500+x)%FRAME2+700,rect,f);
         myBuffer.setColor(new Color(46,49,57));
         myBuffer.fillRect(0,500,FRAME1,250);
         for (int i = 0; i < 31; i++) {
            myBuffer.drawImage(imgHandler.grassTile,i*(FRAME1/30),498,FRAME1/30,FRAME1/30+2,null);
         }
         for (int i = 0; i < 31; i++) {
            for (int k = 1; k < 15; k++) {
               myBuffer.drawImage(imgHandler.dirtTile,i*(FRAME1/30),498+k*(FRAME1/30),FRAME1/30,FRAME1/30+2,null);
            }
         }
         myBuffer.setColor(new Color(33,34,39));
         myBuffer.fillRoundRect(810,30,130,35,15,15);
         Font font = new Font("Arial Black", Font.BOLD, 12);
         myBuffer.setColor(Color.WHITE);
         drawYCentered(myBuffer,"Exit",exit,font);
         myBuffer.setColor(Color.RED);
         myBuffer.fillRect(980,0,20,15);
         myBuffer.setColor(new Color(249, 179, 129));
         myBuffer.fillRect(940,0,40,15);  
         g.drawImage(myImage, 0, 0, FRAME1, FRAME2, null);
      }
      //Level Editor
      if (FRAMESTATE == 5) {
         if (mouseDown) {
            le.checkRects((int)mouseposx,(int)mouseposy,myBuffer);
         }
         myBuffer.setColor(new Color(146,136,147));
         myBuffer.fillRect(0,0,FRAME1,FRAME2);
         le.drawLevelEditor(myBuffer);
         le.drawSelected(myBuffer);
         le.drawGrid(myBuffer);
         myBuffer.setColor(new Color(33,34,39));
         myBuffer.fillRoundRect(810,30,130,35,15,15);
         myBuffer.fillRoundRect(100,630,130,35,15,15);
         myBuffer.fillRoundRect(300,630,130,35,15,15);
         myBuffer.fillRoundRect(500,630,130,35,15,15);

         Font font = new Font("Arial Black", Font.BOLD, 12);
         myBuffer.setColor(Color.WHITE);
         drawYCentered(myBuffer,"Exit",exit,font);
         drawYCentered(myBuffer,"Clear All",clear,font);
         drawYCentered(myBuffer,"Save",save,font);
         drawYCentered(myBuffer,"Save and Play",savePlay,font);

         Rectangle rect = new Rectangle(0,0,FRAME1,FRAME2);
         myBuffer.setColor(Color.WHITE);
         Font f = new Font("Arial Black", Font.BOLD, 20);
         drawCentered(myBuffer,"Level Editor",40,rect,f);
         myBuffer.setColor(Color.RED);
         myBuffer.fillRect(980,0,20,15);
         myBuffer.setColor(new Color(249, 179, 129));
         myBuffer.fillRect(940,0,40,15);  
         g.drawImage(myImage, 0, 0, FRAME1, FRAME2, null);
      }
      //Statistics
      if (FRAMESTATE == 6) {
         myBuffer.setColor(new Color(46,49,57));
         myBuffer.fillRect(0,0,FRAME1,FRAME2);
         Rectangle rect = new Rectangle(0,0,FRAME1,FRAME2);
         myBuffer.setColor(Color.WHITE);
         Font f = new Font("Arial Black", Font.BOLD, 20);
         drawCentered(myBuffer,"Statistics",80,rect,f);
         myBuffer.drawString("Deaths: " + (totalDeaths+player.deathCounter), 150,200);
         int seconds = (int)((System.nanoTime()-startTime)/1000000000);
         seconds+=overallTime;
         int minutes = seconds/60;
         seconds%=60;
         int hours = minutes/60;
         minutes%=60;
         String time = "";
         if (hours< 10) {
            time += "0"+hours+":";
         }
         else{
            time+= hours+ ":";
         }
         if (minutes <10) {
            time += "0"+minutes+":";
         }
         else{
            time+= minutes+ ":";
         }
         if (seconds <10) {
            time += "0"+seconds;
         }
         else{
            time+= seconds;
         }
         myBuffer.drawString("Time Spent Playing: " + time, 150,300);
         myBuffer.setColor(new Color(33,34,39));
         myBuffer.fillRoundRect(810,30,130,35,15,15);
         Font font = new Font("Arial Black", Font.BOLD, 12);
         myBuffer.setColor(Color.WHITE);
         drawYCentered(myBuffer,"Exit",exit,font);
         myBuffer.setColor(Color.RED);
         myBuffer.fillRect(980,0,20,15);
         myBuffer.setColor(new Color(249, 179, 129));
         myBuffer.fillRect(940,0,40,15);  
         g.drawImage(myImage, 0, 0, FRAME1, FRAME2, null);
      }

      //Settings
      if (FRAMESTATE == 7) {
         myBuffer.setColor(new Color(46,49,57));
         myBuffer.fillRect(0,0,FRAME1,FRAME2);
         myBuffer.setColor(Color.WHITE);
         myBuffer.setColor(new Color(33,34,39));
         myBuffer.fillRoundRect(810,30,130,35,15,15);
         if (wallswitch) {
            myBuffer.setColor(Color.GREEN);
            imgHandler.backWallSwitch = true;
         }
         else{
            imgHandler.backWallSwitch = false;
         }
         myBuffer.fillRect(300,170,50,50);
         if (imageswitch) {
            myBuffer.setColor(Color.GREEN);
            myBuffer.fillRect(300,320,50,50);
         }
         else{
            myBuffer.setColor(new Color(33,34,39));
            myBuffer.fillRect(300,320,50,50);
         }
         if (musicswitch) {
            myBuffer.setColor(Color.GREEN);
            myBuffer.fillRect(300,470,50,50);            
         }
         else{
            myBuffer.setColor(new Color(33,34,39));
            myBuffer.fillRect(300,470,50,50);            
         }
         myBuffer.setColor(new Color(33,34,39));
         myBuffer.fillRect(700,170,50,50); 
         Font fe = new Font("Arial Black", Font.BOLD, 16); 
         myBuffer.setColor(Color.WHITE);
         if (zoomswitch == 0) {
            drawYCentered(myBuffer,"1x",zoomSwitch,fe);
         }  
         if (zoomswitch == 1) {
            drawYCentered(myBuffer,"1.5x",zoomSwitch,fe);
         }
         if (zoomswitch == 2) {
            drawYCentered(myBuffer,"2x",zoomSwitch,fe);
         }
         if (zoomswitch == 3) {
            drawYCentered(myBuffer,"3x",zoomSwitch,fe);
         }
         //drawCentered(myBuffer,"Warning! Doesn't work perfectly!",230,zoomSwitch,new Font("Arial Black", Font.BOLD, 12));
                    
         Font font = new Font("Arial Black", Font.BOLD, 12);
         myBuffer.setColor(Color.WHITE);
         drawYCentered(myBuffer,"Exit",exit,font);
         Rectangle rect = new Rectangle(0,0,FRAME1,FRAME2);
         myBuffer.setColor(Color.WHITE);
         Font f = new Font("Arial Black", Font.BOLD, 20);
         drawCentered(myBuffer,"Settings",80,rect,f);
         myBuffer.setFont(new Font("Arial Black", Font.BOLD, 15));
         myBuffer.drawString("Back Wall: ",100,200);
         myBuffer.drawString("Image Drawing: ",100,350);
         myBuffer.drawString("Music: ",100,500);
         myBuffer.drawString("Zoom: ",600,200);
         myBuffer.setColor(Color.RED);
         myBuffer.fillRect(980,0,20,15);
         myBuffer.setColor(new Color(249, 179, 129));
         myBuffer.fillRect(940,0,40,15);  
         g.drawImage(myImage, 0, 0, FRAME1, FRAME2, null);
      }

      //Draws Level Select Screen
      if (FRAMESTATE == 8) {
         myBuffer.setColor(new Color(46,49,57));
         myBuffer.fillRect(0,0,FRAME1,FRAME2);
         myBuffer.setColor(new Color(33,34,39));
         myBuffer.fillRoundRect(810,30,130,35,15,15);
         Font font = new Font("Arial Black", Font.BOLD, 12);
         myBuffer.setColor(Color.WHITE);
         drawYCentered(myBuffer,"Exit",exit,font);
         Rectangle rect = new Rectangle(0,0,FRAME1,FRAME2);
         myBuffer.setColor(Color.WHITE);
         Font f = new Font("Arial Black", Font.BOLD, 20);
         drawCentered(myBuffer,"Levels",80,rect,f);
         myBuffer.setColor(Color.RED);
         myBuffer.fillRect(980,0,20,15);
         myBuffer.setColor(new Color(249, 179, 129));
         myBuffer.fillRect(940,0,40,15);  

         for (int i = 0; i < mapHandler.levels.size(); i++) {
            myBuffer.setColor(Color.WHITE);
            myBuffer.setFont(new Font("Arial Black", Font.BOLD, 15)); 
            myBuffer.drawString(mapHandler.levels.get(i).name, 60+(i/10)*300,160+((i%10)*50));
            myBuffer.setColor(new Color(220,220,220)); 
            myBuffer.setFont(new Font("Arial Black", Font.BOLD, 12));          
            myBuffer.drawString("Deaths: " + mapHandler.levels.get(i).totalDeaths, 60+(i/10)*300,180+((i%10)*50));
            myBuffer.drawLine(60+(i/10)*300, 190+((i%10)*50), 300+(i/10)*300, 190+((i%10)*50));
         }

         myBuffer.setColor(new Color(33,34,39));
         myBuffer.fillRoundRect(435,660,130,25,15,15);
         Font fnt = new Font("Arial Black", Font.BOLD, 12);
         myBuffer.setColor(Color.WHITE);
         drawYCentered(myBuffer,"Remove",new Rectangle(435,660,130,25),fnt);
         g.drawImage(myImage, 0, 0, FRAME1, FRAME2, null);
      }
      Toolkit.getDefaultToolkit().sync(); 
   }

   //Calls repaint in order to update the screen
   private class Listener implements ActionListener {
      public void actionPerformed(ActionEvent e) {
         repaint();   
      }
   } 

   //Handles the Key Presses in the game
   private class Key extends KeyAdapter {
      public void keyPressed(KeyEvent e) {  
         if (e.getKeyCode() == KeyEvent.VK_UP) {player.jump();}
         if (e.getKeyCode() == KeyEvent.VK_DOWN) {} 
         if (e.getKeyCode() == KeyEvent.VK_LEFT) {left = true;}
         if (e.getKeyCode() == KeyEvent.VK_RIGHT) {right = true;}
         if (e.getKeyCode() == KeyEvent.VK_W) {player.jump();}
         if (e.getKeyCode() == KeyEvent.VK_S) {} 
         if (e.getKeyCode() == KeyEvent.VK_A) {left = true;}
         if (e.getKeyCode() == KeyEvent.VK_D) {right = true;}
         //P is the Pause button when in Game and the Play button when in the Pause Menu
         if (e.getKeyCode() == KeyEvent.VK_P) {
            FRAMESTATE = 0;
         }
         //R is the reset button for the level in the game
         if (e.getKeyCode() == KeyEvent.VK_R) {mapHandler.changeLevel(mapHandler.levelNum,player);}
      }

      public void keyReleased(KeyEvent e) {  
         if (e.getKeyCode() == KeyEvent.VK_UP) {}
         if (e.getKeyCode() == KeyEvent.VK_DOWN) {} 
         if (e.getKeyCode() == KeyEvent.VK_LEFT) {left = false;}
         if (e.getKeyCode() == KeyEvent.VK_RIGHT) {right = false;}
         if (e.getKeyCode() == KeyEvent.VK_W) {}
         if (e.getKeyCode() == KeyEvent.VK_S) {} 
         if (e.getKeyCode() == KeyEvent.VK_A) {left = false;}
         if (e.getKeyCode() == KeyEvent.VK_D) {right = false;}
      }
   }

   //Function used in order to better center things related to the x axis
   public void drawCentered(Graphics g, String text, int y, Rectangle rect, Font font) {
      FontMetrics metrics = g.getFontMetrics(font);
      int x = rect.x + (rect.width - metrics.stringWidth(text)) / 2;
      g.setFont(font);
      g.drawString(text, x, y);
   }

   //Centers on both the x and y
   public void drawYCentered(Graphics g, String text, Rectangle rect, Font font) {
      FontMetrics metrics = g.getFontMetrics(font);
      int x = rect.x + (rect.width - metrics.stringWidth(text)) / 2;
      int y = rect.y + (rect.height - metrics.getHeight()) / 2 + metrics.getAscent();
      g.setFont(font);
      g.drawString(text, x, y);
   }

   //Displays the character select and allows users to select and be shown a player
   public void drawColor(int x, int y, int width, int height,Graphics g) {
      if (characterSelect == 0) {
         g.setColor(Color.MAGENTA);
         g.fillRect(x,y,width,height);
      }
      if (characterSelect == 1) {
         g.setColor(Color.GREEN);
         g.fillRect(x,y,width,height);
      }
      if (characterSelect == 2) {
         g.setColor(Color.BLUE);
         g.fillRect(x,y,width,height);
      }
      if (characterSelect == 3) {
         g.setColor(Color.WHITE);
         g.fillRect(x,y,width,height);
      }
      if (characterSelect == 4) {
         g.setColor(Color.BLACK);
         g.fillRect(x,y,width,height);
      }
      if (characterSelect == 5) {
         g.setColor(Color.RED);
         g.fillRect(x,y,width,height);
      }
      if (characterSelect == 6) {
         g.drawImage(imgHandler.grassTile,x,y,width+2,height+2,null);
      }
      if (characterSelect == 7) {
         g.drawImage(square,x,y,width+2,height+2,null);
      }
      if (characterSelect == 8) {
         g.drawImage(goomba,x,y,width+2,height+2,null);
      }
      if (characterSelect == 9) {
         g.drawImage(potato,x,y,width+2,height+2,null);
      }
   }
}