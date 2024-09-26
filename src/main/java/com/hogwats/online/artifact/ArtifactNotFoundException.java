package com.hogwats.online.artifact;

public class ArtifactNotFoundException extends RuntimeException{
    public ArtifactNotFoundException(String id){
        super("Artifact: " + id + " not found");
    }
}
