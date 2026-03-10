Feature: login en servidor

  Scenario: login correcto como b
    Given driver baseUrl + '/login'
    And input('#username', 'b')
    And input('#password', 'aa')
    When submit().click(".form-signin button")
    Then waitForUrl(baseUrl + '/user/2')
  Scenario: navigate to daily game
    Given driver baseUrl + '/guess'
    And script("document.getElementById('btnPlay').click()")
    And delay(1000)
    And script("document.getElementById('btnPlay').click()")
  Scenario: navigate to favorite songs
    Given driver baseUrl + '/favoriteSongs'
    And repeat(3, "script(\"document.querySelectorAll('button:contains(\\\"Reproducir\\\")')[\"+_+\"].click()\")")
    And delay(2000)
    And repeat(3, "script(\"document.querySelectorAll('button:contains(\\\"Ir hacia atrás\\\")')[\"+_+\"].click()\")")
    And delay(2000)
    And repeat(3, "script(\"document.querySelectorAll('button:contains(\\\"Ir hacia adelante\\\")')[\"+_+\"].click()\")")
  Scenario: navigate to gartic and join a lobby
    Given driver baseUrl + '/games'
    And click("a[href='/gartic']")
    And submit().click("button:contains('Crear una sala')")
    And submit().click("button:contains('Iniciar partida')")
  Scenario: navigate to continuation game and create lobby
    Given driver baseUrl + '/games'
    And click("a[href='/lobby/continue']")
    And submit().click("button:contains('Crear una sala')")
  Scenario: logout after login
    Given driver baseUrl + '/login'
    And input('#username', 'a')
    And input('#password', 'aa')
    When submit().click(".form-signin button")
    Then waitForUrl(baseUrl + '/admin')
    When submit().click("{button}logout")
    Then waitForUrl(baseUrl + '/login')
    And select("select[name='answer']", "option:first")
    And submit().click("button:contains('Comprobar')")
