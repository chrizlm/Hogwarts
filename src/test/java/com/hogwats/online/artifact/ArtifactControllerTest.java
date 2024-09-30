package com.hogwats.online.artifact;

import com.hogwats.online.system.StatusCode;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@AutoConfigureMockMvc
class ArtifactControllerTest {

    @MockBean
    ArtifactService artifactService;

    @Autowired
    MockMvc mockMvc;

    List<Artifact> artifacts;

    @BeforeEach
    void setUp() {
        this.artifacts = new ArrayList<>();

        Artifact a1 = Artifact.builder()
                .id("1234")
                .name("wand")
                .description("magic stick")
                .imageUrl("image1")
                .build();

        Artifact a2 = Artifact.builder()
                .id("12345")
                .name("card")
                .description("magic card")
                .imageUrl("image2")
                .build();

        this.artifacts.add(a1);
        this.artifacts.add(a2);
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void findArtifactByIdSuccess() throws Exception {
        //given
        given(this.artifactService.findById("1234")).willReturn(this.artifacts.getFirst());

        //when and then
        this.mockMvc.perform(get("/api/v1/artifacts/1234").accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
                .andExpect(jsonPath("$.message").value("Find one success"))
                .andExpect(jsonPath("$.data.id").value("1234"))
                .andExpect(jsonPath("$.data.name").value("wand"));
    }

    @Test
    void findArtifactByIdNotFound() throws Exception {
        //given
        given(this.artifactService.findById("1234")).willThrow(new ArtifactNotFoundException("1234"));

        //when and then
        this.mockMvc.perform(get("/api/v1/artifacts/1234").accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(false))
                .andExpect(jsonPath("$.code").value(StatusCode.NOT_FOUND))
                .andExpect(jsonPath("$.message").value("Artifact: 1234 not found"))
                .andExpect(jsonPath("$.data").isEmpty());

    }

    @Test
    void findAllArtifactsSuccess() throws Exception {
        //given
        given(this.artifactService.findAll()).willReturn(this.artifacts);

        //when and then
        this.mockMvc.perform(get("/api/v1/artifacts").accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
                .andExpect(jsonPath("$.message").value("Find all success"))
                .andExpect(jsonPath("$.data", Matchers.hasSize(this.artifacts.size())))
                .andExpect(jsonPath("$.data[0].id").value("1234"))
                .andExpect(jsonPath("$.data[0].name").value("wand"))
                .andExpect(jsonPath("$.data[1].id").value("12345"))
                .andExpect(jsonPath("$.data[1].name").value("card"));
    }
}