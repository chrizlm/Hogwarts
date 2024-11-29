package com.hogwats.online.security;

import com.hogwats.online.hogwartsUser.HogwartsUser;
import com.hogwats.online.hogwartsUser.MyUserPrincipal;
import com.hogwats.online.hogwartsUser.converter.UserToUserDtoConverter;
import com.hogwats.online.hogwartsUser.dto.UserDto;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final JwtProvider jwtProvider;
    private final UserToUserDtoConverter userToUserDtoConverter;

    public Map<String, Object> createLoginInfo(Authentication authentication) {
        //Create user info
        MyUserPrincipal principal = (MyUserPrincipal)authentication.getPrincipal();
        HogwartsUser hogwartsUser = principal.getHogwartsUser();
        UserDto userDto = this.userToUserDtoConverter.convert(hogwartsUser);
        //Create a jwt
        String token = this.jwtProvider.createToken(authentication);

        Map<String, Object> loginResultMap = new HashMap<>();
        loginResultMap.put("userInfo", userDto);
        loginResultMap.put("token", token);

        return loginResultMap;
    }
}
