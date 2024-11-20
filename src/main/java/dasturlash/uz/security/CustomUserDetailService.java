package dasturlash.uz.security;

import dasturlash.uz.entity.Profile;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

public class CustomUserDetailService implements UserDetailsService {

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<Profile> optional = profileRepository.findByLoginAndVisibleTrue(username);

        if (optional.isEmpty()){
            throw new UnauthorizedException("Login or password is wrong");
        }

        Profile profile = optional.get();


        return new CustomUserDetails(profile);
    }
}
