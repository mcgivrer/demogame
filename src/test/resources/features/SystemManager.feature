Feature: The System Manager

  As a developer, I want to add and manage some systems in my game.

  Background: My Game is running
    Given Game is instantiated with "w=320 h=200 s=2"
    Then the Game instance initialized

  Scenario: I add a system
    Given the SystemManager is up and running
    And the System TestSystem in created
    And I add this new TestSystem instance
    Then the TestSystem named "testsys" is retrievable from SystemManager
