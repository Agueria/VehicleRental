package com.example.vehiclerental;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

// Student: Cem Cakir, Student ID: 44463, Lab Task 4
public class MainActivity extends AppCompatActivity {
    private static final int GARAGE_COUNT = 5;
    private static final String XML_FILE_NAME = "vehicles.xml";

    private Rental rental;
    private File vehicleFile;
    private TextView inventoryText;
    private TextView statusText;
    private Spinner typeSpinner;
    private EditText nameInput;
    private EditText fuelMaskInput;
    private EditText fuelAmountInput;
    private EditText parkVehicleIdInput;
    private EditText garageNumberInput;
    private EditText removeVehicleIdInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        bindViews();
        setupTypeSpinner();
        bindActions();
        loadRental();
        renderInventory("Loaded initial XML database.");
    }

    @Override
    protected void onPause() {
        super.onPause();
        saveRental();
    }

    private void bindViews() {
        inventoryText = findViewById(R.id.inventoryText);
        statusText = findViewById(R.id.statusText);
        typeSpinner = findViewById(R.id.typeSpinner);
        nameInput = findViewById(R.id.nameInput);
        fuelMaskInput = findViewById(R.id.fuelMaskInput);
        fuelAmountInput = findViewById(R.id.fuelAmountInput);
        parkVehicleIdInput = findViewById(R.id.parkVehicleIdInput);
        garageNumberInput = findViewById(R.id.garageNumberInput);
        removeVehicleIdInput = findViewById(R.id.removeVehicleIdInput);
    }

    private void setupTypeSpinner() {
        String[] types = {"Car", "Motorboat", "Bicycle", "Scooter"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, types);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        typeSpinner.setAdapter(adapter);
    }

    private void bindActions() {
        Button addVehicleButton = findViewById(R.id.addVehicleButton);
        Button parkButton = findViewById(R.id.parkButton);
        Button unparkButton = findViewById(R.id.unparkButton);
        Button removeButton = findViewById(R.id.removeButton);
        Button sortButton = findViewById(R.id.sortButton);
        Button saveButton = findViewById(R.id.saveButton);

        addVehicleButton.setOnClickListener(view -> addVehicle());
        parkButton.setOnClickListener(view -> parkVehicle());
        unparkButton.setOnClickListener(view -> unparkVehicle());
        removeButton.setOnClickListener(view -> removeVehicle());
        sortButton.setOnClickListener(view -> {
            rental.sortVehicles();
            renderInventory("Vehicles sorted by parked status, type, name, fuel type, and fuel amount.");
        });
        saveButton.setOnClickListener(view -> {
            saveRental();
            renderInventory("Saved vehicles to XML.");
        });
    }

    private void loadRental() {
        vehicleFile = new File(getFilesDir(), XML_FILE_NAME);
        copyInitialXmlIfMissing();
        try {
            rental = VehicleXmlStorage.load(vehicleFile, GARAGE_COUNT);
        } catch (Exception exception) {
            rental = new Rental(GARAGE_COUNT);
            statusText.setText("Could not load XML. Started with an empty rental.");
        }
    }

    private void copyInitialXmlIfMissing() {
        if (vehicleFile.exists()) {
            return;
        }

        try (InputStream inputStream = getAssets().open(XML_FILE_NAME);
             FileOutputStream outputStream = new FileOutputStream(vehicleFile)) {
            byte[] buffer = new byte[4096];
            int read;
            while ((read = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, read);
            }
        } catch (Exception ignored) {
            // The UI will still work with an empty rental if the asset cannot be copied.
        }
    }

    private void addVehicle() {
        String type = String.valueOf(typeSpinner.getSelectedItem());
        String name = nameInput.getText().toString();
        int fuelMask = parseInt(fuelMaskInput, 0);
        double fuelAmount = parseDouble(fuelAmountInput, 0.0);

        Vehicle vehicle;
        if ("Car".equals(type)) {
            vehicle = new Car(name, fuelMask, fuelAmount);
        } else if ("Motorboat".equals(type)) {
            vehicle = new Motorboat(name, fuelMask, fuelAmount);
        } else if ("Bicycle".equals(type)) {
            vehicle = new Bicycle(name);
        } else {
            vehicle = new Scooter(name);
        }

        rental.addVehicle(vehicle);
        clearAddInputs();
        renderInventory("Added " + type + " with ID " + vehicle.getId() + ".");
    }

    private void parkVehicle() {
        int vehicleId = parseInt(parkVehicleIdInput, -1);
        int garageNumber = parseInt(garageNumberInput, -1);
        renderInventory(rental.parkVehicle(vehicleId, garageNumber));
    }

    private void unparkVehicle() {
        int vehicleId = parseInt(parkVehicleIdInput, -1);
        renderInventory(rental.unparkVehicle(vehicleId));
    }

    private void removeVehicle() {
        int vehicleId = parseInt(removeVehicleIdInput, -1);
        renderInventory(rental.removeVehicleById(vehicleId));
    }

    private void saveRental() {
        if (rental == null || vehicleFile == null) {
            return;
        }

        try {
            VehicleXmlStorage.save(rental, vehicleFile);
        } catch (Exception exception) {
            statusText.setText("Could not save XML.");
        }
    }

    private void renderInventory(String status) {
        statusText.setText(status);
        inventoryText.setText(rental.printAllVehicles() + "\n\nGarages:\n" + garageSummary());
    }

    private String garageSummary() {
        StringBuilder builder = new StringBuilder();
        for (Garage garage : rental.getGarages()) {
            builder.append("Garage ")
                    .append(garage.getNumber())
                    .append(": ");
            if (garage.isEmpty()) {
                builder.append("empty");
            } else {
                Vehicle vehicle = (Vehicle) garage.getParkedVehicle();
                builder.append("vehicle ID ").append(vehicle.getId());
            }
            builder.append('\n');
        }
        return builder.toString().trim();
    }

    private void clearAddInputs() {
        nameInput.setText("");
        fuelMaskInput.setText("");
        fuelAmountInput.setText("");
    }

    private int parseInt(EditText input, int fallback) {
        try {
            return Integer.parseInt(input.getText().toString().trim());
        } catch (NumberFormatException exception) {
            return fallback;
        }
    }

    private double parseDouble(EditText input, double fallback) {
        try {
            return Double.parseDouble(input.getText().toString().trim());
        } catch (NumberFormatException exception) {
            return fallback;
        }
    }
}
