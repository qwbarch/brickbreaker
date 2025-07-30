package io.github.qwbarch.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import io.github.qwbarch.asset.AssetMap;

import javax.inject.Inject;
import javax.inject.Named;

public final class MenuScreen implements  Screen{
    private final AssetMap assets;
    private final SpriteBatch batch;
    private final GlyphLayout glyphLayout;

    private final String leftLogo;
    private final String rightLogo;
    private final String logo;

    private BitmapFont logoFont;
    private float rightLogoWidth;
    private float logoWidth;
    private float logoHeight;

    private float elapsedTime;

    // Only play the BGM the first time the menu screen is shown.
    private boolean firstRun = true;

    @Inject
    MenuScreen(
        AssetMap assets,
        SpriteBatch batch,
        GlyphLayout glyphLayout,
        @Named("leftLogo") String leftLogo,
        @Named("rightLogo") String rightLogo,
        @Named("logo") String logo
    ) {
        System.out.println("MenuScreen constructor");
        this.assets = assets;
        this.batch = batch;
        this.glyphLayout = glyphLayout;
        this.leftLogo = leftLogo;
        this.rightLogo = rightLogo;
        this.logo = logo;
    }

    @Override
    public void show() {
        logoFont = assets.getMainFont();

        // Calculate dimensions of the right side of the logo.
        glyphLayout.setText(logoFont, rightLogo);
        rightLogoWidth = glyphLayout.width;

        // Calculate dimensions of the combined logo.
        glyphLayout.setText(logoFont, logo);
        logoWidth = glyphLayout.width;
        logoHeight = glyphLayout.height;

        if (firstRun) {
            firstRun = false;
            var music = assets.getBackgroundMusic();
            music.setLooping(0L, true);
            music.play();
        }
    }

    @Override
    public void render() {
        var screenWidth = Gdx.graphics.getWidth();
        var screenHeight = Gdx.graphics.getHeight();

        elapsedTime += Gdx.graphics.getDeltaTime();
        batch.begin();

        // Draw the left logo.
        logoFont.setColor(212f / 255f, 83f / 255f, 83f / 255f, 1f);
        var leftLogoX = screenWidth / 2f - logoWidth / 2f;
        var logoY = screenHeight - logoHeight * 1.5f;
        logoFont.draw(batch, leftLogo, leftLogoX, logoY);

        // Draw the right logo.
        logoFont.setColor(1f, 1f, 1f, 1f);
        logoFont.draw(
            batch,
            rightLogo,
            leftLogoX + logoWidth - rightLogoWidth,
            logoY
        );

        batch.end();
    }
}
