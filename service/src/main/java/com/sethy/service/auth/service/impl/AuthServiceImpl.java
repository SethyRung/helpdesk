package com.sethy.service.auth.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sethy.service.auth.dto.Tokens;
import com.sethy.service.auth.dto.UserInfo;
import com.sethy.service.auth.service.AuthService;
import com.sethy.service.common.exception.UnauthorizedException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    @Value("${spring.keycloak.base-url}")
    private String keycloakBaseUrl;
    @Value("${spring.keycloak.internal-base-url}")
    private String keycloakInternalBaseUrl;
    @Value("${spring.keycloak.realm}")
    private String realm;
    @Value("${spring.keycloak.client-id}")
    private String clientId;
    @Value("${spring.keycloak.redirect-url}")
    private String redirectUrl;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final JwtDecoder jwtDecoder;

    public String buildLoginUrl() {
        return keycloakBaseUrl + "/realms/" + realm +
                "/protocol/openid-connect/auth" +
                "?client_id=" + clientId +
                "&response_type=code" +
                "&scope=openid profile email" +
                "&redirect_uri=" + redirectUrl;
    }

    public Tokens exchangeCodeForTokens(String code) throws Exception {
        String tokenUrl = keycloakInternalBaseUrl + "/realms/" + realm + "/protocol/openid-connect/token";

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "authorization_code");
        body.add("client_id", clientId);
        body.add("code", code);
        body.add("redirect_uri", redirectUrl);

        String response = restTemplate.postForObject(tokenUrl, body, String.class);

        JsonNode node = objectMapper.readTree(response);
        return new Tokens(
                node.get("access_token").asText(),
                node.get("refresh_token").asText(),
                node.get("expires_in").asInt(),
                node.get("refresh_expires_in").asInt()
        );
    }

    public Tokens refreshToken(HttpServletRequest request) throws Exception {
        Cookie[] cookies = request.getCookies();
        String refreshToken = null;
        if (cookies != null) {
            for (Cookie c : cookies) {
                if ("refresh_token".equals(c.getName())) {
                    refreshToken = c.getValue();
                }
            }
        }

        if (refreshToken == null) throw new RuntimeException("No refresh token");

        String tokenUrl = keycloakInternalBaseUrl + "/realms/" + realm + "/protocol/openid-connect/token";

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "refresh_token");
        body.add("client_id", clientId);
        body.add("refresh_token", refreshToken);

        String response = restTemplate.postForObject(tokenUrl, body, String.class);

        JsonNode node = objectMapper.readTree(response);
        return new Tokens(
                node.get("access_token").asText(),
                node.get("refresh_token").asText(),
                node.get("expires_in").asInt(),
                node.get("refresh_expires_in").asInt()
        );
    }

    public void logout(HttpServletRequest request) throws Exception {
        Cookie[] cookies = request.getCookies();
        String refreshToken = null;
        if (cookies != null) {
            for (Cookie c : cookies) {
                if ("refresh_token".equals(c.getName())) {
                    refreshToken = c.getValue();
                }
            }
        }

        if (refreshToken == null || refreshToken.isBlank()) {
            throw new RuntimeException("No refresh token");
        }

        String logoutUrl = keycloakInternalBaseUrl + "/realms/" + realm + "/protocol/openid-connect/logout";
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("client_id", clientId);
        body.add("refresh_token", refreshToken);

        restTemplate.postForObject(logoutUrl, body, String.class);
    }

    private JsonNode decodeJwt(String jwt) throws Exception {
        String[] parts = jwt.split("\\.");
        if (parts.length != 3) throw new RuntimeException("Invalid JWT");

        String payload = new String(Base64.getUrlDecoder().decode(parts[1]));
        return objectMapper.readTree(payload);
    }

    public UserInfo getCurrentUser(HttpServletRequest request) throws Exception {
        String token = extractAccessToken(request);
        if (token == null || token.isBlank()) throw new RuntimeException("No access token");

        JsonNode payload = decodeJwt(token);
        List<String> roles = new ArrayList<>();

        JsonNode resourceAccess = payload.path("resource_access");
        if (resourceAccess.isObject()) {
            JsonNode clientAccess = resourceAccess.path(clientId);
            if (clientAccess.isObject()) {
                JsonNode rolesNode = clientAccess.path("roles");
                if (rolesNode.isArray()) {
                    for (JsonNode role : rolesNode) {
                        if (role.isTextual()) {
                            roles.add(role.asText());
                        }
                    }
                }
            }
        }

        return new UserInfo(
                payload.get("preferred_username").asText(),
                payload.get("email").asText(),
                payload.get("given_name").asText(),
                payload.get("family_name").asText(),
                roles
        );
    }

    public boolean checkAuth(HttpServletRequest request) {
        String token = extractAccessToken(request);
        if (token == null || token.isBlank()) throw new UnauthorizedException("No access token provided");

        Jwt jwt = jwtDecoder.decode(token);
        if (jwt == null) {
            return false;
        }

        java.time.Instant expiresAt = jwt.getExpiresAt();
        if (expiresAt == null) {
            return false;
        }

        if (expiresAt.isBefore(java.time.Instant.now())) {
            throw new UnauthorizedException("Token has expired");
        }

        return true;
    }

    private String extractAccessToken(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("access_token".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }
}
