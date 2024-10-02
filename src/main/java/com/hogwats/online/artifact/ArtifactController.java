package com.hogwats.online.artifact;

import com.hogwats.online.artifact.converter.ArtifactDtoToArtifactConverter;
import com.hogwats.online.artifact.converter.ArtifactToArtifactDtoConverter;
import com.hogwats.online.artifact.dto.ArtifactDto;
import com.hogwats.online.system.Result;
import com.hogwats.online.system.StatusCode;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/artifacts")
public class ArtifactController {
    private final ArtifactService artifactService;

    private final ArtifactToArtifactDtoConverter artifactToArtifactDtoConverter;
    private final ArtifactDtoToArtifactConverter artifactDtoToArtifactConverter;

    public ArtifactController(ArtifactService artifactService, ArtifactToArtifactDtoConverter artifactToArtifactDtoConverter, ArtifactDtoToArtifactConverter artifactDtoToArtifactConverter) {
        this.artifactService = artifactService;
        this.artifactToArtifactDtoConverter = artifactToArtifactDtoConverter;
        this.artifactDtoToArtifactConverter = artifactDtoToArtifactConverter;
    }

    @GetMapping("/{artifactId}")
    public Result findArtifactById(@PathVariable String artifactId){
        Artifact foundArtifact = this.artifactService.findById(artifactId);
        ArtifactDto artifactDto = this.artifactToArtifactDtoConverter.convert(foundArtifact);
        return new Result(true, StatusCode.SUCCESS,"Find one success",artifactDto);
    }

    @GetMapping
    public Result findAllArtifacts(){
        List<Artifact> foundArtifacts = this.artifactService.findAll();
        //convert found artifacts to list of artifacts dtos
        List<ArtifactDto> artifactDtoList = foundArtifacts.stream()
                .map(this.artifactToArtifactDtoConverter::convert)
                .collect(Collectors.toList());

        return new Result(true,StatusCode.SUCCESS, "Find all success", artifactDtoList);
    }

    @PostMapping
    public Result addArtifact(@Valid @RequestBody ArtifactDto artifactDto){
        //convert artifact DTO to artifact
        Artifact newArtifact = this.artifactDtoToArtifactConverter.convert(artifactDto);
        assert newArtifact != null;
        Artifact savedArtifact = this.artifactService.save(newArtifact);
        ArtifactDto savedArtifactDto = this.artifactToArtifactDtoConverter.convert(savedArtifact);
        return Result.builder()
                .flag(true)
                .code(StatusCode.SUCCESS)
                .message("Add success")
                .data(savedArtifactDto)
                .build();
    }

    @PutMapping("/{artifactId}")
    public Result updateArtifact(@PathVariable String artifactId , @Valid @RequestBody ArtifactDto artifactDto){
        Artifact newArtifact = this.artifactDtoToArtifactConverter.convert(artifactDto);
        Artifact updatedArtifact = this.artifactService.update(artifactId, newArtifact);
        ArtifactDto updatedArtifactDto = this.artifactToArtifactDtoConverter.convert(updatedArtifact);
        return Result.builder()
                .flag(true)
                .code(StatusCode.SUCCESS)
                .message("update success")
                .data(updatedArtifactDto)
                .build();
    }

    @DeleteMapping("/{artifactId}")
    public Result deleteArtifact(@PathVariable String artifactId){
        this.artifactService.delete(artifactId);
        return Result.builder()
                .flag(true)
                .code(StatusCode.SUCCESS)
                .message("delete success")
                .build();
    }
}
