package com.hogwats.online.artifact;

import com.hogwats.online.artifact.utils.IdWorker;
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

    @Mock
    IdWorker idWorker;

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

    @Test
    void saveArtifactSuccess(){
        //given
        Artifact newArtifact = Artifact.builder()
                .name("Artifact3")
                .description("Description...")
                .imageUrl("imageUrl..")
                .build();

        given(this.idWorker.nextId()).willReturn(123456L);
        given(this.artifactRepository.save(newArtifact)).willReturn(newArtifact);

        //when
        Artifact savedArtifact = this.artifactService.save(newArtifact);

        //then
        assertThat(savedArtifact.getId()).isEqualTo("123456");
        assertThat(savedArtifact.getName()).isEqualTo(newArtifact.getName());
        assertThat(savedArtifact.getDescription()).isEqualTo(newArtifact.getDescription());
        assertThat(savedArtifact.getImageUrl()).isEqualTo(newArtifact.getImageUrl());
        verify(this.artifactRepository,times(1)).save(newArtifact);
    }

    @Test
    void updateArtifactSuccess(){
        //given
        Artifact oldArtifact = Artifact.builder()
                .id("123")
                .name("Artifact3")
                .description("Description3")
                .imageUrl("imageUrl")
                .build();

        Artifact update = Artifact.builder()
                .id("123")
                .name("Artifact3")
                .description("New Description3")
                .imageUrl("imageUrl")
                .build();

        given(this.artifactRepository.findById("123")).willReturn(Optional.of(oldArtifact));
        given(this.artifactRepository.save(oldArtifact)).willReturn(oldArtifact);//old artifact has updated values

        //when
        Artifact updatedArtifact = this.artifactService.update("123",update);

        //then
        assertThat(updatedArtifact.getId()).isEqualTo(update.getId());
        assertThat(updatedArtifact.getDescription()).isEqualTo(update.getDescription());
        verify(this.artifactRepository,times(1)).findById("123");
        verify(this.artifactRepository,times(1)).save(oldArtifact);
    }

    @Test
    void updateArtifactNotFound(){
        //given
        Artifact update = Artifact.builder()
                .id("123")
                .name("Artifact3")
                .description("New Description3")
                .imageUrl("imageUrl")
                .build();

        given(this.artifactRepository.findById("123")).willReturn(Optional.empty());

        //when
        assertThrows(ArtifactNotFoundException.class,
                ()->{
            this.artifactService.update("123",update);
                });

        //then
        verify(this.artifactRepository,times(1)).findById("123");

    }


}