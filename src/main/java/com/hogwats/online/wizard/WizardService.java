package com.hogwats.online.wizard;

import com.hogwats.online.artifact.utils.IdWorker;
import com.hogwats.online.system.exception.ObjectNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class WizardService {
    private final WizardRepository wizardRepository;
    private final IdWorker idWorker;

    public Wizard createWizard(Wizard newWizard){
        newWizard.setId(this.idWorker.nextId());
        return wizardRepository.save(newWizard);
    }

    public Wizard getWizardById(Long wizardId){
        return wizardRepository.findById(wizardId)
                .orElseThrow(() -> new ObjectNotFoundException("Wizard",wizardId));
    }

    public List<Wizard> getAllWizards(){
        return wizardRepository.findAll();
    }

    public void deleteWizardById(Long wizardId){
        this.wizardRepository.findById(wizardId)
                .orElseThrow(()-> new ObjectNotFoundException("Wizard",wizardId));
        this.wizardRepository.deleteById(wizardId);
    }
}
