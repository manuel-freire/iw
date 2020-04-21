Feature: csrf and log-out endpoint

Background:
* url baseUrl
* call read('login1.feature')
* def util = Java.type('karate.KarateTests')

Scenario: user page
    Given path 'user/1'
    When method get
    Then status 200
    * string response = response
    * def userName = util.selectHtml(response, "h4>span")
    And assert userName == 'a'
