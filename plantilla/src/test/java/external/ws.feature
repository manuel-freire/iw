Feature: envío de mensajes

  Scenario: envio de mensajes via websocket
    Given call read('login.feature@login_a')
    And driver baseUrl + '/user/2'
    And def mensaje = script("'el número secreto es el ' + Math.floor(Math.random() * 1000)")
    And input('#message', mensaje)
    And click("button[id=sendmsg]")
    And delay(500)

    When call read('login.feature@logout')
    And call read('login.feature@login_b')
    Then match html('#mensajes') contains mensaje
    And driver.screenshot()
