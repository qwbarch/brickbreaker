package io.github.qwbarch.entity.system.logic;

import com.artemis.Aspect;
import com.artemis.BaseSystem;
import com.artemis.ComponentMapper;
import com.artemis.EntitySubscription;
import com.artemis.annotations.All;
import com.artemis.utils.IntBag;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import io.github.qwbarch.entity.component.InputListener;

import javax.inject.Inject;

public final class InputSystem extends BaseSystem {
    /**
     * A class that allows us to register multiple input processors at once.
     */
    private final InputMultiplexer multiplexer = new InputMultiplexer();

    private ComponentMapper<InputListener> inputListeners;

    @Inject
    InputSystem() {

    }

    @Override
    protected void initialize() {
        Gdx.input.setInputProcessor(multiplexer);
        world.getAspectSubscriptionManager().get(Aspect.all(InputListener.class)).addSubscriptionListener(new EntitySubscription.SubscriptionListener() {
            @Override
            public void inserted(IntBag entities) {
                for (var i = 0; i < entities.size(); i++) {
                    var entityId = entities.get(i);
                    var inputProcessor = inputListeners.get(entityId).processor;
                    multiplexer.addProcessor((inputProcessor));
                }
            }

            @Override
            public void removed(IntBag entities) {
                for (var i = 0; i < entities.size(); i++) {
                    var entityId = entities.get(i);
                    var inputProcessor = inputListeners.get(entityId).processor;
                    multiplexer.removeProcessor(inputProcessor);
                }
            }
        });
    }

    @Override
    protected void processSystem() {

    }
}
