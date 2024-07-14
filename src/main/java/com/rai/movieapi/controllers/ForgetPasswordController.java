package com.rai.movieapi.controllers;

import com.rai.movieapi.auth.entities.ForgetPassword;
import com.rai.movieapi.auth.entities.User;
import com.rai.movieapi.auth.repositories.ForgetPasswordRepository;
import com.rai.movieapi.auth.repositories.UserRepository;
import com.rai.movieapi.dto.MailBody;
import com.rai.movieapi.service.EmailService;
import com.rai.movieapi.utils.ChangePassword;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.Date;
import java.util.Objects;
import java.util.Random;

@RestController
@RequestMapping("/forgetPassword")
public class ForgetPasswordController {

    private final UserRepository userRepository;
    private final ForgetPasswordRepository forgetPasswordRepository;
    private final PasswordEncoder passwordEncoder;

    private final EmailService emailService;

    public ForgetPasswordController(UserRepository userRepository, ForgetPasswordRepository forgetPasswordRepository, PasswordEncoder passwordEncoder, EmailService emailService) {
        this.userRepository = userRepository;
        this.forgetPasswordRepository = forgetPasswordRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
    }

    //send mail for email verification
    @PostMapping("/verifyMail/{email}")
    public ResponseEntity<String> verifyEmail(@PathVariable String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Please provide a valid email"));

        int otp = otpGenerator();
        MailBody mailBody = MailBody.builder()
                .to(email)
                .text("This is the OTP for your Forgot Password request : " + otp)
                .subject("OTP for Forgot Password request")
                .build();

        ForgetPassword fp = ForgetPassword.builder()
                .otp(otp)
                .expirationTime(new Date(System.currentTimeMillis() + 70 * 10000))
                .user(user)
                .build();

        emailService.sendSimpleMessage(mailBody);
        forgetPasswordRepository.save(fp);

        return ResponseEntity.ok("Email sent for verification");
    }

    @PostMapping("/verifyOtp/{otp}/{email}")
    public ResponseEntity<String> verifyOtp(@PathVariable Integer otp, @PathVariable String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Please provide a valid email"));

        ForgetPassword fp = forgetPasswordRepository.findByOtpAndUser(otp, user)
                .orElseThrow(() -> new RuntimeException("Invalid OTP for email: " + email));

        if (fp.getExpirationTime().before(Date.from(Instant.now()))) {
            forgetPasswordRepository.deleteById(fp.getFpid());
            return new ResponseEntity<>("OTP has expired!", HttpStatus.EXPECTATION_FAILED);
        }

        return ResponseEntity.ok("OTP Verified!");

    }

    @PostMapping("/changePassword/{email}")
    public ResponseEntity<String> changePasswordHandler(@RequestBody ChangePassword changePassword,
                                                        @PathVariable String email){
        if(!Objects.equals(changePassword.password(), changePassword.repeatPassword())){
            return new ResponseEntity<>("Please enter the password again!", HttpStatus.EXPECTATION_FAILED);
        }
        String encodedPassword = passwordEncoder.encode(changePassword.password());
        userRepository.updatePassword(email, encodedPassword);

        return ResponseEntity.ok("Password has been changed !!");
    }

    private Integer otpGenerator() {
        Random random = new Random();
        return random.nextInt(100_000, 999_999);
    }
}
