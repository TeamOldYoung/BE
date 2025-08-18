package com.app.oldYoung.global.security.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;

@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class GoogleDTO {

    private String access_token;
    private int expires_in;
    private String scope;
    private String token_type;
    private String id_token;
}