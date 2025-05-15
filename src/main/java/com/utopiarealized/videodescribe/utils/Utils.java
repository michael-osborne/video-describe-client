package com.utopiarealized.videodescribe.utils;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class Utils {

    final static String DOWNLOAD_LINE_REGEX = "\\[download\\]\\s+(\\d+\\.\\d+)%\\s+of\\s+~?\\s+(\\d+\\.\\d+)([KMGTP]?iB)\\s+at\\s+(\\d+\\.\\d+)([KMGTP]?iB/s)\\s+ETA\\s+(\\d{2}:\\d{2})\\s+\\(frag\\s+(\\d+)/(\\d+)\\)";
    private static final Pattern DOWNLOAD_LINE_PATTERN = Pattern.compile(DOWNLOAD_LINE_REGEX);
   
    public synchronized static Path createDirectoryFromUrl(String urlString, String rootDir, String videoId) throws IOException {
        try {
            // Parse the URL
            URL url = new URL(urlString);
            String hostname = url.getHost();

            if (hostname == null || hostname.isEmpty()) {
                throw new IllegalArgumentException("Invalid URL or no hostname found");
            }

            // Split hostname into parts (e.g., "www.example.com" -> ["www", "example", "com"])
            List<String> domainParts = new ArrayList<>(Arrays.asList(hostname.split("\\.")));
            domainParts.add(videoId);

            // Start with the root directory
            Path currentPath = Paths.get(rootDir);

            // Ensure root directory exists
            if (!Files.exists(currentPath)) {
                Files.createDirectories(currentPath);
            }

            // Create nested directories for each part of the hostname
            for (String part : domainParts) {
                if (!part.isEmpty()) { // Skip empty parts from malformed splits
                    currentPath = currentPath.resolve(part);
                    if (!Files.exists(currentPath)) {
                        Files.createDirectory(currentPath);
                    } 
                }
            }

            return currentPath;

        } catch (java.net.MalformedURLException e) {
            throw new IllegalArgumentException("Invalid URL format: " + urlString, e);
        }
    }
    
    public static void createDirectoryIfNotExists(String directory) {
        Path path = Paths.get(directory);
        if (!Files.exists(path)) {
            try {
                Files.createDirectories(path);
            } catch (IOException e) {
                throw new RuntimeException("Failed to create directory: " + directory, e);
            }
        }
    }
    
    public static String findLastDownloadLine(List<String> lines) {
        for (int i = lines.size() - 1; i >= 0; i--) {
            String line = lines.get(i);
            Matcher matcher = DOWNLOAD_LINE_PATTERN.matcher(line);
            if (matcher.matches()) {
                return line;
            }
        }
        return null;
    }
}