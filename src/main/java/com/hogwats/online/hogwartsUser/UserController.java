package com.hogwats.online.hogwartsUser;

import com.hogwats.online.hogwartsUser.converter.UserDtoToUserConverter;
import com.hogwats.online.hogwartsUser.converter.UserToUserDtoConverter;
import com.hogwats.online.hogwartsUser.dto.UserDto;
import com.hogwats.online.system.Result;
import com.hogwats.online.system.StatusCode;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("${api.endpoint.base-url}/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final UserDtoToUserConverter userDtoToUserConverter;
    private final UserToUserDtoConverter userToUserDtoConverter;

    //findAllUsers
    @GetMapping
    public Result findAllUsers(){
        List<HogwartsUser> usersFound = this.userService.findAllUsers();
        List<UserDto> userDtoList = usersFound.stream()
                .map(this.userToUserDtoConverter::convert).toList();
        return Result.builder()
                .flag(true)
                .code(StatusCode.SUCCESS)
                .message("Users found")
                .data(userDtoList)
                .build();
    }
    //findUserById
    @GetMapping("/{userId}")
    public Result findUserById(@PathVariable Long userId){
        HogwartsUser foundUser = this.userService.findUserById(userId);
        UserDto foundUserDto = this.userToUserDtoConverter.convert(foundUser);
        return Result.builder()
                .flag(true)
                .code(StatusCode.SUCCESS)
                .message("User found")
                .data(foundUserDto)
                .build();
    }
    //addUser
    @PostMapping
    public Result addUser(@Valid @RequestBody HogwartsUser newUser){
        HogwartsUser savedUser = this.userService.addUser(newUser);
        UserDto createdUserDto = this.userToUserDtoConverter.convert(savedUser);
        return Result.builder()
                .flag(true)
                .code(StatusCode.SUCCESS)
                .message("User created")
                .data(createdUserDto)
                .build();
    }

    //updateUser
    @PutMapping("/{userId}")
    public Result updateUser(@PathVariable Long userId, @Valid @RequestBody UserDto userDto){
        HogwartsUser updateUser = this.userDtoToUserConverter.convert(userDto);
//        assert updateUser != null;
        HogwartsUser savedUpdateUser = this.userService.updateUser(userId,updateUser);
        UserDto updateUserDto = this.userToUserDtoConverter.convert(savedUpdateUser);
        return Result.builder()
                .flag(true)
                .code(StatusCode.SUCCESS)
                .message("User updated")
                .data(updateUserDto)
                .build();
    }
    //deleteUser
    @DeleteMapping("/{userId}")
    public Result deleteUser(@PathVariable Long userId){
        this.userService.deleteUser(userId);
        return Result.builder()
                .flag(true)
                .code(StatusCode.SUCCESS)
                .message("User deleted")
                .build();
    }
    //changePassword
    @PatchMapping("/{userId}/password")
    public Result changePassword(@PathVariable Long userId){
        return null;
    }
}
