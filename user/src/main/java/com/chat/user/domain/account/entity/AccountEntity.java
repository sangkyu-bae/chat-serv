package com.chat.user.domain.account.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


@Entity
@Getter
@Setter
@EqualsAndHashCode(of="userId")
@AllArgsConstructor @NoArgsConstructor
@Builder
public class AccountEntity {

    @Id
    private String userId;

    private String password;

    private String name;

    private String email;

    private LocalDateTime createAt;

    private LocalDateTime updateAt;

    @OneToMany(fetch = FetchType.EAGER)
    private Set<RoleEntity> roleList;

    public void addRole(RoleEntity roleEntity){
        if(this.roleList == null){
            roleList = new HashSet<>();
        }

        roleList.add(roleEntity);
    }

    public List<SimpleGrantedAuthority> getAuthList(){
        return roleList.stream()
                .map(role->new SimpleGrantedAuthority(role.getRoleName()))
                .collect(Collectors.toList());
    }

    public List<String> getRoleListToString(){
        return roleList.stream()
                .map(role->role.getRoleName())
                .collect(Collectors.toList());
    }
}
