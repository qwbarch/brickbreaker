package io.github.qwbarch.entity.system;

import com.artemis.Aspect;
import com.artemis.BaseSystem;
import com.artemis.ComponentMapper;
import com.artemis.EntitySubscription;
import com.artemis.utils.IntBag;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.utils.IntMap;
import io.github.qwbarch.entity.component.InputListener;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Handles player input.
 * This is a singleton, so one instance is used for the entire game.
 */
@Singleton
public final class InputSystem extends BaseSystem {
    // Dependencies injected via dagger.
    private final InputMultiplexer inputMultiplexer;
    private final IntMap<InputProcessor> inputProcessors = new IntMap<>();

    // Component mappers are automatically injected via artemis-odb,
    // which gives access to the entity's components.
    private ComponentMapper<InputListener> inputListeners;

    // Package-private constructor since dagger injects the dependencies.
    @Inject
    InputSystem(InputMultiplexer inputMultiplexer) {
        this.inputMultiplexer = inputMultiplexer;
    }

    @Override
    protected void initialize() {
        world.getAspectSubscriptionManager().get(Aspect.all(InputListener.class)).addSubscriptionListener(new EntitySubscription.SubscriptionListener() {
            @Override
            public void inserted(IntBag entities) {
                // When an entity with an input listener is added,
                // add it to the input multiplexer.
                for (var i = 0; i < entities.size(); i++) {
                    var entityId = entities.get(i);
                    var inputProcessor = inputListeners.get(entityId).processor;
                    if (inputProcessor != null) {
                        inputProcessors.put(entityId, inputProcessor);
                        inputMultiplexer.addProcessor((inputProcessor));
                    }
                }
            }

            @Override
            public void removed(IntBag entities) {
                // When an entity with an input listener is removed (or the entity is deleted),
                // removed it from the input multiplexer.
                for (var i = 0; i < entities.size(); i++) {
                    var entityId = entities.get(i);
                    if (inputProcessors.containsKey(entityId)) {
                        inputMultiplexer.removeProcessor(inputProcessors.get(entityId));
                    }
                }
            }
        });
    }

    @Override
    protected void processSystem() { }
}
