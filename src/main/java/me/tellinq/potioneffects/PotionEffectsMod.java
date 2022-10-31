package me.tellinq.potioneffects;

import me.tellinq.potioneffects.config.PotionEffectsConfig;
import me.tellinq.potioneffects.command.ExampleCommand;
import cc.polyfrost.oneconfig.utils.commands.CommandManager;

@net.minecraftforge.fml.common.Mod(modid = PotionEffectsMod.MODID, name = PotionEffectsMod.NAME, version = PotionEffectsMod.VERSION)
public class PotionEffectsMod {
    public static final String MODID = "@ID@";
    public static final String NAME = "@NAME@";
    public static final String VERSION = "@VER@";
    @net.minecraftforge.fml.common.Mod.Instance(MODID)
    public static PotionEffectsMod INSTANCE;
    public PotionEffectsConfig config;

    @net.minecraftforge.fml.common.Mod.EventHandler
    public void onFMLInitialization(net.minecraftforge.fml.common.event.FMLInitializationEvent event) {
        config = new PotionEffectsConfig();
        CommandManager.INSTANCE.registerCommand(ExampleCommand.class);
    }
}
