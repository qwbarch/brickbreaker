package io.github.qwbarch.entity;

import com.artemis.World;
import io.github.qwbarch.asset.AssetMap;
import io.github.qwbarch.entity.component.LinearVelocity;
import io.github.qwbarch.entity.component.Position;
import io.github.qwbarch.entity.component.Size;
import io.github.qwbarch.entity.component.Sprite;

import javax.inject.Inject;
import javax.inject.Named;

public final class EntitySpawner {
    private final World world;
    private final AssetMap assets;
    private final float worldWidth;
    private final float worldHeight;

    @Inject
    EntitySpawner(
        World world,
        AssetMap assets,
        @Named("worldWidth") float worldWidth,
        @Named("worldHeight") float worldHeight
    ) {
        this.world = world;
        this.assets = assets;
        this.worldWidth = worldWidth;
        this.worldHeight = worldHeight;
    }

    public int spawnBall(float x, float y) {
        System.out.println("spawning ball");
        var entityId = world.create();
        var position = world.edit(entityId).create(Position.class);
        var velocity = world.edit(entityId).create(LinearVelocity.class);
        var size = world.edit(entityId).create(Size.class);
        var sprite = world.edit(entityId).create(Sprite.class);

        position.current.set(x, y);
        position.previous.set(position.current);

        size.set(1.21f, 1.21f);
        velocity.x = 140f;
        velocity.y = 100f;

        sprite.texture = assets.getBallTexture();

        System.out.println("done spawning");
        return entityId;
    }

    public int spawnPaddle() {
        System.out.println("spawning paddle");
        var entityId = world.create();
        var position = world.edit(entityId).create(Position.class);
        var size = world.edit(entityId).create(Size.class);
        var sprite = world.edit(entityId).create(Sprite.class);

        position.current.x = worldWidth / 2f;
        position.current.y = 10f;
        position.previous.set(position.current);

        size.set(12.1f, 2.42f);

        sprite.texture = assets.getPaddleTexture();

        return entityId;
    }
}
