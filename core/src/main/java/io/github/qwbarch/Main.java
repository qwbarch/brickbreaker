package io.github.qwbarch;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.ScreenUtils;
import io.github.qwbarch.dagger.component.DaggerClientComponent;
import io.github.qwbarch.dagger.component.DaggerScreenComponent;
import io.github.qwbarch.screen.ScreenHandler;

public class Main implements ApplicationListener {
    private static final Color MAIN_BACKGROUND_COLOR =
        Color.valueOf("#3b3b3b"); // Gray color.

    private static final Color WORLD_BACKGROUND_COLOR =
        Color.valueOf("#525252"); // Gray color.

    /**
     * Update the game's logic at a fixed 30 updates per second.
     */
    private final static float SECONDS_PER_TICK = 1f / 15f;

    /**
     * World width using in-game units.
     */
    private final static float WORLD_WIDTH = 120f;

    /**
     * World height using in-game units.
     */
    private final static float WORLD_HEIGHT = 100f;

    private ScreenHandler screenHandler;

    @Override
    public void create() {
        // Startup dependency injection.
        var clientComponent = DaggerClientComponent.create();
        var screenComponent =
                DaggerScreenComponent
                    .factory()
                    .create(
                        clientComponent,
                        SECONDS_PER_TICK,
                        WORLD_WIDTH,
                        WORLD_HEIGHT,
                        WORLD_BACKGROUND_COLOR
                    );
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

        ScreenUtils.clear(MAIN_BACKGROUND_COLOR);
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
