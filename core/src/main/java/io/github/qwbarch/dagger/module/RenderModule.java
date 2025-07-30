package io.github.qwbarch.dagger.module;

import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import dagger.Module;
import dagger.Provides;

import javax.inject.Singleton;

@Module
public final class RenderModule {
    @Provides
    @Singleton
    public SpriteBatch provideSpriteBatch() {
        System.out.println("provideSpriteBatch");
        return new SpriteBatch();
    }

    @Provides
    @Singleton
    public GlyphLayout provideGlyphLayout() {
        System.out.println("provideGlyphLayout");
        return new GlyphLayout();
    }
}
