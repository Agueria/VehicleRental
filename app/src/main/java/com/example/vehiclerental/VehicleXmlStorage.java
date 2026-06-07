package com.example.vehiclerental;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

public final class VehicleXmlStorage {

    private VehicleXmlStorage() {
    }

    public static Rental load(File file, int garageCount) throws Exception {
        Rental rental = new Rental(garageCount);
        if (file == null || !file.exists()) {
            return rental;
        }

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setIgnoringComments(true);
        Document document = factory.newDocumentBuilder().parse(file);
        NodeList childNodes = document.getDocumentElement().getChildNodes();

        for (int index = 0; index < childNodes.getLength(); index++) {
            Node node = childNodes.item(index);
            if (!(node instanceof Element)) {
                continue;
            }
            Element element = (Element) node;
            Vehicle vehicle = createVehicle(element);
            if (vehicle != null) {
                rental.addVehicle(vehicle);
            }
        }

        return rental;
    }

    public static void save(Rental rental, File file) throws Exception {
        if (file == null) {
            return;
        }
        File parent = file.getParentFile();
        if (parent != null && !parent.exists()) {
            parent.mkdirs();
        }

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        Document document = factory.newDocumentBuilder().newDocument();
        Element root = document.createElement("vehicles");
        document.appendChild(root);

        for (Vehicle vehicle : rental.getVehicles()) {
            root.appendChild(createElementForVehicle(document, vehicle));
        }

        Transformer transformer = TransformerFactory.newInstance().newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty(OutputKeys.ENCODING, StandardCharsets.UTF_8.name());
        try (FileOutputStream outputStream = new FileOutputStream(file)) {
            transformer.transform(new DOMSource(document), new StreamResult(outputStream));
        }
    }

    private static Vehicle createVehicle(Element element) {
        String type = element.getTagName();
        String name = getText(element, "name", "Unnamed vehicle");
        int fuelType = parseInt(getText(element, "fuelType", "0"));
        double fuelAmount = parseDouble(getText(element, "fuelAmount", "0.0"));

        if ("car".equals(type)) {
            return new Car(name, fuelType, fuelAmount);
        }
        if ("motorboat".equals(type)) {
            return new Motorboat(name, fuelType, fuelAmount);
        }
        if ("bicycle".equals(type)) {
            return new Bicycle(name);
        }
        if ("scooter".equals(type)) {
            return new Scooter(name);
        }
        return null;
    }

    private static Element createElementForVehicle(Document document, Vehicle vehicle) {
        Element vehicleElement = document.createElement(tagName(vehicle));
        appendText(document, vehicleElement, "name", vehicle.getName());

        if (vehicle instanceof CombustionVehicle) {
            CombustionVehicle combustionVehicle = (CombustionVehicle) vehicle;
            appendText(document, vehicleElement, "fuelType",
                    String.valueOf(combustionVehicle.getSupportedFuelMask()));
            appendText(document, vehicleElement, "fuelAmount",
                    String.valueOf(combustionVehicle.getFuelAmount()));
        }

        return vehicleElement;
    }

    private static String tagName(Vehicle vehicle) {
        if (vehicle instanceof Car) {
            return "car";
        }
        if (vehicle instanceof Motorboat) {
            return "motorboat";
        }
        if (vehicle instanceof Bicycle) {
            return "bicycle";
        }
        return "scooter";
    }

    private static void appendText(Document document, Element parent, String tagName, String value) {
        Element child = document.createElement(tagName);
        child.appendChild(document.createTextNode(value));
        parent.appendChild(child);
    }

    private static String getText(Element parent, String tagName, String fallback) {
        NodeList nodes = parent.getElementsByTagName(tagName);
        if (nodes.getLength() == 0 || nodes.item(0).getTextContent() == null) {
            return fallback;
        }
        String text = nodes.item(0).getTextContent().trim();
        return text.isEmpty() ? fallback : text;
    }

    private static int parseInt(String value) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException exception) {
            return 0;
        }
    }

    private static double parseDouble(String value) {
        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException exception) {
            return 0.0;
        }
    }
}
