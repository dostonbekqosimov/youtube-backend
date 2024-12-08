package dasturlash.uz.service;

import dasturlash.uz.dto.MessageDTO;
import dasturlash.uz.dto.ProfileDTO;
import dasturlash.uz.dto.ProfileShortInfo;
import dasturlash.uz.dto.request.UpdateProfileDetailDTO;
import dasturlash.uz.dto.request.ChangePasswordRequest;
import dasturlash.uz.dto.response.ResponseCustom;
import dasturlash.uz.dto.response.comment.CommentOwnerInfo;
import dasturlash.uz.entity.Profile;
import dasturlash.uz.enums.ProfileStatus;
import dasturlash.uz.exceptions.AppBadRequestException;
import dasturlash.uz.mapper.CommentOwnerInfoProjection;
import dasturlash.uz.mapper.ProfileShortInfoMapper;
import dasturlash.uz.repository.ProfileRepository;
import dasturlash.uz.security.SpringSecurityUtil;
import dasturlash.uz.service.email.EmailHistoryService;
import dasturlash.uz.service.email.EmailSendingService;
import dasturlash.uz.util.JwtUtil;
import dasturlash.uz.util.RandomUtil;
import dasturlash.uz.util.CustomProjectionMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
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
    private final CustomProjectionMapper projectionMapper;
    private final EmailSendingService emailSendingService;
    private final EmailHistoryService emailHistoryService;
    private final AttachService attachService;

    @Value("${attach.url}")
    private String attachUrl;


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
        messageDTO.setText("Your code is: " + code);

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

    public ResponseCustom confirm(String code) {
        ResponseCustom responseCustom = emailHistoryService.getHistoryByCode(code);
        Long currentUserId = SpringSecurityUtil.getCurrentUserId();
        Profile currentProfile = findById(currentUserId);

        if (responseCustom.getSuccess() && currentProfile != null) {
            currentProfile.setEmail(responseCustom.getMessage());
            repository.save(currentProfile);
            String token = JwtUtil.encode(currentProfile.getEmail(), currentProfile.getRole().toString());
            return new ResponseCustom("New token-> " + token, true);
        }
        return new ResponseCustom("Not completed", false);
    }

    public String updateProfilePhoto(String photoId) {
        Long currentUserId = SpringSecurityUtil.getCurrentUserId();
        Profile currentProfile = findById(currentUserId);

        if (currentProfile != null) {
            if (currentProfile.getPhotoId() != null) {
                attachService.delete(currentProfile.getPhotoId());
            }
            currentProfile.setPhotoId(photoId);
            repository.save(currentProfile);
            return "Profile updated successfully";
        }
        throw new AppBadRequestException("Profile not found(updateProfilePhoto in profileService)");
    }

    public ProfileShortInfo getProfileShortInfo() {
        Long currentUserId = SpringSecurityUtil.getCurrentUserId();
        Profile currentProfile = findById(currentUserId);


        ProfileShortInfoMapper shortInfoMapper = repository.getProfileShortInfoMapper(currentProfile.getEmail());
        return toShortInfo(shortInfoMapper);
    }

    public CommentOwnerInfo getShortInfo(Long profileId) {

        if (profileId == null) {
            throw new AppBadRequestException("Profile id is null");
        }

        CommentOwnerInfoProjection profile = repository.findProfileById(profileId);

        return projectionMapper.toCommentOwnerInfo(profile);


    }

    public ProfileShortInfo toShortInfo(ProfileShortInfoMapper mapper) {
        ProfileShortInfo shortInfo = new ProfileShortInfo();
        shortInfo.setId(mapper.getId());
        shortInfo.setName(mapper.getName());
        shortInfo.setSurname(mapper.getSurname());
        shortInfo.setEmail(mapper.getEmail());
        shortInfo.setPhotoUrl(attachService.openURL(mapper.getPhotoId()));
        return shortInfo;
    }
}
