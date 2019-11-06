package tests;

import core.Game;

public class GameMasterStepDefs {

    protected Game game;
    protected String[] argc;

    public void GameIsInstantiatedWith(String arguments) throws Throwable {
        argc = arguments.split(" ");
        game = new Game(argc);
    }

    public void theGameInstanceInitialized() {
        game.initialize();
    }

    public void demoGameInstanceIsCreated() {
        game = new Game(new String[]{"w=320", "h=200", "s=2"});
        game.initialize();
    }
}
