package io.github.qwbarch.entity.system;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.EntitySubscription;
import com.artemis.annotations.All;
import com.artemis.systems.IteratingSystem;
import com.artemis.utils.IntBag;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import io.github.qwbarch.entity.component.InputListener;

import javax.inject.Inject;

@All(InputListener.class)
public final class InputSystem extends IteratingSystem {
    /**
     * A class that allows us to register multiple input processors at once.
     */
    private final InputMultiplexer multiplexer = new InputMultiplexer();

    // TODO: THIS IS A WORKAROUND FOR A BUG, MAKE MULTIPLEXER NOT LOCAL TO HERE.
    private boolean firstRun = true;

    private ComponentMapper<InputListener> inputListeners;

    @Inject
    InputSystem() {}

    @Override
    protected void initialize() {
        world.getAspectSubscriptionManager().get(Aspect.all(InputListener.class)).addSubscriptionListener(new EntitySubscription.SubscriptionListener() {
            @Override
            public void inserted(IntBag entities) {
                for (var i = 0; i < entities.size(); i++) {
                    var entityId = entities.get(i);
                    var inputProcessor = inputListeners.get(entityId).processor;
                    if (inputProcessor != null) {
                        multiplexer.addProcessor((inputProcessor));
                    }
                }
            }

            @Override
            public void removed(IntBag intBag) {
            }
        });
    }

    @Override
    protected void begin() {
        if (firstRun) {
            firstRun = false;
            Gdx.input.setInputProcessor(multiplexer);
        }
    }

    @Override
    protected void process(int entityId) {
        var inputListener = inputListeners.get(entityId);
        if (inputListener.processor != null && inputListener.unregister) {
            multiplexer.removeProcessor(inputListener.processor);
            inputListener.processor = null;
            world.edit(entityId).remove(InputListener.class);
        }
    }
}
