package tilegame.sprites;

import java.lang.reflect.Constructor;
import graphics.*;

/**
    The Items class contains sprites that the player can pick up
*/
public abstract class Items extends Sprite {

    public Items(Animation anim) {
        super(anim);
    }


    public Object clone() {
        // use reflection to create the correct subclass
        Constructor constructor = getClass().getConstructors()[0];
        try {
            return constructor.newInstance(
                new Object[] {(Animation)anim.clone()});
        }
        catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public static class Coin extends Items {
        public Coin(Animation anim) {
            super(anim);
        }
    }

    public static class Door extends Items {
        public Door(Animation anim) {
            super(anim);
        }
    }

}
