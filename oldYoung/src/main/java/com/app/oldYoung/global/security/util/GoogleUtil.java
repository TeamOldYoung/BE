package com.app.oldYoung.global.security.util;

import com.app.oldYoung.global.common.apiResponse.exception.ErrorCode;
import com.app.oldYoung.global.security.dto.GoogleDTO;
import com.app.oldYoung.global.security.exception.AuthHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

@Component
@Slf4j
public class GoogleUtil {

    private final WebClient webClient;
    private final String clientId;
    private final String clientSecret;
    private final String redirectUri;

    public GoogleUtil(
        WebClient.Builder webClientBuilder,
        @Value("${spring.security.oauth2.client.registration.google.client-id}") String clientId,
        @Value("${spring.security.oauth2.client.registration.google.client-secret}") String clientSecret,
        @Value("${spring.security.oauth2.client.registration.google.redirect-uri}") String redirectUri
    ) {
        this.webClient = webClientBuilder.build();
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.redirectUri = redirectUri;
    }

    public GoogleDTO requestToken(String accessCode) {
        try {
            // URL 디코딩 처리 (이중 인코딩 해결)
            String decodedCode = URLDecoder.decode(accessCode, StandardCharsets.UTF_8);

            // 여전히 인코딩된 상태라면 한 번 더 디코딩
            if (decodedCode.contains("%2F")) {
                decodedCode = URLDecoder.decode(decodedCode, StandardCharsets.UTF_8);
            }

            MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
            params.add("grant_type", "authorization_code");
            params.add("client_id", clientId);
            params.add("client_secret", clientSecret);
            params.add("redirect_uri", redirectUri);
            params.add("code", decodedCode);  // 디코딩된 코드 사용

            return webClient.post()
                .uri("https://oauth2.googleapis.com/token")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .bodyValue(params)
                .retrieve()
                .onStatus(
                    status -> status.is4xxClientError() || status.is5xxServerError(),
                    response -> response.bodyToMono(String.class)
                        .doOnNext(body -> log.error("Google OAuth 에러 응답: {}", body))
                        .then(Mono.error(new AuthHandler(ErrorCode.OAUTH_TOKEN_REQUEST_FAILED)))
                )
                .bodyToMono(GoogleDTO.class)
                .doOnError(error -> {
                    log.error("Google OAuth 토큰 요청 실패: {}", error.getMessage(), error);
                })
                .block();

        } catch (Exception e) {
            log.error("Google OAuth 토큰 요청 중 예외 발생", e);
            throw new AuthHandler(ErrorCode.OAUTH_TOKEN_REQUEST_FAILED);
        }
    }
}
