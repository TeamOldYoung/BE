package com.app.oldYoung.global.security.util;

import com.app.oldYoung.global.common.apiResponse.exception.ErrorCode;
import com.app.oldYoung.global.security.dto.KakaoDTO;
import com.app.oldYoung.global.security.exception.AuthHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;

@Component
@Slf4j
public class KakaoUtil {

    private final WebClient webClient;

    @Value("${spring.security.oauth2.client.registration.kakao.client-id}")
    private String client;

    @Value("${spring.security.oauth2.client.registration.kakao.redirect-uri}")
    private String redirect;

    public KakaoUtil(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build();
    }

    public KakaoDTO.OAuthToken requestToken(String accessCode) {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("client_id", client);
        params.add("redirect_uri", redirect);
        params.add("code", accessCode);

        return webClient.post()
            .uri("https://kauth.kakao.com/oauth/token")
            .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
            .bodyValue(params)
            .retrieve()
            .bodyToMono(KakaoDTO.OAuthToken.class)
            .doOnError(error -> {
                log.error("OAuth 토큰 요청 실패: {}", error.getMessage());
                throw new AuthHandler(ErrorCode.OAUTH_TOKEN_REQUEST_FAILED);
            })
            .block();
    }

    /**
     * requestProfile 메소드는 OIDC 흐름에서 더 이상 사용되지 않습니다.
     * ID Token에서 직접 프로필 정보를 추출하기 때문입니다.
     */
}
