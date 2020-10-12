Feature: DemoGame command line arguments

  The DemoGame instance can be configured via command line arguments.

  Scenario: Add Arguments
    Given DemoGame is instantiated with "w=320 h=200 s=2"
    Then the DemoGame instance initialized
    And the DemoGame config has width=320
    And the DemoGame config has height=200
    And the DemoGame config has scale=2
    And the DemoGame screenBuffer is width=320 and height=200
