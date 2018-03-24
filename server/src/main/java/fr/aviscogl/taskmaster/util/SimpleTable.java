package fr.aviscogl.taskmaster.util;

import java.util.HashMap;

public class SimpleTable extends HashMap<String, String> {

    private int longestWord = 0;

    public String put(String key, String value) {
        String res =  super.put(key, value);
        if (longestWord < key.length())
            longestWord = key.length();
        return res;
    }

    public SimpleTable put(String key, String value, boolean color) {
        if (!color)
            this.put(key, value);
        else
            this.put(key, Color.colorify(value));
        return this;
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder();
        this.forEach((k, v) -> s.append(String.format("%" + longestWord + "s: %s\n", k, v)));
        return s.toString();
    }
}
