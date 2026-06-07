package com.example.vehiclerental;

public abstract class Vehicle {
    private final int id;
    private String name;
    private static int nextId = 1;

    protected Vehicle(String name) {
        id = nextId++;
        setName(name);
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        String trimmedName = name == null ? "" : name.trim();
        this.name = trimmedName.isEmpty() ? "Unnamed vehicle" : trimmedName;
    }

    static void resetIdsForTests(int nextIdValue) {
        nextId = nextIdValue;
    }

    @Override
    public abstract String toString();
}
