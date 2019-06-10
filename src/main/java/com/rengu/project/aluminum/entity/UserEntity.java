package com.rengu.project.aluminum.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.rengu.project.aluminum.enums.SecurityClassificationEnum;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import java.io.Serializable;
import java.util.*;

/**
 * com.rengu.project.aluminum.entity
 *
 * @author hanchangming
 * @date 2019-06-10
 */

@Data
@Entity
public class UserEntity implements UserDetails, Serializable {

    @Id
    private String id = UUID.randomUUID().toString();
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime = new Date();
    private String username;
    private String password;
    private boolean accountNonExpired = true;
    private boolean accountNonLocked = true;
    private boolean credentialsNonExpired = true;
    private boolean enabled = true;
    private int securityClassification = SecurityClassificationEnum.PUBLIC.getCode();
    @ManyToMany(fetch = FetchType.EAGER)
    private Set<RoleEntity> roleEntities;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Set<GrantedAuthority> grantedAuthorities = new HashSet<>();
        for (RoleEntity roleEntity : roleEntities) {
            grantedAuthorities.add(new SimpleGrantedAuthority("ROLE_" + roleEntity.getName()));
        }
        return grantedAuthorities;
    }
}

