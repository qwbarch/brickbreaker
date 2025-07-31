package io.github.qwbarch.screen;

import com.artemis.Aspect;
import com.artemis.World;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import io.github.qwbarch.asset.AssetMap;
import io.github.qwbarch.entity.EntitySpawner;

public abstract class LevelScreen implements Screen {
    private final String startLabel;
    private final World world;
    private final SpriteBatch batch;
    private final EntitySpawner spawner;
    private final float worldWidth;
    private final float worldHeight;
    private final Texture background;
    private final Camera camera = new OrthographicCamera();
    private final Viewport gameViewport;
    private final Viewport uiViewport = new ScreenViewport();
    private final AssetMap assets;
    private final GlyphLayout glyphLayout;

    private long startTime;
    private Viewport currentViewport;
    private BitmapFont headerFont;
    private float startLabelWidth;
    private float startLabelHeight;

    protected LevelScreen(
        String startLabel,
        World world,
        SpriteBatch batch,
        EntitySpawner spawner,
        float worldWidth,
        float worldHeight,
        Color worldBackground,
        AssetMap assets,
        GlyphLayout glyphLayout
    ) {
        System.out.println("LevelScreen constructor");
        this.startLabel = startLabel;
        this.world = world;
        this.batch = batch;
        this.spawner = spawner;
        this.worldWidth = worldWidth;
        this.worldHeight = worldHeight;
        this.assets = assets;
        this.glyphLayout = glyphLayout;
        gameViewport = new FitViewport(worldWidth, worldHeight, camera);
        currentViewport = uiViewport;

        var pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(worldBackground);
        pixmap.fill();
        background = new Texture(pixmap);
    }

    @Override
    public void show() {
        System.out.println("level screen show");

        startTime = System.nanoTime();
        headerFont = assets.getHeaderFont();

        glyphLayout.setText(headerFont, startLabel);
        startLabelWidth = glyphLayout.width;
        startLabelHeight = glyphLayout.height;

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
        currentViewport.update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);
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
        currentViewport.update(width, height, true);
    }

    @Override
    public void render() {
        var screenWidth = Gdx.graphics.getWidth();
        var screenHeight = Gdx.graphics.getHeight();

        // Render start of round overlay.
        var currentTime = System.nanoTime();
        if (currentTime - startTime < 3_000_000_000L) {
            currentViewport.apply();
            batch.setProjectionMatrix(currentViewport.getCamera().combined);
            batch.begin();
            headerFont.draw(
                batch,
                startLabel,
                screenWidth / 2f - startLabelWidth / 2f,
                screenHeight / 4f * 3f
            );
            batch.end();
        } else {
            if (currentViewport == uiViewport) {
                currentViewport = gameViewport;
                currentViewport.apply();
                currentViewport.update(Gdx.graphics.getWidth() , Gdx.graphics.getHeight(), true);
            }

            batch.setProjectionMatrix(camera.combined);
            batch.begin();

            batch.draw(background, 0, 0, worldWidth, worldHeight);

            // batch.draw(ballTexture, WORLD_WIDTH / 2f - 20, WORLD_HEIGHT / 2f - 20, 1.21f, 1.21f);
            world.process();

            batch.end();
        }
    }

    private void clearWorld() {
        var entities = world.getAspectSubscriptionManager().get(Aspect.all()).getEntities();
        for (var i = 0; i < entities.size(); i++) {
            world.delete(entities.get(i));
        }
    }
}
