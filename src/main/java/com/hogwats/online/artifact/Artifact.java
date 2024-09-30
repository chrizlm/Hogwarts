package com.hogwats.online.artifact;

import com.hogwats.online.wizard.Wizard;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@Entity
public class Artifact implements Serializable {
    @Id
    private String id;
    private String name;
    private String description;
    private String imageUrl;
    @ManyToOne
    private Wizard owner;
}
