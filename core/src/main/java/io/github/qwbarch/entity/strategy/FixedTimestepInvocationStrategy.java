package io.github.qwbarch.entity.strategy;

import com.artemis.BaseSystem;
import com.artemis.SystemInvocationStrategy;
import com.badlogic.gdx.utils.OrderedMap;
import io.github.qwbarch.entity.system.LogicSystem;
import io.github.qwbarch.entity.system.RenderSystem;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

/**
 * Implements a fixed timestep game loop.
 * <a href="https://gafferongames.com/post/fix_your_timestep/">Fix your timestep by Gaffer On Games</a>
 * <p>
 * Why are OrderedMaps used for mapping the systems to their "enabled" flag?
 * <p>
 * Firstly, I need o(1) lookup on the keys. According to LibGDX, OrderedMaps rarely degrades to o(n).
 * Secondly, I need quick iteration over the keys and values. Ordered maps are simply much more efficient for
 * the tight iteration cycles that this class performs.
 * Thirdly, LibGDX collections such as OrderedMap pools the iterator object when possible, avoiding unnecessary
 * stress on the garbage collector.
 * <p>
 * These maps are set to unordered via a property since I don't actually care about the iteration order
 * of these systems. OrderedMap set to unordered still iterates faster than ObjectMap.
 */
@Singleton
public final class FixedTimestepInvocationStrategy extends SystemInvocationStrategy {
    /**
     * Time in seconds per game tick.
     */
    private final float secondsPerTick;

    /**
     * Systems that run at a fixed timestep.
     * The mapped values indicates whether the system is enabled or not.
     */
    private OrderedMap<BaseSystem, Boolean> logicSystems = new OrderedMap<>();

    /**
     * Systems that run as often as possible.
     * The mapped values indicate whether the system is enabled or not.
     */
    private OrderedMap<BaseSystem, Boolean> defaultSystems = new OrderedMap<>();

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

    private OrderedMap<BaseSystem, Boolean> isEnabledForSystem(BaseSystem system) {
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
        return system != null && isEnabledForSystem(system).containsKey(system);
    }

    @Override
    public void setEnabled(BaseSystem system, boolean enabled) {
        if (system != null) {
            isEnabledForSystem(system).put(system, enabled);
        }
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
                // If the logic system is enabled, process it.
                var system = logicSystems.orderedKeys().get(i);
                if (logicSystems.get(system)) {
                    system.process();
                    updateEntityStates();
                }
            }
        }
    }

    private void processDefaultSystems() {
        for (var i = 0; i < defaultSystems.size; i++) {
            // If the default system is enabled, process it.
            var system = defaultSystems.orderedKeys().get(i);
            if (defaultSystems.get(system)) {
                // Update the alpha value if the system is a RenderSystem.
                if (system instanceof RenderSystem) {
                    ((RenderSystem) system).alpha = accumulator / secondsPerTick;
                }
                system.process();
                updateEntityStates();
            }
        }
    }
}
