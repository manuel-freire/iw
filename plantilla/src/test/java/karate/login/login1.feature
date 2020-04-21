Feature: csrf and sign-in end point

Background:
* url baseUrl
* def util = Java.type('karate.KarateTests')

Given path 'login'
When method get
Then status 200
* string response = response    
* def csrf = util.selectAttribute(response, "input[name=_csrf]", "value");
* print csrf

# selectores para util.select*: ver https://jsoup.org/cookbook/extracting-data/selector-syntax
# objetivo de la forma
# <html lang="en">...<body><div><form>
#   <input name="_csrf" type="hidden" value="..." />

Scenario: html url encoded form submit - post
    Given path 'login'
    And form field username = 'a'
    And form field password = 'aa'
    And form field _csrf = csrf
    When method post
    Then status 200
    * string response = response    
    * def h4s = util.selectHtml(response, "h4");
    * print h4s
    And match h4s contains 'Usuarios'
