package com.example.vehiclerental;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class Rental {
    private final ArrayList<Vehicle> vehicles = new ArrayList<>();
    private final ArrayList<Garage> garages = new ArrayList<>();

    private static final Comparator<Vehicle> RENTAL_ORDER = (first, second) -> {
        int parkedComparison = Boolean.compare(isParked(second), isParked(first));
        if (parkedComparison != 0) {
            return parkedComparison;
        }

        int typeComparison = Integer.compare(typeOrder(first), typeOrder(second));
        if (typeComparison != 0) {
            return typeComparison;
        }

        int nameComparison = first.getName().compareToIgnoreCase(second.getName());
        if (nameComparison != 0) {
            return nameComparison;
        }

        int fuelTypeComparison = Integer.compare(fuelMask(first), fuelMask(second));
        if (fuelTypeComparison != 0) {
            return fuelTypeComparison;
        }

        return Double.compare(fuelAmount(first), fuelAmount(second));
    };

    public Rental(int garageCount) {
        for (int number = 1; number <= garageCount; number++) {
            garages.add(new Garage(number));
        }
    }

    public void addVehicle(Vehicle vehicle) {
        if (vehicle != null) {
            vehicles.add(vehicle);
        }
    }

    public ArrayList<Vehicle> getVehicles() {
        return new ArrayList<>(vehicles);
    }

    public ArrayList<Garage> getGarages() {
        return new ArrayList<>(garages);
    }

    public Garage getGarage(int number) {
        for (Garage garage : garages) {
            if (garage.getNumber() == number) {
                return garage;
            }
        }
        return null;
    }

    public Vehicle findVehicleById(int id) {
        for (Vehicle vehicle : vehicles) {
            if (vehicle.getId() == id) {
                return vehicle;
            }
        }
        return null;
    }

    public String parkVehicle(int vehicleId, int garageNumber) {
        Vehicle vehicle = findVehicleById(vehicleId);
        if (vehicle == null) {
            return "Vehicle not found.";
        }
        if (!(vehicle instanceof Parkable)) {
            return "Vehicle is not parkable.";
        }

        Parkable parkable = (Parkable) vehicle;
        if (parkable.isParked()) {
            return "Vehicle is already parked.";
        }

        Garage garage = getGarage(garageNumber);
        if (garage == null) {
            return "Garage not found.";
        }
        if (!garage.isEmpty()) {
            return "Garage is occupied.";
        }

        return parkable.park(garage) ? "Parked successfully." : "Parking failed.";
    }

    public String unparkVehicle(int vehicleId) {
        Vehicle vehicle = findVehicleById(vehicleId);
        if (vehicle == null) {
            return "Vehicle not found.";
        }
        if (!(vehicle instanceof Parkable)) {
            return "Vehicle is not parkable.";
        }

        Parkable parkable = (Parkable) vehicle;
        if (!parkable.isParked()) {
            return "Vehicle is not parked.";
        }

        return parkable.unpark() ? "Unparked successfully." : "Unpark failed.";
    }

    public String removeVehicleById(int vehicleId) {
        Vehicle vehicle = findVehicleById(vehicleId);
        if (vehicle == null) {
            return "Vehicle not found.";
        }

        boolean wasParked = false;
        if (vehicle instanceof Parkable) {
            Parkable parkable = (Parkable) vehicle;
            wasParked = parkable.isParked();
            if (wasParked) {
                parkable.unpark();
            }
        }

        vehicles.remove(vehicle);
        return wasParked ? "Vehicle was parked and removed." : "Vehicle removed.";
    }

    public void sortVehicles() {
        Collections.sort(vehicles, RENTAL_ORDER);
    }

    public String printAllVehicles() {
        if (vehicles.isEmpty()) {
            return "No vehicles available.";
        }

        StringBuilder builder = new StringBuilder();
        for (Vehicle vehicle : vehicles) {
            builder.append(vehicle).append('\n');
        }
        return builder.toString().trim();
    }

    private static boolean isParked(Vehicle vehicle) {
        return vehicle instanceof Parkable && ((Parkable) vehicle).isParked();
    }

    private static int typeOrder(Vehicle vehicle) {
        if (vehicle instanceof Car) {
            return 0;
        }
        if (vehicle instanceof Motorboat) {
            return 1;
        }
        if (vehicle instanceof Bicycle) {
            return 2;
        }
        return 3;
    }

    private static int fuelMask(Vehicle vehicle) {
        return vehicle instanceof CombustionVehicle
                ? ((CombustionVehicle) vehicle).getSupportedFuelMask()
                : 0;
    }

    private static double fuelAmount(Vehicle vehicle) {
        return vehicle instanceof CombustionVehicle
                ? ((CombustionVehicle) vehicle).getFuelAmount()
                : 0.0;
    }
}
