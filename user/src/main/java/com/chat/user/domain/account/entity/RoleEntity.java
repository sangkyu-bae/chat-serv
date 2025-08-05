package com.chat.user.domain.account.entity;


import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.*;

@Entity
@Getter
@Setter
@EqualsAndHashCode(of="id")
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RoleEntity {

    @Id @GeneratedValue
    private Long id;

    private String roleName;


}
