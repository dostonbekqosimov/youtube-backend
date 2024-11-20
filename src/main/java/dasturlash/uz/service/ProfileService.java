package dasturlash.uz.service;

import dasturlash.uz.dto.request.ChangePasswordRequest;
import dasturlash.uz.entity.Profile;
import dasturlash.uz.repository.ProfileRepository;
import dasturlash.uz.security.SpringSecurityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class ProfileService {
    @Autowired
    ProfileRepository repository;
    @Autowired
    PasswordEncoder passwordEncoder;

    public String changePassword(ChangePasswordRequest request) {
        Long currentUserId = SpringSecurityUtil.getCurrentUserId();
        Profile entity = findById(currentUserId);

        if (!passwordEncoder.matches(request.getOldPassword(), entity.getPassword())) {
            return "Old password incorrect";
        }

        if (passwordEncoder.matches(request.getNewPassword(), entity.getPassword())) {
            return "New password cannot be the same as the old password";
        }

        if (!isValidPassword(request.getNewPassword())) {
            return "New password incorrect";
        }
        entity.setPassword(passwordEncoder.encode(request.getNewPassword()));
        repository.save(entity);
        return "Password changed successfully";
    }

    public Profile findById(Long id) {
        return repository.findById(id).orElse(null);
    }

    public boolean isValidPassword(String password) {
        return password.length() >= 6 && password.length() <= 16 && password.matches(".*\\d.*");
    }
}
