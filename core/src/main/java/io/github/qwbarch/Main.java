package io.github.qwbarch;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.FPSLogger;
import com.badlogic.gdx.utils.ScreenUtils;
import io.github.qwbarch.asset.AssetMap;
import io.github.qwbarch.dagger.DaggerComponent;
import io.github.qwbarch.screen.ScreenHandler;

/**
 * The main entry-point of the game.
 */
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
     * Update the game's logic at a fixed 60 updates per second.
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
    private static final float SPAWN_BALL_CHANCE = 0.25f;

    /**
     * The cell size of the spatial grid used for collision detection.
     */
    private static final int GRID_CELL_SIZE = (int) BRICK_SIZE;

    /**
     * An FPS logger. Normally it'd be better to display the FPS in-game,
     * but I didn't have the time to work on such a minor feature, so
     * I use this to still be able to see what the FPS is.
     */
    private final FPSLogger fpsLogger = new FPSLogger();

    /**
     * The screen handler, for displaying the currently selected screen.
     * The instance is provided via the dagger component.
     */
    private ScreenHandler screenHandler;

    /**
     * Holds all our game assets. This is disposed of when the window is closed.
     */
    private AssetMap assets;

    @Override
    public void create() {
        // Dagger creates all of our dependencies and injects the required constructor parameters.
        var component =
                DaggerComponent
                    .factory()
                    .create(
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
                        SPAWN_BALL_CHANCE,
                        GRID_CELL_SIZE
                    );
        screenHandler = component.getScreenHandler();
        assets = component.getAssets();

        // Input multiplexer allows multiple input processors to be registered at once.
        Gdx.input.setInputProcessor(component.getInputMultiplexer());

        // Start the loading screen.
        screenHandler.setScreen(component.getLoadingScreen());
    }

    @Override
    public void resize(int width, int height) {
        // Update the current screen with the latest window dimensions.
        screenHandler.getCurrentScreen().resize(width, height);
    }

    // Render is called as often as possible by LibGDX.
    @Override
    public void render() {
        // Every second, fps logger will print the current FPS to the console.
        fpsLogger.log();

        // Clear the screen at the start of every frame.
        ScreenUtils.clear(MAIN_BACKGROUND_COLOR);

        // Render the current screen.
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
        // Release held resources.
        screenHandler.getCurrentScreen().dispose();
        assets.dispose();
    }
}
