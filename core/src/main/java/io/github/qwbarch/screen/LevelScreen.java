package io.github.qwbarch.screen;

import com.artemis.World;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import io.github.qwbarch.asset.AssetMap;
import io.github.qwbarch.dagger.scope.ScreenScope;
import io.github.qwbarch.entity.EntitySpawner;
import io.github.qwbarch.entity.component.LinearVelocity;
import io.github.qwbarch.entity.component.Position;

import javax.inject.Inject;
import javax.inject.Named;

@ScreenScope
public final class LevelScreen implements Screen {
    private final World world;
    private final AssetMap assets;
    private final SpriteBatch batch;
    private final EntitySpawner spawner;
    private final float worldWidth;
    private final float worldHeight;
    private final Texture background;
    private final float brickSize;

    private Viewport viewport;
    private Camera camera = new OrthographicCamera();
    private FPSLogger fpsLogger = new FPSLogger();

    private int spawnedBalls = 0;
    private float accumulator = 0f;

    @Inject
    LevelScreen(
        World world,
        AssetMap assets,
        SpriteBatch batch,
        EntitySpawner spawner,
        @Named("worldWidth") float worldWidth,
        @Named("worldHeight") float worldHeight,
        @Named("brickSize") float brickSize,
        @Named("worldBackground") Color worldBackground
    ) {
        this.world = world;
        this.assets = assets;
        this.batch = batch;
        this.spawner = spawner;
        this.worldWidth = worldWidth;
        this.worldHeight = worldHeight;
        this.brickSize = brickSize;
        viewport = new FitViewport(worldWidth, worldHeight, camera);

        var pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(worldBackground);
        pixmap.fill();
        background = new Texture(pixmap);

    }

    @Override
    public void show() {
        System.out.println("level screen");
        var borderSize = 100f;
        spawner.spawnInvisibleBorder(
            -borderSize,
            -borderSize,
            worldWidth + borderSize * 2,
            borderSize
        );
        spawner.spawnInvisibleBorder(
            -borderSize,
            worldHeight,
            worldWidth + borderSize * 2,
            borderSize
        );
        spawner.spawnInvisibleBorder(
            -borderSize,
            0,
            borderSize,
            worldHeight
        );
        spawner.spawnInvisibleBorder(
            worldWidth,
            0,
            borderSize,
            worldHeight
        );
        //ballTexture = assets.getBallTexture();
        //ballTexture.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        viewport.update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);

        var paddleId = spawner.spawnPaddle();

        for (var i = 0; i < 20; i++) {
            spawner.spawnBrick(i * brickSize, worldHeight - brickSize, 1);
        }

        spawner.spawnStartingBall(paddleId);

        System.out.println(world.getSystems());

        //camera.position.set(WORLD_WIDTH / 2f, WORLD_HEIGHT / 2f, 0f);
        //camera.update();
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }

    @Override
    public void render() {
        // accumulator += Gdx.graphics.getDeltaTime();
        // if (spawnedBalls < 1 && accumulator >= 0.0001f) {
        //     accumulator -= 0.0001f;
        //     spawnedBalls++;
        //     var reflect = MathUtils.random(1) == 0;
        //     var xVel = (float) MathUtils.random(80, 160);
        //     var yVel = (float) MathUtils.random(100, 160);
        //     xVel = reflect ? xVel : -xVel;
        //     xVel *= .9f;
        //     yVel *= .9f;
        //     spawner.spawnBall(worldWidth / 2f, worldHeight / 2f, xVel, yVel);
        // }
        //viewport.apply();
        //camera.update();
        batch.setProjectionMatrix(camera.combined);
        batch.begin();

        batch.draw(background, 0, 0, worldWidth, worldHeight);

        // batch.draw(ballTexture, WORLD_WIDTH / 2f - 20, WORLD_HEIGHT / 2f - 20, 1.21f, 1.21f);
        world.process();

        fpsLogger.log();

        batch.end();
    }
}
