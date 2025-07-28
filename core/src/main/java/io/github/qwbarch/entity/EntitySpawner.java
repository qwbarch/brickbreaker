package io.github.qwbarch.entity;

import com.artemis.World;
import io.github.qwbarch.asset.AssetMap;
import io.github.qwbarch.entity.component.LinearVelocity;
import io.github.qwbarch.entity.component.Position;
import io.github.qwbarch.entity.component.Sprite;

import javax.inject.Inject;

public final class EntitySpawner {
    private final World world;
    private final AssetMap assets;

    @Inject
    EntitySpawner(World world, AssetMap assets) {
       this.world = world;
       this.assets = assets;
    }

    public int spawnBall(float x, float y) {
        System.out.println("spawning ball");
        var entityId = world.create();
        var position = world.edit(entityId).create(Position.class);
        var velocity = world.edit(entityId).create(LinearVelocity.class);
        var sprite = world.edit(entityId).create(Sprite.class);

        position.current.set(x, y);
        position.previous.set(position.current);

        velocity.y = 10f;

        sprite.texture = assets.getBallTexture();

        System.out.println("done spawning");
        return entityId;
    }
}
