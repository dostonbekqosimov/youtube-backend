package dasturlash.uz.security;

import dasturlash.uz.enums.ProfileRole;
import dasturlash.uz.exceptions.AppBadRequestException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class SpringSecurityUtil {

    public static Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            Object principal = authentication.getPrincipal();
            if (principal instanceof CustomUserDetails) {
                return ((CustomUserDetails) principal).getId();
            } else if (principal instanceof String && "anonymousUser".equals(((String) principal).trim())) {
                throw new RuntimeException("Anonymous users can't be authenticated");
            }
        }
        throw new AppBadRequestException("http://localhost:8090/api/auth/login");
    }

    public static CustomUserDetails getCurrentEntity() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetail = (CustomUserDetails) authentication.getPrincipal();

        return userDetail;
    }

    public static ProfileRole getCurrentUserRole() {
        return getCurrentEntity().getRole();
    }

}
