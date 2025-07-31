package io.github.qwbarch.screen;

import com.artemis.Aspect;
import com.artemis.World;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import io.github.qwbarch.entity.EntitySpawner;

public abstract class LevelScreen implements Screen {
    private final World world;
    private final SpriteBatch batch;
    private final EntitySpawner spawner;
    private final float worldWidth;
    private final float worldHeight;
    private final Texture background;
    private final Camera camera = new OrthographicCamera();
    private final Viewport viewport;

    protected LevelScreen(
        World world,
        SpriteBatch batch,
        EntitySpawner spawner,
        float worldWidth,
        float worldHeight,
        Color worldBackground
    ) {
        System.out.println("LevelScreen constructor");
        this.world = world;
        this.batch = batch;
        this.spawner = spawner;
        this.worldWidth = worldWidth;
        this.worldHeight = worldHeight;
        viewport = new FitViewport(worldWidth, worldHeight, camera);

        var pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(worldBackground);
        pixmap.fill();
        background = new Texture(pixmap);
    }

    @Override
    public void show() {
        System.out.println("level screen show");
        var borderSize = 100f;
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
        spawner.spawnPaddle();
        spawner.spawnStartingBall();

        // Center the camera.
        viewport.update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);
    }

    @Override
    public void hide() {
        clearWorld();
    }

    @Override
    public void dispose() {
        hide();
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

        batch.end();
    }

    private void clearWorld() {
        var entities = world.getAspectSubscriptionManager().get(Aspect.all()).getEntities();
        for (var i = 0; i < entities.size(); i++) {
            world.delete(entities.get(i));
        }
    }
}
