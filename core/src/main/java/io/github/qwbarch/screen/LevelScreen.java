package io.github.qwbarch.screen;

import com.artemis.Aspect;
import com.artemis.World;
import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import dagger.Lazy;
import io.github.qwbarch.MenuButton;
import io.github.qwbarch.asset.AssetMap;
import io.github.qwbarch.entity.EntitySpawner;

/**
 * The basis of any level screen.
 */
public abstract class LevelScreen implements Screen {
    // Dependencies injected via dagger.
    private final ScreenHandler screenHandler;
    private final Lazy<MenuScreen> menuScreen;
    private final InputMultiplexer inputMultiplexer;
    private final String startLabel;
    private final World world;
    private final SpriteBatch batch;
    private final EntitySpawner spawner;
    private final float worldWidth;
    private final float worldHeight;
    private final Texture background;
    private final Viewport gameViewport;
    private final AssetMap assets;
    private final GlyphLayout glyphLayout;
    private final Stage pauseStage;
    private final InputProcessor pauseListener;

    // User interface.
    private final Camera camera = new OrthographicCamera();
    private final Viewport uiViewport = new ScreenViewport();
    private Viewport currentViewport;
    private BitmapFont headerFont;
    private float startLabelWidth;

    /**
     * The timestamp of when the level screen is first shown.
     * This is for showing the initial message upon entering the level.
     */
    private long startTime;

    /**
     * Keeps track of if the game is paused to show an appropriate pause menu.
     */
    private boolean isPaused = false;

    /**
     * Used to prevent the start of level message from showing on retries.
     */
    public boolean firstRun = true;

    /**
     * Necessary dependencies that will need to be injected via dagger from the subclass.
     */
    protected LevelScreen(
        String startLabel,
        World world,
        SpriteBatch batch,
        EntitySpawner spawner,
        float worldWidth,
        float worldHeight,
        Color worldBackground,
        AssetMap assets,
        GlyphLayout glyphLayout,
        InputMultiplexer inputMultiplexer,
        ScreenHandler screenHandler,
        Lazy<MenuScreen> menuScreen
    ) {
        this.screenHandler = screenHandler;
        this.menuScreen = menuScreen;
        this.inputMultiplexer = inputMultiplexer;
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
        pauseStage = new Stage(uiViewport, batch);
        pauseListener = createPauseListener();

        // Prepare a background with a different colour to make it more obvious
        // to the user where the world borders are.
        var pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(worldBackground);
        pixmap.fill();
        background = new Texture(pixmap);
    }

    /**
     * Creates an input processor that reacts to the escape key being pressed.
     */
    private InputProcessor createPauseListener() {
        return new InputAdapter() {
            @Override
            public boolean keyDown(int keycode) {
                if (keycode == Input.Keys.ESCAPE) {
                    // Don't allow the game to be paused in the start-of-level message overlay.
                    if (firstRun) return false;

                    isPaused = !isPaused;
                    if (isPaused) {
                        // Game is paused. The pause menu need to be able to react to mouse events.
                        currentViewport = uiViewport;
                        inputMultiplexer.addProcessor(pauseStage);
                    } else {
                        // Game is no longer paused. The pause menu need to be removed from reacting to mouse events.
                        currentViewport = gameViewport;
                        inputMultiplexer.removeProcessor(pauseStage);
                    }

                    // Update the camera to show the correct content.
                    currentViewport.apply();
                    currentViewport.update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);
                }
                return false;
            }
        };
    }

    /**
     * Setup the user interface. This needs to be called whenever the
     * screen resolution changes.
     */
    private void setupStage() {
        // Re-add all components in the case of a screen resize.
        pauseStage.clear();

        var screenWidth = Gdx.graphics.getWidth();
        var screenHeight = Gdx.graphics.getHeight();

        var pausedLabelStyle = new Label.LabelStyle();
        pausedLabelStyle.font = headerFont;
        pausedLabelStyle.fontColor = Color.WHITE;
        var pausedLabel = new Label("Paused", pausedLabelStyle);

        var resumeButton = new MenuButton("Resume game", assets);
        resumeButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                isPaused = false;
                currentViewport = gameViewport;
                currentViewport.apply();
                currentViewport.update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);
            }
        });

        var retryButton = new MenuButton("Retry level", assets);
        retryButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                hide();
                show();
            }
        });

        var mainMenuButton = new MenuButton("Main menu", assets);
        mainMenuButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                screenHandler.setScreen(menuScreen.get());
            }
        });

        var quitButton = new MenuButton("Quit game", assets);
        quitButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.exit();
            }
        });

        var group = new VerticalGroup();
        group.addActor(pausedLabel);
        group.addActor(new Actor()); // Extra space.
        group.addActor(new Actor());
        group.addActor(resumeButton);
        group.addActor(retryButton);
        group.addActor(mainMenuButton);
        group.addActor(quitButton);

        group.space(30f);
        group.setPosition(
            screenWidth / 2f,
            screenHeight / 4f * 3f
        );

        pauseStage.addActor(group);
    }

    @Override
    public void show() {
        isPaused = false;
        currentViewport = uiViewport;
        startTime = System.nanoTime();
        headerFont = assets.getHeaderFont();

        glyphLayout.setText(headerFont, startLabel);
        startLabelWidth = glyphLayout.width;

        // Create 3 invisible borders surrounding the world, at the left, right, and top side.
        // These will prevent collider entities from moving past them.
        // A border is not present at the bottom since we want the balls to fall down.
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

        // Spawn the player's paddle.
        spawner.spawnPaddle();

        // Spawn the starting ball, which is "attached" to the paddle.
        spawner.spawnStartingBall();

        // Center the camera.
        currentViewport.update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);

        setupStage();
        inputMultiplexer.addProcessor(pauseListener);
    }

    @Override
    public void hide() {
        inputMultiplexer.removeProcessor(pauseListener);
        clearWorld();
        isPaused = false;
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
        if (firstRun && currentTime - startTime < 3_000_000_000L) {
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
            // display the pause menu if the game is paused.
            if (isPaused) {
                pauseStage.act(Gdx.graphics.getDeltaTime());
                pauseStage.draw();
            }
            // Process the game as normal if not paused.
            else {
                firstRun = false;
                if (currentViewport == uiViewport) {
                    currentViewport = gameViewport;
                    currentViewport.apply();
                    currentViewport.update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);
                }

                batch.setProjectionMatrix(camera.combined);
                batch.begin();

                batch.draw(background, 0, 0, worldWidth, worldHeight);

                // Process all entities.
                world.process();

                batch.end();
            }
        }
    }

    /**
     * Clears all entities from the world.
     */
    private void clearWorld() {
        var entities = world.getAspectSubscriptionManager().get(Aspect.all()).getEntities();
        for (var i = 0; i < entities.size(); i++) {
            world.delete(entities.get(i));
        }
    }
}
