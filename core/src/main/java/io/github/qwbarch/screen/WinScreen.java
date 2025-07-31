package io.github.qwbarch.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import dagger.Lazy;
import io.github.qwbarch.LevelResolver;
import io.github.qwbarch.MenuButton;
import io.github.qwbarch.asset.AssetMap;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Objects;

/**
 * The screen that's displayed when the player wins the level.
 * This is a singleton, so one instance is used for the entire game.
 */
@Singleton
public final class WinScreen implements Screen {
    /**
     * The text label to display on the win screen.
     */
    private static final String HEADER = "You won!";

    // Dependencies injected via dagger.
    private final Lazy<LevelResolver> levelResolver;
    private final InputMultiplexer inputMultiplexer;
    private final AssetMap assets;
    private final GlyphLayout glyphLayout;
    private final SpriteBatch batch;
    private final Stage stage;
    private final ScreenHandler screenHandler;
    private final Lazy<MenuScreen> menuScreen;

    /**
     * For handling the user interface's camera.
     */
    private final Viewport viewport = new ScreenViewport();

    /**
     * The font used for the header label.
     */
    private BitmapFont headerFont;

    /**
     * The width of the header label.
     */
    private float headerWidth;

    /**
     * The height of the header label.
     */
    private float headerHeight;

    // Package-private constructor since dagger injects the dependencies.
    @Inject
    WinScreen(
        Lazy<LevelResolver> levelResolver,
        InputMultiplexer inputMultiplexer,
        AssetMap assets,
        GlyphLayout glyphLayout,
        SpriteBatch batch,
        ScreenHandler screenHandler,
        Lazy<MenuScreen> menuScreen
    ) {
        this.levelResolver = levelResolver;
        this.inputMultiplexer = inputMultiplexer;
        this.assets = assets;
        this.glyphLayout = glyphLayout;
        this.batch = batch;
        this.screenHandler = screenHandler;
        this.menuScreen = menuScreen;
        stage = new Stage(viewport, batch);
    }

    /**
     * Setup the user interface. This needs to be called whenever the
     * screen resolution changes.
     */
    private void setupStage() {
        // Re-add all components in the case of a screen resize.
        stage.clear();

        var screenWidth = Gdx.graphics.getWidth();
        var screenHeight = Gdx.graphics.getHeight();

        var resolver = Objects.requireNonNull(levelResolver.get());

        // When clicked, brings the user to the next available level.
        var nextLevelButton = new MenuButton("Next level", assets);
        nextLevelButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                screenHandler.setScreen(resolver.getCurrentSaveLevelScreen());
            }
        });

        // When clicked, brings the user to the main menu.
        var mainMenuButton = new MenuButton("Main Menu", assets);
        mainMenuButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                screenHandler.setScreen(menuScreen.get());
            }
        });

        // When clicked, closes the application.
        var quitButton = new MenuButton("Quit game", assets);
        quitButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.exit();
            }
        });

        // Group the buttons vertically.
        var group = new VerticalGroup();
        group.addActor(nextLevelButton);
        group.addActor(mainMenuButton);
        group.addActor(quitButton);

        group.space(20f);
        group.setPosition(
            screenWidth / 2f,
            screenHeight / 4f * 3.1f - headerHeight * 3f
        );

        stage.addActor(group);
    }

    @Override
    public void show() {
        assets.getGameOverSound().play();

        // Only save if not the bonus level and if the played level is the save file level as well.
        var resolver = Objects.requireNonNull(levelResolver.get());
        if (
            resolver.currentSave.level() != LevelResolver.Level.BONUS_LEVEL
                && resolver.currentSave.level() == resolver.currentlyPlaying
        ) {
            // Set the save file to the next level.
            resolver.currentSave = new LevelResolver.LevelSave(
                LevelResolver.Level.values()[resolver.currentSave.level().ordinal() + 1]
            );

            // Save the file.
            resolver.saveFile();
        }

        // Calculate the header label dimensions since we need it later.
        headerFont = assets.getHeaderFont();
        glyphLayout.setText(headerFont, HEADER);
        headerWidth = glyphLayout.width;
        headerHeight = glyphLayout.height;

        // Update viewport since the other screens might've changed the camera positioning.
        viewport.update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);

        setupStage();
        inputMultiplexer.addProcessor(0, stage);
    }

    @Override
    public void hide() {
        dispose();
    }

    @Override
    public void dispose() {
        inputMultiplexer.removeProcessor(stage);
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
        setupStage();
    }

    @Override
    public void render() {
        var screenWidth = Gdx.graphics.getWidth();
        var screenHeight = Gdx.graphics.getHeight();

        viewport.apply();

        batch.setProjectionMatrix(viewport.getCamera().combined);
        batch.begin();

        // Draw the header near the top every available frame.
        headerFont.draw(
            batch,
            HEADER,
            screenWidth / 2f - headerWidth / 2f,
            screenHeight / 4f * 3.1f
        );
        batch.end();

        // Draw the user interface buttons on every available frame.
        stage.act(Gdx.graphics.getDeltaTime());
        stage.draw();
    }
}
