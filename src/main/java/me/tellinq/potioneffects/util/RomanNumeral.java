package me.tellinq.potioneffects.util;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

// Xander used this solution in contrast to my previous solution I used in CheatBreaker. This one should help with performance
// Original creation from Ben-Hur Langoni Junior: https://stackoverflow.com/a/19759564
public class RomanNumeral {

    public static final RomanNumeral INSTANCE = new RomanNumeral();
    private final static TreeMap<Integer, String> map = new TreeMap<>();

    static {
        map.put(1000, "M");
        map.put(900, "CM");
        map.put(500, "D");
        map.put(400, "CD");
        map.put(100, "C");
        map.put(90, "XC");
        map.put(50, "L");
        map.put(40, "XL");
        map.put(10, "X");
        map.put(9, "IX");
        map.put(5, "V");
        map.put(4, "IV");
        map.put(1, "I");
    }

    private final Map<Integer, String> numeralCache = new HashMap<>();

    public String getCache(int number) {
        if (numeralCache.containsKey(number)) return numeralCache.get(number);

        String roman = toRoman(number);
        numeralCache.put(number, roman);
        return roman;
    }

    public static String toRoman(int number) {
        int l = map.floorKey(number);
        if (number == l) {
            return map.get(number);
        }
        return map.get(l) + toRoman(number - l);
    }

}
