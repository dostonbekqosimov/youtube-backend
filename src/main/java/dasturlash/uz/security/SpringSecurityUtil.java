package dasturlash.uz.security;

import dasturlash.uz.enums.ProfileRole;
import dasturlash.uz.exceptions.AppBadRequestException;
import dasturlash.uz.util.UserInfoUtil;
import jakarta.servlet.http.HttpServletRequest;
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

    private static String currentIp(HttpServletRequest request) {
        String ip = request.getHeader("x-forwarded-for");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }

    private static String getCurrentBrowserInfo(HttpServletRequest request) {
        String userAgent = request.getHeader("user-agent");
        if (userAgent != null) {
            return "User's Browser Info: " + userAgent;
        } else {
            return "User's Browser Info not available";
        }
    }

    public static UserInfoUtil currentUserInfo(HttpServletRequest request) {
        UserInfoUtil userInfoUtil = new UserInfoUtil();
        userInfoUtil.setIpAddress(currentIp(request));
        userInfoUtil.setUserAgent(getCurrentBrowserInfo(request));
        userInfoUtil.setUserId(checkCurrentUser());
        return userInfoUtil;
    }

    public static Long checkCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            Object principal = authentication.getPrincipal();
            if (principal instanceof CustomUserDetails) {
                return ((CustomUserDetails) principal).getId();
            }
        }
        return null;
    }
}
