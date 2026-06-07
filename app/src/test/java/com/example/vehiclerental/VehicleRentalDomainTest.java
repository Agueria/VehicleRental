package com.example.vehiclerental;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class VehicleRentalDomainTest {

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Before
    public void resetVehicleIds() {
        Vehicle.resetIdsForTests(1);
    }

    @Test
    public void vehiclesReceiveAutomaticUniqueIds() {
        Vehicle car = new Car("Honda Civic", CombustionVehicle.DIESEL | CombustionVehicle.PETROL, 0.0);
        Vehicle bicycle = new Bicycle("Giant");
        Vehicle scooter = new Scooter("City scooter");

        assertEquals(1, car.getId());
        assertEquals(2, bicycle.getId());
        assertEquals(3, scooter.getId());
    }

    @Test
    public void combustionVehicleRefuelUsesBitmaskAndValidLiters() {
        Car car = new Car("Mercedes CLK", CombustionVehicle.DIESEL | CombustionVehicle.LPG, 5.0);

        assertTrue(car.refuel(CombustionVehicle.DIESEL, 10.0));
        assertTrue(car.refuel(CombustionVehicle.LPG, 2.5));
        assertFalse(car.refuel(CombustionVehicle.PETROL, 50.0));
        assertFalse(car.refuel(CombustionVehicle.DIESEL, 0.0));
        assertFalse(car.refuel(CombustionVehicle.DIESEL, -1.0));
        assertEquals(17.5, car.getFuelAmount(), 0.001);
    }

    @Test
    public void parkAndUnparkMaintainVehicleAndGarageReferences() {
        Garage garage = new Garage(1);
        Garage secondGarage = new Garage(2);
        Car car = new Car("Honda Civic", CombustionVehicle.PETROL, 0.0);
        Bicycle bicycle = new Bicycle("Giant");

        assertTrue(car.park(garage));
        assertTrue(car.isParked());
        assertEquals(garage, car.getGarage());
        assertEquals(car, garage.getParkedVehicle());
        assertFalse(garage.isEmpty());

        assertFalse(bicycle.park(garage));
        assertFalse(car.park(secondGarage));

        assertTrue(car.unpark());
        assertFalse(car.isParked());
        assertNull(car.getGarage());
        assertTrue(garage.isEmpty());
        assertNull(garage.getParkedVehicle());
    }

    @Test
    public void rentalParkAndRemoveApplyAssignmentRules() {
        Rental rental = new Rental(2);
        Car car = new Car("Honda Civic", CombustionVehicle.PETROL, 0.0);
        Motorboat motorboat = new Motorboat("Lake Boat", CombustionVehicle.DIESEL, 0.0);
        rental.addVehicle(car);
        rental.addVehicle(motorboat);

        assertEquals("Parked successfully.", rental.parkVehicle(car.getId(), 1));
        assertEquals("Vehicle is not parkable.", rental.parkVehicle(motorboat.getId(), 2));
        assertEquals("Vehicle was parked and removed.", rental.removeVehicleById(car.getId()));
        assertTrue(rental.getGarage(1).isEmpty());
        assertEquals("Vehicle not found.", rental.removeVehicleById(999));
    }

    @Test
    public void rentalSortsByParkedTypeNameFuelTypeAndFuelAmount() {
        Rental rental = new Rental(3);
        Scooter scooter = new Scooter("Zoom");
        Bicycle bicycle = new Bicycle("Alpha Bike");
        Car parkedCar = new Car("Beta Car", CombustionVehicle.PETROL, 2.0);
        Car unparkedCar = new Car("Alpha Car", CombustionVehicle.DIESEL, 1.0);
        Motorboat motorboat = new Motorboat("Aqua", CombustionVehicle.CNG, 3.0);
        rental.addVehicle(scooter);
        rental.addVehicle(bicycle);
        rental.addVehicle(parkedCar);
        rental.addVehicle(unparkedCar);
        rental.addVehicle(motorboat);
        rental.parkVehicle(parkedCar.getId(), 1);
        rental.parkVehicle(bicycle.getId(), 2);

        rental.sortVehicles();
        List<Vehicle> sorted = rental.getVehicles();

        assertEquals(parkedCar, sorted.get(0));
        assertEquals(bicycle, sorted.get(1));
        assertEquals(unparkedCar, sorted.get(2));
        assertEquals(motorboat, sorted.get(3));
        assertEquals(scooter, sorted.get(4));
    }

    @Test
    public void xmlStorageLoadsAndSavesVehicleTypesAndFuelMasks() throws Exception {
        File file = temporaryFolder.newFile("vehicles.xml");
        String xml = "<vehicles>"
                + "<car><name>Honda Civic</name><fuelType>3</fuelType><fuelAmount>4.5</fuelAmount></car>"
                + "<motorboat><name>Super motorboat</name><fuelType>8</fuelType><fuelAmount>1.0</fuelAmount></motorboat>"
                + "<bicycle><name>Giant</name></bicycle>"
                + "<scooter><name>Cool scooter</name></scooter>"
                + "</vehicles>";
        Files.write(file.toPath(), xml.getBytes(StandardCharsets.UTF_8));

        Rental loaded = VehicleXmlStorage.load(file, 5);

        assertEquals(4, loaded.getVehicles().size());
        assertTrue(loaded.getVehicles().get(0) instanceof Car);
        assertEquals(CombustionVehicle.DIESEL | CombustionVehicle.PETROL,
                ((CombustionVehicle) loaded.getVehicles().get(0)).getSupportedFuelMask());
        assertEquals(4.5, ((CombustionVehicle) loaded.getVehicles().get(0)).getFuelAmount(), 0.001);

        VehicleXmlStorage.save(loaded, file);
        String savedXml = new String(Files.readAllBytes(file.toPath()), StandardCharsets.UTF_8);

        assertTrue(savedXml.contains("<car>"));
        assertTrue(savedXml.contains("<motorboat>"));
        assertTrue(savedXml.contains("<bicycle>"));
        assertTrue(savedXml.contains("<scooter>"));
        assertTrue(savedXml.contains("<fuelType>3</fuelType>"));
        assertTrue(savedXml.contains("<fuelAmount>4.5</fuelAmount>"));
    }
}
