package io.github.qwbarch;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.ScreenUtils;
import io.github.qwbarch.asset.AssetMap;
import io.github.qwbarch.dagger.component.DaggerClientComponent;
import io.github.qwbarch.dagger.component.DaggerScreenComponent;
import io.github.qwbarch.screen.ScreenHandler;

public class Main implements ApplicationListener {
    /**
     * Main background color used to clear the screen with.
     */
    private static final Color MAIN_BACKGROUND_COLOR =
        Color.valueOf("#3b3b3b"); // Gray color.

    /**
     * Background colour of the world (inside the playable world bounds).
     */
    private static final Color WORLD_BACKGROUND_COLOR =
        Color.valueOf("#525252"); // Gray color.

    /**
     * Update the game's logic at a fixed 30 updates per second.
     */
    private final static float SECONDS_PER_TICK = 1f / 60f;

    /**
     * World width using in-game units.
     */
    private final static float WORLD_WIDTH = 120f;

    /**
     * World height using in-game units.
     */
    private final static float WORLD_HEIGHT = 100f;

    /**
     * The brick's width/height using in-game units.
     */
    private final static float BRICK_SIZE = 4f;

    /**
     * The ball's width/height using in-game units.
     */
    private final static float BALL_SIZE = 1.3f;

    /**
     * The paddle's width using in-game units.
     */
    private final static float PADDLE_WIDTH = 12.1f;

    /**
     * The paddle's height using in-game units.
     */
    private final static float PADDLE_HEIGHT = 2.42f;

    /**
     * The paddle's spawning x-coordinate using in-game units.
     */
    private final static float PADDLE_SPAWN_X = (WORLD_WIDTH / 2f) - (PADDLE_WIDTH / 2f);

    /**
     * The paddle's spawning y-coordinate using in-game units.
     */
    private final static float PADDLE_SPAWN_Y = 10f;

    /**
     * The speed of the paddle using in-game units.
     */
    private final static float PADDLE_VELOCITY = 160f;

    /**
     * The ball's spawning x-coordinate using in-game units.
     */
    private final static float STARTING_BALL_SPAWN_X = (WORLD_WIDTH / 2f) - (BALL_SIZE / 2f);

    /**
     * The ball's spawning y-coordinate using in-game units.
     */
    private final static float STARTING_BALL_SPAWN_Y = PADDLE_SPAWN_Y + BALL_SIZE + 1.1f;

    /**
     * The ball's velocity using in-game units.
     */
    private final static float BALL_VELOCITY = 120f;

    /**
     * The velocity of the spawned ball dropping towards the ground.
     */
    private final static float BALL_SPAWN_VELOCITY = 30f;

    /**
     * The left part of the logo.
     */
    private static final String LEFT_LOGO = "Brick";

    /**
     * The right part of the logo.
     */
    private static final String RIGHT_LOGO = "Breaker";

    /**
     * The full logo.
     */
    private static final String LOGO = LEFT_LOGO + " " + RIGHT_LOGO;

    /**
     * Chance to spawn a ball from hitting a brick.
     */
    private static final float SPAWN_BALL_CHANCE = 0.1f;

    private ScreenHandler screenHandler;
    private AssetMap assets;

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
                        WORLD_BACKGROUND_COLOR,
                        BRICK_SIZE,
                        BALL_SIZE,
                        BALL_VELOCITY,
                        BALL_SPAWN_VELOCITY,
                        STARTING_BALL_SPAWN_X,
                        STARTING_BALL_SPAWN_Y,
                        PADDLE_WIDTH,
                        PADDLE_HEIGHT,
                        PADDLE_VELOCITY,
                        PADDLE_SPAWN_X,
                        PADDLE_SPAWN_Y,
                        LEFT_LOGO,
                        RIGHT_LOGO,
                        LOGO,
                        SPAWN_BALL_CHANCE
                    );
        screenHandler = screenComponent.getScreenHandler();
        assets = screenComponent.getAssets();

        // Start the loading screen.
        screenHandler.setScreen(screenComponent.getLoadingScreen());
    }

    @Override
    public void resize(int width, int height) {
        screenHandler.getCurrentScreen().resize(width, height);
    }

    @Override
    public void render() {
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
        screenHandler.getCurrentScreen().dispose();
        assets.dispose();
    }
}
