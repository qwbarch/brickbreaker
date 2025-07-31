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

@Singleton
public final class LoseScreen implements Screen {
    private static final String HEADER = "You died. Game over!";

    private final Viewport viewport = new ScreenViewport();

    private final Lazy<LevelResolver> levelResolver;
    private final InputMultiplexer inputMultiplexer;
    private final AssetMap assets;
    private final GlyphLayout glyphLayout;
    private final SpriteBatch batch;
    private final Stage stage;
    private final ScreenHandler screenHandler;
    private final Lazy<MenuScreen> menuScreen;

    private BitmapFont headerFont;
    private float headerWidth;
    private float headerHeight;

    @Inject
    LoseScreen(
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

    private void setupStage() {
        // Re-add all components in the case of a screen resize.
        stage.clear();

        var screenWidth = Gdx.graphics.getWidth();
        var screenHeight = Gdx.graphics.getHeight();

        var tryAgainButton = new MenuButton("Try again", assets);
        tryAgainButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                screenHandler.setScreen(Objects.requireNonNull(levelResolver.get()).getCurrentlyPlayingLevelScreen());
            }
        });

        var mainMenuButton = new MenuButton("Main Menu", assets);
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
        group.addActor(tryAgainButton);
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
        headerFont = assets.getHeaderFont();

        glyphLayout.setText(headerFont, HEADER);
        headerWidth = glyphLayout.width;
        headerHeight = glyphLayout.height;

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
        headerFont.draw(
            batch,
            HEADER,
            screenWidth / 2f - headerWidth / 2f,
            screenHeight / 4f * 3.1f
        );
        batch.end();

        stage.act(Gdx.graphics.getDeltaTime());
        stage.draw();
    }
}
