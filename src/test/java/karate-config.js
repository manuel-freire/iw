function fn() {
    var env = karate.env; // get system property 'karate.env'
    karate.log('karate.env system property was:', env);

    if (!env) {
        env = 'dev';
    }

    /**
     * Variables here are available in all tests
     */
    var config = {
        env: env,
        myVarName: 'someValue',
        baseUrl: 'http://localhost:8080'
    }

    /**
     * Drivers for tests - currently configured value is good for Linux
     */
    karate.configure('driver', {
        type: 'chrome',
        // descomentar para chromium bajo linux
        executable: '/usr/bin/chromium',
        //executable: "C:\\Program Files\\Google\\Chrome\\Application\\chrome.exe",
         addOptions: ["--remote-allow-origins=*", "--incognito",'--disable-gcm','--disable-google-cloud-messaging'],
        showDriverLog: true,
        showBrowser: true
    })

    if (env == 'dev') {
        // customize
        // e.g. config.foo = 'bar';
    } else if (env == 'e2e') {
        // customize
    }
    //for external testing.
    karate.configure('connectTimeout', 10000);
    karate.configure('readTimeout', 30000);
    return config;
}