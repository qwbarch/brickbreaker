package io.github.qwbarch.entity.system;

import com.artemis.Aspect;
import com.artemis.BaseSystem;
import com.artemis.ComponentMapper;
import com.artemis.EntitySubscription;
import com.artemis.utils.IntBag;
import dagger.Lazy;
import io.github.qwbarch.entity.component.PlayerHealth;
import io.github.qwbarch.entity.component.Target;
import io.github.qwbarch.screen.LoseScreen;
import io.github.qwbarch.screen.Screen;
import io.github.qwbarch.screen.ScreenHandler;
import io.github.qwbarch.screen.WinScreen;
import io.github.qwbarch.screen.level.Level1Screen;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class GameOverSystem extends BaseSystem {
    private final ScreenHandler screenHandler;
    private final Screen loseScreen;
    private final Screen winScreen;
    private final Lazy<Level1Screen> level1Screen;

    private ComponentMapper<PlayerHealth> playerHealths;
    private ComponentMapper<Target> targets;

    private int remainingHealth;
    private int remainingTargets;

    @Inject
    GameOverSystem(
        ScreenHandler screenHandler,
        LoseScreen loseScreen,
        WinScreen winScreen,
        Lazy<Level1Screen> level1Screen
    ) {
        System.out.println("Player Health System constructor");
        this.screenHandler = screenHandler;
        this.loseScreen = loseScreen;
        this.winScreen = winScreen;
        this.level1Screen = level1Screen;
    }

    @Override
    public void initialize() {
        world.getAspectSubscriptionManager().get(Aspect.all(PlayerHealth.class)).addSubscriptionListener(new EntitySubscription.SubscriptionListener() {
            @Override
            public void inserted(IntBag entities) {
                remainingHealth += entities.size();
                System.out.println("+ player health: " + remainingHealth);
            }

            @Override
            public void removed(IntBag entities) {
                remainingHealth -= entities.size();
                System.out.println("- player health: " + remainingHealth);
            }
        });
        world.getAspectSubscriptionManager().get(Aspect.all(Target.class)).addSubscriptionListener(new EntitySubscription.SubscriptionListener() {
            @Override
            public void inserted(IntBag entities) {
                remainingTargets += entities.size();
                System.out.println("+ remaining targets: " + remainingTargets);
            }

            @Override
            public void removed(IntBag entities) {
                remainingTargets -= entities.size();
                System.out.println("- remaining targets: " + remainingTargets);
            }
        });
    }

    @Override
    protected void processSystem() {
        screenHandler.setScreen(winScreen);

        if (remainingTargets == 0) {
            System.out.println("win");
            screenHandler.setScreen(winScreen);
        } else if (remainingHealth == 0) {
            System.out.println("lose");
            screenHandler.setScreen(loseScreen);
        }
    }
}
