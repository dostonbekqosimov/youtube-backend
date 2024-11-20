package dasturlash.uz.security;

import dasturlash.uz.entity.Profile;
import dasturlash.uz.enums.ProfileRole;
import dasturlash.uz.enums.ProfileStatus;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Getter
public class CustomUserDetails implements UserDetails {

    private final String id;
    private final String name;
    private final String surname;
    private final String email;
    private final String password;
    private final ProfileStatus status;
    private final ProfileRole role;

    public CustomUserDetails(Profile profile) {
       this.id = profile.getId();
       this.name = profile.getName();
       this.surname = profile.getSurname();
       this.email = profile.getEmail();
       this.password = profile.getPassword();
       this.status = profile.getStatus();
       this.role = profile.getRole();
    }




    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(role.name()));

        System.out.println("Role from enum: " + role.name());
        return authorities;
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
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return status == ProfileStatus.ACTIVE;
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
