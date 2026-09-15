package de.ep.astralcores.datagen;

import de.ep.astralcores.advancement.advancements.AstralCoresRootAdvancement;
import de.ep.astralcores.advancement.advancements.cores.*;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricAdvancementProvider;
import net.minecraft.advancements.*;
import net.minecraft.core.HolderLookup;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;


public class AstralCoresAdvancementProvider extends FabricAdvancementProvider {

    protected AstralCoresAdvancementProvider(
            FabricPackOutput output,
            CompletableFuture<HolderLookup.Provider> registryLookup
    ) {
        super(output, registryLookup);
    }

    @Override
    public void generateAdvancement(
            HolderLookup.Provider lookup,
            Consumer<AdvancementHolder> consumer
    ) {
        AdvancementHolder root = AstralCoresRootAdvancement.generate(consumer);

        AeroCoreAdvancement.generate(lookup, consumer, root);
        ShadowCoreAdvancement.generate(consumer, root);
        NatureCoreAdvancement.generate(lookup, consumer, root);
        ChronoCoreAdvancement.generate(lookup, consumer, root);
        LeviathanCoreAdvancement.generate(consumer, root);
        GaleCoreAdvancement.generate(lookup, consumer, root);
        PhoenixCoreAdvancement.generate(lookup, consumer, root);
        BerserkerCoreAdvancement.generate(consumer, root);
        GravityCoreAdvancement.generate(consumer, root);
        MagnetCoreAdvancement.generate(consumer, root);
        FrostCoreAdvancement.generate(lookup, consumer, root);
        IllusionCoreAdvancement.generate(consumer, root);
    }
}
