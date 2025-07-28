package io.github.qwbarch.screen;

import com.artemis.World;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import io.github.qwbarch.dagger.scope.ScreenScope;
import io.github.qwbarch.entity.component.LinearVelocity;
import io.github.qwbarch.entity.component.Position;

import javax.inject.Inject;

@ScreenScope
public final class LevelScreen implements Screen {
    public static float WORLD_WIDTH = 100f;
    public static float WORLD_HEIGHT = 100f;

    private Camera camera = new PerspectiveCamera();
    private Viewport viewport = new FitViewport(WORLD_WIDTH, WORLD_HEIGHT, camera);

    private World world;

    @Inject
    LevelScreen(World world) {
        this.world = world;
        var entityId = world.create();
        var position = world.edit(entityId).create(Position.class);
        var velocity = world.edit(entityId).create(LinearVelocity.class);
    }

    @Override
    public void show() {
        System.out.println("level screen");
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }

    @Override
    public void render() {
        viewport.apply(true);

        world.process();
    }
}
