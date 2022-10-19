# Potion Effects
A mod from [OneConfig](https://github.com/Polyfrost/OneConfig) that allows you to see the active potion effects!

# NOTE
### Because [OneConfig](https://github.com/Polyfrost/OneConfig) is still in development, this mod is not completed. Some bugs may appear due to issues with OneConfig. In the meantime, you may still compile the mod yourself and try it out.
Once OneConig releases and this is ensured that this is also in a releasable state, I will make a release!

I also want to note that I have distributed jars privately. The main thing blocking me from releasing this is that this mod doesn't appear to work when no other OneConfig mod loads. I am not really familiar with Gradle and Mixin so I do believe this is more of my fault for lack of experience with both (and I have a burning hatred for Gradle as 99% of my projects are using Maven).

This mod also has some limitations as I am waiting for certain functions in OneConfig to either be implemented or accessible (mainly anchor points being private, and I need them to shift text and icon positions when you move the potion effects around)

## Main Features

- Unlike most potion effects mods, this potion effects mod allows you to customize effects on a global *and* individual basis.
  - You can also only override certain options to be on an individual basis instead of all.

![](https://i.imgur.com/crcC6ze.png)
- This version of Potion Effects allows you to fine tune when a potion effect should show such as limiting allowed duration times

![](https://i.imgur.com/8qO7Mr5.png)
- This mod also includes standard potion effects options that you may see in other mods such as:
  - The ability to change the color of the name and duration text
  - Make the effects blink when the duration is low
  - Make only certain components appear
  - Change how the amplifiers are shown
  - Format how the individual text looks

More customization options can be found when using the mod!

## Credits
I originally made improvements from the original Potion Effects mod in [CheatBreaker 2](https://cheatbreaker2.com). Most options such as excluding options have been ported to this. However, CheatBreaker 2 nor myself are the original creators of this mod hence why the following are listed below:

- Mojang themselves actually display Potion Effects in your inventory
- [bsprks and jadedcat](https://www.curseforge.com/minecraft/mc-mods/statuseffecthud) for making the original StatusEffectsHUD mod, allowing you to see effects active outside of your inventory.
- [powns](https://www.youtube.com/c/pownsdev) for originally making a potion effects mod that allowed individual potion effects customization and allowing name formatting
