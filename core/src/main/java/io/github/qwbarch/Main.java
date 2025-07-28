package io.github.qwbarch;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import io.github.qwbarch.dagger.component.DaggerClientComponent;
import io.github.qwbarch.dagger.component.DaggerScreenComponent;
import io.github.qwbarch.screen.ScreenHandler;

public class Main implements ApplicationListener {
    /**
     * Update the game's logic at a fixed 30 updates per second.
     */
    public static float SECONDS_PER_TICK = 1f / 30f;

    private ScreenHandler screenHandler;

    // private SpriteBatch batch;
    // private Texture image;

    @Override
    public void create() {
        // batch = new SpriteBatch();
        // image = new Texture("libgdx.png");

        // Startup dependency injection.
        var clientComponent = DaggerClientComponent.create();
        var screenComponent = DaggerScreenComponent.factory().create(SECONDS_PER_TICK);
        screenHandler = screenComponent.getScreenHandler();

        // Start the loading screen.
        screenHandler.setScreen(screenComponent.getLoadingScreen());
    }

    @Override
    public void resize(int width, int height) {
        screenHandler.getCurrentScreen().resize(width, height);
    }

    @Override
    public void render() {
        // ScreenUtils.clear(0.15f, 0.15f, 0.2f, 1f);
        // batch.begin();
        // batch.draw(image, 140, 210);
        // batch.end();

        Gdx.gl.glClearColor(0f, 0f, 0f, 1f); // Clear black screen.
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
        screenHandler.getCurrentScreen().render();
    }

    @Override
    public void pause() {
        screenHandler.getCurrentScreen().pause();
    }

    @Override
    public void resume() {
        screenHandler.getCurrentScreen().resume();
    }

    @Override
    public void dispose() {
        // batch.dispose();
        // image.dispose();

        screenHandler.getCurrentScreen().dispose();
    }
}
