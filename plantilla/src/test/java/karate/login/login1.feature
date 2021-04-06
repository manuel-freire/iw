Feature: csrf and sign-in end point

Background:
    * url baseUrl
    * def util = Java.type('karate.KarateTests')

Scenario: get login page, capture csrf, send login
    * path 'login'
    * method get
    * status 200
    * print response
    * print responseCookies
    # ... name="_csrf" value="0a7c65e8-4e8e-452f-ad44-40b995bb91d6" => 0a7c65e8-4e8e-452f-ad44-40b995bb91d6"
    * def csrf = karate.extract(response, '"_csrf" value="([^"]*)"', 1) 
    * print csrf

    * path 'login'
    * form field username = 'a'
    * form field password = 'aa'
    * form field _csrf = csrf
    * method post    
    * status 200
    * print response
    * print responseCookies
    * def h4s = util.selectHtml(response, "h4");
    * print h4s
    * match h4s contains 'Usuarios'
