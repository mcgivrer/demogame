Feature: manage GameObject

  The Game will offer capability to add and remove one or more GameObject(s).

  Scenario: Add an object
    Given Game instance is created
    When I Add a new GameObject named "player1" at (160,100) with size (16,16)
    Then the GameObject "player1" exists in the Game objects map.

  Scenario: Remove an object
    Given Game instance is created
    When I Add a new GameObject named "player2" at (160,100) with size (16,16)
    Then the GameObject "player2" exists in the Game objects map.
    And I Remove the GameObject named "player2"
    Then the GameObject "player2" does not exist in the Game objects map.

  Scenario Outline: Add collection of object
    Given Game instance is created
    When I Add a new GameObject named "<name>" at (<x>,<y>) with size (<width>,<height>)
    Then the GameObject "<name>" exists in the Game objects map.

    Examples:
      | name    | x  | y   | width | height |
      | player3 | 12 | 102 | 16    | 16     |
      | enem_1  | 22 | 132 | 16    | 16     |
      | enem_2  | 32 | 164 | 16    | 16     |
      | enem_3  | 42 | 180 | 16    | 16     |

  Scenario: Remove filtered name objects
    Given Game instance is created
    When I removed all objects based name "enem_"
    Then the GameObject "enem_1" does not exist in the Game objects map.
    Then the GameObject "enem_2" does not exist in the Game objects map.
    Then the GameObject "enem_3" does not exist in the Game objects map.