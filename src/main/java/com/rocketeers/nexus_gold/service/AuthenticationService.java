package com.rocketeers.nexus_gold.service;

import com.rocketeers.nexus_gold.dto.*;

public interface AuthenticationService {
    SignUpAuthenticationResponse signUp(SignUpRequest signUpRequest);
    JwtAuthenticationResponse signIn(SignInRequest signInRequest);
    JwtAuthenticationResponse refreshToken(RefreshTokenRequest refreshTokenRequest);
    EmailVerificationResponse sendEmailVerificationCode(EmailVerificationCodeRequest request);
    EmailVerificationResponse verifyEmail(VerifyEmailRequest request);

    PasswordResetResponse forgetPassword(ForgetPasswordRequest request);

    VerifyResetCodeResponse verifyResetCode(VerifyResetCodeRequest request);

    PasswordResetResponse resetPassword(ResetPasswordRequest request);

}
