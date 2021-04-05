Feature: MapReader read map from file

  Test the read capability of the MapReader.

  Scenario: Read a test map with a test asset.
    Given a BddTestGame instance is running
    When the file "map-test.json" is read
    Then the MapObjectAsset contains "14" MapObject items
    And the MapLevel width=1
    And the MapLevel height=13

  Scenario: Read a test map with a test asset gets GameObject objects
    Given a BddTestGame instance is running
    When the file "map-test.json" is read
    Then a GameObject named "player" is created
    And a GameObject named "enemy_1" is created


