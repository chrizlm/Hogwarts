package com.hogwats.online.wizard;

import com.hogwats.online.system.Result;
import com.hogwats.online.system.StatusCode;
import com.hogwats.online.wizard.converter.WizardDtoToWizardConverter;
import com.hogwats.online.wizard.converter.WizardToWizardDtoConverter;
import com.hogwats.online.wizard.dto.WizardDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("${api.endpoint.base-url}/wizard")
@RequiredArgsConstructor
public class WizardController {
    private final WizardService wizardService;
    private final WizardDtoToWizardConverter wizardDtoToWizardConverter;
    private final WizardToWizardDtoConverter wizardToWizardDtoConverter;

    @PostMapping
    public Result createWizard(@RequestBody @Valid WizardDto wizardDto){
        Wizard newWizard = this.wizardDtoToWizardConverter.convert(wizardDto);
        assert newWizard != null;
        Wizard foundWizard = this.wizardService.createWizard(newWizard);
        WizardDto newWizDto = this.wizardToWizardDtoConverter.convert(foundWizard);
        return Result.builder()
                .flag(true)
                .code(StatusCode.SUCCESS)
                .message("Wizard created")
                .data(newWizDto)
                .build();
    }

    @GetMapping("{wizardId}")
    public Result getWizardById(@PathVariable Long wizardId){
        Wizard foundWizard = this.wizardService.getWizardById(wizardId);
        return Result.builder()
                .flag(true)
                .code(StatusCode.SUCCESS)
                .message("Wizard found")
                .data(foundWizard)
                .build();
    }

    @GetMapping
    public Result getAllWizards(){
        List<Wizard> foundWizards = this.wizardService.getAllWizards();
        List<WizardDto> foundWizardDtos = foundWizards.stream()
                .map(this.wizardToWizardDtoConverter::convert).toList();
        return Result.builder()
                .flag(true)
                .code(StatusCode.SUCCESS)
                .message("Wizard List retrieved")
                .data(foundWizardDtos).build();
    }

    @DeleteMapping("{wizardId}")
    public Result deleteWizardById(@PathVariable Long wizardId){
        this.wizardService.deleteWizardById(wizardId);
        return Result.builder()
                .flag(true)
                .code(StatusCode.SUCCESS)
                .message("Wizard Deleted")
                .build();
    }
}
