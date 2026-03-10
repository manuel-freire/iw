package internal.root;

import com.intuit.karate.junit5.Karate;

class InternalRootRunner {
    
    @Karate.Test
    Karate testRoot() {
        return Karate.run("root").relativeTo(getClass());
    }    
}
