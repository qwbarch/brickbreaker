package io.github.qwbarch.entity.system;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.EntitySubscription;
import com.artemis.annotations.All;
import com.artemis.utils.IntBag;
import com.badlogic.gdx.utils.*;
import io.github.qwbarch.entity.component.*;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Handles the movement and collision of entities.<br />
 * Why both at once? It was originally separated, but I couldn't figure out how to avoid the tunneling issue
 * without doing multiple passes in a single game tick. <br />
 */
@All({Position.class, LinearVelocity.class, Size.class})
@Singleton
public final class MovementCollisionSystem extends LogicSystem {
    /**
     * Entities do multiple movement / collision detection passes
     * in order to avoid the tunneling issue (where entities
     * clip through collidable objects if the entity is moving too fast).
     * <p/>
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

    private final int gridCellSize;

    // Component mappers are automatically injected via artemis-odb,
    // which gives access to the entity's components.
    private ComponentMapper<Position> positions;
    private ComponentMapper<LinearVelocity> velocities;
    private ComponentMapper<Size> sizes;
    private ComponentMapper<Collider> colliders;
    private ComponentMapper<Collidable> collidables;
    private ComponentMapper<CollisionListener> collisionListeners;
    private ComponentMapper<ImpactSound> impactSounds;

    /**
     * Used to iterate over all collidable entities.
     */
    private EntitySubscription collidableSubscription;

    /**
     * Used for splitting up the world into a grid of cells.
     * Collision detection is only performed on the closest cells of the collider.
     */
    private final LongMap<IntBag> spatialGrid = new LongMap<>();

    @Inject
    public MovementCollisionSystem(
        @Named("secondsPerTick") float secondsPerTick,
        @Named("gridCellSize") int gridCellSize
    ) {
        this.secondsPerTick = secondsPerTick;
        this.gridCellSize = gridCellSize;
    }

    @Override
    protected void initialize() {
        collidableSubscription =
            world
                .getAspectSubscriptionManager()
                .get(Aspect.all(Position.class, Size.class, Collidable.class));
    }

    @Override
    protected void begin() {
        var collidableEntities = collidableSubscription.getEntities();

        // Re-calculate the available collidable entities for the spatial
        // grid on every game tick.
        spatialGrid.clear();
        for (var i = 0; i < collidableEntities.size(); i++) {
            int entityId = collidableEntities.get(i);
            if (!collidables.has(entityId)) continue;

            var position = positions.get(entityId).current;
            var size = sizes.get(entityId);
            var minCellX = (int) Math.floor(position.x / gridCellSize);
            var maxCellX = (int) Math.floor((position.x + size.width) / gridCellSize);
            var minCellY = (int) Math.floor(position.y / gridCellSize);
            var maxCellY = (int) Math.floor((position.y + size.height) / gridCellSize);

            // Store the collidable entities in their respective cells.
            for (int cellX = minCellX; cellX <= maxCellX; cellX++) {
                for (int cellY = minCellY; cellY <= maxCellY; cellY++) {
                    // Store cellX and cellY into a single long key:
                    // https://stackoverflow.com/questions/12772939/java-storing-two-ints-in-a-long
                    var key = (((long) cellX) << 32) | (cellY & 0xffffffffL);
                    var bag = spatialGrid.get(key);
                    if (bag == null) {
                        bag = new IntBag();
                        spatialGrid.put(key, bag);
                    }
                    bag.add(entityId);
                }
            }
        }
    }

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

        // Do multiple passes to avoid the tunneling issue.
        for (var pass = 0; pass < PASSES; pass++) {
            var previousLeft = position.current.x;
            var previousBottom = position.current.y;
            var previousRight = previousLeft + size.width;
            var previousTop = previousBottom + size.height;

            // Update the entity's position.
            position.current.x += velocity.x * secondsPerTick * SECONDS_PER_PASS;
            position.current.y += velocity.y * secondsPerTick * SECONDS_PER_PASS;

            if (!colliders.has(entityId)) continue;
            var collider = colliders.get(entityId);

            // Collider's current position/bounds.
            var currentLeft = position.current.x;
            var currentBottom = position.current.y;
            var currentRight = currentLeft + size.width;
            var currentTop = currentBottom + size.height;

            // Grid cell bounds to check for.
            var minCellX = (int) Math.floor(currentLeft / gridCellSize);
            var maxCellX = (int) Math.floor(currentRight / gridCellSize);
            var minCellY = (int) Math.floor(currentBottom / gridCellSize);
            var maxCellY = (int) Math.floor(currentTop / gridCellSize);

            // Track tested collidable entities to avoid duplicate tests.
            var collidableTested = new IntSet();

            // Check for collisions over overlapping cells.
            for (int cellX = minCellX; cellX <= maxCellX; cellX++) {
                for (int cellY = minCellY; cellY <= maxCellY; cellY++) {
                    var key = (((long) cellX) << 32L) | (cellY & 0xffffffffL);
                    var bucket = spatialGrid.get(key);
                    if (bucket == null) continue;

                    for (var i = 0; i < bucket.size(); i++) {
                        var collidableId = bucket.get(i);

                        if (!collidableTested.add(collidableId)) continue;
                        if (!collidables.has(collidableId)) continue;

                        var collidablePosition = positions.get(collidableId).current;
                        var collidableSize = sizes.get(collidableId);

                        // Position/bounds of the collidable entity.
                        var collidableLeft = collidablePosition.x;
                        var collidableBottom = collidablePosition.y;
                        var collidableRight = collidableLeft + collidableSize.width;
                        var collidableTop = collidableBottom + collidableSize.height;

                        // Collision detection of entities.
                        // https://lazyfoo.net/tutorials/SDL/27_collision_detection/index.php
                        //
                        // If entity is within collision bounds.
                        // This doesn't necessarily mean a collision has happened,
                        // which requires the below checks to be exact.
                        if (currentLeft < collidableRight
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
                            }
                            // Collided into the top of the entity.
                            else if (previousBottom >= collidableTop) {
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
        }
    }

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

        // Run the collision listener function for the collider and the collidable entities.
        if (collisionListeners.has(colliderId)) {
            var collisionListener = collisionListeners.get(colliderId);
            var currentTime = System.nanoTime();
            var timeSinceLastPlayed = (currentTime - collisionListener.lastCollisionTime) / 1_000_000_000f;
            if (collisionListener.listener != null && timeSinceLastPlayed > 0.1f) {
                collisionListener.lastCollisionTime = currentTime;
                collisionListener.listener.collide(colliderId, collidableId);
            }
        }
        if (collisionListeners.has(collidableId)) {
            var collisionListener = collisionListeners.get(collidableId);
            var currentTime = System.nanoTime();
            var timeSinceLastPlayed = (currentTime - collisionListener.lastCollisionTime) / 1_000_000_000f;
            if (collisionListener.listener != null && timeSinceLastPlayed > 0.1f) {
                collisionListener.lastCollisionTime = currentTime;
                collisionListener.listener.collide(colliderId, collidableId);
            }
        }
    }
}
