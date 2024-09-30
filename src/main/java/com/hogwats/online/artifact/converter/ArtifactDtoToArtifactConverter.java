package com.hogwats.online.artifact.converter;

import com.hogwats.online.artifact.Artifact;
import com.hogwats.online.artifact.dto.ArtifactDto;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class ArtifactDtoToArtifactConverter implements Converter<ArtifactDto, Artifact> {
    @Override
    public Artifact convert(ArtifactDto source) {
        return Artifact.builder()
                .id(source.id())
                .name(source.name())
                .description(source.description())
                .imageUrl(source.imageUrl())
                .build();

    }
}
