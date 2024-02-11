package com.primapp.utils;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LinkExtractor {
    // Function to extract all the URLs from the string
    public static String extractURL(String str) {
        // Creating an empty ArrayList to store URLs
        ArrayList<String> urlList = new ArrayList<>();

        // Regular Expression to extract URL from the string
        String regexStr = "\\b((?:https?|ftp|file):"
                + "\\/\\/[a-zA-Z0-9+&@#\\/%?=~_|!:,.;]*"
                + "[a-zA-Z0-9+&@#\\/%=~_|])";

        // Compile the Regular Expression pattern
        Pattern pattern = Pattern.compile(regexStr, Pattern.CASE_INSENSITIVE);

        // Create a Matcher that matches the pattern with the input string
        Matcher matcher = pattern.matcher(str);

        // Find and add all matching URLs to the ArrayList
        while (matcher.find()) {
            // Add the matched URL to the ArrayList
            urlList.add(matcher.group());
        }

        String url = "";
        // If no URL is found, print -1
        if (urlList.isEmpty()) {
            System.out.println("-1");
        } else {
            // Print all the URLs stored in the ArrayList
            for (String url1 : urlList) {
               url = url1;
            }
        }

        return url;
    }

    public static void main(String[] args) {
        // Given String str
        String str = "Welcome to https://www.geeksforgeeks.org "
                + "Computer Science Portal";

        // Function Call
        extractURL(str);
    }
}
