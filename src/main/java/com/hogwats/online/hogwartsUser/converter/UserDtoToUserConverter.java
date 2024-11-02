package com.hogwats.online.hogwartsUser.converter;

import com.hogwats.online.hogwartsUser.HogwartsUser;
import com.hogwats.online.hogwartsUser.dto.UserDto;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class UserDtoToUserConverter implements Converter<UserDto, HogwartsUser> {
    @Override
    public HogwartsUser convert(UserDto source) {
        return HogwartsUser.builder()
                .id(source.id())
                .username(source.username())
                .enabled(source.enabled())
                .roles(source.roles())
                .build();
    }
}
