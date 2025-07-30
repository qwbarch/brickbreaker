package io.github.qwbarch.entity.system;

import com.artemis.Aspect;
import com.artemis.BaseSystem;
import com.artemis.ComponentMapper;
import com.artemis.EntitySubscription;
import com.artemis.utils.IntBag;
import io.github.qwbarch.entity.component.PlayerHealth;
import io.github.qwbarch.screen.GameOverScreen;
import io.github.qwbarch.screen.Screen;
import io.github.qwbarch.screen.ScreenHandler;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class PlayerHealthSystem extends BaseSystem  {
    private final ScreenHandler screenHandler;
    private final Screen gameOverScreen;

    private ComponentMapper<PlayerHealth> playerHealths;

    private int totalHealth;

    @Inject
    PlayerHealthSystem(
        ScreenHandler screenHandler,
        GameOverScreen gameOverScreen
    ) {
        System.out.println("Player Health System constructor");
        this.screenHandler = screenHandler;
        this.gameOverScreen = gameOverScreen;
    }

    @Override
    public void initialize() {
        var subscription = world.getAspectSubscriptionManager().get(Aspect.all(PlayerHealth.class));
        subscription.addSubscriptionListener(new EntitySubscription.SubscriptionListener() {
            @Override
            public void inserted(IntBag intBag) {
                totalHealth++;
            }

            @Override
            public void removed(IntBag intBag) {
                totalHealth--;
            }
        });
    }

    @Override
    protected void processSystem() {
        if (totalHealth == 0) {
            System.out.println("game over");
            screenHandler.setScreen(gameOverScreen);
        }
    }
}
