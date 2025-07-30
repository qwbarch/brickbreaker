package io.github.qwbarch.screen;

import javax.inject.Inject;

public final class InstructionScreen implements Screen {
    @Inject
    InstructionScreen() {
       System.out.println("InstructionScreen constructor");
    }
}
