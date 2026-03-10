Feature: Login

  @login_como_admin
  Scenario: Login como administrador
    Given url baseUrl
    And path 'login'
    When method GET
    Then status 200
    * def csrf = karate.extract(response, '"_csrf" value="([^"]*)"', 1)
    Given path 'login'
    And form field username = adminUsername
    And form field password = adminPassword
    And form field _csrf    = csrf
    When method POST
    Then status 200