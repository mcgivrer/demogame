package tests;

import static org.junit.Assert.assertTrue;

import core.Game;
import core.object.GameObject;
import core.object.ObjectManager;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

public class GameObjectStepDefs {

    Game dg;

    @Given("^DemoGame instance is created$")
    public void demoGameInstanceIsCreated() {
        dg = new Game(new String[]{"w=320", "h=200", "s=2"});
        dg.initialize();
    }

    @When("^I Add a new GameObject named \"([^\"]*)\" at \\((-?\\d+),(-?\\d+)\\) with size \\((-?\\d+),(-?\\d+)\\)$")
    public void iAddANewGameObjectNamedAt(String name, int x, int y, int width, int height) throws Throwable {
        GameObject go = new GameObject(name, x, y, width, height);
        dg.sysMan.getSystem(ObjectManager.class).add(go);
    }

    @Then("^the GameObject \"([^\"]*)\" exists in the DemoGame objects map\\.$")
    public void theGameObjectExistsInTheDemoGameObjectsMap(String name) throws Throwable {
        assertTrue("", dg.sysMan.getSystem(ObjectManager.class).contains(name));
    }

    @Then("^the GameObject \"([^\"]*)\" does not exist in the DemoGame objects map\\.$")
    public void theGameObjectDoesNotExistInTheDemoGameObjectsMap(String name) throws Throwable {
        assertTrue("", !dg.sysMan.getSystem(ObjectManager.class).contains(name));
    }

    @And("^I Remove the GameObject named \"([^\"]*)\"$")
    public void iRemoveTheGameObjectNamed(String name) throws Throwable {
    	dg.sysMan.getSystem(ObjectManager.class).removeObject(name);
    }

    @When("^I removed all objects based name \"([^\"]*)\"$")
    public void iRemovedAllObjectsBasedName(String filteredName) throws Throwable {
    	dg.sysMan.getSystem(ObjectManager.class).removeFilteredObjects(filteredName);
    }

}