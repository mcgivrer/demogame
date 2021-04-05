package tests;

import static org.junit.Assert.assertTrue;

import core.Game;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;

import static org.junit.Assert.assertEquals;

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
        assertEquals("The width is not correctly set", width, dg.config.screenWidth);
    }

    @And("^the DemoGame config has height=(\\d+)$")
    public void theDemoGameInstanceHasHeight(int height) {
        assertEquals("The height is not correctly set", height, dg.config.screenHeight);
    }

    @And("^the DemoGame config has scale=(\\d+)$")
    public void theDemoGameInstanceHasScale(double scale) {
        assertEquals("The scale is not correctly set", scale, dg.config.screenScale,0.005);
    }

    @Then("^the DemoGame instance initialized$")
    public void theDemoGameInstanceInitialized() {
        dg.initialize();
    }

    @And("^the DemoGame screenBuffer is width=(\\d+) and height=(\\d+)$")
    public void theDemoGameScreenBufferIsWidthAndHeight(int width, int height) {
        assertEquals("screenBuffer width has not been set correctly to " + width, dg.renderer.getScreenBuffer().getWidth(),width);
        assertEquals("screenBuffer height has not been set correctly to " + height, dg.renderer.getScreenBuffer().getHeight(),height);
    }
}
