package com.rocketeers.nexus_gold.service;

import com.rocketeers.nexus_gold.dto.authenrication.*;
import com.rocketeers.nexus_gold.dto.email_verfication.EmailVerificationCodeRequest;
import com.rocketeers.nexus_gold.dto.email_verfication.EmailVerificationResponse;
import com.rocketeers.nexus_gold.dto.sign_in.SignInRequest;
import com.rocketeers.nexus_gold.dto.sign_up.SignUpAuthenticationResponse;
import com.rocketeers.nexus_gold.dto.sign_up.SignUpRequest;
import com.rocketeers.nexus_gold.dto.verify_rest_code.VerifyResetCodeRequest;
import com.rocketeers.nexus_gold.dto.verify_rest_code.VerifyResetCodeResponse;

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
