package com.hogwats.online.artifact.dto;

import com.hogwats.online.wizard.dto.WizardDto;

public record ArtifactDto(String id,
                          String name,
                          String description,
                          String imageUrl,
                          WizardDto owner) {
}
