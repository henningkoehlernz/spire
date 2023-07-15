package nimble;

import com.evacipated.cardcrawl.modthespire.lib.ByRef;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.screens.CharSelectInfo;
import com.megacrit.cardcrawl.vfx.combat.BlockedWordEffect;
import com.megacrit.cardcrawl.vfx.combat.StrikeEffect;

import java.util.ArrayList;

public class NimblePatcher {

    // add ShrinkRayGun to starting relics
    @SpirePatch(
            clz = CharSelectInfo.class,
            method = SpirePatch.CONSTRUCTOR,
            paramtypez = {String.class, String.class, int.class, int.class, int.class, int.class, int.class, AbstractPlayer.class, ArrayList.class, ArrayList.class, boolean.class}
    )
    public static class CharSelectInfoConstructor {
        public static void Postfix(CharSelectInfo __instance, String name, String flavorText, int currentHp, int maxHp, int maxOrbs, int gold, int cardDraw, AbstractPlayer player, ArrayList<String> relics, ArrayList<String> deck, boolean resumeGame) {
            __instance.relics.add(ShrinkRayGun.ID);
        }
    }

    // damage avoidance mechanic
    @SpirePatch(
            clz = AbstractPlayer.class,
            method = "damage",
            paramtypez = {DamageInfo.class}
    )
    public static class AbstractPlayerDamage {
        public static SpireReturn<Void> Prefix(AbstractPlayer p, DamageInfo info) {
            ShrinkRayGun r = (ShrinkRayGun)p.getRelic(ShrinkRayGun.ID);
            if (r == null)
                return SpireReturn.Continue();
            // attack damage can be avoided
            if (info.type == DamageInfo.DamageType.NORMAL && info.output > 0 && info.owner != p && info.owner != null) {
                float dodgeChance = r.getDodgeChance();
                r.decreaseCurrentAgility(1);
                if (AbstractDungeon.miscRng.randomBoolean(dodgeChance)) {
                    r.flash();
                    AbstractDungeon.effectList.add(new BlockedWordEffect(p, p.hb.cX, p.hb.cY, r.DESCRIPTIONS[2]));
                    p.useStaggerAnimation();
                    p.lastDamageTaken = 0;
                    return SpireReturn.Return();
                }
                return SpireReturn.Continue();
            }
            // other damage sources apply to agility instead
            r.decreaseCurrentAgility(info.output);
            AbstractDungeon.effectList.add(new StrikeEffect(p, p.hb.cX, p.hb.cY, info.output));
            p.lastDamageTaken = 0;
            return SpireReturn.Return();
        }
    }

    // make player look smaller
    @SpirePatch(
            clz = AbstractCreature.class,
            method = "loadAnimation",
            paramtypez = {String.class, String.class, float.class}
    )
    public static class LoadAnimation {
        public static void Prefix(AbstractCreature p, String atlasUrl, String skeletonUrl, @ByRef float[] scale) {
            if (p instanceof AbstractPlayer && ((AbstractPlayer)p).hasRelic(ShrinkRayGun.ID))
                scale[0] += 0.5f;
        }
    }

}
