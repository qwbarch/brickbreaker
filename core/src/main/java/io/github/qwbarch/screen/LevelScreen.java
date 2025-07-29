package io.github.qwbarch.screen;

import com.artemis.World;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
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

    private Viewport viewport;
    private Camera camera = new OrthographicCamera();
    private FPSLogger fpsLogger = new FPSLogger();

    @Inject
    LevelScreen(
        World world,
        AssetMap assets,
        SpriteBatch batch,
        EntitySpawner spawner,
        @Named("worldWidth") float worldWidth,
        @Named("worldHeight") float worldHeight,
        @Named("worldBackground") Color worldBackground
    ) {
        this.world = world;
        this.assets = assets;
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
        System.out.println("level screen");
        //ballTexture = assets.getBallTexture();
        //ballTexture.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        viewport.update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);

        spawner.spawnBall(worldWidth / 2f, worldHeight / 2f);
        spawner.spawnPaddle();

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
