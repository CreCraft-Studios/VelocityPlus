package com.crecraftstudios.velocityplus.exceptions;

public class DuplicateNetworkKeyException extends Exception{
    public DuplicateNetworkKeyException(String key) {
        super("Can't register network key "+key+" as it is already registered to a different message");
    }
}
