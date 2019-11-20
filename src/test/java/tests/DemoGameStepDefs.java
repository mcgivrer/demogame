package tests;

import core.Game;
import core.Renderer;
import core.system.SystemManager;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;

import static org.junit.Assert.assertTrue;

public class DemoGameStepDefs {
    Game dg;
    String[] argc;

    @Given("^Game is instantiated with \"([^\"]*)\"$")
    public void gameIsInstantiatedWith(String arguments) throws Throwable {
        argc = arguments.split(" ");
        dg = new Game(argc);
    }

    @And("^the Game config has width=(\\d+)$")
    public void theGameInstanceHasWidth(int width) {
        assertTrue("The width is not correctly set", dg.config.screenWidth == width);
    }

    @And("^the Game config has height=(\\d+)$")
    public void theGameInstanceHasHeight(int height) {
        assertTrue("The height is not correctly set", dg.config.screenHeight == height);
    }

    @And("^the Game config has scale=(\\d+)$")
    public void theGameInstanceHasScale(int scale) {
        assertTrue("The scale is not correctly set", dg.config.screenScale == scale);
    }

    @Then("^the Game instance initialized$")
    public void theGameInstanceInitialized() {
        dg.initialize();
    }

    @And("^the Game screenBuffer is width=(\\d+) and height=(\\d+)$")
    public void theGameScreenBufferIsWidthAndHeight(int width, int height) {
        Renderer renderer = SystemManager.get(Renderer.class);
        assertTrue("screenBuffer width has not been set correctly to " + width, renderer.screenBuffer.getWidth() == width);
        assertTrue("screenBuffer height has not been set correctly to " + height, renderer.screenBuffer.getHeight() == height);
    }
}
