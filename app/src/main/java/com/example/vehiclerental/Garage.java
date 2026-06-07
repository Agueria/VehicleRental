package com.example.vehiclerental;

public class Garage {
    private final int number;
    private Parkable parkedVehicle;

    public Garage(int number) {
        if (number <= 0) {
            throw new IllegalArgumentException("Garage number must be positive.");
        }
        this.number = number;
    }

    public int getNumber() {
        return number;
    }

    public boolean isEmpty() {
        return parkedVehicle == null;
    }

    public Parkable getParkedVehicle() {
        return parkedVehicle;
    }

    void setParkedVehicle(Parkable parkedVehicle) {
        this.parkedVehicle = parkedVehicle;
    }
}
