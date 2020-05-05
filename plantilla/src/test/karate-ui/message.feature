Feature: browser automation 2

Background:
  # chromium bajo linux; si usas google-chrome, puedes quitar executable (que es lo que usaría por defecto)
  * configure driver = { type: 'chrome', executable: '/usr/bin/chromium-browser', showDriverLog: true }
    
Scenario: login using chrome

  Given driver 'http://localhost:8080/login'
  And input('#username', 'a')
  And input('#password', 'aa')
  When submit().click("button[type=submit]")
  And match html('title') contains 'Admin'
  * driver.screenshot()

  When click("img[class=userthumb]")
  And match html('title') contains 'Perfil'
  * driver.screenshot()

  
