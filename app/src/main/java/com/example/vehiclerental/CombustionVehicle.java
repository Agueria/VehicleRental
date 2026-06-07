package com.example.vehiclerental;

public interface CombustionVehicle {
    // XML fuelType mapping uses these exact bit values:
    // 1 = DIESEL, 2 = PETROL, 4 = LPG, 8 = CNG. Multiple fuels are stored with bitwise OR.
    int DIESEL = 1 << 0;
    int PETROL = 1 << 1;
    int LPG = 1 << 2;
    int CNG = 1 << 3;

    boolean refuel(int fuelMask, double liters);

    int getSupportedFuelMask();

    double getFuelAmount();
}
