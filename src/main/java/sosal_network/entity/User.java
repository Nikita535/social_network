package sosal_network.entity;


import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import sosal_network.Enum.Role;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.util.*;

import static org.aspectj.weaver.tools.cache.SimpleCacheFactory.enabled;

@Entity(name = "user")
@Table(name = "users")
@Data
@Getter
@Setter
@NoArgsConstructor
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private int id;
    private String username;
    private String userSurname;
    private String password;
    @Transient
    private String userPasswordConfirm;
    private String userEmail;


    @ElementCollection(targetClass = Role.class, fetch = FetchType.EAGER)
    @CollectionTable(name = "user_role", joinColumns = @JoinColumn(name = "user_id"))
    @Enumerated(EnumType.STRING)
    private Set<Role> roles = new HashSet<>();

    @OneToMany(mappedBy = "userID", fetch = FetchType.EAGER, orphanRemoval = true)
    private Set<Friend> friendsList = new HashSet<Friend>();


    public User(String userName, String userSurname, String password, String passwordConfirm, String email) {
        this.username = userName;
        this.userSurname = userSurname;
        this.password = password;
        this.userPasswordConfirm = passwordConfirm;
        this.userEmail = email;
    }


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles;
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return this.username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }


}