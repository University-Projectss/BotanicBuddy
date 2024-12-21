package com.mops.bb_backend.service;

import com.mops.bb_backend.utils.ApplicationProperties;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class LocationService {
    private final ApplicationProperties applicationProperties;
    private static final String GEOLOCATION_API = "https://ipinfo.io/{ip}?token={token}";

    public Map<String, String> getLocation(HttpServletRequest request) {
        String ipAddress = getClientIp(request);

        RestTemplate restTemplate = new RestTemplate();

        if (ipAddress.equals("0:0:0:0:0:0:0:1"))
            ipAddress = "";

        String url = GEOLOCATION_API
                .replace("{ip}", ipAddress)
                .replace("{token}", applicationProperties.getGeolocationApiKey());
        HashMap<String, String> location = restTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<HashMap<String, String>>() {}
        ).getBody();

        return location != null ? location : new HashMap<>();
    }

    private String getClientIp(HttpServletRequest request) {
        String remoteAddr = "";

        if (request != null) {
            remoteAddr = request.getHeader("X-FORWARDED-FOR");
            if (remoteAddr == null || remoteAddr.isEmpty()) {
                remoteAddr = request.getRemoteAddr();
            }
        }

        return remoteAddr;
    }
}
