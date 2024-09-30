package com.hogwats.online.artifact;

import com.hogwats.online.wizard.Wizard;
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
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ArtifactServiceTest {

    @Mock
    ArtifactRepository artifactRepository;

    @InjectMocks
    ArtifactService artifactService;

    List<Artifact> artifacts = new ArrayList<>();

    @BeforeEach
    void setUp() {
        Artifact a1 = Artifact.builder()
                .id("12")
                .name("Wand")
                .description("Magic stick")
                .imageUrl("image1")
                .build();

        Artifact a2 = Artifact.builder()
                .id("123")
                .name("Broom")
                .description("Magic broom")
                .imageUrl("image2")
                .build();

        this.artifacts.add(a1);
        this.artifacts.add(a2);
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void findByIdSuccess() {
        //Given
        Wizard wizard = Wizard.builder()
                .id(1)
                .name("Harry")
                .build();

        Artifact artifact = Artifact.builder()
                .id("1234")
                .name("Wand")
                .description("Magic stick")
                .imageUrl("image1")
                .owner(wizard)
                .build();

        given(this.artifactRepository.findById("1234")).willReturn(Optional.of(artifact));

        //When
        Artifact foundArtifact = this.artifactService.findById("1234");

        //Then
        assertThat(foundArtifact.getId()).isEqualTo("1234");
        assertThat(foundArtifact.getName()).isEqualTo("Wand");
        assertThat(foundArtifact.getDescription()).isEqualTo("Magic stick");
        assertThat(foundArtifact.getImageUrl()).isEqualTo("image1");
        verify(this.artifactRepository, times(1)).findById("1234");

    }

    @Test
    void findByIdFail(){
        //given
        given(this.artifactRepository.findById(Mockito.anyString())).willReturn(Optional.empty());

        //when
        Throwable thrown = catchThrowable(
                () -> {Artifact foundArtifact = this.artifactService.findById("1234");}
        );

        //then
        assertThat(thrown)
                .isInstanceOf(ArtifactNotFoundException.class)
                .hasMessage("Artifact: 1234 not found");
        verify(this.artifactRepository,times(1)).findById("1234");
    }

    @Test
    void findAllSuccess(){
        //given
        given(this.artifactRepository.findAll()).willReturn(artifacts);

        //when
        List<Artifact> foundArtifacts = this.artifactService.findAll();

        //then
        assertThat(foundArtifacts.size()).isEqualTo(this.artifacts.size());
        verify(this.artifactRepository,times(1)).findAll();
    }


}