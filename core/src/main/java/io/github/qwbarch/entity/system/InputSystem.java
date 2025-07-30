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

@Singleton
public final class InputSystem extends BaseSystem {
    private ComponentMapper<InputListener> inputListeners;

    private final InputMultiplexer inputMultiplexer;
    private final IntMap<InputProcessor> inputProcessors = new IntMap<>();

    @Inject
    InputSystem(InputMultiplexer inputMultiplexer) {
        this.inputMultiplexer = inputMultiplexer;
    }

    @Override
    protected void initialize() {
        world.getAspectSubscriptionManager().get(Aspect.all(InputListener.class)).addSubscriptionListener(new EntitySubscription.SubscriptionListener() {
            @Override
            public void inserted(IntBag entities) {
                for (var i = 0; i < entities.size(); i++) {
                    var entityId = entities.get(i);
                    var inputProcessor = inputListeners.get(entityId).processor;
                    if (inputProcessor != null) {
                        System.out.println("InputSystem addProcessor");
                        inputProcessors.put(entityId, inputProcessor);
                        inputMultiplexer.addProcessor((inputProcessor));
                    }
                }
            }

            @Override
            public void removed(IntBag entities) {
                for (var i = 0; i < entities.size(); i++) {
                    var entityId = entities.get(i);
                    if (inputProcessors.containsKey(entityId)) {
                        System.out.println("removing processor");
                        inputMultiplexer.removeProcessor(inputProcessors.get(entityId));
                    }
                }
            }
        });
    }

    @Override
    protected void processSystem() { }
}
