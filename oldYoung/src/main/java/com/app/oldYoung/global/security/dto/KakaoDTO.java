package com.app.oldYoung.global.security.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;

public class KakaoDTO {

    @Getter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class OAuthToken {

        private String access_token;
        private String token_type;
        private String refresh_token;
        private int expires_in;
        private String scope;
        private int refresh_token_expires_in;
        private String id_token;
    }

    /**
     * OIDC를 사용하면 ID Token에서 직접 프로필 정보를 얻으므로,
     * 기존의 KakaoProfile 클래스는 더 이상 사용되지 않습니다.
     */
}
