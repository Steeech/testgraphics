package com.company.testgraphics.exceptions;

public class TubeOutOfSightException extends Exception{

    public TubeOutOfSightException(String message, Throwable cause) {
        super(message, cause);
    }

    public TubeOutOfSightException(String message) {
        super(message);
    }

}
