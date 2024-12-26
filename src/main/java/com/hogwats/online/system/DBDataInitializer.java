package com.hogwats.online.system;

import com.hogwats.online.artifact.Artifact;
import com.hogwats.online.artifact.ArtifactRepository;
import com.hogwats.online.hogwartsUser.HogwartsUser;
import com.hogwats.online.hogwartsUser.UserRepository;
import com.hogwats.online.hogwartsUser.UserService;
import com.hogwats.online.wizard.Wizard;
import com.hogwats.online.wizard.WizardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Profile("dev")
public class DBDataInitializer implements CommandLineRunner {

    private final ArtifactRepository artifactRepository;
    private final WizardRepository wizardRepository;
    private final UserService userService;


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

        this.wizardRepository.save(w1);
        this.wizardRepository.save(w2);

        this.artifactRepository.save(a3);


        HogwartsUser usr1 = HogwartsUser.builder()
                .id(123L)
                .username("user1")
                .enabled(true)
                .roles("admin")
                .password("123").build();

        HogwartsUser usr2 = HogwartsUser.builder()
                .id(1234L)
                .username("user2")
                .enabled(false)
                .roles("user")
                .password("1234").build();

        HogwartsUser usr3 = HogwartsUser.builder()
                .id(1234L)
                .username("user3")
                .enabled(true)
                .roles("user")
                .password("12345").build();


        this.userService.addUser(usr1);
        this.userService.addUser(usr2);
        this.userService.addUser(usr3);

    }
}
