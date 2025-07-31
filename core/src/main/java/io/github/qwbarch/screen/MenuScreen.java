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
import dagger.Lazy;
import io.github.qwbarch.LevelResolver;
import io.github.qwbarch.MenuButton;
import io.github.qwbarch.asset.AssetMap;
import io.github.qwbarch.screen.level.BonusLevelScreen;
import io.github.qwbarch.screen.level.Level1Screen;
import io.github.qwbarch.screen.level.Level2Screen;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import java.util.Objects;

/**
 * The main menu screen.
 * This is a singleton, so one instance is used for the entire game.
 */
@Singleton
public final class MenuScreen implements Screen {
    // Dependencies injected via dagger.
    private final Lazy<LevelResolver> levelResolver;
    private final InputMultiplexer inputMultiplexer;
    private final ScreenHandler screenHandler;
    private final Screen instructionScreen;
    private final Screen selectLevelScreen;
    private final AssetMap assets;
    private final SpriteBatch batch;
    private final GlyphLayout glyphLayout;
    private final Stage stage;
    private final String leftLogo;
    private final String rightLogo;
    private final String logo;
    private BitmapFont font;
    private float rightLogoWidth;
    private float logoWidth;
    private float logoHeight;

    // Only play the background music the first time the menu screen is shown.
    private boolean firstRun = true;

    // Package-private constructor since dagger injects the dependencies.
    @Inject
    MenuScreen(
        Lazy<LevelResolver> levelResolver,
        InputMultiplexer inputMultiplexer,
        ScreenHandler screenHandler,
        Level1Screen level1Screen,
        Level2Screen level2Screen,
        BonusLevelScreen bonusLevelScreen,
        InstructionScreen instructionScreen,
        SelectLevelScreen selectLevelScreen,
        AssetMap assets,
        SpriteBatch batch,
        GlyphLayout glyphLayout,
        @Named("leftLogo") String leftLogo,
        @Named("rightLogo") String rightLogo,
        @Named("logo") String logo
    ) {
        this.levelResolver = levelResolver;
        this.inputMultiplexer = inputMultiplexer;
        this.screenHandler = screenHandler;
        this.instructionScreen = instructionScreen;
        this.selectLevelScreen = selectLevelScreen;
        this.assets = assets;
        this.batch = batch;
        this.glyphLayout = glyphLayout;
        this.leftLogo = leftLogo;
        this.rightLogo = rightLogo;
        this.logo = logo;
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

        var group = new VerticalGroup();

        var playButton = new MenuButton("Play Game", assets);
        playButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                var screen = Objects.requireNonNull(levelResolver.get()).getCurrentSaveLevelScreen();
                screen.firstRun = true;
                screenHandler.setScreen(screen);
            }
        });

        var selectLevelButton = new MenuButton("Select Level", assets);
        selectLevelButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                screenHandler.setScreen(selectLevelScreen);
            }
        });

        var howToPlayButton = new MenuButton("How To Play", assets);
        howToPlayButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                screenHandler.setScreen(instructionScreen);
            }
        });

        var quitButton = new MenuButton("Quit Game", assets);
        quitButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.exit();
            }
        });

        group.addActor(playButton);
        group.addActor(selectLevelButton);
        group.addActor(howToPlayButton);
        group.addActor(quitButton);

        group.space(20f);
        group.setPosition(
            screenWidth / 2f - group.getWidth() / 2f,
            screenHeight - logoHeight * 6.5f
        );

        stage.addActor(group);
        stage.getViewport().update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);
    }

    @Override
    public void show() {
        font = assets.getHeaderFont();

        // Calculate dimensions of the right side of the logo.
        glyphLayout.setText(font, rightLogo);
        rightLogoWidth = glyphLayout.width;

        // Calculate dimensions of the combined logo.
        glyphLayout.setText(font, logo);
        logoWidth = glyphLayout.width;
        logoHeight = glyphLayout.height;

        setupStage();
        inputMultiplexer.addProcessor(0, stage);

        if (firstRun) {
            firstRun = false;
            var music = assets.getBackgroundMusic();
            music.setLooping(0L, true);
            music.play();
        }
    }

    @Override
    public void hide() {
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

        // Draw the left logo.
        font.setColor(212f / 255f, 83f / 255f, 83f / 255f, 1f);
        var leftLogoX = screenWidth / 2f - logoWidth / 2f;
        var logoY = screenHeight - logoHeight * 4f;
        font.draw(batch, leftLogo, leftLogoX, logoY);

        // Draw the right logo.
        font.setColor(1f, 1f, 1f, 1f);
        font.draw(
            batch,
            rightLogo,
            leftLogoX + logoWidth - rightLogoWidth,
            logoY
        );
        batch.end();

        stage.act(Gdx.graphics.getDeltaTime());
        stage.draw();
    }

    @Override
    public void dispose() {
        inputMultiplexer.removeProcessor(stage);
        stage.dispose();
    }
}
