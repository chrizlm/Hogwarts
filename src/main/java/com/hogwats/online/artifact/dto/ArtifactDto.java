package com.hogwats.online.artifact.dto;

import com.hogwats.online.wizard.dto.WizardDto;
import jakarta.validation.constraints.NotEmpty;

public record ArtifactDto(String id,

                          @NotEmpty(message = "name is required")
                          String name,

                          @NotEmpty(message = "description is required")
                          String description,

                          @NotEmpty(message = "imageUrl is required")
                          String imageUrl,

                          WizardDto owner) {
}
