package tests;

import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;

import static org.junit.Assert.assertTrue;

public class GameStepDefs extends GameMasterStepDefs {

    @Given("^Game is instantiated with \"([^\"]*)\"$")
    public void GameIsInstantiatedWith(String arguments) throws Throwable {
        super.GameIsInstantiatedWith(arguments);
    }

    @Then("^the Game instance initialized$")
    public void theGameInstanceInitialized() {
        super.theGameInstanceInitialized();
    }


    @And("^the Game config has width=(\\d+)$")
    public void theGameInstanceHasWidth(int width) {
        assertTrue("The width is not correctly set", game.config.screenWidth == width);
    }

    @And("^the Game config has height=(\\d+)$")
    public void theGameInstanceHasHeight(int height) {
        assertTrue("The height is not correctly set", game.config.screenHeight == height);
    }

    @And("^the Game config has scale=(\\d+)$")
    public void theGameInstanceHasScale(int scale) {
        assertTrue("The scale is not correctly set", game.config.screenScale == scale);
    }

    @And("^the Game screenBuffer is width=(\\d+) and height=(\\d+)$")
    public void theGameScreenBufferIsWidthAndHeight(int width, int height) {
        assertTrue("screenBuffer width has not been set correctly to " + width, game.renderer.screenBuffer.getWidth() == width);
        assertTrue("screenBuffer height has not been set correctly to " + height, game.renderer.screenBuffer.getHeight() == height);
    }
}
