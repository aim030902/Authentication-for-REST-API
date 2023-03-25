package uz.aim.zerikdim5.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.aim.zerikdim5.domains.entities.auth.User;
import uz.aim.zerikdim5.dtos.auth.LoginDTO;
import uz.aim.zerikdim5.dtos.auth.RegisterDTO;
import uz.aim.zerikdim5.dtos.jwt.JwtResponseDto;
import uz.aim.zerikdim5.dtos.jwt.RefreshTokenDTO;
import uz.aim.zerikdim5.dtos.response.ApiResponse;
import uz.aim.zerikdim5.services.auth.AuthService;
import uz.aim.zerikdim5.services.recaptcha.ReCaptchaService;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    AuthService authService;
    @Autowired
    ReCaptchaService reCaptchaService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterDTO dto, @RequestParam(name="g-recaptcha-response") String captcha) {
        if (reCaptchaService.validateCaptcha(captcha)) {
            ApiResponse<User> apiResponse = authService.register(dto);
            return ResponseEntity.status(201).body(apiResponse);
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Please Verify Captcha");
    }

    @PostMapping(value = "/login", produces = "application/json")
    public ResponseEntity<JwtResponseDto> login(@RequestBody LoginDTO dto) {
        return ResponseEntity.ok(authService.login(dto));
    }

    @PostMapping("/refreshToken")
    public ResponseEntity<JwtResponseDto> refreshToken(@RequestBody RefreshTokenDTO dto) {
        return ResponseEntity.ok(authService.refreshToken(dto));
    }

    @GetMapping("/activate")
    public ResponseEntity<?> activate(@RequestParam(name = "activation_code") String activationCode) {
        return ResponseEntity.status(200).body(authService.activateUser(activationCode));
    }
}
