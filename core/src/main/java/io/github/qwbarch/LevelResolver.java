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

@Singleton
public final class LevelResolver {
    private static final String SAVE_FILE_PATH = "save_file.json";

    public enum Level {
        LEVEL_1,
        LEVEL_2,
        BONUS_LEVEL;
    }

    public record LevelSave(Level level) {
    }

    private final LevelScreen level1Screen;
    private final LevelScreen level2Screen;
    private final LevelScreen bonusLevelScreen;

    private ObjectMapper mapper = new ObjectMapper().registerModule(new ParameterNamesModule());
    public Level currentlyPlaying = LEVEL_1;
    public LevelSave currentSave = new LevelSave(currentlyPlaying);

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

    public void loadSave() {
        var fileHandle = Gdx.files.internal("save_file.json");
        if (fileHandle.exists()) {
            try (var reader = new InputStreamReader(fileHandle.read(), StandardCharsets.UTF_8)) {
                currentSave = mapper.readValue(reader, LevelSave.class);
            } catch (IOException exception) {
                throw new RuntimeException(exception);
            }
        } else {
            try {
                mapper.writeValue(fileHandle.file(), currentSave);
            } catch (IOException exception) {
                // Failed to write to file for whatever reason.
                throw new RuntimeException(exception);
            }
        }

    }

    public LevelScreen getCurrentSaveLevelScreen() {
        return switch (currentSave.level) {
            case LEVEL_1 -> level1Screen;
            case LEVEL_2 -> level2Screen;
            case BONUS_LEVEL -> bonusLevelScreen;
        };
    }

    public LevelScreen getCurrentlyPlayingLevelScreen() {
        return switch (currentlyPlaying) {
            case LEVEL_1 -> level1Screen;
            case LEVEL_2 -> level2Screen;
            case BONUS_LEVEL -> bonusLevelScreen;
        };
    }


    public void saveFile() {
        try {
            mapper.writeValue(Gdx.files.internal(SAVE_FILE_PATH).file(), currentSave);
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }
}
