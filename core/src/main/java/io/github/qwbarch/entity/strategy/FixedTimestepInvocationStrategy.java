package io.github.qwbarch.entity.strategy;

import com.artemis.BaseSystem;
import com.artemis.SystemInvocationStrategy;
import com.badlogic.gdx.utils.ArrayMap;
import io.github.qwbarch.entity.system.LogicSystem;
import io.github.qwbarch.entity.system.RenderSystem;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * Implements a fixed timestep game loop.
 * https://gafferongames.com/post/fix_your_timestep/
 */
public final class FixedTimestepInvocationStrategy extends SystemInvocationStrategy {
    /**
     * Time in seconds per game tick.
     */
    private final float secondsPerTick;

    /**
     * Systems that run at a fixed timestep.
     * The mapped values indicates whether the system is enabled or not.
     */
    private ArrayMap<BaseSystem, Boolean> logicSystems = new ArrayMap<>();

    /**
     * Systems that run as often as possible.
     * The mapped values indicate whether the system is enabled or not.
     */
    private ArrayMap<BaseSystem, Boolean> defaultSystems = new ArrayMap<>();

    /**
     * Accumulator used for a fixed time-step implementation.
     */
    private float accumulator = 0f;

    /**
     * Snapshot of the previously elapsed frame time.
     */
    private float previousFrameTime = System.nanoTime();

    @Inject
    FixedTimestepInvocationStrategy(@Named("secondsPerTick") float secondsPerTick) {
        this.secondsPerTick = secondsPerTick;
    }

    private ArrayMap<BaseSystem, Boolean> isEnabledForSystem(BaseSystem system) {
        if (system instanceof LogicSystem) return logicSystems;
        else return defaultSystems;
    }

    @Override
    public void initialize() {
        for (var system : systems.getData()) {
           setEnabled(system, true);
        }
    }

    @Override
    public boolean isEnabled(BaseSystem system) {
        return isEnabledForSystem(system).containsKey(system);
    }

    @Override
    public void setEnabled(BaseSystem system, boolean enabled) {
        isEnabledForSystem(system).put(system, enabled);
    }

    @Override
    protected void process() {
        var newTime = System.nanoTime();
        world.delta = (newTime - previousFrameTime) / 1000000000f;
        previousFrameTime = newTime;

        // If delta value is too huge, cap it at (SECONDS_PER_TICK * 25f)
        // This slows the simulation down if the frame rate is too low, avoiding spiral of death
        accumulator += Math.min(world.delta, secondsPerTick * 25f);

        // This must be called before updating systems.
        updateEntityStates();

        processLogicSystems();
        processDefaultSystems();
    }

    private void processLogicSystems() {
        while (accumulator >= secondsPerTick) {
            accumulator -= secondsPerTick;
            for (var i = 0; i < logicSystems.size; i++) {
                // If the logic system is enabled.
                if (logicSystems.values[i]) {
                    // Process the logic system and update its entities.
                    logicSystems.keys[i].process();
                    updateEntityStates();
                }
            }
        }
    }

    private void processDefaultSystems() {
        for (var i = 0; i < defaultSystems.size; i++) {
            // If the default system is enabled.
            if (defaultSystems.values[i]) {
                var system = defaultSystems.keys[i];
                if (system instanceof RenderSystem) {
                    ((RenderSystem) system).alpha = accumulator / secondsPerTick;
                }
                system.process();
                updateEntityStates();
            }
        }
    }
}
