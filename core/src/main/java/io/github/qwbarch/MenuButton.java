package io.github.qwbarch;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import io.github.qwbarch.asset.AssetMap;

public class MenuButton extends TextButton  {
    private float lastPlayedTime;

    public MenuButton(String text, AssetMap assets) {
        super(text, assets.getSkin());
        addListener(new InputListener() {
            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                var currentTime = System.nanoTime();
                var timeSinceLastPlayed = (currentTime - lastPlayedTime) / 1_000_000_000f;

                // If mouse is moving over the button.
                if (pointer == -1 && timeSinceLastPlayed > 0.2f) {
                    lastPlayedTime = currentTime;
                    assets.getButtonHoverSound().play();
                }
            }
        });
        addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                assets.getButtonClickSound().play();
            }
        });
    }
}
