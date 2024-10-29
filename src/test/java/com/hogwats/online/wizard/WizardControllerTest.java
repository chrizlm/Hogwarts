package com.hogwats.online.wizard;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hogwats.online.system.StatusCode;
import com.hogwats.online.system.exception.ObjectNotFoundException;
import com.hogwats.online.wizard.dto.WizardDto;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@AutoConfigureMockMvc
class WizardControllerTest {

    @MockBean
    WizardService wizardService;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    MockMvc mockMvc;

    @Value("${api.endpoint.base-url}")
    String baseUrl;

    List<Wizard> wizards = new ArrayList<>();

    @BeforeEach
    void setUp() {
        Wizard w1 = Wizard.builder()
                .id(123L)
                .name("Wizard Test1")
                .build();

        Wizard w2 = Wizard.builder()
                .id(1234L)
                .name("Wizard Test2")
                .build();

        this.wizards.add(w1);
        this.wizards.add(w2);
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void createWizard() throws Exception {
        //given
        WizardDto wDtoTest = new WizardDto(
                null,
                "Wizard Test1",
                null
        );

        String jsonContent = this.objectMapper.writeValueAsString(wDtoTest);

        Wizard wTest = Wizard.builder()
                .id(123L)
                .name("Wizard Test1")
                .build();

        given(this.wizardService.createWizard(Mockito.any(Wizard.class))).willReturn(wTest);

        //when and then
        this.mockMvc.perform(post(baseUrl+"/wizard")
                        .contentType(MediaType.APPLICATION_JSON).content(jsonContent)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
                .andExpect(jsonPath("$.message").value("Wizard created"))
                .andExpect(jsonPath("$.data.id").value(wTest.getId()))
                .andExpect(jsonPath("$.data.name").value(wTest.getName()));
    }

    @Test
    void getWizardById() throws Exception {
        //given
        Wizard wT1 = Wizard.builder()
                .id(1234L)
                .name("test")
                .build();

        given(this.wizardService.getWizardById(Mockito.anyLong())).willReturn(wT1);

        //when and then
        this.mockMvc.perform(get(baseUrl+"/wizard/1234")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
                .andExpect(jsonPath("$.message").value("Wizard found"))
                .andExpect(jsonPath("$.data.id").value(wT1.getId()))
                .andExpect(jsonPath("$.data.name").value(wT1.getName()));
    }

    @Test
    void getWizardByIdNotFound() throws Exception {
        //given
        given(this.wizardService.getWizardById(1234L))
                .willThrow(new ObjectNotFoundException("Wizard",1234L));

        //when and then
        this.mockMvc.perform(get(baseUrl+"/wizard/1234")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(false))
                .andExpect(jsonPath("$.code").value(StatusCode.NOT_FOUND))
                .andExpect(jsonPath("$.message").value("Wizard: "+1234L+" not found"))
                .andExpect(jsonPath("$.data").isEmpty());

    }

    @Test
    void getAllWizards() throws Exception {
        //given
        given(this.wizardService.getAllWizards()).willReturn(this.wizards);

        //when and then
        this.mockMvc.perform(get(baseUrl+"/wizard").accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
                .andExpect(jsonPath("$.message").value("Wizard List retrieved"))
                .andExpect(jsonPath("$.data[0].id").value(123L))
                .andExpect(jsonPath("$.data[1].name").value("Wizard Test2"));
    }

    @Test
    void deleteWizardById() throws Exception {
        //given
        doNothing().when(this.wizardService).deleteWizardById(1234L);

        //when and then
        this.mockMvc.perform(delete(baseUrl+"/wizard/1234").accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
                .andExpect(jsonPath("$.message").value("Wizard Deleted"))
                .andExpect(jsonPath("$.data").isEmpty());

    }

    @Test
    void deleteWizardByIdNotFound() throws Exception {
        //given
        doThrow(new ObjectNotFoundException("Wizard",1234L))
                .when(this.wizardService).deleteWizardById(1234L);

        //when and then
        this.mockMvc.perform(delete(baseUrl+"/wizard/1234").accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(false))
                .andExpect(jsonPath("$.code").value(StatusCode.NOT_FOUND))
                .andExpect(jsonPath("$.message").value("Wizard: 1234 not found"))
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @Test
    void assignArtifactSuccess() throws Exception {
        //given
        doNothing().when(this.wizardService).assignArtifact(12L,"123");

        //when and then
        this.mockMvc.perform(put(baseUrl+"/wizard/12/artifacts/123").accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
                .andExpect(jsonPath("$.message").value("Artifact assignment success"))
                .andExpect(jsonPath("$.data").isEmpty());

    }

    @Test
    void assignArtifactErrorWithNonExistentWizardId() throws Exception {
        //given
        doThrow(new ObjectNotFoundException("Wizard", 123L))
                .when(this.wizardService).assignArtifact(123L,"123");

        //when and then
        this.mockMvc.perform(put(baseUrl+"/wizard/123/artifacts/123").accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(false))
                .andExpect(jsonPath("$.code").value(StatusCode.NOT_FOUND))
                .andExpect(jsonPath("$.message").value("Wizard: " + 123L + " not found"))
                .andExpect(jsonPath("$.data").isEmpty());

    }

    @Test
    void assignArtifactErrorWithNonExistentArtifactId() throws Exception {
        //given
        doThrow(new ObjectNotFoundException("Artifact", "457"))
                .when(this.wizardService).assignArtifact(123L,"457");

        //when and then
        this.mockMvc.perform(put(baseUrl+"/wizard/123/artifacts/457").accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(false))
                .andExpect(jsonPath("$.code").value(StatusCode.NOT_FOUND))
                .andExpect(jsonPath("$.message").value("Artifact: 457 not found"))
                .andExpect(jsonPath("$.data").isEmpty());

    }
}