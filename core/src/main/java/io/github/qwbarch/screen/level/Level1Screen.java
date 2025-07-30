package io.github.qwbarch.screen.level;

import com.artemis.World;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import io.github.qwbarch.entity.EntitySpawner;
import io.github.qwbarch.screen.LevelScreen;

import javax.inject.Inject;
import javax.inject.Named;

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
        for (var i = 0; i < 20; i++) {
            spawner.spawnBrick(i * brickSize, worldHeight - brickSize, 1);
        }
    }
}
