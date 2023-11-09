package org.lukasschreiber.corpsefabric.entities;

import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import org.lukasschreiber.corpsefabric.Corpse;

public class EntityTypes {
    public static final EntityType<DummyPlayerEntity> DUMMY_PLAYER_ENTITY_TYPE = Registry.register(
            Registries.ENTITY_TYPE,
            new Identifier(Corpse.NAMESPACE, "dummy_player"),
            FabricEntityTypeBuilder.create(SpawnGroup.MISC, DummyPlayerEntity::new).dimensions(EntityDimensions.fixed(0.6F, 1.95F)).build()
    );

    public static final EntityType<CorpseEntity> CORPSE_ENTITY_TYPE = Registry.register(
        Registries.ENTITY_TYPE,
        new Identifier(Corpse.NAMESPACE, "corpse"),
        FabricEntityTypeBuilder.create(SpawnGroup.MISC, CorpseEntity::new).dimensions(EntityDimensions.fixed(0.6F, 1.95F)).build()
    );
}
