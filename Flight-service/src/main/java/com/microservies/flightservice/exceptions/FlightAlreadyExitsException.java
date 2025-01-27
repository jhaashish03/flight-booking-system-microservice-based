package com.microservies.flightservice.exceptions;

public class FlightAlreadyExitsException extends Exception{
    public FlightAlreadyExitsException() {
        super();
    }

    public FlightAlreadyExitsException(String message) {
        super(message);
    }
}
