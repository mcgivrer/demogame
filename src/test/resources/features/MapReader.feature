Feature: MapReader read map from file

  Test the read capability of the MapReader.

  Scenario: Read a test map with a test asset
    Given a Game instance is running
    When the file "/res/maps/map-test.json" is read
    Then the MapObjectAsset length=21 items
    And the MapLevel width=80
    And the MapLevel height=40
    And a GameObject named "player" is created
    And a GameObject named "enemy_0001" is created
    And a GameObject named "enemy_0002" is created
    And a GameObject named "enemy_0003" is created
    And a GameObject named "enemy_0004" is created
    And a GameObject named "enemy_0005" is created

  Scenario: Verify that GameObject "player" is well initialized
    Given a Game instance is running
    When the file "/res/maps/map-test.json" is read
    Then a GameObject named "player" is created
    And GameObject "player" has "size" is 32 x 32
    And GameObject "player" has "priority"=10
    And GameObject "player" has "layer"=2
    And GameObject "player" has "canCollect"=true
    And GameObject "player" attribute "energy"=100.0
    And GameObject "player" attribute "mana"=100.0
    And GameObject "player" attribute "damage"=50.0
    And GameObject "player" attribute "coins"=250

  Scenario: Verify that MapObject "O" is well initialized
    Given a Game instance is running
    When the file "/res/maps/map-test.json" is read
    Then an asset MapObject named "O" is created
    And MapObject "O" type is "object"
    And MapObject "O" has "size" is 32 x 32
    And MapObject "O" has "collectible"=true
    And MapObject "O" has "money"=5