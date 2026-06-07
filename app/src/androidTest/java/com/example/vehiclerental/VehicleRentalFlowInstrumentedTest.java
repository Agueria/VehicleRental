package com.example.vehiclerental;

import android.content.Context;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class VehicleRentalFlowInstrumentedTest {

    @Before
    public void resetStoredXml() {
        Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        context.deleteFile("vehicles.xml");
    }

    @Test
    public void mainScreenShowsInitialVehicleDatabase() {
        try (ActivityScenario<MainActivity> scenario = ActivityScenario.launch(MainActivity.class)) {
            scenario.onActivity(activity -> {
                TextView titleText = activity.findViewById(R.id.titleText);
                TextView statusText = activity.findViewById(R.id.statusText);
                TextView inventoryText = activity.findViewById(R.id.inventoryText);

                assertTrue(titleText.getText().toString().contains("Vehicle Rental"));
                assertTrue(statusText.getText().toString().contains("Loaded initial XML database."));
                assertTrue(inventoryText.getText().toString().contains("Honda Civic"));
                assertTrue(inventoryText.getText().toString().contains("Garage 1: empty"));
            });
        }
    }

    @Test
    public void menuCanAddParkSortRemoveAndSave() {
        try (ActivityScenario<MainActivity> scenario = ActivityScenario.launch(MainActivity.class)) {
            scenario.onActivity(activity -> {
                EditText nameInput = activity.findViewById(R.id.nameInput);
                EditText fuelMaskInput = activity.findViewById(R.id.fuelMaskInput);
                EditText fuelAmountInput = activity.findViewById(R.id.fuelAmountInput);
                Button addVehicleButton = activity.findViewById(R.id.addVehicleButton);
                TextView statusText = activity.findViewById(R.id.statusText);
                TextView inventoryText = activity.findViewById(R.id.inventoryText);

                nameInput.setText("Test Car");
                fuelMaskInput.setText("2");
                fuelAmountInput.setText("6.5");
                addVehicleButton.performClick();

                assertTrue(statusText.getText().toString().contains("Added Car"));
                assertTrue(inventoryText.getText().toString().contains("Test Car"));

                EditText parkVehicleIdInput = activity.findViewById(R.id.parkVehicleIdInput);
                EditText garageNumberInput = activity.findViewById(R.id.garageNumberInput);
                Button parkButton = activity.findViewById(R.id.parkButton);
                parkVehicleIdInput.setText("1");
                garageNumberInput.setText("1");
                parkButton.performClick();

                assertTrue(statusText.getText().toString().contains("Parked successfully."));
                assertTrue(inventoryText.getText().toString().contains("Garage 1: vehicle ID 1"));

                Button sortButton = activity.findViewById(R.id.sortButton);
                sortButton.performClick();
                assertTrue(statusText.getText().toString().contains("Vehicles sorted"));

                EditText removeVehicleIdInput = activity.findViewById(R.id.removeVehicleIdInput);
                Button removeButton = activity.findViewById(R.id.removeButton);
                removeVehicleIdInput.setText("1");
                removeButton.performClick();
                assertTrue(statusText.getText().toString().contains("Vehicle was parked and removed."));

                Button saveButton = activity.findViewById(R.id.saveButton);
                saveButton.performClick();
                assertTrue(statusText.getText().toString().contains("Saved vehicles to XML."));
            });
        }
    }
}
