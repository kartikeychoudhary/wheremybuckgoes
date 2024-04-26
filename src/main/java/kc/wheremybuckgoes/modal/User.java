package kc.wheremybuckgoes.modal;


import jakarta.persistence.*;
import kc.wheremybuckgoes.constants.ApplicationConstant.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serial;
import java.io.Serializable;
import java.util.Collection;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "UserTable")
public class User implements UserDetails, Serializable {
    @Serial
    private static final long serialVersionUID = 1905122041950251208L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;
    private String firstname;
    private String lastname;
    private String email;
    private String password;
    private Long creationTimestamp;
    private String profilePicURL;

    @Builder.Default
    private boolean isUserExpired = false;
    @Builder.Default
    private boolean isUserLocked = false;
    @Builder.Default
    private boolean isCredentialsExpired = false;

    @Enumerated(EnumType.STRING)
    private Role role;
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role.name()));
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return !isUserExpired;
    }

    @Override
    public boolean isAccountNonLocked() {
        return !isUserLocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return !isCredentialsExpired;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public String getDataLinkedWithEmailJSON() {
        String fname = null == firstname || firstname.isBlank() ? "" : firstname;
        String lname = null == lastname || lastname.isBlank() ? "" : lastname;
        return "{\n\t\"email\":" + "\"" + email + "\",\n\t\"name\":" + "\"" + fname + " " + lname + "\",\n\t\"profilePic\":"+"\""+profilePicURL+"\"\n}";
    }

    public User maskCredentials(){
        this.password = "MASKED";
        return this;
    }
}
