package com.example.vehiclerental;

public class Bicycle extends Vehicle implements Parkable {
    private Garage garage;

    public Bicycle(String name) {
        super(name);
    }

    @Override
    public boolean park(Garage garage) {
        if (garage == null || !garage.isEmpty() || isParked()) {
            return false;
        }
        this.garage = garage;
        garage.setParkedVehicle(this);
        return true;
    }

    @Override
    public boolean unpark() {
        if (!isParked()) {
            return false;
        }
        Garage previousGarage = garage;
        garage = null;
        previousGarage.setParkedVehicle(null);
        return true;
    }

    @Override
    public boolean isParked() {
        return garage != null;
    }

    @Override
    public Garage getGarage() {
        return garage;
    }

    @Override
    public String toString() {
        return String.format("[%d] Bicycle - %s | parked: %s",
                getId(),
                getName(),
                isParked() ? "yes, garage " + garage.getNumber() : "no");
    }
}
