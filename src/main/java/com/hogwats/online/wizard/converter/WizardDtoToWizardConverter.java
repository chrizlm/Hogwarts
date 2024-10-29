package com.hogwats.online.wizard.converter;

import com.hogwats.online.wizard.Wizard;
import com.hogwats.online.wizard.dto.WizardDto;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class WizardDtoToWizardConverter implements Converter<WizardDto, Wizard> {
    @Override
    public Wizard convert(WizardDto source) {
        return Wizard.builder()
                .id(source.id())
                .name(source.name())
                .build();
    }
}
