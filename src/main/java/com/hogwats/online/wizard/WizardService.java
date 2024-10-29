package com.hogwats.online.wizard;

import com.hogwats.online.artifact.Artifact;
import com.hogwats.online.artifact.ArtifactRepository;
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
    private final ArtifactRepository artifactRepository;
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

    public void assignArtifact(Long wizardId, String artifactId){
        //find artifact by Id
        Artifact foundArtifact =this.artifactRepository.findById(artifactId).orElseThrow(
                () -> new ObjectNotFoundException("Artifact", artifactId)
        );

        //find wizard by Id
        Wizard newOwner = this.wizardRepository.findById(wizardId).orElseThrow(
                () -> new ObjectNotFoundException("Wizard", wizardId)
        );

        //artifact assignment
        //is artifact owned?
        if(foundArtifact.getOwner() != null){
            foundArtifact.getOwner().removeArtifact(foundArtifact);
        }
        //assignment
        newOwner.addArtifact(foundArtifact);
    }
}
