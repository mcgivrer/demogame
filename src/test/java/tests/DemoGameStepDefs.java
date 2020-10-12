package tests;

import core.Game;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;

import static org.junit.Assert.assertTrue;

public class DemoGameStepDefs {
    Game dg;
    String[] argc;

    @Given("^DemoGame is instantiated with \"([^\"]*)\"$")
    public void demoGameIsInstantiatedWith(String arguments) throws Throwable {
        argc = arguments.split(" ");
        dg = new Game(argc);
    }

    @And("^the DemoGame config has width=(\\d+)$")
    public void theDemoGameInstanceHasWidth(int width) {
        assertTrue("The width is not correctly set", dg.config.screenWidth == width);
    }

    @And("^the DemoGame config has height=(\\d+)$")
    public void theDemoGameInstanceHasHeight(int height) {
        assertTrue("The height is not correctly set", dg.config.screenHeight == height);
    }

    @And("^the DemoGame config has scale=(\\d+)$")
    public void theDemoGameInstanceHasScale(int scale) {
        assertTrue("The scale is not correctly set", dg.config.screenScale == scale);
    }

    @Then("^the DemoGame instance initialized$")
    public void theDemoGameInstanceInitialized() {
        dg.initialize();
    }

    @And("^the DemoGame screenBuffer is width=(\\d+) and height=(\\d+)$")
    public void theDemoGameScreenBufferIsWidthAndHeight(int width, int height) {
        assertTrue("screenBuffer width has not been set correctly to " + width, dg.renderer.screenBuffer.getWidth() == width);
        assertTrue("screenBuffer height has not been set correctly to " + height, dg.renderer.screenBuffer.getHeight() == height);
    }
}
