package io.github.qwbarch;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;
import io.github.qwbarch.dagger.component.DaggerClientComponent;
import io.github.qwbarch.dagger.component.DaggerScreenComponent;
import io.github.qwbarch.screen.ScreenHandler;

public class Main implements ApplicationListener {
    public static final Color DEFAULT_BACKGROUND_COLOR =
        Color.valueOf("#525252"); // Gray color.

    /**
     * Update the game's logic at a fixed 30 updates per second.
     */
    public static float SECONDS_PER_TICK = 1f / 15f;

    private ScreenHandler screenHandler;

    // private SpriteBatch batch;
    // private Texture image;

    @Override
    public void create() {
        // batch = new SpriteBatch();
        // image = new Texture("libgdx.png");

        // Startup dependency injection.
        var clientComponent = DaggerClientComponent.create();
        var screenComponent =
                DaggerScreenComponent
                    .factory()
                    .create(clientComponent, SECONDS_PER_TICK);
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

        ScreenUtils.clear(DEFAULT_BACKGROUND_COLOR);
        screenHandler.getCurrentScreen().render();
    }

    @Override
    public void pause() {
        screenHandler.getCurrentScreen().pause();
    }

    @Override
    public void resume() {
        System.out.println("resume called");
        screenHandler.getCurrentScreen().resume();
    }

    @Override
    public void dispose() {
        // batch.dispose();
        // image.dispose();

        screenHandler.getCurrentScreen().dispose();
    }
}
