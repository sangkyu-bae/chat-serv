package org.example.domain.chat.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter @Setter
public class RequestCreateRoom {

    @NotNull
    @NotBlank
    private String formUserId;

    private String toUserId;

}
