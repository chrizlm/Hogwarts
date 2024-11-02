package com.hogwats.online.hogwartsUser;

import com.hogwats.online.system.exception.ObjectNotFoundException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

//import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @Mock
    UserRepository userRepository;

//    @Mock
//    PasswordEncoder passwordEncoder;

//    @Mock
//    RedisCacheClient redisCacheClient;

    @InjectMocks
    UserService userService;

    List<HogwartsUser> users = new ArrayList<>();

    @BeforeEach
    void setUp() {
        HogwartsUser h1 = HogwartsUser.builder()
                .id(123L)
                .username("h1")
                .enabled(true)
                .roles("admin")
                .password("123").build();

        HogwartsUser h2 = HogwartsUser.builder()
                .id(1234L)
                .username("h2")
                .enabled(false)
                .roles("user")
                .password("1234").build();

        users.add(h1);
        users.add(h2);
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void findAllUsers() {
        //given
        given(this.userRepository.findAll()).willReturn(this.users);

        //when
        List<HogwartsUser> testUsers = this.userService.findAllUsers();

        //then
        assertThat(testUsers.size()).isEqualTo(this.users.size());
        assertThat(testUsers.get(1).getId()).isEqualTo(this.users.get(1).getId());
        verify(this.userRepository,times(1)).findAll();
    }

    @Test
    void findUserById() {
        //given
        given(this.userRepository.findById(1234L)).willReturn(Optional.of(this.users.get(1)));

        //when
        HogwartsUser foundUser = this.userService.findUserById(1234L);

        //then
        assertThat(foundUser.getId()).isEqualTo(this.users.get(1).getId());
        assertThat(foundUser.getUsername()).isEqualTo(this.users.get(1).getUsername());
        verify(this.userRepository, times(1)).findById(1234L);
    }

    @Test
    void findUserByIdNotFound() {
        //given
        given(this.userRepository.findById(Mockito.anyLong())).willReturn(Optional.empty());

        //when
        Throwable thrown = assertThrows(ObjectNotFoundException.class,
                ()->{
                    HogwartsUser foundUser = this.userService.findUserById(1234L);
                });

        //then
        assertThat(thrown).isInstanceOf(ObjectNotFoundException.class).hasMessage("User: " + 1234L + " not found");
        verify(this.userRepository, times(1)).findById(1234L);
    }

    @Test
    void addUser() {
        //given
        given(this.userRepository.save(this.users.getFirst())).willReturn(this.users.getFirst());

        //when
        HogwartsUser savedUser = this.userService.addUser(this.users.getFirst());

        //then
        assertThat(savedUser.getId()).isEqualTo(this.users.getFirst().getId());
        verify(this.userRepository, times(1)).save(this.users.getFirst());
    }

    @Test
    void updateUser() {
        //given
        HogwartsUser updtUser = HogwartsUser.builder()
                .id(123L)
                .username("update")
                .enabled(true)
                .roles("normal")
                .password("1234").build();

        given(this.userRepository.findById(this.users.getFirst().getId()))
                .willReturn(Optional.of(this.users.getFirst()));
        given(this.userRepository.save(this.users.getFirst()))
                .willReturn(this.users.getFirst());

        //when
        HogwartsUser updatedUser = this.userService
                .updateUser(this.users.getFirst().getId(),updtUser);

        //then
        assertThat(updatedUser.getId()).isEqualTo(this.users.getFirst().getId());
        verify(this.userRepository,times(1)).save(this.users.getFirst());
    }

    @Test
    void updateUserNotFound() {
        //given
        given(this.userRepository.findById(Mockito.anyLong()))
                .willReturn(Optional.empty());

        //when
        Throwable thrown = assertThrows(ObjectNotFoundException.class,
                ()-> this.userService
                        .updateUser(this.users.getFirst().getId(),Mockito.any())
        );

        //then
        assertThat(thrown).isInstanceOf(ObjectNotFoundException.class)
                .hasMessage("User: " + 123L + " not found");
    }

    @Test
    void deleteUser() {
        //given
        given(this.userRepository.findById(this.users.getFirst().getId()))
                .willReturn(Optional.of(this.users.getFirst()));

        doNothing().when(this.userRepository)
                .deleteById(this.users.getFirst().getId());

        //when
        this.userService.deleteUser(this.users.getFirst().getId());

        //then
        verify(this.userRepository,times(1))
                .deleteById(this.users.getFirst().getId());
    }

    @Test
    void deleteUserNotFound() {
        //given
        given(this.userRepository.findById(Mockito.anyLong()))
                .willReturn(Optional.empty());

        //when
        Throwable thrown = assertThrows(ObjectNotFoundException.class,
                ()->{
                    this.userService.deleteUser(this.users.getFirst().getId());
                });

        //then
        assertThat(thrown).isInstanceOf(ObjectNotFoundException.class)
                        .hasMessage("User: " + 123L + " not found");
        verify(this.userRepository,times(1))
                .findById(this.users.getFirst().getId());
    }

    /*
    @Test
    void changePassword() {
        //given
        HogwartsUser testUser = HogwartsUser.builder()
                .id(123L)
                .password("oldPasswordEncrypted").build();

        given(this.userRepository.findById(testUser.getId()))
                .willReturn(Optional.of(testUser));
        given(this.passwordEncoder.matches(anyString(), anyString())).willReturn(true);
        given(this.passwordEncoder.encode(anyString())).willReturn("newPasswordEncrypted");
        given(this.userRepository.save(testUser)).willReturn(testUser);
        doNothing().when(this.redisCacheClient).delete(anyString());

        //when
        this.userService.changePassword(123L,"oldPasswordUnencrypted","Abc12345","Abc12345");

        //then
        assertThat(testUser.getPassword()).isEqualTo("newPasswordEncrypted");
        verify(this.userRepository, times(1)).save(testUser);
    }
    */
}