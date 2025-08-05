package com.chat.user.domain.account.dto.request;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter @Builder
public class LoginRequest {

    private String id;

    private String password;
}
