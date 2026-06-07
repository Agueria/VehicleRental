package com.example.vehiclerental;

public class Car extends Vehicle implements CombustionVehicle, Parkable {
    private final int supportedFuelMask;
    private double fuelAmount;
    private Garage garage;

    public Car(String name, int supportedFuelMask, double fuelAmount) {
        super(name);
        this.supportedFuelMask = supportedFuelMask;
        this.fuelAmount = Math.max(0.0, fuelAmount);
    }

    @Override
    public boolean refuel(int fuelMask, double liters) {
        if (liters <= 0 || (supportedFuelMask & fuelMask) == 0) {
            return false;
        }
        fuelAmount += liters;
        return true;
    }

    @Override
    public int getSupportedFuelMask() {
        return supportedFuelMask;
    }

    @Override
    public double getFuelAmount() {
        return fuelAmount;
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
        return String.format("[%d] Car - %s | fuel mask: %d | fuel: %.1f L | parked: %s",
                getId(),
                getName(),
                supportedFuelMask,
                fuelAmount,
                isParked() ? "yes, garage " + garage.getNumber() : "no");
    }
}
