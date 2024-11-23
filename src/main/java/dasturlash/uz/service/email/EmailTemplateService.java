package dasturlash.uz.service.email;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class EmailTemplateService {

    @Value("${spring.application.name}")
    private String appName;

    @Value("${app.frontend.base-url}")
    private String frontendBaseUrl;

    // Common CSS style for all email templates
    private String getEmailStyles() {
        return """
        <style>
            .container {
                max-width: 600px;
                margin: 0 auto;
                font-family: Arial, sans-serif;
            }
            .header {
                background-color: #2196F3;
                padding: 20px;
                text-align: center;
            }
            .header h1 {
                color: white;
                margin: 0;
            }
            .content {
                padding: 20px;
                background-color: #f9f9f9;
            }
            .button-container {
                text-align: center;
                margin: 30px 0;
            }
            .footer {
                text-align: center;
                padding: 20px;
                color: #666;
                font-size: 14px;
            }
            .welcome-message {
                font-size: 18px;
                color: #333;
                line-height: 1.5;
            }
            .deadline-warning {
                color: #e65100;
                font-weight: bold;
                margin: 20px 0;
                padding: 10px;
                border: 1px solid #e65100;
                border-radius: 4px;
                text-align: center;
            }
            .attempts-info {
                color: #2196F3;
                margin: 10px 0;
                font-size: 14px;
                text-align: center;
            }
            .verification-code-container {
                background-color: #f5f5f5;
                padding: 20px;
                border-radius: 8px;
                border: 2px dashed #2196F3;
                display: inline-block;
                margin: 20px 0;
            }
            .verification-code {
                font-size: 32px;
                font-family: monospace;
                letter-spacing: 4px;
                font-weight: bold;
                color: #1976D2;
                user-select: all;
            }
            .security-notice {
                color: #666;
                font-size: 14px;
                margin-top: 20px;
                padding: 10px;
                background-color: #fff3e0;
                border-radius: 4px;
            }
        </style>
    """;
    }

    private String getEmailHeader(String title) {
        return """
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                %s
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <h1>%s</h1>
                    </div>
            """.formatted(getEmailStyles(), title);
    }

    private String getEmailFooter() {
        return """
                    <div class="footer">
                        <p>This is an automated message. Please do not reply to this email.</p>
                        <p>If you need assistance, please contact our support team.</p>
                        <p>&copy; 2024 %s. All rights reserved.</p>
                    </div>
                </div>
            </body>
            </html>
        """.formatted(appName);
    }

    public String getRegistrationEmailTemplate(Long userId, String userName, int deadlineHours, int remainingAttempts, Integer verificationCode) {
        String header = getEmailHeader("Welcome to " + appName + "!");
        String content = """
                <div class="content">
                    <p class="welcome-message">Hello %s,</p>
                    <p>Thank you for registering with us. We're excited to have you on board!</p>
                    <p>Please use the verification code below to complete your registration:</p>
                    
                    <div class="button-container">
                        <div class="verification-code-container">
                            <span class="verification-code">%06d</span>
                        </div>
                    </div>
                    
                    <div class="deadline-warning">
                        ⚠️ Important: This verification code will expire in %d minutes
                    </div>
                    
                    <div class="attempts-info">
                        You have %d remaining verification attempts
                    </div>
                    
                    <div class="security-notice">
                        <p>🔒 For your security:</p>
                        <ul>
                            <li>Enter this code on the verification page</li>
                            <li>Never share this code with anyone</li>
                            <li>Our team will never ask for this code</li>
                        </ul>
                    </div>
                </div>
            """.formatted(
                userName,
                verificationCode,
                deadlineHours,
                remainingAttempts
        );

        return header + content + getEmailFooter();
    }
    public String getResendConfirmationEmailTemplate(Long userId, String userName, int deadlineHours, int remainingAttempts, Integer verificationCode) {
        String header = getEmailHeader(appName + " - Registration Confirmation");
        String content = """
                <div class="content">
                    <p class="welcome-message">Hello %s,</p>
                    <p>A new verification code has been generated for your registration.</p>
                    <p>Please use the code below to complete your registration:</p>
                    
                    <div class="button-container">
                        <div class="verification-code-container">
                            <span class="verification-code">%06d</span>
                        </div>
                    </div>
                    
                    <div class="deadline-warning">
                        ⚠️ Important: This verification code will expire in %d minutes
                    </div>
                    
                    <div class="attempts-info">
                        ⚠️ You have %d remaining verification attempts
                    </div>
                    
                    <div class="security-notice">
                        <p>🔒 For your security:</p>
                        <ul>
                            <li>Enter this code on the verification page</li>
                            <li>Never share this code with anyone</li>
                            <li>Our team will never ask for this code</li>
                        </ul>
                    </div>
                </div>
            """.formatted(
                userName,
                verificationCode,
                deadlineHours,
                remainingAttempts
        );

        return header + content + getEmailFooter();
    }
}