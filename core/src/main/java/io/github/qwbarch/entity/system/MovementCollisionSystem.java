package io.github.qwbarch.entity.system;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.EntitySubscription;
import com.artemis.annotations.All;
import com.artemis.utils.IntBag;
import io.github.qwbarch.dagger.scope.ScreenScope;
import io.github.qwbarch.entity.component.*;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * Handles the movement and collision of entities.<br />
 * Why both at once? It was originally separated, but I couldn't figure out how to avoid the tunneling issue
 * without doing multiple passes in a single game tick. <br />
 */
@ScreenScope
@All({Position.class, LinearVelocity.class, Size.class})
public final class MovementCollisionSystem extends LogicSystem {
    /**
     * Entities do multiple movement / collision detection passes
     * in order to avoid the tunneling issue (where entities
     * clip through collidable objects if the entity is moving too fast).
     * <br />
     * The higher # of passes, the less likely tunneling happens.
     * However, the higher # of passes, the higher the cpu usage as well.
     */
    private static final int PASSES = 4;
    private static final float SECONDS_PER_PASS = 1f / PASSES;

    /**
     * Amount of real-world seconds that passes by per game tick.
     * This is used to make movement a set distance no matter the game's ticks per second.
     * <br />
     * Whether the ticks per second is 15 or 60 for example, entities will move the same distance.
     */
    private final float secondsPerTick;

    // Component mappers are automatically injected via artemis-odb,
    // which gives access to the entity's components.
    private ComponentMapper<Position> positions;
    private ComponentMapper<LinearVelocity> velocities;
    private ComponentMapper<Size> sizes;
    private ComponentMapper<Collider> colliders;
    private ComponentMapper<Collidable> collidables;
    private ComponentMapper<CollisionListener> collisionListeners;
    private ComponentMapper<ImpactSound> impactSounds;

    // Allows us to iterate over collidable entities.
    private EntitySubscription collidableSubscription;
    private IntBag collidableEntities;

    @Inject
    public MovementCollisionSystem(
        @Named("secondsPerTick") float secondsPerTick
    ) {
        this.secondsPerTick = secondsPerTick;
    }

    @Override
    protected void initialize() {
        // Subscribe to collidable entities to read from later on, for collision detection purposes.
        collidableSubscription =
            world
                .getAspectSubscriptionManager()
                .get(Aspect.all(Position.class, Size.class, Collidable.class));
    }

    @Override
    protected void begin() {
        // Begin method is called once every game logic tick.
        // This updates the collidable entities every tick.
        collidableEntities = collidableSubscription.getEntities();
    }

    /**
     * Called once per game tick on every entity that matches the specified aspects above
     * (from the @All, @One, etc. annotations).
     */
    @Override
    protected void process(int entityId) {
        // Get the components of the current entity to process.
        var position = positions.get(entityId);
        var velocity = velocities.get(entityId);
        var size = sizes.get(entityId);

        // Update the previous position to the last frame.
        // This is needed for the render system to interpolate
        // between the previous and current game tick,
        // smoothing out the animation for users who
        // have higher fps than the game's tick rate.
        position.previous.set(position.current);

        // Do multiple passes to avoid tunneling issue.
        for (var pass = 0; pass < PASSES; pass++) {
            // Position at the previous game state (before updating position).
            var previousLeft = position.current.x;
            var previousBottom = position.current.y;
            var previousRight = previousLeft + size.width;
            var previousTop = previousBottom + size.height;

            // Update the entity's position.
            //
            // Since they move extra times due to # of passes, each movement is shorter,
            // so they eventually move the same distance as if we weren't doing multiple passes.
            position.current.x += velocity.x * secondsPerTick * SECONDS_PER_PASS;
            position.current.y += velocity.y * secondsPerTick * SECONDS_PER_PASS;

            // Only do collision detection if the current entity is a collider,
            // otherwise only the above movement is applied.
            if (!colliders.has(entityId)) continue;
            var collider = colliders.get(entityId);

            // Position at the current game state (after updating position).
            var currentLeft = position.current.x;
            var currentBottom = position.current.y;
            var currentRight = currentLeft + size.width;
            var currentTop = currentBottom + size.height;

            // Collision detection on every collidable entity.
            // This is inefficient since it's unnecessarily detecting collisions over entities that aren't even close.
            //
            // TODO: Optimize this to only detect collision on nearby entities.
            // E.g. using a quad tree to process only within one quadrant.
            for (var i = 0; i < collidableEntities.size(); i++) {
                var collidableId = collidableEntities.get(i);

                if (!collidables.has(collidableId)) continue;

                var collidablePosition = positions.get(collidableId);
                var collidableSize = sizes.get(collidableId);

                // Position of the collidable entity.
                var collidableLeft = collidablePosition.current.x;
                var collidableBottom = collidablePosition.current.y;
                var collidableRight = collidableLeft + collidableSize.width;
                var collidableTop = collidableBottom + collidableSize.height;

                // Collision detection of entities marked as a BouncySurface.
                // https://lazyfoo.net/tutorials/SDL/27_collision_detection/index.php

                // If entity is within collision bounds.
                // This doesn't necessarily mean a collision has happened,
                // which requires the below checks to be exact.
                if (
                    currentLeft < collidableRight
                        && currentRight > collidableLeft
                        && currentBottom < collidableTop
                        && currentTop > collidableBottom
                ) {
                    // Collided into the left of the entity.
                    if (previousRight <= collidableLeft) {
                        if (!collider.ghosted) {
                            position.current.x = collidableLeft - size.width;
                            if (collider.bounce) velocity.reverseX();
                            else velocity.setZero();
                        }
                        handleCollision(entityId, collidableId);
                    }
                    // Collided into the right of the entity.
                    else if (previousLeft >= collidableRight) {
                        if (!collider.ghosted) {
                            position.current.x = collidableRight;
                            if (collider.bounce) velocity.reverseX();
                            else velocity.setZero();
                        }
                        handleCollision(entityId, collidableId);
                    }
                    // Collided into the bottom of the entity.
                    else if (previousTop <= collidableBottom) {
                        if (!collider.ghosted) {
                            position.current.y = collidableBottom - size.height;
                            if (collider.bounce) velocity.reverseY();
                            else velocity.setZero();
                        }
                        handleCollision(entityId, collidableId);
                    } else if (previousBottom >= collidableTop) {
                        if (!collider.ghosted) {
                            position.current.y = collidableTop;
                            if (collider.bounce) velocity.reverseY();
                            else velocity.setZero();
                        }
                        handleCollision(entityId, collidableId);
                    }
                }
            }
        }
    }

    /**
     * Play the impact sound if it isn't on cooldown already.
     */
    private void handleCollision(int colliderId, int collidableId) {
        // Play the impact sound if it isn't currently on cooldown.
        if (colliders.get(colliderId).playImpactSound && impactSounds.has(collidableId)) {
            var impactSound = impactSounds.get(collidableId);
            var currentTime = System.nanoTime();
            var timeSinceLastPlayed = (currentTime - impactSound.lastPlayedTime) / 1_000_000_000f;
            if (impactSound.sound != null && timeSinceLastPlayed > 0.1f) {
                impactSound.lastPlayedTime = currentTime;
                impactSound.sound.play();
            }
        }

        // Run the collision listener function.
        if (collisionListeners.has(colliderId)) {
            var listener = collisionListeners.get(colliderId).listener;
            if (listener != null) listener.collide(colliderId, collidableId);
        }
        if (collisionListeners.has(collidableId)) {
            var listener = collisionListeners.get(collidableId).listener;
            if (listener != null) listener.collide(colliderId, collidableId);
        }
    }
}
