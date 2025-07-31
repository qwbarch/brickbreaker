package io.github.qwbarch.screen.level;

import com.artemis.World;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import dagger.Lazy;
import io.github.qwbarch.asset.AssetMap;
import io.github.qwbarch.entity.EntitySpawner;
import io.github.qwbarch.screen.LevelScreen;
import io.github.qwbarch.screen.MenuScreen;
import io.github.qwbarch.screen.ScreenHandler;

import javax.inject.Inject;
import javax.inject.Named;

public class Level2Screen extends LevelScreen {
    private static final String START_LABEL = "Level 2.\n\nDon't even bother trying.";

    private final EntitySpawner spawner;
    private final float brickSize;

    @Inject
    Level2Screen(
        World world,
        SpriteBatch batch,
        EntitySpawner spawner,
        @Named("worldWidth") float worldWidth,
        @Named("worldHeight") float worldHeight,
        @Named("worldBackground") Color worldBackground,
        AssetMap assets,
        GlyphLayout glyphLayout,
        InputMultiplexer inputMultiplexer,
        ScreenHandler screenHandler,
        Lazy<MenuScreen> menuScreen,
        @Named("brickSize") float brickSize
    ) {
        super(
            START_LABEL,
            world,
            batch,
            spawner,
            worldWidth,
            worldHeight,
            worldBackground,
            assets,
            glyphLayout,
            inputMultiplexer,
            screenHandler,
            menuScreen
        );
        System.out.println("level 1 constructor");
        this.spawner = spawner;
        this.brickSize = brickSize;
    }

    @Override
    public void show() {
        super.show();

        // Left grey bricks.
        for (var i = 4; i < 19; i++) {
            spawner.spawnBrick(brickSize * 3, i * brickSize);
        }
        spawner.spawnBrick(brickSize * 4, 18 * brickSize);
        spawner.spawnBrick(brickSize * 4, 19 * brickSize);
        spawner.spawnBrick(brickSize * 5, 19 * brickSize);
        spawner.spawnBrick(brickSize * 5, 20 * brickSize);
        spawner.spawnBrick(brickSize * 6, 20 * brickSize);
        spawner.spawnBrick(brickSize * 6, 21 * brickSize);
        spawner.spawnBrick(brickSize * 7, 21 * brickSize);
        spawner.spawnBrick(brickSize * 8, 21 * brickSize);
        spawner.spawnBrick(brickSize * 9, 21 * brickSize);
        spawner.spawnBrick(brickSize * 10, 21 * brickSize);
        spawner.spawnBrick(brickSize * 2, brickSize * 12);
        spawner.spawnBrick(brickSize, brickSize * 12);
        spawner.spawnBrick(0, brickSize * 12);

        // Right grey bricks.
        for (var i = 4; i < 19; i++) {
            spawner.spawnBrick(brickSize * 26, i * brickSize);
        }
        spawner.spawnBrick(brickSize * 25, 18 * brickSize);
        spawner.spawnBrick(brickSize * 25, 19 * brickSize);
        spawner.spawnBrick(brickSize * 24, 19 * brickSize);
        spawner.spawnBrick(brickSize * 24, 20 * brickSize);
        spawner.spawnBrick(brickSize * 23, 20 * brickSize);
        spawner.spawnBrick(brickSize * 23, 21 * brickSize);
        spawner.spawnBrick(brickSize * 22, 21 * brickSize);
        spawner.spawnBrick(brickSize * 21, 21 * brickSize);
        spawner.spawnBrick(brickSize * 20, 21 * brickSize);
        spawner.spawnBrick(brickSize * 19, 21 * brickSize);
        spawner.spawnBrick(brickSize * 27, brickSize * 12);
        spawner.spawnBrick(brickSize * 28, brickSize * 12);
        spawner.spawnBrick(brickSize * 29, brickSize * 12);

        // Left green bricks.
        for (var i = 13; i < 25; i++) {
            for (var j = 0; j < 3; j++) {
                spawner.spawnBrick(j * brickSize, i * brickSize, 10);
            }
        }

        // Right green bricks.
        for (var i = 13; i < 25; i++) {
            for (var j = 27; j < 30; j++) {
                spawner.spawnBrick(j * brickSize, i * brickSize, 10);
            }
        }

        // Top left yellow corner.
        spawner.spawnBrick(3 * brickSize, 20 * brickSize, 2);
        spawner.spawnBrick(3 * brickSize, 21 * brickSize, 2);
        spawner.spawnBrick(3 * brickSize, 19 * brickSize, 2);
        spawner.spawnBrick(4 * brickSize, 21 * brickSize, 2);
        spawner.spawnBrick(5 * brickSize, 21 * brickSize, 2);
        spawner.spawnBrick(4 * brickSize, 20 * brickSize, 2);

        // Top green strip.
        for (var i = 3; i < 27; i++) {
            for (var j = 0; j < 3; j++) {
                spawner.spawnBrick(i * brickSize, 24 * brickSize - j * brickSize, 10);
            }
        }

        // Top right yellow corner.
        spawner.spawnBrick(26 * brickSize, 20 * brickSize, 2);
        spawner.spawnBrick(26 * brickSize, 21 * brickSize, 2);
        spawner.spawnBrick(26 * brickSize, 19 * brickSize, 2);
        spawner.spawnBrick(25 * brickSize, 21 * brickSize, 2);
        spawner.spawnBrick(24 * brickSize, 21 * brickSize, 2);
        spawner.spawnBrick(25 * brickSize, 20 * brickSize, 2);

        // Top red strip.
        for (var i = 0; i < 8; i++) {
            spawner.spawnBrick(18 * brickSize - i * brickSize, 21 * brickSize, 1);
        }

        // Bottom-left red.
        for (var i = 0; i < 8; i++) {
            for (var j = 0; j < 3; j++) {
                spawner.spawnBrick(j * brickSize, 4 * brickSize + i * brickSize, 1);
            }
        }

        // Bottom-right red.
        for (var i = 0; i < 8; i++) {
            for (var j = 27; j < 30; j++) {
                spawner.spawnBrick(j * brickSize, 4 * brickSize + i * brickSize, 1);
            }
        }
    }
}
