package dasturlash.uz.service;

import dasturlash.uz.dto.MessageDTO;
import dasturlash.uz.dto.ProfileDTO;
import dasturlash.uz.dto.request.UpdateProfileDetailDTO;
import dasturlash.uz.dto.request.ChangePasswordRequest;
import dasturlash.uz.entity.Profile;
import dasturlash.uz.enums.ProfileStatus;
import dasturlash.uz.exceptions.AppBadRequestException;
import dasturlash.uz.repository.ProfileRepository;
import dasturlash.uz.security.SpringSecurityUtil;
import dasturlash.uz.service.email.EmailSendingService;
import dasturlash.uz.util.RandomUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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
    private final EmailSendingService emailSendingService;


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

    public String updateEmail(@Valid String email) {
        Long currentUserId = SpringSecurityUtil.getCurrentUserId();
        Profile currentProfile = findById(currentUserId);
        int code = RandomUtil.getRandomInt();


        if (email.equals(currentProfile.getEmail())) {
            return "Email already exists";
        }
        MessageDTO messageDTO = new MessageDTO();
        messageDTO.setToAccount(email);
        messageDTO.setSubject("");
        messageDTO.setText("Your code is: "+ code);

        emailSendingService.sendMimeMessage(messageDTO, currentProfile, code);
        return "Sent code your email";
    }

    public String updateDetail(@Valid UpdateProfileDetailDTO dto) {
        Long currentUserId = SpringSecurityUtil.getCurrentUserId();
        Profile currentProfile = findById(currentUserId);

        if (currentProfile != null) {
            currentProfile.setName(dto.getName());
            currentProfile.setSurname(dto.getSurname());
            repository.save(currentProfile);
            return "Profile updated successfully";
        }
        return "Profile not found";
    }

    public String confirm(String code) {
        return null;
    }
}
