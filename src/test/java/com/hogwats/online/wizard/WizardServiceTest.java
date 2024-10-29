package com.hogwats.online.wizard;

import com.hogwats.online.artifact.Artifact;
import com.hogwats.online.artifact.ArtifactRepository;
import com.hogwats.online.artifact.utils.IdWorker;
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
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WizardServiceTest {
    @Mock
    WizardRepository wizardRepository;

    @Mock
    ArtifactRepository artifactRepository;

    @Mock
    IdWorker idWorker;

    @InjectMocks
    WizardService wizardService;

    List<Wizard> wizards = new ArrayList<>();

    @BeforeEach
    void setUp() {
        Wizard wz1 = Wizard.builder()
                .id(123L)
                .name("Test 1")
                .build();

        Wizard wz2 = Wizard.builder()
                .id(1234L)
                .name("Test 2")
                .build();

        this.wizards.add(wz1);
        this.wizards.add(wz2);
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void createWizard() {
        //given
        Wizard wizard = Wizard.builder()
                .name("WizardTest")
                .build();

        given(this.idWorker.nextId()).willReturn(12345L);
        given(this.wizardRepository.save(wizard)).willReturn(wizard);

        //when
        Wizard savedWizard = this.wizardService.createWizard(wizard);

        //then
        assertThat(savedWizard.getId()).isEqualTo(12345L);
        assertThat(savedWizard.getName()).isEqualTo(wizard.getName());
        verify(this.wizardRepository,times(1)).save(wizard);
    }

    @Test
    void getWizardById(){
        //given
        Wizard wizard1 = Wizard.builder()
                .id(1234L)
                .name("Test wizard found")
                .build();
        given(this.wizardRepository.findById(Mockito.anyLong())).willReturn(Optional.of(wizard1));

        //when
        Wizard foundWizard = this.wizardService.getWizardById(1234L);

        //then
        assertThat(foundWizard.getId()).isEqualTo(wizard1.getId());
        assertThat(foundWizard.getName()).isEqualTo(wizard1.getName());

        verify(this.wizardRepository,times(1)).findById(1234L);
    }

    @Test
    void getWizardByIdNotFound(){
        //given
        given(this.wizardRepository.findById(Mockito.anyLong())).willReturn(Optional.empty());

        //when
        assertThrows(ObjectNotFoundException.class,
                () -> this.wizardService.getWizardById(1234L));

        //then
        verify(this.wizardRepository,times(1)).findById(1234L);
    }

    @Test
    void getAllWizards(){
        //given
        given(this.wizardRepository.findAll()).willReturn(this.wizards);

        //when
        List<Wizard> wizardList = this.wizardService.getAllWizards();

        //then
        assertThat(wizardList.size()).isEqualTo(this.wizards.size());
        assertThat(wizardList.getFirst().getId()).isEqualTo(this.wizards.getFirst().getId());
        assertThat(wizardList.get(1).getName()).isEqualTo(this.wizards.get(1).getName());

        verify(this.wizardRepository, times(1)).findAll();
    }

    @Test
    void getAllWizardsNotFound(){
        //given
        given(this.wizardRepository.findAll()).willReturn(Collections.emptyList());

        //when
        List<Wizard> foundWizardList = this.wizardService.getAllWizards();

        //then
        assertThat(foundWizardList).isEmpty();
        verify(this.wizardRepository,times(1)).findAll();
    }

    @Test
    void deleteWizardById(){
        //given
        Wizard toDelete = Wizard.builder()
                .id(1234L)
                .name("Delete Wizard")
                .build();
        given(this.wizardRepository.findById(1234L)).willReturn(Optional.of(toDelete));
        doNothing().when(this.wizardRepository).deleteById(1234L);

        //when
        this.wizardService.deleteWizardById(1234L);

        //then
        verify(this.wizardRepository,times(1)).deleteById(1234L);
    }

    @Test
    void deleteWizardByIdNotFound(){
        //given
        given(this.wizardRepository.findById(1234L)).willReturn(Optional.empty());

        //when
        assertThrows(ObjectNotFoundException.class,
                ()-> this.wizardService.deleteWizardById(1234L));

        //then
        verify(this.wizardRepository,times(1)).findById(1234L);
        verify(this.wizardRepository,times(0)).deleteById(1234L);
    }

    @Test
    void assignArtifactSuccess(){
        //given
        Artifact art1 = Artifact.builder()
                .id("1234")
                .name("Wand")
                .description("Magic stick")
                .imageUrl("image1")
                .build();

        Wizard wiz1 = Wizard.builder()
                .id(1235L)
                .name("Test 1")
                .build();

        Wizard wiz2 = Wizard.builder()
                .id(1237L)
                .name("Test 2")
                .build();

        wiz1.addArtifact(art1);

        given(this.artifactRepository.findById(art1.getId())).willReturn(Optional.of(art1));
        given(this.wizardRepository.findById(wiz2.getId())).willReturn(Optional.of(wiz2));

        //when
        this.wizardService.assignArtifact(wiz2.getId(), art1.getId());

        //then
        assertThat(art1.getOwner().getId()).isEqualTo(wiz2.getId());
        assertThat(wiz2.getArtifacts()).contains(art1);
    }

    @Test
    void assignArtifactErrorWithNonExistentWizardId(){
        //given
        Artifact art1 = Artifact.builder()
                .id("1234")
                .name("Wand")
                .description("Magic stick")
                .imageUrl("image1")
                .build();

        Wizard wiz1 = Wizard.builder()
                .id(1235L)
                .name("Test 1")
                .build();
        wiz1.addArtifact(art1);

        given(this.artifactRepository.findById(art1.getId())).willReturn(Optional.of(art1));
        given(this.wizardRepository.findById(Mockito.anyLong())).willReturn(Optional.empty());

        //when
        Throwable thrown = assertThrows(ObjectNotFoundException.class,
                ()->{
            this.wizardService.assignArtifact(143L,art1.getId());
                });

        //then
        assertThat(thrown).isInstanceOf(ObjectNotFoundException.class)
                .hasMessage("Wizard: " + 143L + " not found");
        assertThat(art1.getOwner().getId()).isEqualTo(wiz1.getId());

    }

    @Test
    void assignArtifactErrorWithNonExistentArtifactId(){
        //given
        given(this.artifactRepository.findById("1234")).willReturn(Optional.empty());

        //when
        Throwable thrown = assertThrows(ObjectNotFoundException.class,
                ()->{
            this.wizardService.assignArtifact(143L,"1234");
                });

        //then
        assertThat(thrown).isInstanceOf(ObjectNotFoundException.class)
                .hasMessage("Artifact: 1234 not found");
    }
}