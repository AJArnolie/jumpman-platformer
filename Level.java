//AJ Arnolie 6th 6/13/17
import java.util.*;
import java.awt.*;
import javax.swing.*;
import java.io.*;
//Level class stores variables, ArrayLists, and Arrays for each level
//Helps with organization and level creation
public class Level{
   public int[][] map;
   //ArrayLists to hold lasers for map
   public ArrayList<Laser> lasers;
   public ArrayList<Turret> turrets;
   //Gives starting location for player
   public Point playerLocation;
   //Provides data for totals in game
   public int totalDeaths;
   //Can set Level name
   public String name;
   //Constructor for level class
   public Level(int[][] mapp,String n){
      map = mapp;
      name = n;
      playerLocation = new Point(150,150);
      checkForStart();
      lasers = new ArrayList<Laser>();
      turrets = new ArrayList<Turret>();
      checkForLasers();
      checkForTurrets();
   }
   
   //Method that loops through the tile map for the int value attributed to the laser
   //It then adds the laser to the ArrayList of Lasers
   private void checkForLasers(){
      for(int i = 0;i<map.length;i++){
         for(int k = 0;k<map[i].length;k++){
            if(map[i][k] == 5){
               lasers.add(new Laser(map,i,k));
            }
         }
      }
   }
   //Loops through and checks for turrets(11)
   //Creates a new turret object
   private void checkForTurrets(){
      for(int i = 0;i<map.length;i++){
         for(int k = 0;k<map[i].length;k++){
            if(map[i][k] == 11){
               turrets.add(new Turret(map,i,k));
            }
         }
      }
   }
   //Checks for the start block(50)
   //Helps to put the player in the right place
   private void checkForStart(){
      for(int i = 0;i<map.length;i++){
         for(int k = 0;k<map[i].length;k++){
            if(map[i][k] == 50){
               playerLocation = new Point(k*50,i*50);
            }
         }
      }
   
   }
   //Increases amount of deaths by one
   public void incrementDeaths(){
      totalDeaths++;
   }
   //Prints to a data file in a format that can be read back into the program
   public void printToFile(PrintWriter pw){
      pw.write(""+map.length+"\r\n");
      pw.write(""+map[0].length+"\r\n");
      for(int i = 0;i<map.length;i++){
         String line = "";
         for(int n = 0;n<map[i].length;n++){
            line+=map[i][n] + " ";
         }
         pw.write(""+line+"\r\n");
      }
   }
}