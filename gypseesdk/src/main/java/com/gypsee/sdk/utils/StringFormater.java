package com.gypsee.sdk.utils;

public class StringFormater {
    public static String capitalizeWord(String str) {

        if (str.length() == 0) {
            return str;
        }
        String words[] = str.split("\\s");
        String capitalizeWord = "";
        for (String w : words) {
            String first = w.substring(0, 1);
            String afterfirst = w.substring(1);
            capitalizeWord += first.toUpperCase() + afterfirst + " ";
        }
        return capitalizeWord.trim();
    }
}
