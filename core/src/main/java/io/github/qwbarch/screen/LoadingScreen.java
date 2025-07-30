package io.github.qwbarch.screen;

import com.artemis.World;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.*;
import com.holidaystudios.tools.GifDecoder;
import io.github.qwbarch.asset.AssetMap;
import io.github.qwbarch.dagger.scope.ScreenScope;

import javax.inject.Inject;
import javax.inject.Named;

@ScreenScope
public final class LoadingScreen implements Screen {
    private static final String LEFT_LOGO = "Brick";
    private static final String RIGHT_LOGO = "Breaker";
    private static final String LOGO = LEFT_LOGO + " " + RIGHT_LOGO;

    private final Animation<TextureRegion> loadingAnimation;

    private final SpriteBatch batch;
    private final ScreenHandler screenHandler;
    private final LevelScreen levelScreen;
    private final AssetMap assets;
    private final BitmapFont logoFont;

    private final float leftLogoWidth;
    private final float leftLogoHeight;
    private final float rightLogoWidth;
    private final float rightLogoHeight;
    private final float logoWidth;
    private final float logoHeight;

    private float elapsedTime;
    private boolean firstFrame = true;

    @Inject
    LoadingScreen(
        ScreenHandler screenHandler,
        LevelScreen levelScreen,
        AssetMap assets,
        World world,
        SpriteBatch batch,
        @Named("logoFontSize") int logoFontSize
    ) {
        this.batch = batch;
        this.screenHandler = screenHandler;
        this.levelScreen = levelScreen;
        this.assets = assets;

        // Load the loading animation blocking the thread, since we need it immediately.
        loadingAnimation = GifDecoder.loadGIFAnimation(
            Animation.PlayMode.LOOP,
            Gdx.files.internal("loading.gif").read()
        );

        logoFont = assets.loadLogoFont();
        var layout = new GlyphLayout();

        // Calculate dimensions of the left side of the logo.
        layout.setText(logoFont, LEFT_LOGO);
        leftLogoWidth = layout.width;
        leftLogoHeight = layout.height;

        // Calculate dimensions of the right side of the logo.
        layout.setText(logoFont, RIGHT_LOGO);
        rightLogoWidth = layout.width;
        rightLogoHeight = layout.height;

        // Calculate dimensions of the combined logo.
        layout.setText(logoFont, LOGO);
        logoWidth = layout.width;
        logoHeight = layout.height;
    }

    @Override
    public void render() {
        var screenWidth = Gdx.graphics.getWidth();
        var screenHeight = Gdx.graphics.getHeight();

        elapsedTime += Gdx.graphics.getDeltaTime();
        batch.begin();

        // Draw the current loading animation frame.
        // var loadingFrame = loadingAnimation.getKeyFrame(elapsedTime);
        // batch.draw(
        //     loadingFrame,
        //     Gdx.graphics.getWidth() / 2f - loadingFrame.getRegionWidth() / 2f,
        //     Gdx.graphics.getHeight() / 2f - loadingFrame.getRegionHeight() / 2f - logoTexture.getHeight(),
        //     loadingFrame.getRegionWidth(),
        //     loadingFrame.getRegionHeight()
        // );

        // Draw the left logo.
        logoFont.setColor(207f / 255f, 60f / 255f, 60f / 255f, 1f);
        var leftLogoX = screenWidth / 2f - logoWidth / 2f;
        var logoY = screenHeight / 3f * 2f + logoHeight / 2f;
        logoFont.draw(batch, "Brick", leftLogoX, logoY);

        // Draw the right logo.
        logoFont.setColor(1f, 1f, 1f, 1f);
        logoFont.draw(
            batch,
            "Breaker",
            leftLogoX + logoWidth - rightLogoWidth,
            logoY
        );

        batch.end();


        if (firstFrame) {
            firstFrame = false;
            assets.loadAssets();
        } else {
            assets.update();
            if (assets.isFinishedLoading()) {
                var music = assets.getBackgroundMusic();
                music.setLooping(0L, true);
                music.play();
                //screenHandler.setScreen(levelScreen);
            }
        }
    }
}
