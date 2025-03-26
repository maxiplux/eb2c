Feature: Organization Service
  As an application
  I want to manage organizations
  So that I can create, update, retrieve, and delete organization information

  Scenario: Create a new organization
    Given I have a valid organization request with name "Test Organization", description "Test Description" and tax ID "123456789"
    When I create a new organization
    Then the organization is successfully created
    And the created organization has the name "Test Organization", description "Test Description" and tax ID "123456789"

  Scenario: Get all organizations
    Given the following organizations exist:
      | name           | description   | taxId     |
      | Organization 1 | Description 1 | 111111111 |
      | Organization 2 | Description 2 | 222222222 |
    When I request all organizations
    Then I should receive a list of 2 organizations

  Scenario: Get organization by ID
    Given an organization with ID "1" exists
    When I request the organization with ID "1"
    Then I should receive the organization details

  Scenario: Get organization with non-existent ID
    When I request the organization with ID "999"
    Then I should receive an entity not found error with message "Organization not found with id: 999"

  Scenario: Update an organization
    Given an organization with ID "1" exists
    When I update the organization with ID "1" with name "Updated Organization", description "Updated Description" and tax ID "987654321"
    Then the organization is successfully updated
    And the updated organization has the name "Updated Organization", description "Updated Description" and tax ID "987654321"

  Scenario: Delete an organization
    Given an organization with ID "1" exists
    When I delete the organization with ID "1"
    Then the organization is successfully deleted

  Scenario: Search organizations by name
    Given the following organizations exist:
      | name           | description   | taxId     |
      | ABC Corp       | Description 1 | 111111111 |
      | XYZ Ltd        | Description 2 | 222222222 |
      | ABC Industries | Description 3 | 333333333 |
    When I search for organizations with name "ABC"
    Then I should receive a list of 2 organizations
    And the list should contain organizations with names "ABC Corp" and "ABC Industries"

  Scenario: Get organization by tax ID
    Given an organization with tax ID "123456789" exists
    When I request the organization with tax ID "123456789"
    Then I should receive the organization details with tax ID "123456789"

  Scenario: Get organization with non-existent tax ID
    When I request the organization with tax ID "999999999"
    Then I should receive an entity not found error with message "Organization not found with tax ID: 999999999" 