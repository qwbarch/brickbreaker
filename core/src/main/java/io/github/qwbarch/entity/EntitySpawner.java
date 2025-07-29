package io.github.qwbarch.entity;

import com.artemis.World;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import io.github.qwbarch.asset.AssetMap;
import io.github.qwbarch.entity.component.*;

import javax.inject.Inject;
import javax.inject.Named;

public final class EntitySpawner {
    private final World world;
    private final AssetMap assets;
    private final float worldWidth;
    private final float brickSize;

    @Inject
    EntitySpawner(
        World world,
        AssetMap assets,
        @Named("worldWidth") float worldWidth,
        @Named("brickSize") float brickSize
    ) {
        this.world = world;
        this.assets = assets;
        this.worldWidth = worldWidth;
        this.brickSize = brickSize;
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
        collider.playImpactSound = true;
    }

    public void spawnPaddle() {
        var entityId = world.create();
        var position = world.edit(entityId).create(Position.class);
        var size = world.edit(entityId).create(Size.class);
        var sprite = world.edit(entityId).create(Sprite.class);
        var collider = world.edit(entityId).create(Collider.class);
        var impactSound = world.edit(entityId).create(ImpactSound.class);
        world.edit(entityId).create(Collidable.class);
        world.edit(entityId).create(LinearVelocity.class);
        world.edit(entityId).create(Player.class);

        position.current.x = worldWidth / 2f;
        position.current.y = 10f;
        position.previous.set(position.current);

        size.set(12.1f, 2.42f);
        sprite.texture = assets.getPaddleTexture();

        collider.bounce = false;
        collider.playImpactSound = false;

        impactSound.sound = assets.getHardBounceSound();
        impactSound.lastPlayedTime = 0f;
    }

    public void spawnInvisibleBorder(float x, float y, float width, float height) {
        var entityId = world.create();
        var position = world.edit(entityId).create(Position.class);
        var size = world.edit(entityId).create(Size.class);
        var impactSound = world.edit(entityId).create(ImpactSound.class);
        world.edit(entityId).create(Collidable.class);

        position.current.set(x, y);
        position.previous.set(position.current);
        size.set(width, height);

        impactSound.sound = assets.getHardBounceSound();
        impactSound.lastPlayedTime = 0f;
    }

    public void spawnBrick(float x, float y) {
        spawnBrick(x, y, -1);
    }

    public void spawnBrick(float x, float y, int startHitpoints) {
        var entityId = world.create();
        var position = world.edit(entityId).create(Position.class);
        var size = world.edit(entityId).create(Size.class);
        var sprite = world.edit(entityId).create(Sprite.class);
        var impactSound = world.edit(entityId).create(ImpactSound.class);
        var collisionListener = world.edit(entityId).create(CollisionListener.class);
        var hitpoints = world.edit(entityId).create(Hitpoints.class);
        world.edit(entityId).create(Collidable.class);

        position.current.set(x, y);
        position.previous.set(position.current);

        size.set(brickSize, brickSize);

        // If brick has 3+ hp, start as a green brick.
        if (startHitpoints > 2) {
            sprite.texture = assets.getGreenBrickTexture();
        } else if (startHitpoints > 0) {
            switch (startHitpoints) {
                // If brick starts with 2 hp, start as a yellow brick.
                case 2:
                    sprite.texture = assets.getYellowBrickTexture();
                    break;
                // If brick starts with 1 hp, start as a red brick.
                case 1:
                    sprite.texture = assets.getRedBrickTexture();
                    break;
            }
        }
        // If starting hitpoints is negative, this is an invulnerable brick.
        else {
            sprite.texture = assets.getGreyBrickTexture();
        }

        impactSound.sound = assets.getHardBounceSound();
        impactSound.lastPlayedTime = 0f;

        hitpoints.value = startHitpoints;

        collisionListener.listener = (var colliderId, var brickId) -> {
            if (hitpoints.value > 0) {
                hitpoints.value -= 1;

                // Remaining hp as a percentage, from 0f to 1f;
                var remainingHitpoints = (float) hitpoints.value / (float) startHitpoints;

                if (startHitpoints > 3) {
                    if (remainingHitpoints > 0.6f) sprite.texture = assets.getGreenBrickTexture();
                    else if (remainingHitpoints > 0.3f) sprite.texture = assets.getYellowBrickTexture();
                    else sprite.texture = assets.getRedBrickTexture();
                } else {
                    switch (hitpoints.value) {
                        case 3:
                            sprite.texture = assets.getGreenBrickTexture();
                            break;
                        case 2:
                            sprite.texture = assets.getGreenBrickTexture();
                        case 1:
                            sprite.texture = assets.getGreenBrickTexture();
                    }
                }
            }

            if (hitpoints.value == 0) {
                world.delete(brickId);
            }
        };
    }
}
