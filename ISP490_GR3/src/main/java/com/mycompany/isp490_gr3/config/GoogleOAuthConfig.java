package com.mycompany.isp490_gr3.config;

import com.google.api.client.auth.oauth2.AuthorizationCodeFlow;
import com.google.api.client.auth.oauth2.BearerToken;
import com.google.api.client.auth.oauth2.ClientParametersAuthentication;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.List;

/**
 * Configuration class for Google OAuth2 authentication
 */
public class GoogleOAuthConfig {
    
    // Google OAuth2 Configuration
    private static final String CLIENT_ID = getConfigValue("GOOGLE_CLIENT_ID", "YOUR_GOOGLE_CLIENT_ID");
    private static final String CLIENT_SECRET = getConfigValue("GOOGLE_CLIENT_SECRET", "YOUR_GOOGLE_CLIENT_SECRET");
    private static final String REDIRECT_URI = "http://localhost:8080/ISP490_GR3/auth/google/callback";
    
    // Google OAuth2 Scopes
    private static final List<String> SCOPES = Arrays.asList(
        "https://www.googleapis.com/auth/userinfo.email",
        "https://www.googleapis.com/auth/userinfo.profile"
    );
    
    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    private static HttpTransport httpTransport;
    private static GoogleAuthorizationCodeFlow flow;
    
    static {
        try {
            httpTransport = GoogleNetHttpTransport.newTrustedTransport();
            flow = new GoogleAuthorizationCodeFlow.Builder(
                httpTransport, JSON_FACTORY, CLIENT_ID, CLIENT_SECRET, SCOPES)
                .setAccessType("offline")
                .setApprovalPrompt("force")
                .build();
        } catch (GeneralSecurityException | IOException e) {
            System.err.println("Error initializing Google OAuth: " + e.getMessage());
        }
    }
    
    public static String getClientId() {
        return CLIENT_ID;
    }
    
    public static String getClientSecret() {
        return CLIENT_SECRET;
    }
    
    public static String getRedirectUri() {
        return REDIRECT_URI;
    }
    
    public static List<String> getScopes() {
        return SCOPES;
    }
    
    public static HttpTransport getHttpTransport() {
        return httpTransport;
    }
    
    public static JsonFactory getJsonFactory() {
        return JSON_FACTORY;
    }
    
    public static GoogleAuthorizationCodeFlow getFlow() {
        return flow;
    }
    
    /**
     * Generate Google OAuth authorization URL
     */
    public static String getAuthorizationUrl() {
        if (flow == null) {
            return null;
        }
        
        return flow.newAuthorizationUrl()
                .setRedirectUri(REDIRECT_URI)
                .setState("state_token_" + System.currentTimeMillis())
                .build();
    }
    
    /**
     * Check if Google OAuth is properly configured
     */
    public static boolean isConfigured() {
        return !CLIENT_ID.equals("YOUR_GOOGLE_CLIENT_ID") && 
               !CLIENT_SECRET.equals("YOUR_GOOGLE_CLIENT_SECRET") &&
               flow != null;
    }
    
    /**
     * Get configuration value from environment variable, system property, or default value
     */
    private static String getConfigValue(String key, String defaultValue) {
        // Try environment variable first
        String value = System.getenv(key);
        if (value != null && !value.trim().isEmpty()) {
            return value;
        }
        
        // Try system property
        value = System.getProperty(key);
        if (value != null && !value.trim().isEmpty()) {
            return value;
        }
        
        // Return default value
        return defaultValue;
    }
} 