package org.cataclysm.utils.font;

import java.util.HashMap;
import java.util.Map;

public class TinyCaps {
    public static String tinyCaps(String text) {
        final var styledText = new StringBuilder();
        for (var textChar : text.toCharArray()) {
            final var characterString = String.valueOf(textChar);
            styledText.append(formatMap.getOrDefault(textChar, characterString));
        }
        return styledText.toString();
    }

    private static final Map<Character, String> formatMap = createFormatMap();

    private static Map<Character, String> createFormatMap() {
        Map<Character, String> map = new HashMap<>();
        map.put('a', "ᴀ");
        map.put('b', "ʙ");
        map.put('c', "ᴄ");
        map.put('d', "ᴅ");
        map.put('e', "ᴇ");
        map.put('f', "ғ");
        map.put('g', "ɢ");
        map.put('h', "ʜ");
        map.put('i', "ɪ");
        map.put('j', "ᴊ");
        map.put('k', "ᴋ");
        map.put('l', "ʟ");
        map.put('m', "ᴍ");
        map.put('n', "ɴ");
        map.put('o', "ᴏ");
        map.put('p', "ᴘ");
        map.put('q', "ǫ");
        map.put('r', "ʀ");
        map.put('t', "ᴛ");
        map.put('u', "ᴜ");
        map.put('v', "ᴠ");
        map.put('w', "ᴡ");
        map.put('y', "ʏ");
        map.put('z', "ᴢ");
        return map;
    }
}
