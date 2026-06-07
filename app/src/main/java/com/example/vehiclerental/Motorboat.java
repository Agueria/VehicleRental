package com.example.vehiclerental;

public class Motorboat extends Vehicle implements CombustionVehicle {
    private final int supportedFuelMask;
    private double fuelAmount;

    public Motorboat(String name, int supportedFuelMask, double fuelAmount) {
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
    public String toString() {
        return String.format("[%d] Motorboat - %s | fuel mask: %d | fuel: %.1f L",
                getId(), getName(), supportedFuelMask, fuelAmount);
    }
}
