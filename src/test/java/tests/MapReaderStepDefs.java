package tests;

import core.ResourceManager;
import core.map.MapLevel;
import core.map.MapReader;
import core.object.GameObject;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class MapReaderStepDefs extends GameMasterStepDefs {

    MapLevel ml;

    @Given("^a Game instance is running$")
    public void aGameInstanceIsRunning() {
        super.demoGameInstanceIsCreated();
    }

    @When("^the file \"([^\"]*)\" is read$")
    public void theFileMapTestJsonIsRead(String mapPath) {
        ResourceManager.add(new String[]{
                "/res/maps/map-test.json",
                "/res/assets/asset-test.json",
                "/res/images/background-1.jpg",
                "/res/images/tileset-1.png"});
        ml = MapReader.readFromFile(mapPath);
    }

    @Then("^the MapObjectAsset length=(\\d+) items$")
    public void theMapObjectAssetContainsMapObjectItems(int nbItems) {
        assertEquals("read map does not contains " + nbItems + " map objects", ml.asset.objects.size(), nbItems);
    }

    @And("^the MapLevel width=(\\d+)$")
    public void theMapLevelWidth(int width) {
        assertEquals("The map width is not " + width, ml.tiles.length, width);
    }

    @And("^the MapLevel height=(\\d+)$")
    public void theMapLevelHeight(int height) {
        assertEquals("The map height is not " + height, ml.tiles[0].length, height);
    }

    @And("^a GameObject named \"([^\"]*)\" is created$")
    public void aGameObjectNamedIsCreated(String gameObjectName) {
        assertNotNull("the read MapLevel does not contain an object named " + gameObjectName, ml.mapObjects.get(gameObjectName));
    }

    @And("^GameObject \"([^\"]*)\" has \"([^\"]*)\" is (\\d+) x (\\d+)$")
    public void gameobjectHasIsX(String objectName, String attribute, int sx, int sy) throws Throwable {
        GameObject go = ml.mapObjects.get(objectName);
        // TODO retrieve attribute from Object.

    }
}
