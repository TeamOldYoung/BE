package com.app.oldYoung.global.security.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

public class KakaoDTO {

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record OAuthToken(
        String access_token,
        String token_type,
        String refresh_token,
        int expires_in,
        String scope,
        int refresh_token_expires_in
    ) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record KakaoProfile(
        Long id,
        String connected_at,
        Properties properties,
        KakaoAccount kakao_account
    ) {
        
        @JsonIgnoreProperties(ignoreUnknown = true)
        public record Properties(
            String nickname
        ) {}

        @JsonIgnoreProperties(ignoreUnknown = true)
        public record KakaoAccount(
            String email,
            Boolean is_email_verified,
            Boolean has_email,
            Boolean profile_nickname_needs_agreement,
            Boolean email_needs_agreement,
            Boolean is_email_valid,
            Profile profile
        ) {
            
            @JsonIgnoreProperties(ignoreUnknown = true)
            public record Profile(
                String nickname,
                Boolean is_default_nickname
            ) {}
        }
    }
}
