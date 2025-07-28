package io.github.qwbarch.dagger.module;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import dagger.Module;
import dagger.Provides;
import io.github.qwbarch.dagger.scope.ClientScope;

@Module
public final class RenderModule {
    @Provides
    @ClientScope
    public SpriteBatch provideSpriteBatch() {
        return new SpriteBatch();
    }
}
