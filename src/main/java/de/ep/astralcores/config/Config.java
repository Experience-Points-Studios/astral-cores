package de.ep.astralcores.config;

import blue.endless.jankson.Comment;

// Defines the root configuration structure for the mod settings
public class Config {

    @Comment("General core mechanics and server rules")
    public General general = new General();
    public Cores cores = new Cores();


    // Nested configurations containing base server rules
    public static class General {
        @Comment("Should the equipped core be removed from the slot when a player dies?")
        public boolean drop_core_on_death = true;
    }

    public static class Cores {
        public AeroCore aero_core = new AeroCore();
        public BerserkerCore berserker_core = new BerserkerCore();
        public ChronoCore chrono_core = new ChronoCore();
        public FrostCore frost_core = new FrostCore();
        public GaleCore gale_core = new GaleCore();
        public GravityCore gravity_core = new GravityCore();
        public IllusionCore illusion_core = new IllusionCore();
        public LeviathanCore leviathan_core = new LeviathanCore();
        public MagnetCore magnet_core = new MagnetCore();
        public NatureCore nature_core = new NatureCore();
        public PhoenixCore phoenix_core = new PhoenixCore();
        public ShadowCore shadow_core = new ShadowCore();
    }

    public static class AeroCore {
        public int active_cooldown = 45;
    }
    public static class BerserkerCore {
        public int active_cooldown = 300;
    }
    public static class ChronoCore {
        public int active_cooldown = 45;
    }
    public static class FrostCore {
        public int active_cooldown = 25;
    }
    public static class GaleCore {
        public int active_cooldown = 30;
    }
    public static class GravityCore {
        public int active_cooldown = 25;
    }
    public static class IllusionCore {
        public int active_cooldown = 75;
    }
    public static class LeviathanCore {
        public int active_cooldown = 30;
    }
    public static class MagnetCore {
        public int active_cooldown = 20;
    }
    public static class NatureCore {
        public int active_cooldown = 60;
    }
    public static class PhoenixCore {
        public int active_cooldown = 30;
    }
    public static class ShadowCore {
        public int active_cooldown = 40;
    }
}
