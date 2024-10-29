package com.hogwats.online.system.exception;

public class ObjectNotFoundException extends RuntimeException{
    public ObjectNotFoundException(String objectName, String id){
        super(objectName + ": " + id + " not found");
    }

    public ObjectNotFoundException(String objectName, Long id){
        super(objectName + ": " + id + " not found");
    }
}
