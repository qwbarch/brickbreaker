package io.github.qwbarch.dagger.module;

import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import dagger.Module;
import dagger.Provides;
import io.github.qwbarch.dagger.scope.ClientScope;

@Module
public final class RenderModule {
    @Provides
    @ClientScope
    public SpriteBatch provideSpriteBatch() {
        System.out.println("provideSpriteBatch");
        return new SpriteBatch();
    }

    @Provides
    @ClientScope
    public GlyphLayout provideGlyphLayout() {
        System.out.println("provideGlyphLayout");
        return new GlyphLayout();
    }
}
