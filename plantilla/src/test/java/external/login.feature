Feature: login en servidor

  Scenario: login malo en github
    Given driver 'https://github.com/login'
    And input('#login_field', 'dummy')
    And input('#password', 'world')
    When submit().click("input[name=commit]")
    Then match html('.flash-error') contains 'Incorrect username or password.'

  Scenario: login malo en plantilla
    Given driver baseUrl + '/user/2'
    And input('#username', 'dummy')
    And input('#password', 'world')
    When submit().click(".form-signin button")
    Then match html('.error') contains 'Error en nombre de usuario o contraseña'

  @login_b
  Scenario: login correcto como b
    Given driver baseUrl + '/login'
    And input('#username', 'b')
    And input('#password', 'aa')
    When submit().click(".form-signin button")
    Then waitForUrl(baseUrl + '/user/2')

  @logout
  Scenario: logout after login
    Given driver baseUrl + '/user/2'
    When submit().click("{button}logout")
    Then waitForUrl(baseUrl + '/login')

  @login_a
  Scenario: login correcto como a
    Given driver baseUrl + '/login'
    And input('#username', 'a')
    And input('#password', 'aa')
    When submit().click(".form-signin button")
    Then waitForUrl(baseUrl + '/admin')

  Scenario: logout after login
    Given driver baseUrl + '/user/2'
    When submit().click("{button}logout")
    Then waitForUrl(baseUrl + '/login')
