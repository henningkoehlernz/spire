package evolution;

import basemod.abstracts.CustomRelic;
import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.actions.common.RelicAboveCreatureAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import com.megacrit.cardcrawl.rooms.MonsterRoomBoss;

public class Axolotl extends CustomRelic {

    public static final String ID = "EVO:Axolotl";
    private static final String IMG_PATH = Evolution.IMG_PATH + "axolotl.png";

    public Axolotl() {
        super(ID, new Texture(IMG_PATH), RelicTier.STARTER, LandingSound.MAGICAL);
    }

    @Override
    public void onVictory() {
        AbstractPlayer p = AbstractDungeon.player;
        AbstractRoom r = AbstractDungeon.getCurrRoom();
        if ( r instanceof MonsterRoomBoss && p.currentHealth > 0 ) {
            this.flash();
            this.addToTop(new RelicAboveCreatureAction(p, this));
            this.counter++;
            Evolution.addEvolution(p.chosenClass, AbstractDungeon.ascensionLevel, 1);
        }
    }

    @Override
    public boolean canSpawn() { return false; }

    @Override
    public String getUpdatedDescription() {
        return DESCRIPTIONS[0];
    }

}
