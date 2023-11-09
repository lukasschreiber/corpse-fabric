package org.lukasschreiber.corpsefabric.gui;

import java.util.HashMap;
import java.util.Map;
import java.util.function.IntBinaryOperator;

public class TextUtils {
    private static final Map<Character, Integer> CHARACTER_SIZE_MAP = new HashMap<>() {{
        put(' ', 4);
        put('!', 2);
        put('"', 5);
        put('#', 6);
        put('$', 6);
        put('%', 6);
        put('&', 6);
        put('\'', 3);
        put('(', 5);
        put(')', 5);
        put('*', 5);
        put('+', 6);
        put(',', 2);
        put('-', 6);
        put('.', 2);
        put('/', 6);
        put('0', 6);
        put('1', 6);
        put('2', 6);
        put('3', 6);
        put('4', 6);
        put('5', 6);
        put('6', 6);
        put('7', 6);
        put('8', 6);
        put('9', 6);
        put(':', 2);
        put(';', 2);
        put('<', 5);
        put('=', 6);
        put('>', 5);
        put('?', 6);
        put('@', 7);
        put('A', 6);
        put('B', 6);
        put('C', 6);
        put('D', 6);
        put('E', 6);
        put('F', 6);
        put('G', 6);
        put('H', 6);
        put('I', 4);
        put('J', 6);
        put('K', 6);
        put('L', 6);
        put('M', 6);
        put('N', 6);
        put('O', 6);
        put('P', 6);
        put('Q', 6);
        put('R', 6);
        put('S', 6);
        put('T', 6);
        put('U', 6);
        put('V', 6);
        put('W', 6);
        put('X', 6);
        put('Y', 6);
        put('Z', 6);
        put('[', 4);
        put('\\', 6);
        put(']', 4);
        put('^', 6);
        put('_', 6);
        put('`', 0);
        put('a', 6);
        put('b', 6);
        put('c', 6);
        put('d', 6);
        put('e', 6);
        put('f', 5);
        put('g', 6);
        put('h', 6);
        put('i', 2);
        put('j', 6);
        put('k', 5);
        put('l', 3);
        put('m', 6);
        put('n', 6);
        put('o', 6);
        put('p', 6);
        put('q', 6);
        put('r', 6);
        put('s', 6);
        put('t', 4);
        put('u', 6);
        put('v', 6);
        put('w', 6);
        put('x', 6);
        put('y', 6);
        put('z', 6);
        put('{', 5);
        put('|', 2);
        put('}', 5);
        put('~', 7);
    }};

    public static Integer computeStringLength(String string) {
        return string.chars().map(i -> TextUtils.getCharacterLength((char) i)).sum();
    }

    public static Integer getCharacterLength(char character) {
        return CHARACTER_SIZE_MAP.getOrDefault(character, 0);
    }
}
