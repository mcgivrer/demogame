package tests;

import core.Game;
import core.object.GameObject;
import core.state.StateManager;
import core.system.SystemManager;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

import static org.junit.Assert.assertTrue;

public class GameObjectStepDefs {

    Game dg;

    @Given("^Game instance is created$")
    public void gameInstanceIsCreated() {
        dg = new Game(new String[]{"w=320", "h=200", "s=2"});
        dg.initialize();
    }

    @When("^I Add a new GameObject named \"([^\"]*)\" at \\((-?\\d+),(-?\\d+)\\) with size \\((-?\\d+),(-?\\d+)\\)$")
    public void iAddANewGameObjectNamedAt(String name, int x, int y, int width, int height) throws Throwable {
        GameObject go = new GameObject(name, x, y, width, height);
        SystemManager.get(StateManager.class).getCurrent().addObject(go);
    }

    @Then("^the GameObject \"([^\"]*)\" exists in the Game objects map\\.$")
    public void theGameObjectExistsInTheGameObjectsMap(String name) throws Throwable {
        assertTrue("", SystemManager.get(StateManager.class).getCurrent().getObjects().containsKey(name));
    }

    @Then("^the GameObject \"([^\"]*)\" does not exist in the Game objects map\\.$")
    public void theGameObjectDoesNotExistInTheGameObjectsMap(String name) throws Throwable {
        assertTrue("", !SystemManager.get(StateManager.class).getCurrent().getObjects().containsKey(name));
    }

    @And("^I Remove the GameObject named \"([^\"]*)\"$")
    public void iRemoveTheGameObjectNamed(String name) throws Throwable {
        SystemManager.get(StateManager.class).getCurrent().removeObject(name);
    }

    @When("^I removed all objects based name \"([^\"]*)\"$")
    public void iRemovedAllObjectsBasedName(String filteredName) throws Throwable {
        SystemManager.get(StateManager.class).getCurrent().removeFilteredObjects(filteredName);
    }

}