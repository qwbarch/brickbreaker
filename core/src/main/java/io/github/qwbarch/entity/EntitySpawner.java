package io.github.qwbarch.entity;

import com.artemis.World;
import io.github.qwbarch.asset.AssetMap;
import io.github.qwbarch.entity.component.*;

import javax.inject.Inject;
import javax.inject.Named;

public final class EntitySpawner {
    public static final String PADDLE_TAG = "PADDLE";

    private final World world;
    private final AssetMap assets;
    private final float worldWidth;

    @Inject
    EntitySpawner(
        World world,
        AssetMap assets,
        @Named("worldWidth") float worldWidth
    ) {
        this.world = world;
        this.assets = assets;
        this.worldWidth = worldWidth;
    }

    public void spawnBall(float x, float y, float xVel, float yVel) {
        var entityId = world.create();
        var position = world.edit(entityId).create(Position.class);
        var velocity = world.edit(entityId).create(LinearVelocity.class);
        var size = world.edit(entityId).create(Size.class);
        var sprite = world.edit(entityId).create(Sprite.class);
        var collider = world.edit(entityId).create(Collider.class);

        position.current.set(x, y);
        position.previous.set(position.current);

        size.set(1.3f, 1.3f);
        velocity.x = xVel;
        velocity.y = yVel;

        sprite.texture = assets.getBallTexture();
        collider.bounce = true;
    }

    public void spawnPaddle() {
        var entityId = world.create();
        var position = world.edit(entityId).create(Position.class);
        var size = world.edit(entityId).create(Size.class);
        var sprite = world.edit(entityId).create(Sprite.class);
        var collider = world.edit(entityId).create(Collider.class);
        world.edit(entityId).create(Collidable.class);
        world.edit(entityId).create(LinearVelocity.class);
        world.edit(entityId).create(Player.class);

        position.current.x = worldWidth / 2f;
        position.current.y = 10f;
        position.previous.set(position.current);

        size.set(12.1f, 2.42f);
        sprite.texture = assets.getPaddleTexture();

        collider.bounce = false;
    }

    public void spawnInvisibleBorder(float x, float y, float width, float height) {
         var entityId = world.create();
         var position = world.edit(entityId).create(Position.class);
         var size = world.edit(entityId).create(Size.class);
         world.edit(entityId).create(Collidable.class);

         position.current.set(x, y);
         position.previous.set(position.current);
         size.set(width, height);
    }
}
