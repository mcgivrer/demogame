Feature: Game command line arguments

  The Game instance can be configured via command line arguments.

  Scenario: Add Arguments
    Given Game is instantiated with "w=320 h=200 s=2"
    Then the Game instance initialized
    And the Game config has width=320
    And the Game config has height=200
    And the Game config has scale=2
    And the Game screenBuffer is width=320 and height=200
