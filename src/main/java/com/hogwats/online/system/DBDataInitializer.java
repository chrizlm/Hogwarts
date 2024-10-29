package com.hogwats.online.system;

import com.hogwats.online.artifact.Artifact;
import com.hogwats.online.artifact.ArtifactRepository;
import com.hogwats.online.wizard.Wizard;
import com.hogwats.online.wizard.WizardRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DBDataInitializer implements CommandLineRunner {

    private final ArtifactRepository artifactRepository;
    private final WizardRepository wizardRepository;

    public DBDataInitializer(ArtifactRepository artifactRepository, WizardRepository wizardRepository) {
        this.artifactRepository = artifactRepository;
        this.wizardRepository = wizardRepository;
    }

    @Override
    public void run(String... args) throws Exception {

        Artifact a1 = Artifact.builder()
                .id("1234")
                .name("Wand")
                .description("Magic stick")
                .imageUrl("image1")
                .build();

        Artifact a2 = Artifact.builder()
                .id("12345")
                .name("Broom")
                .description("Magic broom")
                .imageUrl("image2")
                .build();

        Artifact a3 = Artifact.builder()
                .id("123456")
                .name("Stone")
                .description("Magic stone")
                .imageUrl("image3")
                .build();

        Wizard w1 = Wizard.builder()
                .id(1L)
                .name("Harry")
                .build();

        w1.addArtifact(a1);

        Wizard w2 = Wizard.builder()
                .id(2L)
                .name("Tom")
                .build();

        w2.addArtifact(a2);

        wizardRepository.save(w1);
        wizardRepository.save(w2);

        artifactRepository.save(a3);

    }
}
