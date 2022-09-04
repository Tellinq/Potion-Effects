package cc.polyfrost.example.command;

import cc.polyfrost.example.PotionEffectsMod;
import cc.polyfrost.oneconfig.utils.commands.annotations.Command;
import cc.polyfrost.oneconfig.utils.commands.annotations.Main;

@Command(value = PotionEffectsMod.MODID, description = "Access the " + PotionEffectsMod.NAME + " GUI.")
public class ExampleCommand {

    @Main
    private static void main() {
        PotionEffectsMod.INSTANCE.config.openGui();
    }
}