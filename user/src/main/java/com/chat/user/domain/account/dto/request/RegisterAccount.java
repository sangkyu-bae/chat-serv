package com.chat.user.domain.account.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class RegisterAccount {

    @NotNull
    @NotBlank
    @Size(min = 4,max = 20)
    private String userId;

    @NotNull
    @NotBlank
    @Email
    private String email;

    @NotNull
    @NotBlank
    @Size(min = 4,max = 20)
    private String password;

    @NotNull
    @NotBlank
    @Size(min = 4,max = 20)
    private String checkPassword;

    @NotNull
    @NotBlank
    @Size(min = 4,max = 20)
    private String name;
}
