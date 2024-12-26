package com.hogwats.online.artifact;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hogwats.online.artifact.dto.ArtifactDto;
import com.hogwats.online.system.StatusCode;
import com.hogwats.online.system.exception.ObjectNotFoundException;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;

//import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles(value = "dev")
class ArtifactControllerTest {

    @MockBean
    ArtifactService artifactService;

    @Autowired
    ObjectMapper objectMapper;

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
        given(this.artifactService.findById("1234")).willThrow(new ObjectNotFoundException("Artifact","1234"));

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

    @Test
    void addArtifactSuccess() throws Exception {
        //given
        ArtifactDto artifactDto = new ArtifactDto(
                null,
                "Remembral",
                "Just a remembral",
                "imageUrl remembral",
                null);

        String json = this.objectMapper.writeValueAsString(artifactDto);

        Artifact savedArtifact = Artifact.builder()
                .id("12345678901234567890")
                .name("Remembral")
                .description("Just a remembral")
                .imageUrl("imageUrl remembral")
                .build();

        given(this.artifactService.save(Mockito.any(Artifact.class))).willReturn(savedArtifact);

        //when and then
        this.mockMvc.perform(post("/api/v1/artifacts")
                        .contentType(MediaType.APPLICATION_JSON).content(json)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
                .andExpect(jsonPath("$.message").value("Add success"))
                .andExpect(jsonPath("$.data.id").isNotEmpty())
                .andExpect(jsonPath("$.data.name").value(savedArtifact.getName()))
                .andExpect(jsonPath("$.data.description").value(savedArtifact.getDescription()))
                .andExpect(jsonPath("$.data.imageUrl").value(savedArtifact.getImageUrl()));
    }

    @Test
    void updateArtifactSuccess() throws Exception {
        //given
        ArtifactDto newArtifactDto = new ArtifactDto(
                "123",
                "update",
                "update artifact",
                "update imageUrl",
                null
        );

        String json = this.objectMapper.writeValueAsString(newArtifactDto);

        Artifact updatedArtifact = Artifact.builder()
                .id("123")
                .name("update")
                .description("update artifact")
                .imageUrl("update imageUrl")
                .build();

        given(this.artifactService.update(eq("123"),Mockito.any(Artifact.class))).willReturn(updatedArtifact);

        //when and then
        this.mockMvc.perform(put("/api/v1/artifacts/123")
                        .contentType(MediaType.APPLICATION_JSON).content(json)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
                .andExpect(jsonPath("$.message").value("update success"))
                .andExpect(jsonPath("$.data.id").isNotEmpty())
                .andExpect(jsonPath("$.data.name").value(updatedArtifact.getName()))
                .andExpect(jsonPath("$.data.description").value(updatedArtifact.getDescription()))
                .andExpect(jsonPath("$.data.imageUrl").value(updatedArtifact.getImageUrl()));
    }

    @Test
    void updateArtifactErrorWithNonExistentId() throws Exception {
       //given
        ArtifactDto artifactDto = new ArtifactDto(
                "123",
                "update",
                "update description",
                "update url",
                null
        );

        String json = this.objectMapper.writeValueAsString(artifactDto);

        given(this.artifactService.update(eq("123"),Mockito.any(Artifact.class))).willThrow(new ObjectNotFoundException("Artifact","123"));

       //when and then
        this.mockMvc.perform(put("/api/v1/artifacts/123")
                        .contentType(MediaType.APPLICATION_JSON).content(json)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(false))
                .andExpect(jsonPath("$.code").value(StatusCode.NOT_FOUND))
                .andExpect(jsonPath("$.message").value("Artifact: 123 not found"))
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @Test
    void deleteArtifactSuccess() throws Exception {
        //given
        doNothing().when(this.artifactService).delete("123");

        //when and then
        this.mockMvc.perform(delete("/api/v1/artifacts/123").accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
                .andExpect(jsonPath("$.message").value("delete success"))
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @Test
    void deleteArtifactNotFound() throws Exception {
        //given
        doThrow(new ObjectNotFoundException("Artifact","123"))
                .when(this.artifactService).delete("123");

        //when and then
        this.mockMvc.perform(delete("/api/v1/artifacts/123").accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(false))
                .andExpect(jsonPath("$.code").value(StatusCode.NOT_FOUND))
                .andExpect(jsonPath("$.message").value("Artifact: 123 not found"))
                .andExpect(jsonPath("$.data").isEmpty());
    }
}