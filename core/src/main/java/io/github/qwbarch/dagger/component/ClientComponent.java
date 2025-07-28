package io.github.qwbarch.dagger.component;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import dagger.Component;
import io.github.qwbarch.dagger.module.RenderModule;
import io.github.qwbarch.dagger.scope.ClientScope;

@Component(modules = { RenderModule.class })
@ClientScope
public interface ClientComponent {
    SpriteBatch getSpriteBatch();
}
