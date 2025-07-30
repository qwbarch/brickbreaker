package io.github.qwbarch.entity.system;

import com.artemis.Aspect;
import com.artemis.BaseSystem;
import com.artemis.ComponentMapper;
import com.artemis.EntitySubscription;
import com.artemis.utils.IntBag;
import dagger.Lazy;
import io.github.qwbarch.entity.component.PlayerHealth;
import io.github.qwbarch.screen.GameOverScreen;
import io.github.qwbarch.screen.Screen;
import io.github.qwbarch.screen.ScreenHandler;
import io.github.qwbarch.screen.level.Level1Screen;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class PlayerHealthSystem extends BaseSystem  {
    private final ScreenHandler screenHandler;
    private final Screen gameOverScreen;
    private final Lazy<Level1Screen> level1Screen;

    private ComponentMapper<PlayerHealth> playerHealths;

    private int totalHealth;

    @Inject
    PlayerHealthSystem(
        ScreenHandler screenHandler,
        GameOverScreen gameOverScreen,
        Lazy<Level1Screen> level1Screen
    ) {
        System.out.println("Player Health System constructor");
        this.screenHandler = screenHandler;
        this.gameOverScreen = gameOverScreen;
        this.level1Screen = level1Screen;
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
            screenHandler.remove(level1Screen.get());
        }
    }
}
