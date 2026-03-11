Feature: en este apartado se realiza las pruebas correspondientes al RootController

  Background:
    * configure driver = { type: 'chrome', showDriverLog: true }

  @login_admin
  Scenario: Login como administrador
    # Navegamos hacia la página de login y comprobamos que carga correctamente el formulario
    * driver baseUrl + '/login'
    * screenshot()
    * match html('title') contains 'Login'

    # Introducimos nuestras credenciales y enviamos el formulario
    * input('#username', adminUsername)
    * input('#password', adminPassword)
    * submit().click("button[type=submit]")
    * screenshot()

    # Validamos que después de hacer el login, nos lleva al panel de admin
    * match html('body') contains 'Usuarios'

  Scenario: Flujo de navegación desde home hasta el catálogo de juegos
    # Hacemos login
    * call read('root.feature@login_admin')

    # Verificamos que estamos en el enlace que contiene el catálogo de juegos
    * driver baseUrl + '/'
    * screenshot()
    * match html('body') contains 'href="/games"'

    # Navegamos al catálogo y validamos que aparecen los modos de juego
    * driver baseUrl + '/games'
    * screenshot()
    * match html('body') contains 'Canción sorpresa'
    * match html('body') contains 'Continuación de canción'

  Scenario: navegamos al lobby del modo continuación de canción y se muestra correctamente 
    # Hacemos login
    * call read('root.feature@login_admin')

    * driver baseUrl + '/lobby/continuacion'
    * screenshot()
    * match html('body') contains 'Continuación'

  Scenario: navegamos al modo de gartic y comprobamos que se muestra correctamente
    # Hacemos login
    * call read('root.feature@login_admin')

    # Navegamos hacia el leaderboard
    * driver baseUrl + '/gartic'
    * screenshot()
    
    # Verificamos que la página tiene el título correcto
    * match html('body') contains 'Gartic'

  Scenario: navegamos al leaderboard y comprobamos que se muestra correctamente
    # Hacemos login
    * call read('root.feature@login_admin')

    # Navegamos hacia el leaderboard
    * driver baseUrl + '/leaderboard'
    * screenshot()

    # Verificamos que la página tiene título de leaderboard
    * match html('body') contains 'leaderboard'

  Scenario: La página sobre los creadores cargan sin errores
    # Hacemos login
    * call read('root.feature@login_admin')

    # Navegamos hacia la página de "Sobre nosotros"
    * driver baseUrl + '/about'
    * screenshot()
    * match html('body') contains 'Sobre nosotros'

  Scenario: La página sobre las canciones favoritas del usuario actual cargan sin errores
    # Hacemos login
    * call read('root.feature@login_admin')

    # Navegamos hacia la página de canciones favoritas
    * driver baseUrl + '/favoriteSongs'
    * screenshot()
    * match html('body') contains 'Mis canciones favoritas'  