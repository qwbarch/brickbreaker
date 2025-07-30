package io.github.qwbarch.screen.level;

import com.artemis.World;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.ObjectSet;
import io.github.qwbarch.entity.EntitySpawner;
import io.github.qwbarch.screen.LevelScreen;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

@Singleton
public final class Level1Screen extends LevelScreen {
    private final EntitySpawner spawner;
    private final float brickSize;
    private final float worldHeight;

    @Inject
    Level1Screen(
        World world,
        SpriteBatch batch,
        EntitySpawner spawner,
        @Named("worldWidth") float worldWidth,
        @Named("worldHeight") float worldHeight,
        @Named("brickSize") float brickSize,
        @Named("worldBackground") Color worldBackground
    ) {
        super(world, batch, spawner, worldWidth, worldHeight, worldBackground);
        System.out.println("level 1 constructor");
        this.brickSize = brickSize;
        this.spawner = spawner;
        this.worldHeight = worldHeight;
    }

    @Override
    public void show() {
        super.show();

        // 9 right (0  index)
        // 2 down

        var greenBricks = new ObjectSet<Vector2>();
        greenBricks.addAll(new Vector2[]{
            // Letter "H"
            new Vector2(5, 1),
            new Vector2(5, 2),
            new Vector2(5, 3),
            new Vector2(5, 4),
            new Vector2(5, 5),
            new Vector2(6, 3),
            new Vector2(7, 1),
            new Vector2(7, 2),
            new Vector2(7, 3),
            new Vector2(7, 4),
            new Vector2(7, 5),

            // Letter "E"
            new Vector2(9, 1),
            new Vector2(10, 1),
            new Vector2(11, 1),
            new Vector2(9, 2),
            new Vector2(9, 3),
            new Vector2(9, 4),
            new Vector2(9, 5),
            new Vector2(10, 3),
            new Vector2(11, 3),
            new Vector2(10, 5),
            new Vector2(11, 5),

            // Letter "L"
            new Vector2(13, 1),
            new Vector2(13, 2),
            new Vector2(13, 3),
            new Vector2(13, 4),
            new Vector2(13, 5),
            new Vector2(14, 5),
            new Vector2(15, 5),

            // Letter "L"
            new Vector2(17, 1),
            new Vector2(17, 2),
            new Vector2(17, 3),
            new Vector2(17, 4),
            new Vector2(17, 5),
            new Vector2(18, 5),
            new Vector2(19, 5),

            // Letter "O"
            new Vector2(21, 1),
            new Vector2(21, 2),
            new Vector2(21, 3),
            new Vector2(21, 4),
            new Vector2(21, 5),
            new Vector2(24, 1),
            new Vector2(24, 2),
            new Vector2(24, 3),
            new Vector2(24, 4),
            new Vector2(24, 5),
            new Vector2(22, 1),
            new Vector2(23, 1),
            new Vector2(22, 5),
            new Vector2(23, 5),

            // Letter "W"
            new Vector2(3, 7),
            new Vector2(3, 8),
            new Vector2(3, 9),
            new Vector2(3, 10),
            new Vector2(4, 10),
            new Vector2(5, 10),
            new Vector2(6, 10),
            new Vector2(7, 10),
            new Vector2(5, 9),
            new Vector2(5, 8),
            new Vector2(7, 9),
            new Vector2(7, 8),
            new Vector2(7, 7),

            // Letter "O"
            new Vector2(9, 7),
            new Vector2(9, 8),
            new Vector2(9, 9),
            new Vector2(9, 10),
            new Vector2(10, 7),
            new Vector2(11, 7),
            new Vector2(10, 10),
            new Vector2(11, 10),
            new Vector2(12, 7),
            new Vector2(12, 8),
            new Vector2(12, 9),
            new Vector2(12, 10),

            // Letter "O"
            new Vector2(14, 7),
            new Vector2(14, 8),
            new Vector2(14, 9),
            new Vector2(14, 10),
            new Vector2(15, 8),
            new Vector2(16, 7),
            new Vector2(17, 7),

            // Letter "L"
            new Vector2(19, 7),
            new Vector2(19, 8),
            new Vector2(19, 9),
            new Vector2(19, 10),
            new Vector2(20, 10),
            new Vector2(21, 10),

            // Letter "L"
            new Vector2(23, 7),
            new Vector2(23, 8),
            new Vector2(23, 9),
            new Vector2(23, 10),
            new Vector2(24, 7),
            new Vector2(24, 10),
            new Vector2(25, 9),
            new Vector2(25, 8),

            // Symbol "!"
            new Vector2(27, 1),
            new Vector2(27, 2),
            new Vector2(27, 3),
            new Vector2(28, 1),
            new Vector2(28, 2),
            new Vector2(28, 3),
            new Vector2(27, 4),
            new Vector2(27, 5),
            new Vector2(27, 6),
            new Vector2(27, 7),
            new Vector2(27, 9),
            new Vector2(27, 10),
            new Vector2(28, 4),
            new Vector2(28, 5),
            new Vector2(28, 6),
            new Vector2(28, 7),
            new Vector2(28, 9),
            new Vector2(28, 10)
        });

        var currentBrick = new Vector2();
        for (var i = 0; i < 30; i++) {
            for (var j = 0; j < 12; j++) {
                currentBrick.set(i, j);
                var startHitpoints =
                    greenBricks.contains(currentBrick)
                        ? 3
                        : 1;
                spawner.spawnBrick(
                    i * brickSize,
                    worldHeight - (j + 1) * brickSize,
                    startHitpoints
                );
            }
        }
    }
}
