package com.rocketeers.nexus_gold.service.impl;

import com.rocketeers.nexus_gold.service.EmailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class GmailEmailService implements EmailService {

    private final JavaMailSender mailSender;
    private final String fromEmail;

    public GmailEmailService(JavaMailSender mailSender, @Value("${spring.mail.username}") String fromEmail) {
        this.mailSender = mailSender;
        this.fromEmail = fromEmail;
    }

    @Override
    public void sendVerificationCode(String to, String code) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(fromEmail);
            helper.setTo(to);
            helper.setSubject("Nexus Gold email verification code");
            helper.setText(buildPlainTextEmail(code), buildHtmlEmail(code));

            mailSender.send(message);
        } catch (MessagingException ex) {
            throw new IllegalStateException("Failed to create verification email", ex);
        }
    }

    private static String buildPlainTextEmail(String code) {
        return """
                Your Nexus Gold verification code is:

                %s

                This code expires soon. If you did not create this account, ignore this email.
                """.formatted(code);
    }

    private static String buildHtmlEmail(String code) {
        String safeCode = escapeHtml(code);

        return """
            <!doctype html>
            <html lang="en">
            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <style>
                    body { margin:0; padding:0; background-color:#0b1326; font-family:'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; }
                    .container { max-width: 600px; margin: 40px auto; background-color: #131b2e; border: 1px solid #31394d; border-radius: 16px; overflow: hidden; box-shadow: 0 10px 25px rgba(0,0,0,0.5); }
                    .header { background: linear-gradient(135deg, #7c3aed 0%, #4c1d95 100%); padding: 32px; text-align: center; }
                    .header h1 { margin: 0; color: #ffffff; font-size: 24px; font-weight: 800; letter-spacing: 1px; text-transform: uppercase; }
                    .content { padding: 40px; text-align: center; color: #94a3b8; }
                    .code-label { font-size: 12px; text-transform: uppercase; letter-spacing: 2px; color: #7c3aed; margin-bottom: 16px; font-weight: 700; }
                    .code-box { display: inline-block; padding: 20px 32px; background-color: #0b1326; border: 2px solid #31394d; border-radius: 12px; color: #ffffff; font-family: 'Courier New', monospace; font-size: 42px; font-weight: 800; letter-spacing: 8px; margin: 16px 0; }
                    .footer { padding: 24px; text-align: center; font-size: 12px; color: #64748b; background-color: #0b1326; border-top: 1px solid #31394d; }
                    .btn { display: inline-block; padding: 12px 24px; background-color: #7c3aed; color: #ffffff; text-decoration: none; border-radius: 8px; font-weight: 600; margin-top: 24px; }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <h1>NexusGold</h1>
                    </div>
                    <div class="content">
                        <div class="code-label">Secure Verification Code</div>
                        <p style="color: #ffffff; font-size: 18px; margin-bottom: 24px;">Confirm your email address</p>
                        <div class="code-box">{{verificationCode}}</div>
                        <p style="margin-top: 24px;">Enter this code in the app to finish verifying your account. This code expires in 10 minutes.</p>
                        <p style="font-size: 13px; margin-top: 32px;">If you did not request this, you can safely ignore this email.</p>
                    </div>
                    <div class="footer">
                        &copy; 2024 NexusGold. Secure Gaming Solutions.
                    </div>
                </div>
            </body>
            </html>
            """.replace("{{verificationCode}}", safeCode);
    }

    private static String escapeHtml(String value) {
        return value
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }
}
