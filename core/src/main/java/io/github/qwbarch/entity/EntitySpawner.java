package io.github.qwbarch.entity;

import com.artemis.World;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.IntSet;
import io.github.qwbarch.asset.AssetMap;
import io.github.qwbarch.entity.component.*;

import javax.inject.Inject;
import javax.inject.Named;

import static io.github.qwbarch.entity.system.MovementCollisionSystem.PASSES;

public final class EntitySpawner {
    private final World world;
    private final AssetMap assets;
    private final float brickSize;
    private final float ballSize;
    private final float ballVelocity;
    private final float startingBallSpawnX;
    private final float startingBallSpawnY;
    private final float paddleVelocity;
    private final float paddleSpawnX;
    private final float paddleSpawnY;
    private final float paddleWidth;
    private final float paddleHeight;

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
        @Named("ballVelocity") float ballVelocity,
        @Named("startingBallSpawnX") float startingBallSpawnX,
        @Named("startingBallSpawnY") float startingBallSpawnY
    ) {
        this.world = world;
        this.assets = assets;
        this.brickSize = brickSize;
        this.ballSize = ballSize;
        this.ballVelocity = ballVelocity;
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

    private void launchBall(LinearVelocity velocity) {
        var angle = MathUtils.random(
            (float) Math.toRadians(10),
            (float) Math.toRadians(170)
        );
        velocity.x = ballVelocity * MathUtils.cos(angle);
        velocity.y = ballVelocity * MathUtils.sin(angle);
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
        sprite.texture = assets.getBallTexture();
        collider.bounce = true;
        collider.playImpactSound = true;
        launchBall(velocity);
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
        var paddleCollisionListener = world.edit(paddleId).create(CollisionListener.class);
        paddleCollisionListener.listener = (var colliderId, var collidableId) -> {
            var paddlePosition = world.getMapper(Position.class).get(paddleId);
            var paddleSize = world.getMapper(Size.class).get(paddleId);

            velocity.setZero();

            // Center the ball.
            position.current.x = paddlePosition.current.x + paddleSize.width / 2f - ballSize / 2f;
            position.previous.set(position.current);
        };

        // Move the starting ball left/right using the arrow keys.
        var keysPressed = new IntSet();
        inputListener.unregister = false;
        inputListener.processor = new InputAdapter() {
            @Override
            public boolean keyDown(int keycode) {
                keysPressed.add(keycode);
                handlePlayerInput(keysPressed, velocity, 1f);

                // Launch the ball.
                if (keycode == Input.Keys.SPACE) {
                    launchBall(velocity);
                    assets.getBallSpawnSound().play();

                    // Request the input listener to be removed.
                    inputListener.unregister = true;

                    // Remove the CollisionListener since it's for centering the ball
                    // when colliding with the paddle.
                    paddleCollisionListener.listener = null;
                    world.edit(entityId).remove(CollisionListener.class);

                    // Make the ball a collider that bounces.
                    var collider = world.edit(entityId).create(Collider.class);
                    collider.bounce = true;
                    collider.playImpactSound = true;
                }

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

        impactSound.sound = assets.getSoftBounceSound();
        impactSound.lastPlayedTime = 0f;

        // Move the paddle left and right using the arrow keys.
        // Its velocity is multiplied by "PASSES" as a workaround for a bug with
        // MovementCollisionSystem, where collidable entities move slower by a factor
        // of "PASSES."
        var keysPressed = new IntSet();
        inputListener.unregister = false;
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

        impactSound.sound = assets.getSoftBounceSound();
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
        } else if (startHitpoints == 2) {
            sprite.texture = assets.getYellowBrickTexture();
        } else if (startHitpoints == 1) {
            sprite.texture = assets.getRedBrickTexture();
        } else if (startHitpoints < 0) {
            sprite.texture = assets.getGreyBrickTexture();
        }

        impactSound.sound = assets.getSoftBounceSound();
        impactSound.lastPlayedTime = 0f;

        hitpoints.value = startHitpoints;

        collisionListener.listener = (var colliderId, var brickId) -> {
            var currentTime = System.nanoTime();
            var timeSinceLastHit = (currentTime - hitpoints.lastHitTime) / 1_000_000_000f;
            if (hitpoints.value > 0 && timeSinceLastHit > 0.1f) {
                hitpoints.value -= 1;
                impactSound.lastPlayedTime = currentTime;

                if (startHitpoints > 3) {
                    // Remaining hp as a percentage, from 0f to 1f;
                    var remainingHitpoints = (float) hitpoints.value / (float) startHitpoints;
                    if (remainingHitpoints > 0.6f) sprite.texture = assets.getGreenBrickTexture();
                    else if (remainingHitpoints > 0.3f) sprite.texture = assets.getYellowBrickTexture();
                    else sprite.texture = assets.getRedBrickTexture();
                } else if (hitpoints.value == 2) {
                    sprite.texture = assets.getYellowBrickTexture();
                } else if (hitpoints.value == 1) {
                    sprite.texture = assets.getRedBrickTexture();
                }
            }

            if (hitpoints.value == 0) {
                world.delete(brickId);
            }
        };
    }
}
