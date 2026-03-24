package es.ucm.fdi.iw.auxiliar;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class GameUtils {
    private static final String alfabeto = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
    private static final SecureRandom random = new SecureRandom();

    public static String generateRandomCode(int length){
        String randomCode;
        randomCode = random.ints(length, 0, alfabeto.length()).mapToObj(alfabeto::charAt).map(Object::toString).collect(Collectors.joining());
        return randomCode;
    }

    public static <K, V> Map<K, V> shiftValuesRight(Map<K, V> originalMap) {
        // Handle edge cases (empty or 1 item)
        if (originalMap == null || originalMap.size() <= 1) {
            return originalMap == null ? null : new LinkedHashMap<>(originalMap);
        }

        // 1. Extract keys into a list to lock in the "order" (i)
        List<K> keys = new ArrayList<>(originalMap.keySet());
        int len = keys.size();

        // 2. Use LinkedHashMap to ensure the returned map maintains this exact order
        Map<K, V> shiftedMap = new LinkedHashMap<>();

        // 3. Shift values using safe circular modulo math
        for (int i = 0; i < len; i++) {
            K currentKey = keys.get(i);
            
            // Safe way to do (i - 1 % len) in Java to avoid negative numbers
            int prevIndex = (i - 1 + len) % len; 
            K prevKey = keys.get(prevIndex);
            
            // The current key gets the value that belonged to the previous key
            shiftedMap.put(currentKey, originalMap.get(prevKey));
        }

        return shiftedMap;
    }
}
