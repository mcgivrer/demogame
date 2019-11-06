package tests;

import core.object.GameObject;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

import static org.junit.Assert.assertTrue;

public class GameObjectStepDefs extends GameMasterStepDefs {


    @Given("^Game instance is created$")
    public void demoGameInstanceIsCreated() {
        super.demoGameInstanceIsCreated();
    }

    @When("^I Add a new GameObject named \"([^\"]*)\" at \\((-?\\d+),(-?\\d+)\\) with size \\((-?\\d+),(-?\\d+)\\)$")
    public void iAddANewGameObjectNamedAt(String name, int x, int y, int width, int height) throws Throwable {
        GameObject go = new GameObject(name, x, y, width, height);
        game.stateManager.getCurrent().addObject(go);
    }

    @Then("^the GameObject \"([^\"]*)\" exists in the DemoGame objects map\\.$")
    public void theGameObjectExistsInTheDemoGameObjectsMap(String name) throws Throwable {
        assertTrue("", game.stateManager.getCurrent().getObjects().containsKey(name));
    }

    @Then("^the GameObject \"([^\"]*)\" does not exist in the DemoGame objects map\\.$")
    public void theGameObjectDoesNotExistInTheDemoGameObjectsMap(String name) throws Throwable {
        assertTrue("", !game.stateManager.getCurrent().getObjects().containsKey(name));
    }

    @And("^I Remove the GameObject named \"([^\"]*)\"$")
    public void iRemoveTheGameObjectNamed(String name) throws Throwable {
        game.stateManager.getCurrent().removeObject(name);
    }

    @When("^I removed all objects based name \"([^\"]*)\"$")
    public void iRemovedAllObjectsBasedName(String filteredName) throws Throwable {
        game.stateManager.getCurrent().removeFilteredObjects(filteredName);
    }
}