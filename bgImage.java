//AJ Arnolie 6th 6/13/17
import java.awt.*;
//Basic class for background Images
//Allows for easy implementation of parrallax backgrounds 
public class bgImage {
   
   public Image image;
   public int width, height, screenWidth, x, y;
   public double dx;

   public bgImage(int swidth, int X, int Y, Image img, double Dx) {
   //Necessary variables to calculate position based on player movement
      screenWidth = swidth;
      image = img;
      width = img.getWidth(null);
      height = img.getHeight(null);
      dx = Dx;
      x = X;
      y = Y;
   }  
   public void drawImg(Graphics2D g, int dx, int dx2, int ix, int ix2) {
   //Decides which part of the image to draw where
   //Logic is done in imageHandler
      g.drawImage(image, dx, y, dx2, y + height, ix, 0, ix2, height, null);
   }
}