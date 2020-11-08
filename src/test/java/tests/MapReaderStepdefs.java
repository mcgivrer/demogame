package tests;

import cucumber.api.java8.En;

public class MapReaderStepdefs implements En {
    public MapReaderStepdefs() {
        Given("^a BddTestGame instance is running$", () -> {
        });
        Then("^the MapObjectAsset contains \"([^\"]*)\" MapObject items$", (String arg0) -> {
        });
        When("^the file \"([^\"]*)\" is read$", (String arg0) -> {
        });
        And("^the MapLevel width=(\\d+)$", (Integer arg0) -> {
        });
        And("^the MapLevel height=(\\d+)$", (Integer arg0) -> {
        });
        Then("^a GameObject named \"([^\"]*)\" is created$", (String arg0) -> {
        });
    }
}
