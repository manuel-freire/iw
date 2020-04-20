Feature: csrf and log-out endpoint

Background:
* url baseUrl
* call read('login1.feature')

Scenario: user page

    Given path 'user/1'
    When method get
    Then status 200
    * string response = response
    And match response contains 'Información del usuario <span>a'
