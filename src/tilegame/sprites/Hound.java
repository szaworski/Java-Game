package tilegame.sprites;

import graphics.Animation;

/**
    A Scoprion is a Creature that moves slowly on the ground.
*/
public class Hound extends Creature {

    public Hound(Animation left, Animation right,
        Animation deadLeft, Animation deadRight)
    {
        super(left, right, deadLeft, deadRight);
    }


    public float getMaxSpeed() {
        return 0.09f;
    }
    
    public boolean isFlying() {
        return isAlive();
    }

}
