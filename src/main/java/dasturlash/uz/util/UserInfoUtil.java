package dasturlash.uz.util;

import lombok.Data;

@Data
public class UserInfoUtil {
    private Long userId;
    private String ipAddress;
    private String userAgent;

    public UserInfoUtil(Long userId, String ipAddress, String userAgent) {
        this.userId = userId;
        this.ipAddress = ipAddress;
        this.userAgent = userAgent;
    }

    public UserInfoUtil() {
    }

    public UserInfoUtil(String userAgent, String ipAddress) {
        this.userAgent = userAgent;
        this.ipAddress = ipAddress;
    }
}
