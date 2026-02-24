package es.ucm.fdi.iw.auxiliar;

import java.security.SecureRandom;
import java.util.stream.Collectors;

public class GameUtils {
    private static final String alfabeto = "ABCDEFGHIJKLMNÑOPQRSTUVWXYZ1234567890";
    private static final SecureRandom random = new SecureRandom();

    public static String generateRandomCode(int length){
        String randomCode;
        randomCode = random.ints(length, 0, alfabeto.length()).mapToObj(alfabeto::charAt).map(Object::toString).collect(Collectors.joining());
        return randomCode;
    }
}
