package me.tellinq.potioneffects.command;

import cc.polyfrost.oneconfig.utils.commands.annotations.Command;
import cc.polyfrost.oneconfig.utils.commands.annotations.Main;

import me.tellinq.potioneffects.PotionEffectsMod;

@Command(
        value = PotionEffectsMod.MODID,
        description = "Access the " + PotionEffectsMod.NAME + " GUI.")
public class PotionEffectsCommand {

    /** Opens OneConfig's GUI when the user runs /potioneffects */
    @Main
    private static void main() {
        PotionEffectsMod.INSTANCE.config.openGui();
    }
}
