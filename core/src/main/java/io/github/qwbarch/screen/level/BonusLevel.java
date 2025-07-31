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

public class BonusLevel extends LevelScreen {
    private static final String START_LABEL = "Bonus level\n\nThat's a lot of balls.";

    private final float brickSize;
    private final EntitySpawner spawner;

    @Inject
    protected BonusLevel(
        World world,
        SpriteBatch batch,
        EntitySpawner spawner,
        @Named("worldWidth") float worldWidth,
        @Named("worldHeight") float worldHeight,
        @Named("brickSize") float brickSize,
        @Named("worldBackground") Color worldBackground,
        AssetMap assets,
        GlyphLayout glyphLayout,
        InputMultiplexer inputMultiplexer,
        ScreenHandler screenHandler,
        Lazy<MenuScreen> menuScreen
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
        this.brickSize = brickSize;
        this.spawner = spawner;
    }

    @Override
    public void show() {
        super.show();

        var brickHealth = 200;

        // Bottom grey strip.
        for (var i = 0; i < 27; i++) {
            spawner.spawnBrick(26f * brickSize - i * brickSize, 4 * brickSize);
        }

        for (var i = 0; i < 21; i++) {
            spawner.spawnBrick(29f * brickSize, 4f * brickSize + i * brickSize, brickHealth);
        }

        for (var i = 0; i < 19; i++) {
            spawner.spawnBrick(27f * brickSize, 4f * brickSize + i * brickSize, brickHealth);
        }

        for (var i = 0; i < 29; i++) {
            spawner.spawnBrick(28f * brickSize - i * brickSize, 24 * brickSize, brickHealth);
        }

        for (var i = 1; i < 27; i++) {
            spawner.spawnBrick(i * brickSize, 22 * brickSize, brickHealth);
        }

        for (var i = 0; i < 16; i++) {
            spawner.spawnBrick(brickSize, 21 * brickSize - i * brickSize, brickHealth);
        }

        for (var i = 0; i < 24; i++) {
            spawner.spawnBrick(2 * brickSize + i * brickSize, 6 * brickSize, brickHealth);
        }

        for (var i = 0; i < 14; i++) {
            spawner.spawnBrick(25 * brickSize, 7 * brickSize + i * brickSize, brickHealth);
        }

        for (var i = 0; i < 22; i++) {
            spawner.spawnBrick(24 * brickSize - i * brickSize, 20 * brickSize, brickHealth);
        }

        for (var i = 0; i < 12; i++) {
            spawner.spawnBrick(3 * brickSize, 19 * brickSize - i * brickSize, brickHealth);
        }

        for (var i = 0; i < 20; i++) {
            spawner.spawnBrick(4 * brickSize + i * brickSize, 8 * brickSize, brickHealth);
        }

        for (var i = 0; i < 10; i++) {
            spawner.spawnBrick(23 * brickSize, 9 * brickSize + i * brickSize, brickHealth);
        }

        for (var i = 0; i < 18; i++) {
            spawner.spawnBrick(22 * brickSize - i * brickSize, 18 * brickSize, brickHealth);
        }

        for (var i = 0; i < 8; i++) {
            spawner.spawnBrick(5 * brickSize, 17 * brickSize - i * brickSize, brickHealth);
        }

        for (var i = 0; i < 16; i++) {
            spawner.spawnBrick(6 * brickSize + i * brickSize, 10 * brickSize, brickHealth);
        }

        for (var i = 0; i < 6; i++) {
            spawner.spawnBrick(21 * brickSize, 11 * brickSize + i * brickSize, brickHealth);
        }

        for (var i = 0; i < 14; i++) {
            spawner.spawnBrick(20 * brickSize - i * brickSize, 16 * brickSize, brickHealth);
        }

        for (var i = 0; i < 4; i++) {
            spawner.spawnBrick(7 * brickSize, 15 * brickSize - i * brickSize, brickHealth);
        }

        for (var i = 0; i < 12; i++) {
            spawner.spawnBrick(8 * brickSize + i * brickSize, 12 * brickSize, brickHealth);
        }

        spawner.spawnBrick(19 * brickSize, 13 * brickSize, brickHealth);

        for (var i = 0; i < 11; i++) {
            spawner.spawnBrick(19 * brickSize - i * brickSize, 14 * brickSize, brickHealth);
        }
    }
}
