package com.mycompany.isp490_gr3.service;

import com.google.api.client.auth.oauth2.TokenResponse;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.mycompany.isp490_gr3.config.GoogleOAuthConfig;
import com.mycompany.isp490_gr3.model.GoogleUserInfo;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Service class for handling Google OAuth operations
 */
public class GoogleOAuthService {
    
    private static final String GOOGLE_USER_INFO_URL = "https://www.googleapis.com/oauth2/v1/userinfo";
    private final Gson gson = new Gson();
    
    /**
     * Exchange authorization code for access token and get user info
     */
    public GoogleUserInfo getUserInfoFromCode(String authorizationCode) throws IOException {
        // Get the authorization flow
        GoogleAuthorizationCodeFlow flow = GoogleOAuthConfig.getFlow();
        if (flow == null) {
            throw new IOException("Google OAuth not properly configured");
        }
        
        // Exchange authorization code for access token
        TokenResponse tokenResponse = flow.newTokenRequest(authorizationCode)
                .setRedirectUri(GoogleOAuthConfig.getRedirectUri())
                .execute();
        
        String accessToken = tokenResponse.getAccessToken();
        
        // Use access token to get user info
        return getUserInfo(accessToken);
    }
    
    /**
     * Get user information using access token
     */
    private GoogleUserInfo getUserInfo(String accessToken) throws IOException {
        String urlString = GOOGLE_USER_INFO_URL + "?access_token=" + accessToken;
        URL url = new URL(urlString);
        
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Authorization", "Bearer " + accessToken);
        
        int responseCode = connection.getResponseCode();
        if (responseCode != HttpURLConnection.HTTP_OK) {
            throw new IOException("Failed to get user info. Response code: " + responseCode);
        }
        
        // Read response
        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        StringBuilder response = new StringBuilder();
        String line;
        
        while ((line = reader.readLine()) != null) {
            response.append(line);
        }
        reader.close();
        
        // Parse JSON response
        JsonObject jsonObject = gson.fromJson(response.toString(), JsonObject.class);
        
        GoogleUserInfo userInfo = new GoogleUserInfo();
        userInfo.setId(getJsonString(jsonObject, "id"));
        userInfo.setEmail(getJsonString(jsonObject, "email"));
        userInfo.setName(getJsonString(jsonObject, "name"));
        userInfo.setGivenName(getJsonString(jsonObject, "given_name"));
        userInfo.setFamilyName(getJsonString(jsonObject, "family_name"));
        userInfo.setPicture(getJsonString(jsonObject, "picture"));
        userInfo.setLocale(getJsonString(jsonObject, "locale"));
        userInfo.setVerifiedEmail(jsonObject.has("verified_email") && 
                                  jsonObject.get("verified_email").getAsBoolean());
        
        return userInfo;
    }
    
    /**
     * Helper method to safely get string value from JSON object
     */
    private String getJsonString(JsonObject jsonObject, String key) {
        if (jsonObject.has(key) && !jsonObject.get(key).isJsonNull()) {
            return jsonObject.get(key).getAsString();
        }
        return null;
    }
    
    /**
     * Generate Google OAuth authorization URL
     */
    public String getAuthorizationUrl() {
        return GoogleOAuthConfig.getAuthorizationUrl();
    }
    
    /**
     * Check if Google OAuth is configured
     */
    public boolean isConfigured() {
        return GoogleOAuthConfig.isConfigured();
    }
} 