# Project Tree
```text
src/main/java/de/ep/astralcores/
├── actionbar
│   ├── ActionBarManager.java
│   └── ActionBarMode.java
├── advancement
│   ├── advancements
│   │   └── cores
│   │       ├── AeroCoreAdvancement.java
│   │       ├── BerserkerCoreAdvancement.java
│   │       ├── ChronoCoreAdvancement.java
│   │       ├── FrostCoreAdvancement.java
│   │       ├── GaleCoreAdvancement.java
│   │       ├── GravityCoreAdvancement.java
│   │       ├── IllusionCoreAdvancement.java
│   │       ├── LeviathanCoreAdvancement.java
│   │       ├── MagnetCoreAdvancement.java
│   │       ├── NatureCoreAdvancement.java
│   │       ├── PhoenixCoreAdvancement.java
│   │       └── ShadowCoreAdvancement.java
│   └── trigger
│       ├── triggers
│       │   ├── CompassTrigger.java
│       │   ├── EntityKilledTrigger.java
│       │   ├── HasItemCountTrigger.java
│       │   ├── NetherTimeTrigger.java
│       │   ├── TraveledOnBlockTrigger.java
│       │   └── VoidSurvivalTrigger.java
│       └── TriggerRegistry.java
├── command
│   ├── actionbar
│   │   ├── ActionBarCommand.java
│   │   └── ActionBarCommandLogic.java
│   ├── activate
│   │   ├── ActivateCommand.java
│   │   └── ActivateCommandLogic.java
│   ├── astralcores
│   │   ├── AstralCoresCommand.java
│   │   └── AstralCoresCommandLogic.java
│   ├── core
│   │   ├── CoreCommand.java
│   │   └── CoreCommandLogic.java
│   ├── trust
│   │   ├── TrustCommand.java
│   │   └── TrustCommandLogic.java
│   ├── untrust
│   │   ├── UntrustCommand.java
│   │   └── UntrustCommandLogic.java
│   ├── withdraw
│   │   ├── WithdrawCommand.java
│   │   └── WithdrawCommandLogic.java
│   └── CommandRegistry.java
├── config
│   ├── Config.java
│   └── ConfigManager.java
├── core
│   ├── cores
│   │   ├── logic
│   │   │   ├── AeroCoreLogic.java
│   │   │   ├── BerserkerCoreLogic.java
│   │   │   ├── ChronoCoreLogic.java
│   │   │   ├── FrostCoreLogic.java
│   │   │   ├── GaleCoreLogic.java
│   │   │   ├── GravityCoreLogic.java
│   │   │   ├── IllusionCoreLogic.java
│   │   │   ├── LeviathanCoreLogic.java
│   │   │   ├── MagnetCoreLogic.java
│   │   │   ├── NatureCoreLogic.java
│   │   │   ├── PhoenixCoreLogic.java
│   │   │   └── ShadowCoreLogic.java
│   │   ├── AeroCore.java
│   │   ├── BerserkerCore.java
│   │   ├── ChronoCore.java
│   │   ├── FrostCore.java
│   │   ├── GaleCore.java
│   │   ├── GravityCore.java
│   │   ├── IllusionCore.java
│   │   ├── LeviathanCore.java
│   │   ├── MagnetCore.java
│   │   ├── NatureCore.java
│   │   ├── PhoenixCore.java
│   │   └── ShadowCore.java
│   ├── data
│   │   └── CoreActivationResult.java
│   ├── respawn
│   │   ├── data
│   │   │   ├── AltarData.java
│   │   │   └── CoreRespawnData.java
│   │   ├── CoreRespawnDataManager.java
│   │   └── CoreRespawnManager.java
│   ├── Core.java
│   ├── CoreFactory.java
│   ├── CoreRegistry.java
│   └── CoreType.java
├── datagen
│   ├── AstralCoresAdvancementProvider.java
│   └── AstralCoresDataGenerator.java
├── event
│   ├── logic
│   │   ├── CoreDeathLogic.java
│   │   └── CoreInteractLogic.java
│   ├── EntityCombatEventsListener.java
│   ├── PlayerEventsListener.java
│   └── ServerLifecycleEventsListener.java
├── manager
│   ├── AltarManager.java
│   ├── CoreActivateManager.java
│   ├── CoreCooldownManager.java
│   ├── CoreTickManager.java
│   ├── NetherTimeManager.java
│   └── TriggerManager.java
├── mixin
│   ├── BundleItemMixin.java
│   ├── ClientboundSetEquipmentPacketMixin.java
│   ├── CompassItemMixin.java
│   ├── HopperBlockEntityMixin.java
│   ├── InventoryMixin.java
│   ├── ItemEntityMixin.java
│   ├── LivingEntityMixin.java
│   ├── MannequinAccessor.java
│   ├── MobMixin.java
│   ├── PlayerEntityMixin.java
│   ├── ServerExplosionMixin.java
│   ├── ServerGamePacketListenerImpMixin.java
│   └── SlotAndShulkerBoxSlotMixin.java
├── playerdata
│   ├── PlayerData.java
│   └── PlayerDataManager.java
├── util
│   ├── AdvancementUtil.java
│   ├── BiomeUtils.java
│   ├── CropUtils.java
│   ├── Effects.java
│   ├── FoodUtils.java
│   ├── SaplingUtils.java
│   └── TickTimer.java
├── AstralCores.java
└── MainLoop.java
```