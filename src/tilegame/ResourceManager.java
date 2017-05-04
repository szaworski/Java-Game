package tilegame;

import java.awt.*

;
import java.awt.event.KeyEvent;
import java.awt.geom.AffineTransform;
import java.io.*;
import java.util.ArrayList;
import javax.swing.ImageIcon;
import graphics.*;
import tilegame.sprites.*;



/**
    The ResourceManager class loads and manages tile Images and
     Sprites used in the game. Game Sprites are cloned from
     the original Sprites.
*/
public class ResourceManager{

    private ArrayList tiles;
    private int currentMap;
    private GraphicsConfiguration gc;

    // Sprites used for cloning
    private Sprite playerSprite;
    private Sprite playerJump;
    private Sprite coinSprite;
    private Sprite doorSprite;
    private Sprite grubSprite;
    private Sprite batSprite;
    private Sprite houndSprite;


    
 
 
    public ResourceManager(GraphicsConfiguration gc) {
        this.gc = gc;
        loadTileImages();
        loadCreatureSprites();
        loadItemSprites();
        

    }
    
    
    public Image loadImage(String name) {
        String filename = "images/" + name;
        return new ImageIcon(filename).getImage();
    }


    public Image getMirrorImage(Image image) {
        return getScaledImage(image, -1, 1);
    }
    
