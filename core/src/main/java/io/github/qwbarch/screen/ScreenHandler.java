package io.github.qwbarch.screen;

import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.ObjectSet;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Manages the currently displayed screen.
 * This is a singleton, so one instance is used for the entire game.
 */
@Singleton
public class ScreenHandler implements Disposable {
    /**
     * The currently visible screen.
     */
    private Screen currentScreen = null;

    // Package-private constructor since dagger injects the dependencies.
    @Inject
    ScreenHandler() { }

    /**
     * A list of all currently loaded screens.
     */
    private ObjectSet<Screen> screens = new ObjectSet<Screen>();

    /**
     * Get the currently visible screen.
     */
    public Screen getCurrentScreen() {
        return currentScreen;
    }

    /**
     * Adds a reference to the screen.
     *
     * @param screen The screen to add to the screen handler.
     */
    public void add(Screen screen) {
        screens.add(screen);
    }

    /**
     * Removes a reference to a screen.
     *
     * @param screen The screen to remove from the screen handler.
     */
    public void remove(Screen screen) {
        screens.remove(screen);
    }

    /**
     * Returns whether or not the screen already exists on the screen handler.
     */
    public boolean contains(Screen screen) {
        return screens.contains(screen);
    }

    /**
     * Change the currently visible screen to a new screen.
     *
     * @param screen The new screen to change to.
     */
    public void setScreen(Screen screen) {
        if (!contains(screen)) add(screen);
        if (currentScreen != null) {
            currentScreen.hide();
        }
        currentScreen = screen;
        currentScreen.show();
    }

    /**
     * Release the resources of all loaded screens.
     */
    @Override
    public void dispose() {
        for (var screen : screens) {
            screen.dispose();
        }
    }
}
