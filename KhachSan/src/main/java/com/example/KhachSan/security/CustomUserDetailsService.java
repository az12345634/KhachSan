package com.example.KhachSan.security;

import  com.example.KhachSan.entity.RoleEntity;
import  com.example.KhachSan.entity.UserEntity;
import  com.example.KhachSan.repository.RoleRepository;
import  com.example.KhachSan.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    public RoleRepository roleRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Lấy ra thông tin user theo username
        UserEntity userEntity = userRepository.findByUsername(username);
        // Lấy ra các quyền của user theo username
        List<RoleEntity> roleEntities = roleRepository.getRoleByUsername(username);

        if (userEntity != null){
            UserDetails userDetails = new org.springframework.security.core.userdetails.User(
                    userEntity.getUsername(),
                    userEntity.getPassword(),
                    convertStrToAuthor(roleEntities)
            );
            return userDetails;
        }else {
            throw new UsernameNotFoundException("Invalid user with username!");
        }
    }

    private Collection<? extends GrantedAuthority> convertStrToAuthor(Collection<RoleEntity> roles){
//        return roles.stream().map(role -> new SimpleGrantedAuthority(role.getName())).collect(Collectors.toList());
//        return List.of(new SimpleGrantedAuthority("ROLE_ADMIN"));
        List<RoleEntity> roleEntities = roles.stream().toList();
        List<SimpleGrantedAuthority> roleConfigSecurity = new ArrayList<>();
        for (int i = 0; i < roles.size(); i++) {
            RoleEntity roleEntity = roleEntities.get(i);
            SimpleGrantedAuthority simpleGrantedAuthority = new SimpleGrantedAuthority(roleEntity.getName());
            roleConfigSecurity.add(simpleGrantedAuthority);
        }
        return roleConfigSecurity;
    }
}
