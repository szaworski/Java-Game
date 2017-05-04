package tilegame;

import java.awt.*;

import java.awt.event.KeyEvent;
import java.io.File;
import java.util.Iterator;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import graphics.*;
import sound.*;
import input.*;
import core.GameCore;
import tilegame.sprites.*;
import tilegame.ResourceManager;


/**
    GameManager manages all parts of the game.
*/
public class GameManager extends GameCore {
	
    public static void main(String[] args) {
        new GameManager().run();  
        
        
    }
    
    private static final AudioFormat PLAYBACK_FORMAT =
            new AudioFormat(44100, 16, 1, true, false);
    
    public static final float GRAVITY = 0.002f;

    private Point pointCache = new Point();
    private TileMap map;
    private SoundManager soundManager;
    private ResourceManager resourceManager;
    private InputManager inputManager;
    private TileMapRenderer renderer;
    private Sound jumpSound;

    private GameAction moveLeft;
    private GameAction moveRight;
    private GameAction jump;
    private GameAction exit;
    


    public void init() {
        super.init();
        
        // set up input manager
        initInput();    

        // start resource manager
        resourceManager = new ResourceManager(
        screen.getFullScreenWindow().getGraphicsConfiguration());

        // load resources
        renderer = new TileMapRenderer();
        renderer.setBackground(
        resourceManager.loadImage("background.png"));
 
        // load first map
        map = resourceManager.loadNextMap();
        
        // load sounds
        soundManager = new SoundManager(PLAYBACK_FORMAT);
        jumpSound = soundManager.getSound("sounds/jump.wav");
        
        String fileName = "sounds/music.wav";
	    playLoop(fileName);
        
    }  

    public void stop() {
        super.stop();
        soundManager.close();
    }


    private void initInput() {
    	
        moveLeft = new GameAction("moveLeft");
        moveRight = new GameAction("moveRight");
        jump = new GameAction("jump",
            GameAction.DETECT_INITAL_PRESS_ONLY);
        exit = new GameAction("exit",
            GameAction.DETECT_INITAL_PRESS_ONLY);

        inputManager = new InputManager(
            screen.getFullScreenWindow());
        inputManager.setCursor(InputManager.INVISIBLE_CURSOR);

        inputManager.mapToKey(moveLeft, KeyEvent.VK_LEFT);
        inputManager.mapToKey(moveRight, KeyEvent.VK_RIGHT);
        inputManager.mapToKey(jump, KeyEvent.VK_SPACE);
        inputManager.mapToKey(exit, KeyEvent.VK_ESCAPE);
        
    }

    // checks the users input for player movement 
    private void checkInput(long elapsedTime) {

        if (exit.isPressed()) {
            stop();
        }

        Player player = (Player)map.getPlayer();
        if (player.isAlive()) {
            float velocityX = 0;
            if (moveLeft.isPressed()) {
                velocityX-=player.getMaxSpeed();
            }
            if (moveRight.isPressed()) {
                velocityX+=player.getMaxSpeed();
            }
            if (jump.isPressed()) {
                player.jump(false);
                soundManager.play(jumpSound);
            }
            player.setVelocityX(velocityX);
        }
}
    
