Feature: csrf and sign-in end point

Background:
* url baseUrl

Given path 'login'
When method get
Then status 200
* def csrf = //input[@name="_csrf"]/@value

#<html lang="en">...<body><div><form>
#   <input name="_csrf" type="hidden" value="..." />


Scenario: html url encoded form submit - post
    Given path 'login'
    And form field username = 'a'
    And form field password = 'aa'
    And form field _csrf = csrf
    When method post
    Then status 200
    * string response = response
    And match response contains 'Plantilla de IW'
