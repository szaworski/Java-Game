package tilegame.sprites;

import graphics.Animation;

/**
    A Bat is a Creature that fly slowly in the air.
*/
public class Bat extends Creature {

    public Bat(Animation left, Animation right,
        Animation deadLeft, Animation deadRight)
    {
        super(left, right, deadLeft, deadRight);
    }


    public float getMaxSpeed() {
        return 0.2f;
    }


    public boolean isFlying() {
        return isAlive();
    }

}
