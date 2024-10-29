package com.hogwats.online.wizard.dto;

import jakarta.validation.constraints.NotNull;

public record WizardDto(Long id,
                        @NotNull
                        String name,
                        Integer numberOfArtifacts) {
}
