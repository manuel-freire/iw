Feature: RootController - historias de usuario completas

    Background:
        * url baseUrl
    
      Scenario: Flujo de navegación desde home hasta lobby pasando por juegos

    # enlace a /games
    Given path '/'
    When method GET
    Then status 200
    And match response contains 'href="/games"'

    # están los dos modos d juego
    Given path 'games'
    When method GET
    Then status 200
    And match response contains 'href="/gartic"'
    And match response contains 'href="/lobby/continue"'
    And match response contains 'Canción sorpresa'
    And match response contains 'Continuación de canción'

    # entrar al modo gartic
    Given path 'gartic'
    When method GET
    Then status 200
    And match response contains '<html'

    # volver a game y dar a continue
    Given path 'lobby/continue'
    When method GET
    Then status 200
    And match response contains '<html'