    // plays an instance of a chosen sound
	public void playSound(String soundName){
    	
	      try {   
	    	  
	       AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new File(soundName).getAbsoluteFile( ));
	       Clip clip = AudioSystem.getClip( );
	       clip.open(audioInputStream);
	       clip.start( );  
	       
	      }     
	      catch(Exception ex)      
	      {  	  
	        System.out.println("Error");
	        ex.printStackTrace( );        
	    }       
	 }
    
    
    // loops an instance of a chosen sound
    public void playLoop(String soundName){
    	
        try {   
      	  
         AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new File(soundName).getAbsoluteFile( ));
         Clip clip = AudioSystem.getClip( );
         clip.open(audioInputStream);
         clip.start( );  
         clip.loop(Clip.LOOP_CONTINUOUSLY);

        }     
        catch(Exception ex)      
        {  	  
          System.out.println("Error");
          ex.printStackTrace( );        
      }   
        
   }
       
    public void draw(Graphics2D g) {
        renderer.draw(g, map,
            screen.getWidth(), screen.getHeight());

    }

    public TileMap getMap() {
        return map;
    }

    public Point getTileCollision(Sprite sprite,
        float newX, float newY)
    {
        float fromX = Math.min(sprite.getX(), newX);
        float fromY = Math.min(sprite.getY(), newY);
        float toX = Math.max(sprite.getX(), newX);
        float toY = Math.max(sprite.getY(), newY);

        // get the tile locations
        int fromTileX = TileMapRenderer.pixelsToTiles(fromX);
        int fromTileY = TileMapRenderer.pixelsToTiles(fromY);
        int toTileX = TileMapRenderer.pixelsToTiles(
            toX + sprite.getWidth() - 1);
        int toTileY = TileMapRenderer.pixelsToTiles(
            toY + sprite.getHeight() - 1);

        // check each tile for a collision
        for (int x=fromTileX; x<=toTileX; x++) {
            for (int y=fromTileY; y<=toTileY; y++) {
                if (x < 0 || x >= map.getWidth() ||
                    map.getTile(x, y) != null)
                {
                    // collision found, return the tile
                    pointCache.setLocation(x, y);
                    return pointCache;
                }
            }
        }

        // no collision found
        return null;
    }

    public boolean isCollision(Sprite s1, Sprite s2) {
        // if the Sprites are the same, return false
        if (s1 == s2) {
            return false;
        }

        // if one of the Sprites is a dead Creature, return false
        if (s1 instanceof Creature && !((Creature)s1).isAlive()) {
            return false;
        }
        if (s2 instanceof Creature && !((Creature)s2).isAlive()) {
            return false;
        }

        // get the pixel location of the Sprites
        int s1x = Math.round(s1.getX());
        int s1y = Math.round(s1.getY());
        int s2x = Math.round(s2.getX());
        int s2y = Math.round(s2.getY());

        // check if the two sprites' boundaries intersect
        return (s1x < s2x + s2.getWidth() &&
            s2x < s1x + s1.getWidth() &&
            s1y < s2y + s2.getHeight() &&
            s2y < s1y + s1.getHeight());
    }

    public Sprite getSpriteCollision(Sprite sprite) {

        // run through the list of Sprites
        Iterator i = map.getSprites();
        while (i.hasNext()) {
            Sprite otherSprite = (Sprite)i.next();
            if (isCollision(sprite, otherSprite)) {
                // collision found, return the Sprite
                return otherSprite;
            }
        }

        // no collision found
        return null;
    }

    public void update(long elapsedTime) {
        Creature player = (Creature)map.getPlayer();


        // player is dead! start map over
        if (player.getState() == Creature.STATE_DEAD) {
            map = resourceManager.reloadMap();
            return;
        }              

        // get keyboard/mouse input
        checkInput(elapsedTime);

        // update player
        updateCreature(player, elapsedTime);
        player.update(elapsedTime);

        // update other sprites
        Iterator i = map.getSprites();
        while (i.hasNext()) {
            Sprite sprite = (Sprite)i.next();
            if (sprite instanceof Creature) {
                Creature creature = (Creature)sprite;
                if (creature.getState() == Creature.STATE_DEAD) {
                    i.remove();
                }
                else {
                    updateCreature(creature, elapsedTime);
                }
            }
            // normal update
            sprite.update(elapsedTime);
        }
    }

    private void updateCreature(Creature creature,
        long elapsedTime)
    {

        // apply gravity
        if (!creature.isFlying()) {
            creature.setVelocityY(creature.getVelocityY() +
                GRAVITY * elapsedTime);
        }

        // change x
        float dx = creature.getVelocityX();
        float oldX = creature.getX();
        float newX = oldX + dx * elapsedTime;
        Point tile =
            getTileCollision(creature, newX, creature.getY());
        if (tile == null) {
            creature.setX(newX);
        }
        else {
            // line up with the tile boundary
            if (dx > 0) {
                creature.setX(
                    TileMapRenderer.tilesToPixels(tile.x) -
                    creature.getWidth());
            }
            else if (dx < 0) {
                creature.setX(
                    TileMapRenderer.tilesToPixels(tile.x + 1));
            }
            creature.collideHorizontal();
        }
        if (creature instanceof Player) {
            checkPlayerCollision((Player)creature, false);
        }

        // change y
        float dy = creature.getVelocityY();
        float oldY = creature.getY();
        float newY = oldY + dy * elapsedTime;
        tile = getTileCollision(creature, creature.getX(), newY);
        if (tile == null) {
            creature.setY(newY);
        }
        else {
            // line up with the tile boundary
            if (dy > 0) {
                creature.setY(
                    TileMapRenderer.tilesToPixels(tile.y) -
                    creature.getHeight());
            }
            else if (dy < 0) {
                creature.setY(
                    TileMapRenderer.tilesToPixels(tile.y + 1));
            }
            creature.collideVertical();
        }
        if (creature instanceof Player) {
            boolean canKill = (oldY < creature.getY());
            checkPlayerCollision((Player)creature, canKill);
        }

    }

    public void checkPlayerCollision(Player player,
        boolean canKill)
    {
        if (!player.isAlive()) {
            return;
        }

        // check for player collision with other sprites
        Sprite collisionSprite = getSpriteCollision(player);
        if (collisionSprite instanceof Items) {
            acquirePowerUp((Items)collisionSprite);
        }
        else if (collisionSprite instanceof Creature) {
            Creature badguy = (Creature)collisionSprite;
            if (canKill) {
                // kill the badguy and make player bounce
            	String fileName = "sounds/impact.wav";
        	    playSound(fileName);
                badguy.setState(Creature.STATE_DYING);
                player.setY(badguy.getY() - player.getHeight());
                player.jump(true);
            }
            else {
                // player dies!
            	String fileName = "sounds/death.wav";
        	    playSound(fileName);
                player.setState(Creature.STATE_DYING);
            }
        }
    }
    
    public void acquirePowerUp(Items powerUp) {
        // remove it from the map
        map.removeSprite(powerUp);

        if (powerUp instanceof Items.Coin) {
            // do something here, like give the player points
            String fileName = "sounds/coin.wav";
    	    playSound(fileName);
    	    
        }
        else if (powerUp instanceof Items.Door) {
            // advance to next map
        	String fileName = "sounds/door.wav";
    	    playSound(fileName);
            map = resourceManager.loadNextMap();
        }
    }

}
