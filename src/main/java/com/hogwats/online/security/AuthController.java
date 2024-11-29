package com.hogwats.online.security;

import com.hogwats.online.system.Result;
import com.hogwats.online.system.StatusCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("${api.endpoint.base-url}/users")
public class AuthController {
    private final AuthService authService;

    @PostMapping("/login")
    public Result getLoginInfo(Authentication authentication){
        log.info("Authenticated user: {}",authentication.getName());
        return Result.builder()
                .flag(true)
                .code(StatusCode.SUCCESS)
                .message("User Info and JSON Web Token")
                .data(this.authService.createLoginInfo(authentication))
                .build();
    }
}
