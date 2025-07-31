package io.github.qwbarch.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import dagger.Lazy;
import io.github.qwbarch.MenuButton;
import io.github.qwbarch.asset.AssetMap;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Displays instructions on how to play the game to the user.
 * This is a singleton, so one instance is used for the entire game.
 */
@Singleton
public final class InstructionScreen implements Screen {
    private static final String HEADER = "How To Play";

    // Dependencies injected via dagger.
    private final InputMultiplexer inputMultiplexer;
    private final ScreenHandler screenHandler;
    private final Lazy<MenuScreen> menuScreen;
    private final SpriteBatch batch;
    private final AssetMap assets;
    private final GlyphLayout glyphLayout;
    private final Stage stage;
    private BitmapFont titleFont;
    private BitmapFont bodyFont;
    private float headerWidth;
    private float headerHeight;

    // Package-private constructor since dagger injects the dependencies.
    @Inject
    InstructionScreen(
        InputMultiplexer inputMultiplexer,
        ScreenHandler screenHandler,
        Lazy<MenuScreen> menuScreen,
        SpriteBatch batch,
        AssetMap assets,
        GlyphLayout glyphLayout
    ) {
        this.inputMultiplexer = inputMultiplexer;
        this.screenHandler = screenHandler;
        this.menuScreen = menuScreen;
        this.batch = batch;
        this.assets = assets;
        this.glyphLayout = glyphLayout;
        stage = new Stage(new ScreenViewport(), batch);
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

        var label = new Label(
            """
                ----------------------------------------------------------------
                1. Use your left and right arrow keys to move the paddle around.
                2. Press the space key to launch the ball.
                3. Objective: Use the ball(s) to destroy all bricks in the level.
                4. Game fail: You lose if all your balls drop to the bottom.
                4. Use the paddle to keep the ball up.
                5. Bricks have a chance to spawn new balls when hit.
                ----------------------------------------------------------------
                Brick health:
                Grey - Indestructible. These don't count towards the objective.
                Green - High health
                Yellow - Medium health
                Red - Low health
                ----------------------------------------------------------------
                """,
            new Label.LabelStyle(bodyFont, Color.WHITE)
        );

        var mainMenuButton = new MenuButton("Main Menu", assets);
        mainMenuButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                screenHandler.setScreen(menuScreen.get());
            }
        });

        var group = new VerticalGroup();
        group.addActor(label);
        group.addActor(mainMenuButton);
        group.setPosition(
            screenWidth / 2f,
            screenHeight - headerHeight * 3f
        );

        stage.addActor(group);
        stage.getViewport().update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);
    }

    @Override
    public void show() {
        titleFont = assets.getHeaderFont();
        bodyFont = assets.getBodyFont();

        glyphLayout.setText(titleFont, HEADER);
        headerWidth = glyphLayout.width;
        headerHeight = glyphLayout.height;

        setupStage();
        inputMultiplexer.addProcessor(0, stage);
    }

    @Override
    public void hide() {
        inputMultiplexer.removeProcessor(stage);
    }

    @Override
    public void dispose() {
        inputMultiplexer.removeProcessor(stage);
    }

    @Override
    public void resize(int width, int height) {
        setupStage();
    }

    @Override
    public void render() {
        var screenWidth = Gdx.graphics.getWidth();
        var screenHeight = Gdx.graphics.getHeight();

        batch.begin();
        titleFont.draw(
            batch,
            HEADER,
            screenWidth / 2f - headerWidth / 2f,
            screenHeight - headerHeight
        );
        batch.end();

        stage.act(Gdx.graphics.getDeltaTime());
        stage.draw();
    }
}
