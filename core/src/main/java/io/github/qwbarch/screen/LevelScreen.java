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
import com.badlogic.gdx.scenes.scene2d.InputListener;
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

public abstract class LevelScreen implements Screen {
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
    private final Camera camera = new OrthographicCamera();
    private final Viewport gameViewport;
    private final Viewport uiViewport = new ScreenViewport();
    private final AssetMap assets;
    private final GlyphLayout glyphLayout;
    private final Stage pauseStage;
    private final InputProcessor pauseListener;

    private long startTime;
    private Viewport currentViewport;
    private BitmapFont headerFont;
    private float startLabelWidth;
    private boolean isPaused = false;

    public boolean firstRun = true;

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
        System.out.println("LevelScreen constructor");
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

        var pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(worldBackground);
        pixmap.fill();
        background = new Texture(pixmap);
    }

    private InputProcessor createPauseListener() {
        return new InputAdapter() {
            @Override
            public boolean keyDown(int keycode) {
                if (keycode == Input.Keys.ESCAPE) {
                    isPaused = !isPaused;
                    if (isPaused) {
                        currentViewport = uiViewport;
                        inputMultiplexer.addProcessor(pauseStage);
                    } else {
                        currentViewport = gameViewport;
                        inputMultiplexer.removeProcessor(pauseStage);
                    }
                    currentViewport.apply();
                    currentViewport.update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);
                }
                return false;
            }
        };
    }

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
        System.out.println("level screen show");

        isPaused = false;
        currentViewport = uiViewport;
        startTime = System.nanoTime();
        headerFont = assets.getHeaderFont();

        glyphLayout.setText(headerFont, startLabel);
        startLabelWidth = glyphLayout.width;

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

        setupStage();
        inputMultiplexer.addProcessor(pauseListener);
    }

    @Override
    public void hide() {
        inputMultiplexer.removeProcessor(pauseListener);
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
        if (firstRun && currentTime - startTime < 3_000_000_000L) {
            System.out.println("rendering this");
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
            firstRun = false;
            if (isPaused) {
                pauseStage.act(Gdx.graphics.getDeltaTime());
                pauseStage.draw();
            } else {
                if (currentViewport == uiViewport) {
                    currentViewport = gameViewport;
                    currentViewport.apply();
                    currentViewport.update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);
                }

                batch.setProjectionMatrix(camera.combined);
                batch.begin();

                batch.draw(background, 0, 0, worldWidth, worldHeight);

                // batch.draw(ballTexture, WORLD_WIDTH / 2f - 20, WORLD_HEIGHT / 2f - 20, 1.21f, 1.21f);
                world.process();

                batch.end();
            }
        }
    }

    private void clearWorld() {
        var entities = world.getAspectSubscriptionManager().get(Aspect.all()).getEntities();
        for (var i = 0; i < entities.size(); i++) {
            world.delete(entities.get(i));
        }
    }
}
