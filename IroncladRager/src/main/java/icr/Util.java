package icr;

import com.megacrit.cardcrawl.characters.AbstractPlayer;

public class Util {

    public static boolean isBloodied(AbstractPlayer player) {
        return player.currentHealth <= player.maxHealth / 2;
    }

}