    public Image getFlippedImage(Image image) {
        return getScaledImage(image, 1, -1);
    }
   
    
    public KeyEvent keyPressed(KeyEvent e) {

    	if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
            System.out.println("Right key pressed");
            
        }
		return e;
    }
    
    
    


    private Image getScaledImage(Image image, float x, float y) {

        // set up the transform
        AffineTransform transform = new AffineTransform();
        transform.scale(x, y);
        transform.translate(
            (x-1) * image.getWidth(null) / 2,
            (y-1) * image.getHeight(null) / 2);

        // create a transparent (not translucent) image
        Image newImage = gc.createCompatibleImage(
            image.getWidth(null),
            image.getHeight(null),
            Transparency.BITMASK);

        // draw the transformed image
        Graphics2D g = (Graphics2D)newImage.getGraphics();
        g.drawImage(image, transform, null);
        g.dispose();

        return newImage;
    }


    public TileMap loadNextMap(){
        TileMap map = null;
        while (map == null) {
            currentMap++;
            
            try {
                map = loadMap(
                    "maps/map" + currentMap + ".txt");
                
                    Thread.sleep(200);                
                } catch(InterruptedException ex) {
                    Thread.currentThread().interrupt();            

            }
            catch (IOException ex) {
                if (currentMap == 1) {
                    // no maps to load!
                    return null;
                }
               
                currentMap = 0;
                map = null;
            }
        }
        

        return map;
    }


    public TileMap reloadMap() {
        try {
            return loadMap(
                "maps/map" + currentMap + ".txt");
        }
        catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
    }


    private TileMap loadMap(String filename)
        throws IOException
    {
        ArrayList lines = new ArrayList();
        int width = 0;
        int height = 0;

        // read every line in the text file into the list
        BufferedReader reader = new BufferedReader(
            new FileReader(filename));
        while (true) {
            String line = reader.readLine();
            // no more lines to read
            if (line == null) {
                reader.close();
                break;
            }

            // add every line except for comments
            if (!line.startsWith("#")) {
                lines.add(line);
                width = Math.max(width, line.length());
            }
        }

        // parse the lines to create a TileEngine
        height = lines.size();
        TileMap newMap = new TileMap(width, height);
        for (int y=0; y<height; y++) {
            String line = (String)lines.get(y);
            for (int x=0; x<line.length(); x++) {
                char ch = line.charAt(x);

                // check if the char represents tile A, B, C etc.
                int tile = ch - 'A';
                if (tile >= 0 && tile < tiles.size()) {
                    newMap.setTile(x, y, (Image)tiles.get(tile));
                }

                // check if the char represents a sprite
                else if (ch == 'o') {
                    addSprite(newMap, coinSprite, x, y);
                }
                else if (ch == '*') {
                    addSprite(newMap, doorSprite, x, y);
                }
                else if (ch == '1') {
                    addSprite(newMap, grubSprite, x, y);
                }
                else if (ch == '2') {
                    addSprite(newMap, batSprite, x, y);
                }
                else if (ch == '3') {
                    addSprite(newMap, houndSprite, x, y);
                }
            }
        }
        

        // add the player to the map
        Sprite player = (Sprite)playerSprite.clone();
        player.setX(TileMapRenderer.tilesToPixels(2));
        player.setY(0);
        newMap.setPlayer(player);
         
        return newMap;
    }


    private void addSprite(TileMap map,
        Sprite hostSprite, int tileX, int tileY)
    {
        if (hostSprite != null) {
            // clone the sprite from the "host"
            Sprite sprite = (Sprite)hostSprite.clone();

            // center the sprite
            sprite.setX(
                TileMapRenderer.tilesToPixels(tileX) +
                (TileMapRenderer.tilesToPixels(1) -
                sprite.getWidth()) / 2);

            // bottom-justify the sprite
            sprite.setY(
                TileMapRenderer.tilesToPixels(tileY + 1) -
                sprite.getHeight());

            // add it to the map
            map.addSprite(sprite);
        }
    }


    // -----------------------------------------------------------
    // code for loading sprites and images
    // -----------------------------------------------------------


    public void loadTileImages() {
        // keep looking for tile A,B,C, etc. this makes it
        // easy to drop new tiles in the images/ directory
        tiles = new ArrayList();
        char ch = 'A';
        while (true) {
            String name = "tile_" + ch + ".png";
            File file = new File("images/" + name);
            if (!file.exists()) {
                break;
            }
            tiles.add(loadImage(name));
            ch++;
        }
    }
    
    
       
    public void loadCreatureSprites() {

        Image[][] images = new Image[8][];

        // load left-facing images
        images[0] = new Image[] {
            loadImage("player1.png"),
            loadImage("player2.png"),
            loadImage("player3.png"),
            loadImage("player4.png"),
            loadImage("player5.png"),
            loadImage("player6.png"),
            loadImage("player7.png"),
            loadImage("player8.png"),
            loadImage("bat1.png"),
            loadImage("bat2.png"),
            loadImage("bat3.png"),
            loadImage("bat4.png"),
            loadImage("grub1.png"),
            loadImage("grub2.png"),
            loadImage("hound1.png"),
            loadImage("hound2.png"),
            loadImage("hound3.png"),
            loadImage("hound4.png"),
            loadImage("hound5.png"),
            loadImage("jump1.png"),
            loadImage("jump2.png")
           
        };

        images[1] = new Image[images[0].length];
        images[2] = new Image[images[0].length];
        images[3] = new Image[images[0].length];

        for (int i=0; i<images[0].length; i++) {
            // right-facing images
            images[1][i] = getMirrorImage(images[0][i]);
            // left-facing "dead" images
            images[2][i] = getFlippedImage(images[0][i]);
            // right-facing "dead" images
            images[3][i] = getFlippedImage(images[1][i]);

        }

        // create creature animations
        Animation[] playerAnim = new Animation[4];
        Animation[] batAnim = new Animation[4];
        Animation[] grubAnim = new Animation[4];
        Animation[] houndAnim = new Animation[4];
        Animation[] playerJumpAnim = new Animation[4];

        for (int i=0; i<4; i++) {
        	
            playerAnim[i] = createPlayerAnim(
                images[i][0], images[i][1], images[i][2],
                images[i][3], images[i][4], images[i][5],
                images[i][6], images[i][7]);
            		  	
            batAnim[i] = createBatAnim(
                images[i][8], images[i][9], images[i][10], images[i][11]);
            
            grubAnim[i] = createGrubAnim(
                images[i][12], images[i][13]);
            
            houndAnim[i] = createHoundAnim(
                    images[i][14], images[i][15], images[i][16], 
                    images[i][17], images [i][18]);
            
            playerJumpAnim[i] = createJumpAnim(
                    images[i][19], images[i][20]);

           
        }
                
        playerSprite = new Player(playerAnim[0], playerAnim[1],
            	            playerAnim[2], playerAnim[3]);
        
        playerJump = new Player(playerJumpAnim[0], playerJumpAnim[1],
	            playerJumpAnim[2], playerJumpAnim[3]);   
        
        batSprite = new Bat(batAnim[0], batAnim[1],
            batAnim[2], batAnim[3]);
        
        grubSprite = new Grub(grubAnim[0], grubAnim[1],
            grubAnim[2], grubAnim[3]);
        
        houndSprite = new Hound(houndAnim[0], houndAnim[1],
                houndAnim[2], houndAnim[3]);
        
    } 
    
	private Animation createPlayerAnim(Image img1, Image img2, 
		Image img3, Image img4, 
		Image img5, Image img6, 
		Image img7, Image img8)
    {
        Animation anim = new Animation();		      
        anim.addFrame(img1, 100);  
        anim.addFrame(img2, 100); 
        anim.addFrame(img3, 100);
        anim.addFrame(img4, 100);  
        anim.addFrame(img5, 100); 
        anim.addFrame(img6, 100);
        anim.addFrame(img7, 100);     
        anim.addFrame(img8, 100);               
        return anim;
              
    }
	
	private Animation createJumpAnim(Image img1, Image img2)
	    {
	        Animation anim = new Animation();		      
	        anim.addFrame(img1, 100);  
	        anim.addFrame(img2, 100); 	       
	        return anim;
	       
	    }

    private Animation createBatAnim(Image img1, Image img2,
        Image img3, Image img4)
    {
        Animation anim = new Animation();
        anim.addFrame(img1, 100);
        anim.addFrame(img2, 100);
        anim.addFrame(img3, 100);
        anim.addFrame(img4, 125);                     
        return anim;
    }

    private Animation createGrubAnim(Image img1, Image img2) {
        Animation anim = new Animation();
        anim.addFrame(img1, 250);
        anim.addFrame(img2, 250);
        return anim;
    }
    
    private Animation createHoundAnim(Image img1, Image img2, 
    		Image img3, Image img4, Image img5) {
    	
        Animation anim = new Animation();
        anim.addFrame(img1, 200);
        anim.addFrame(img2, 150);
        anim.addFrame(img3, 200);
        anim.addFrame(img4, 150);
        anim.addFrame(img5, 150);    
        return anim;
    }

    private void loadItemSprites() {
        // create "door" sprite
        Animation anim = new Animation();
        anim.addFrame(loadImage("door1.png"), 300);
        anim.addFrame(loadImage("door2.png"), 250);
        anim.addFrame(loadImage("door3.png"), 300);
        anim.addFrame(loadImage("door4.png"), 475);
        anim.addFrame(loadImage("door3.png"), 300);
        anim.addFrame(loadImage("door2.png"), 200);
        anim.addFrame(loadImage("door1.png"), 800);
        doorSprite = new Items.Door(anim);

        // create "coin" sprite
        anim = new Animation();
        anim.addFrame(loadImage("coin1.png"), 150);
        anim.addFrame(loadImage("coin2.png"), 150);
        anim.addFrame(loadImage("coin3.png"), 150);
        anim.addFrame(loadImage("coin4.png"), 150);
        anim.addFrame(loadImage("coin5.png"), 150);
        anim.addFrame(loadImage("coin6.png"), 150);
        coinSprite = new Items.Coin(anim);

    }


}
