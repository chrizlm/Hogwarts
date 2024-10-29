package com.hogwats.online.wizard;

import com.hogwats.online.artifact.Artifact;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@Entity
public class Wizard implements Serializable {
    @Id
    private Long id;
    private String name;
    @OneToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, mappedBy = "owner")
    @Builder.Default
    private List<Artifact> artifacts = new ArrayList<>();

    public void addArtifact(Artifact artifact) {
        artifact.setOwner(this); //set this wizard as owner of artifact
        this.artifacts.add(artifact); //add the artifact to the list of artifacts the wizard has
    }

    public Integer getNumberOfArtifacts() {
        return this.artifacts.size();
    }

    public void removeArtifact(Artifact foundArtifact) {
        //remove artifact owner
        foundArtifact.setOwner(null);
        this.artifacts.remove(foundArtifact);
    }
}
