package com.sethy.service.common.util;

import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

/**
 * Utility class for extracting user information from JWT tokens.
 */
public class JwtUtil {

    /**
     * Extract the JWT from the Authentication object.
     */
    public static Jwt extractJwt(Authentication authentication) {
        if (authentication instanceof JwtAuthenticationToken jwtToken) {
            // Need to get the token as string and decode it since getToken() returns the decoded Jwt
            return jwtToken.getToken();
        }
        return null;
    }

    /**
     * Extract the username (preferred_username) from the JWT.
     * Falls back to the authentication name if preferred_username is not available.
     */
    public static String extractUsername(Authentication authentication) {
        Jwt jwt = extractJwt(authentication);
        if (jwt != null) {
            String preferredUsername = jwt.getClaimAsString("preferred_username");
            if (preferredUsername != null && !preferredUsername.isBlank()) {
                return preferredUsername;
            }
            return jwt.getSubject();
        }
        return authentication.getName();
    }

    /**
     * Extract the email from the JWT.
     */
    public static String extractEmail(Authentication authentication) {
        Jwt jwt = extractJwt(authentication);
        if (jwt != null) {
            return jwt.getClaimAsString("email");
        }
        return null;
    }

    /**
     * Extract the first name (given_name) from the JWT.
     */
    public static String extractFirstName(Authentication authentication) {
        Jwt jwt = extractJwt(authentication);
        if (jwt != null) {
            return jwt.getClaimAsString("given_name");
        }
        return null;
    }

    /**
     * Extract the last name (family_name) from the JWT.
     */
    public static String extractLastName(Authentication authentication) {
        Jwt jwt = extractJwt(authentication);
        if (jwt != null) {
            return jwt.getClaimAsString("family_name");
        }
        return null;
    }
}
