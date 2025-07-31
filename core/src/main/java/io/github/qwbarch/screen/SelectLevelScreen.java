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
import io.github.qwbarch.MenuButton;
import io.github.qwbarch.asset.AssetMap;
import io.github.qwbarch.screen.level.BonusLevel;
import io.github.qwbarch.screen.level.Level1Screen;
import io.github.qwbarch.screen.level.Level2Screen;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Objects;

@Singleton
public final class SelectLevelScreen implements Screen {
    private static final String HEADER = "Select a level";

    private final Viewport viewport = new ScreenViewport();

    private final InputMultiplexer inputMultiplexer;
    private final AssetMap assets;
    private final GlyphLayout glyphLayout;
    private final SpriteBatch batch;
    private final Stage stage;
    private final ScreenHandler screenHandler;
    private final Lazy<Level1Screen> level1Screen;
    private final Lazy<Level2Screen> level2Screen;
    private final Lazy<BonusLevel> bonusLevelScreen;
    private final Lazy<MenuScreen> menuScreen;

    private BitmapFont headerFont;
    private float headerWidth;
    private float headerHeight;

    @Inject
    SelectLevelScreen(InputMultiplexer inputMultiplexer, AssetMap assets, GlyphLayout glyphLayout, SpriteBatch batch, ScreenHandler screenHandler, Lazy<Level1Screen> level1Screen, Lazy<Level2Screen> level2Screen, Lazy<BonusLevel> bonusLevelScreen, Lazy<MenuScreen> menuScreen) {
        this.inputMultiplexer = inputMultiplexer;
        this.assets = assets;
        this.glyphLayout = glyphLayout;
        this.batch = batch;
        this.screenHandler = screenHandler;
        this.level1Screen = level1Screen;
        this.level2Screen = level2Screen;
        this.bonusLevelScreen = bonusLevelScreen;
        this.menuScreen = menuScreen;
        stage = new Stage(viewport, batch);
    }

    private void setupStage() {
        // Re-add all components in the case of a screen resize.
        stage.clear();

        var screenWidth = Gdx.graphics.getWidth();
        var screenHeight = Gdx.graphics.getHeight();

        var level1Button = new MenuButton("Level 1", assets);
        level1Button.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Objects.requireNonNull(level1Screen.get()).firstRun = true;
                screenHandler.setScreen(level1Screen.get());
            }
        });

        var level2Button = new MenuButton("Level 2", assets);
        level2Button.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Objects.requireNonNull(level2Screen.get()).firstRun = true;
                screenHandler.setScreen(level2Screen.get());
            }
        });

        var bonusLevelButton = new MenuButton("Bonus Level", assets);
        bonusLevelButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Objects.requireNonNull(bonusLevelScreen.get()).firstRun = true;
                screenHandler.setScreen(bonusLevelScreen.get());
            }
        });

        var mainMenuButton = new MenuButton("Main Menu", assets);
        mainMenuButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                screenHandler.setScreen(menuScreen.get());
            }
        });

        var group = new VerticalGroup();
        group.addActor(level1Button);
        group.addActor(level2Button);
        group.addActor(bonusLevelButton);
        group.addActor(mainMenuButton);

        group.space(20f);
        group.setPosition(screenWidth / 2f, screenHeight / 4f * 3.1f - headerHeight * 3f);

        stage.addActor(group);
        stage.getViewport().update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);
    }

    @Override
    public void show() {
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
        headerFont.draw(batch, HEADER, screenWidth / 2f - headerWidth / 2f, screenHeight / 4f * 3.1f);
        batch.end();

        stage.act(Gdx.graphics.getDeltaTime());
        stage.draw();
    }
}
