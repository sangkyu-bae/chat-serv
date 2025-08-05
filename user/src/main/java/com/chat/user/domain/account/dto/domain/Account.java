package com.chat.user.domain.account.dto.domain;

import com.chat.user.domain.account.entity.RoleEntity;
import lombok.*;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@AllArgsConstructor @NoArgsConstructor
@Builder
public class Account {

    private String userId;

    private String password;

    private String name;

    private String email;

    private LocalDateTime createAt;

    private LocalDateTime updateAt;

    private List<String> roleList;

    public void setRoleList(List<RoleEntity> roleList){
        if(roleList == null){
            roleList = new ArrayList<>();
            return;
        }

        this.roleList = roleList.stream()
                .map(role->role.getRoleName())
                .collect(Collectors.toList());
    }
    public List<SimpleGrantedAuthority> getAuthList(){
        if(this.roleList == null){
            return new ArrayList<>();
        }

        return roleList.stream()
                .map(role->new SimpleGrantedAuthority(role))
                .collect(Collectors.toList());
    }

}
