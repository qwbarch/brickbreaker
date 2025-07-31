package io.github.qwbarch;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import io.github.qwbarch.asset.AssetMap;

/**
 * A menu button that uses the comic-ui skin.
 */
public class MenuButton extends TextButton  {
    /**
     * A timestamp of the last time a hover sound was played.
     * This is needed since the "enter" event is triggered very often,
     * causing the hover sound to be spammed.
     */
    private float lastPlayedTime;

    /**
     * Create a new menu button.
     * @param text The text to display.
     * @param assets The assets containing the comic ui skin.
     */
    public MenuButton(String text, AssetMap assets) {
        super(text, assets.getSkin());

        // Detect muse hovers.
        addListener(new InputListener() {
            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                var currentTime = System.nanoTime();
                var timeSinceLastPlayed = (currentTime - lastPlayedTime) / 1_000_000_000f;

                // Only play the hover sound if it's been at least 0.2 seconds since the last sound was played.
                if (pointer == -1 && timeSinceLastPlayed > 0.2f) {
                    lastPlayedTime = currentTime;
                    assets.getButtonHoverSound().play();
                }
            }
        });

        // Play a sound on clicking the button.
        addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                assets.getButtonClickSound().play();
            }
        });
    }
}
