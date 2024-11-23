package dasturlash.uz.controller;


import dasturlash.uz.dto.AttachDTO;
import dasturlash.uz.dto.ProfileDTO;
import dasturlash.uz.dto.request.UpdateProfileDetailDTO;
import dasturlash.uz.dto.request.ChangePasswordRequest;
import dasturlash.uz.entity.Profile;
import dasturlash.uz.service.AttachService;
import dasturlash.uz.service.ProfileService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/profile")
public class ProfileController {
    @Autowired
    ProfileService service;
    @Autowired
    AttachService attachService;

    //API for change password with old password
    @PostMapping("/changePassword")
    public ResponseEntity<String> changePassword(@RequestBody ChangePasswordRequest request) {
        return ResponseEntity.ok(service.changePassword(request));
    }

    @PostMapping("/create")
    public ResponseEntity<ProfileDTO> create(@RequestBody @Valid ProfileDTO profileDTO) {
        return ResponseEntity.ok(service.create(profileDTO));
    }


    @GetMapping("/getAll")
    public ResponseEntity<List<Profile>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    @PutMapping("/updateEmail")
    public ResponseEntity<String> updateEmail(@RequestParam @Valid String email){
        return ResponseEntity.ok(service.updateEmail(email));
    }

    @PutMapping("/updateDetail")
    public ResponseEntity<String> updateDetail(@RequestBody @Valid UpdateProfileDetailDTO dto){
        return ResponseEntity.ok(service.updateDetail(dto));
    }

    @GetMapping("/confirm/{code}")
    public ResponseEntity<String> confirm(@PathVariable String code){
        return ResponseEntity.ok(service.confirm(code));
    }

    @PutMapping("/updateAttach/{photoId}")
    public ResponseEntity<String> updatePhoto(@PathVariable String photoId){
        return ResponseEntity.ok(service.updateProfilePhoto(photoId));
    }
}
