package io.github.qwbarch;

import com.badlogic.gdx.Gdx;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import io.github.qwbarch.screen.LevelScreen;
import io.github.qwbarch.screen.level.BonusLevelScreen;
import io.github.qwbarch.screen.level.Level1Screen;
import io.github.qwbarch.screen.level.Level2Screen;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

import static io.github.qwbarch.LevelResolver.Level.LEVEL_1;

/**
 * Handles the logic of level selection and its save file.
 * This is a singleton, so one instance is used for the entire game.
 */
@Singleton
public final class LevelResolver {
    /**
     * The save file's file path.
     */
    private static final String SAVE_FILE_PATH = "save_file.json";

    /**
     * The available levels the player can play on.
     */
    public enum Level {
        LEVEL_1,
        LEVEL_2,
        BONUS_LEVEL;
    }

    /**
     * Represent's the player's save file.
     *
     * @param level The level of the current save file.
     */
    public record LevelSave(Level level) { }

    // Dependencies injected via dagger.
    private final LevelScreen level1Screen;
    private final LevelScreen level2Screen;
    private final LevelScreen bonusLevelScreen;

    /**
     * A JSON serializer for converting LevelSave <-> String.
     */
    private final ObjectMapper mapper = new ObjectMapper().registerModule(new ParameterNamesModule());

    /**
     * The currently playing level. This is mainly for game retries.
     */
    public Level currentlyPlaying = LEVEL_1;

    /**
     * The current save file. Note that if you change this value, you still need to call LevelResolver#saveFile
     * if you want to actually write it to disk.
     */
    public LevelSave currentSave = new LevelSave(currentlyPlaying);

    // Package-private constructor since dagger injects the dependencies.
    @Inject
    LevelResolver(
        Level1Screen level1Screen,
        Level2Screen level2Screen,
        BonusLevelScreen bonusLevelScreen
    ) {
        this.level1Screen = level1Screen;
        this.level2Screen = level2Screen;
        this.bonusLevelScreen = bonusLevelScreen;
    }

    /**
     * Load the current save file from disk.
     */
    public void loadSave() {
        var fileHandle = Gdx.files.internal("save_file.json");
        if (fileHandle.exists()) {
            // Try to read the file and convert it to the LevelSave record.
            // If it fails it's likely the user messed with the file.
            // I haven't specifically done extra error handling here because the player
            // needs to really go out of their way in order for it to error.
            try (var reader = new InputStreamReader(fileHandle.read(), StandardCharsets.UTF_8)) {
                currentSave = mapper.readValue(reader, LevelSave.class);
            } catch (IOException exception) {
                throw new RuntimeException(exception);
            }
        } else {
            // The save file doesn't exist yet. Create the file and save it.
            try {
                mapper.writeValue(fileHandle.file(), currentSave);
            } catch (IOException exception) {
                // Failed to write to file for whatever reason.
                throw new RuntimeException(exception);
            }
        }

    }

    /**
     * Get the currently saved level as its Screen object.
     */
    public LevelScreen getCurrentSaveLevelScreen() {
        return switch (currentSave.level) {
            case LEVEL_1 -> level1Screen;
            case LEVEL_2 -> level2Screen;
            case BONUS_LEVEL -> bonusLevelScreen;
        };
    }

    /**
     * Get the currently playing level as its Screen object.
     */
    public LevelScreen getCurrentlyPlayingLevelScreen() {
        return switch (currentlyPlaying) {
            case LEVEL_1 -> level1Screen;
            case LEVEL_2 -> level2Screen;
            case BONUS_LEVEL -> bonusLevelScreen;
        };
    }


    /**
     * Save the currentSave into the actual file.
     */
    public void saveFile() {
        try {
            mapper.writeValue(Gdx.files.internal(SAVE_FILE_PATH).file(), currentSave);
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }
}
