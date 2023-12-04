package com.oddsandends.api.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@Builder
@ToString
public class Login {

    @NotBlank(message = "email error")
    private String email;

    @NotBlank(message = "password error")
    private String password;

}
