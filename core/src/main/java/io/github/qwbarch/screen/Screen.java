package io.github.qwbarch.screen;

public interface Screen {
    /**
     * Called on every screen render from LibGDX.
     */
    default void render() {}

    /**
     * Called when the screen is changed to via ScreenHandler.
     */
    default void show() {}

    /**
     * Called when the screen is changed from via ScreenHandler.
     */
    default void hide() {}

    /**
     * Disposes resources. This must be manually invoked.
     */
    default void dispose() {}

    /**
     * Called when the application is paused.
     */
    default void pause() {}

    /**
     * Called when the application is resumed.
     */
    default void resume() {}

    /**
     * Called when the window is resized.
     * @param width The width of the resized window.
     * @param height The height of the resized window.
     */
    default void resize(int width, int height) {}
}
