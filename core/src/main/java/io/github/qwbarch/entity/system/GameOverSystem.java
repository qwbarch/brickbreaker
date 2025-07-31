package io.github.qwbarch.entity.system;

import com.artemis.Aspect;
import com.artemis.BaseSystem;
import com.artemis.ComponentMapper;
import com.artemis.EntitySubscription;
import com.artemis.utils.IntBag;
import io.github.qwbarch.entity.component.PlayerHealth;
import io.github.qwbarch.entity.component.Target;
import io.github.qwbarch.screen.LoseScreen;
import io.github.qwbarch.screen.Screen;
import io.github.qwbarch.screen.ScreenHandler;
import io.github.qwbarch.screen.WinScreen;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Handles and checks for the game over condition.
 * This is a singleton, so one instance is used for the entire game.
 */
@Singleton
public class GameOverSystem extends BaseSystem {
    // Dependencies injected via dagger.
    private final ScreenHandler screenHandler;
    private final Screen loseScreen;
    private final Screen winScreen;

    // Component mappers are automatically injected via artemis-odb,
    // which gives access to the entity's components.
    private ComponentMapper<PlayerHealth> playerHealths;
    private ComponentMapper<Target> targets;

    /**
     * When this reaches 0, the player loses.
     * The player gains one health for each entity that has a "PlayerHealth" component attached.
     */
    private int remainingHealth;

    /**
     * When this reaches 0, the player wins.
     * The number of targets increases for each entity that has a "Target" component attached.
     */
    private int remainingTargets;

    // Package-private constructor since dagger injects the dependencies.
    @Inject
    GameOverSystem(
        ScreenHandler screenHandler,
        LoseScreen loseScreen,
        WinScreen winScreen
    ) {
        this.screenHandler = screenHandler;
        this.loseScreen = loseScreen;
        this.winScreen = winScreen;
    }

    @Override
    public void initialize() {
        world.getAspectSubscriptionManager().get(Aspect.all(PlayerHealth.class)).addSubscriptionListener(new EntitySubscription.SubscriptionListener() {
            @Override
            public void inserted(IntBag entities) {
                // When "PlayerHealth" component is attached to an entity.
                remainingHealth += entities.size();
            }

            @Override
            public void removed(IntBag entities) {
                // When "PlayerHealth" component is removed from an entity.
                remainingHealth -= entities.size();
            }
        });
        world.getAspectSubscriptionManager().get(Aspect.all(Target.class)).addSubscriptionListener(new EntitySubscription.SubscriptionListener() {
            @Override
            public void inserted(IntBag entities) {
                // When "Target" component is attached to an entity.
                remainingTargets += entities.size();
            }

            @Override
            public void removed(IntBag entities) {
                // When "Target" component is removed from an entity.
                remainingTargets -= entities.size();
            }
        });
    }

    @Override
    protected void processSystem() {
        // Checked once every available render tick.
        if (remainingTargets == 0) {
            screenHandler.setScreen(winScreen);
        } else if (remainingHealth == 0) {
            screenHandler.setScreen(loseScreen);
        }
    }
}
