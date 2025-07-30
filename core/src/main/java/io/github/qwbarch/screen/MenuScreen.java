package io.github.qwbarch.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import io.github.qwbarch.MenuButton;
import io.github.qwbarch.asset.AssetMap;
import io.github.qwbarch.dagger.scope.ScreenScope;

import javax.inject.Inject;
import javax.inject.Named;

@ScreenScope
public final class MenuScreen implements Screen {
    private final ScreenHandler screenHandler;
    private final Screen instructionScreen;
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

    // Only play the BGM the first time the menu screen is shown.
    private boolean firstRun = true;

    @Inject
    MenuScreen(
        ScreenHandler screenHandler,
        InstructionScreen instructionScreen,
        AssetMap assets,
        SpriteBatch batch,
        GlyphLayout glyphLayout,
        @Named("leftLogo") String leftLogo,
        @Named("rightLogo") String rightLogo,
        @Named("logo") String logo
    ) {
        System.out.println("MenuScreen constructor");
        this.screenHandler = screenHandler;
        this.instructionScreen = instructionScreen;
        this.assets = assets;
        this.batch = batch;
        this.glyphLayout = glyphLayout;
        this.leftLogo = leftLogo;
        this.rightLogo = rightLogo;
        this.logo = logo;
        stage = new Stage(new ScreenViewport(), batch);
    }

    private void setupStage() {
        // Re-add all components in the case of a screen resize.
        stage.clear();

        var screenWidth = Gdx.graphics.getWidth();
        var screenHeight = Gdx.graphics.getHeight();

        var selectLevelButton = new MenuButton("Select Level", assets);
        selectLevelButton.setPosition(
            screenWidth/ 2f - selectLevelButton.getWidth() / 2f,
            screenHeight - logoHeight * 7f
        );
        selectLevelButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
            }
        });

        var howToPlayButton = new MenuButton("How To Play", assets);
        howToPlayButton.setPosition(
            screenWidth/ 2f - howToPlayButton.getWidth() / 2f,
            screenHeight - logoHeight * 8f
        );
        howToPlayButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                screenHandler.setScreen(instructionScreen);
            }
        });

        var quitButton = new MenuButton("Quit Game", assets);
        quitButton.setPosition(
            screenWidth/ 2f - quitButton.getWidth() / 2f,
            screenHeight - logoHeight * 9f
        );
        quitButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.exit();
            }
        });

        stage.addActor(selectLevelButton);
        stage.addActor(howToPlayButton);
        stage.addActor(quitButton);
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
        Gdx.input.setInputProcessor(stage);

        if (firstRun) {
            firstRun = false;
            var music = assets.getBackgroundMusic();
            music.setLooping(0L, true);
            music.play();
        }
    }

    @Override
    public void hide() {
        // Stop the UI from trying to react to inputs.
        Gdx.input.setInputProcessor(null);
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
        stage.dispose();
    }
}
