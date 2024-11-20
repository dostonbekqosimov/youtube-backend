package dasturlash.uz.service;

import dasturlash.uz.dto.ProfileDTO;
import dasturlash.uz.dto.request.ChangePasswordRequest;
import dasturlash.uz.entity.Profile;
import dasturlash.uz.enums.ProfileStatus;
import dasturlash.uz.exceptions.AppBadRequestException;
import dasturlash.uz.repository.ProfileRepository;
import dasturlash.uz.security.SpringSecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProfileService {

    private final ProfileRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final ResourceBundleService resourceBundleService;


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

    public ProfileDTO create(ProfileDTO profileDTO) {
        Profile byEmail = findByEmail(profileDTO.getEmail());

        if (byEmail != null) {
            throw new AppBadRequestException("Email already exists");
        }
        Profile entity = new Profile();
        entity.setName(profileDTO.getName());
        entity.setSurname(profileDTO.getSurname());
        entity.setEmail(profileDTO.getEmail());
        entity.setCreatedDate(LocalDateTime.now());
        entity.setStatus(ProfileStatus.ACTIVE);
        entity.setRole(profileDTO.getRole());
        entity.setVisible(true);
        entity.setPassword(passwordEncoder.encode(profileDTO.getPassword()));
        repository.save(entity);
        profileDTO.setId(entity.getId());
        return profileDTO;
    }

    public Profile findById(Long id) {
        return repository.findById(id).orElse(null);
    }

    public boolean isValidPassword(String password) {
        return password.length() >= 6 && password.length() <= 16 && password.matches(".*\\d.*");
    }


    public Profile findByEmail(String email) {
        Optional<Profile> byEmailAndVisibleTrue = repository.findByEmailAndVisibleTrue(email);
        return byEmailAndVisibleTrue.orElse(null);
    }


    public List<Profile> getAll() {
        List<Profile> profiles = new ArrayList<>();
        for (Profile profile : repository.findAll()) {
            profiles.add(profile);
        }
        return profiles;
    }
}
