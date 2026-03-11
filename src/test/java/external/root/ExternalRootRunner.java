package external.root;

import com.intuit.karate.junit5.Karate;

public class ExternalRootRunner {
    
    @Karate.Test
    Karate testRoot() {
        return Karate.run("root").relativeTo(getClass());
    }    

}
