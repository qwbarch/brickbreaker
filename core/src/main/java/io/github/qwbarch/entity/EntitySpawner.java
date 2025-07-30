package io.github.qwbarch.entity;

import com.artemis.World;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.utils.IntMap;
import com.badlogic.gdx.utils.IntSet;
import com.badlogic.gdx.utils.ObjectMap;
import io.github.qwbarch.asset.AssetMap;
import io.github.qwbarch.entity.component.*;

import javax.inject.Inject;
import javax.inject.Named;

import java.util.HashMap;

import static io.github.qwbarch.entity.system.logic.MovementCollisionSystem.PASSES;

public final class EntitySpawner {
    private final World world;
    private final AssetMap assets;
    private final float brickSize;
    private final float ballSize;
    private final float startingBallSpawnX;
    private final float startingBallSpawnY;
    private final float paddleVelocity;
    private final float paddleSpawnX;
    private final float paddleSpawnY;
    private final float paddleWidth;
    private final float paddleHeight;

    private float previousVelocity;

    @Inject
    EntitySpawner(
        World world,
        AssetMap assets,
        @Named("paddleVelocity") float paddleVelocity,
        @Named("paddleSpawnX") float paddleSpawnX,
        @Named("paddleSpawnY") float paddleSpawnY,
        @Named("paddleWidth") float paddleWidth,
        @Named("paddleHeight") float paddleHeight,
        @Named("brickSize") float brickSize,
        @Named("ballSize") float ballSize,
        @Named("startingBallSpawnX") float startingBallSpawnX,
        @Named("startingBallSpawnY") float startingBallSpawnY
    ) {
        this.world = world;
        this.assets = assets;
        this.brickSize = brickSize;
        this.ballSize = ballSize;
        this.paddleVelocity = paddleVelocity;
        this.paddleSpawnX = paddleSpawnX;
        this.paddleSpawnY = paddleSpawnY;
        this.paddleWidth = paddleWidth;
        this.paddleHeight = paddleHeight;
        this.startingBallSpawnX = startingBallSpawnX;
        this.startingBallSpawnY = startingBallSpawnY;
    }

    private void handlePlayerInput(IntSet keysPressed, LinearVelocity velocity, float multiplier) {
        var leftPressed = keysPressed.contains(Input.Keys.LEFT);
        var rightPressed = keysPressed.contains(Input.Keys.RIGHT);
        if (leftPressed == rightPressed) {
            velocity.x = 0;
        } else {
            velocity.x = leftPressed ? -paddleVelocity : paddleVelocity;
            velocity.x *= multiplier;
        }
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
        size.set(ballSize, ballSize);
        velocity.x = xVel;
        velocity.y = yVel;
        sprite.texture = assets.getBallTexture();
        collider.bounce = true;
        collider.playImpactSound = true;
    }

    public void spawnStartingBall(int paddleId) {
        var entityId = world.create();
        var position = world.edit(entityId).create(Position.class);
        var velocity = world.edit(entityId).create(LinearVelocity.class);
        var size = world.edit(entityId).create(Size.class);
        var sprite = world.edit(entityId).create(Sprite.class);
        var inputListener = world.edit(entityId).create(InputListener.class);

        position.current.x = startingBallSpawnX;
        position.current.y = startingBallSpawnY;
        position.previous.set(position.current);
        velocity.setZero();
        size.set(ballSize, ballSize);
        sprite.texture = assets.getBallTexture();

        // Keep the ball centered on the paddle if the paddle colliders into a world border.
        world.edit(paddleId).create(CollisionListener.class).listener = (var colliderId, var collidableId) -> {
            var paddlePosition = world.getMapper(Position.class).get(paddleId);
            var paddleSize = world.getMapper(Size.class).get(paddleId);

            if (velocity.x != 0) {
                previousVelocity = velocity.x;
            }
            velocity.setZero();

            // Center the ball.
            position.current.x = paddlePosition.current.x + paddleSize.width / 2f - ballSize / 2f;
            position.previous.set(position.current);
        };

        // Move the starting ball left/right using the arrow keys.
        var keysPressed = new IntSet();
        inputListener.processor = new InputAdapter() {
            @Override
            public boolean keyDown(int keycode) {
                keysPressed.add(keycode);
                handlePlayerInput(keysPressed, velocity, 1f);
                return false;
            }

            @Override
            public boolean keyUp(int keycode) {
                // If key hasn't been pressed yet, we don't want to affect the velocity.
                if (!keysPressed.contains(keycode)) return false;
                keysPressed.remove(keycode);
                handlePlayerInput(keysPressed, velocity, 1f);
                return false;
            }
        };
    }

    public int spawnPaddle() {
        var entityId = world.create();
        var position = world.edit(entityId).create(Position.class);
        var size = world.edit(entityId).create(Size.class);
        var sprite = world.edit(entityId).create(Sprite.class);
        var collider = world.edit(entityId).create(Collider.class);
        var impactSound = world.edit(entityId).create(ImpactSound.class);
        var collisionListener = world.edit(entityId).create(CollisionListener.class);
        var inputListener = world.edit(entityId).create(InputListener.class);
        var velocity = world.edit(entityId).create(LinearVelocity.class);

        world.edit(entityId).create(Collidable.class);

        position.current.x = paddleSpawnX;
        position.current.y = paddleSpawnY;
        position.previous.set(position.current);

        velocity.setZero();

        size.set(paddleWidth, paddleHeight);
        sprite.texture = assets.getPaddleTexture();

        collider.bounce = false;
        collider.playImpactSound = false;

        impactSound.sound = assets.getHardBounceSound();
        impactSound.lastPlayedTime = 0f;

        // Move the paddle left and right using the arrow keys.
        // Its velocity is multiplied by "PASSES" as a workaround for a bug with
        // MovementCollisionSystem, where collidable entities move slower by a factor
        // of "PASSES."
        var keysPressed = new IntSet();
        inputListener.processor = new InputAdapter() {
            @Override
            public boolean keyDown(int keycode) {
                keysPressed.add(keycode);
                handlePlayerInput(keysPressed, velocity, PASSES);
                return false;
            }

            @Override
            public boolean keyUp(int keycode) {
                // If key hasn't been pressed yet, we don't want to affect the velocity.
                if (!keysPressed.contains(keycode)) return false;
                keysPressed.remove(keycode);
                handlePlayerInput(keysPressed, velocity, PASSES);
                return false;
            }
        };

        return entityId;
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
