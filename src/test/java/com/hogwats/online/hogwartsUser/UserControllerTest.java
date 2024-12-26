package com.hogwats.online.hogwartsUser;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hogwats.online.hogwartsUser.dto.UserDto;
import com.hogwats.online.system.StatusCode;
import com.hogwats.online.system.exception.ObjectNotFoundException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles(value = "dev")
class UserControllerTest {
    @MockBean
    UserService userService;

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Value("${api.endpoint.base-url}")
    String baseUrl;

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
    void findAllUsers() throws Exception {
        //given
        given(this.userService.findAllUsers()).willReturn(this.users);

        //when and then
        this.mockMvc.perform(get(baseUrl+"/users").accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
                .andExpect(jsonPath("$.message").value("Users found"))
                .andExpect(jsonPath("$.data[0].id").value(this.users.getFirst().getId()));
    }

    @Test
    void findUserById() throws Exception {
        //given
        given(this.userService.findUserById(Mockito.anyLong())).willReturn(this.users.get(1));

        //when and then
        this.mockMvc.perform(get(baseUrl+"/users/"+this.users.get(1).getId()).accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
                .andExpect(jsonPath("$.message").value("User found"))
                .andExpect(jsonPath("$.data.roles").value(this.users.get(1).getRoles()));
    }

    @Test
    void findUserByIdNotFound() throws Exception {
        //given
        given(this.userService.findUserById(Mockito.anyLong()))
                .willThrow(new ObjectNotFoundException("User",this.users.get(1).getId()));

        //when and then
        this.mockMvc.perform(get(baseUrl+"/users/"+this.users.get(1).getId()).accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(false))
                .andExpect(jsonPath("$.code").value(StatusCode.NOT_FOUND))
                .andExpect(jsonPath("$.message").value("User: "+this.users.get(1).getId()+" not found"))
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @Test
    void addUser() throws Exception {
        //given
        HogwartsUser testUsr = HogwartsUser.builder()
                .id(1235L)
                .username("TEST")
                .enabled(true)
                .roles("admin")
                .password("test123")
                .build();

        String jsonString = this.objectMapper.writeValueAsString(testUsr);

        given(this.userService.addUser(Mockito.any(HogwartsUser.class))).willReturn(testUsr);

        //when and then
        this.mockMvc.perform(post(baseUrl+"/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonString).accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
                .andExpect(jsonPath("$.message").value("User created"))
                .andExpect(jsonPath("$.data.id").isNotEmpty())
                .andExpect(jsonPath("$.data.username").value(testUsr.getUsername()));
    }

    @Test
    void updateUser() throws Exception {
        //given
        UserDto testDto1 = new UserDto(
                null,
                "h2",
                false,
                "user"
        );

        HogwartsUser userTest1 = HogwartsUser.builder()
                .id(1234L)
                .username("h2")
                .enabled(false)
                .roles("user")
                .password("1234").build();

        String jsonContent = this.objectMapper.writeValueAsString(testDto1);

        given(this.userService.updateUser(eq(1234L),Mockito.any(HogwartsUser.class)))
                .willReturn(userTest1);

        //when and then
        this.mockMvc.perform(put(baseUrl+"/users/1234")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonContent).accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
                .andExpect(jsonPath("$.message").value("User updated"))
                .andExpect(jsonPath("$.data.id").isNotEmpty())
                .andExpect(jsonPath("$.data.username").value(userTest1.getUsername()));
    }


    @Test
    void updateUserNotFound() throws Exception {
        //given
        UserDto testDto2 = new UserDto(
                12345L,
                "h2",
                false,
                "user"
        );

        HogwartsUser userTest2 = HogwartsUser.builder()
                .id(12345L)
                .username("h2")
                .enabled(false)
                .roles("user")
                .password("1234").build();

        String jsonCont = this.objectMapper.writeValueAsString(testDto2);

        given(this.userService.updateUser(eq(12345L),Mockito.any(HogwartsUser.class)))
                .willThrow(new ObjectNotFoundException("User",12345L));

        //when and then
        this.mockMvc.perform(put(baseUrl+"/users/12345")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonCont).accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(false))
                .andExpect(jsonPath("$.code").value(StatusCode.NOT_FOUND))
                .andExpect(jsonPath("$.message").value("User: "+12345L+" not found"))
                .andExpect(jsonPath("$.data").isEmpty());
    }


    @Test
    void deleteUser() throws Exception {
        //given
        doNothing().when(this.userService).deleteUser(this.users.getFirst().getId());

        //when and then
        this.mockMvc.perform(delete(baseUrl+"/users/"+this.users.get(1).getId()).accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
                .andExpect(jsonPath("$.message").value("User deleted"))
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @Test
    void deleteUserNotFound() throws Exception {
        //given
        doThrow(new ObjectNotFoundException("User",123L))
                .when(this.userService).deleteUser(123L);

        //when and then
        this.mockMvc.perform(delete(baseUrl+"/users/123").accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(false))
                .andExpect(jsonPath("$.code").value(StatusCode.NOT_FOUND))
                .andExpect(jsonPath("$.message").value("User: "+123L+" not found"))
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @Test
    void changePassword() {
    }
}