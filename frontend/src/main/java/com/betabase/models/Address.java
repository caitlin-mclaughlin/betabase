package com.betabase.models;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Address {
    private final StringProperty streetAddress = new SimpleStringProperty();
    private final StringProperty apartmentNumber = new SimpleStringProperty(); // Optional
    private final StringProperty city = new SimpleStringProperty();
    private final StringProperty state = new SimpleStringProperty();
    private final StringProperty zipCode = new SimpleStringProperty();
//    private final StringProperty country = new SimpleStringProperty(); // Optional, especially for international addresses

    // Constructors
    public Address() {}

    public Address(String streetAddress, String city, String state, String zipCode/* , String country */) {
        this.streetAddress.set(streetAddress);
        this.city.set(city);
        this.state.set(state);
        this.zipCode.set(zipCode);
//        this.country.set(country);
    }

    public Address(String streetAddress, String apartmentNumber, String city, String state, String zipCode/* , String country */) {
        this(streetAddress, city, state, zipCode/* , country */); // Call primary constructor
        this.apartmentNumber.set(apartmentNumber);
    }

    // Getters and setters
    public String getStreetAddress() { return streetAddress.get(); }
    public void setStreetAddress(String streetAddress) { this.streetAddress.set(streetAddress); }
    public StringProperty streetAddressProperty() { return streetAddress; }

    public String getApartmentNumber() { return apartmentNumber.get(); }
    public void setApartmentNumber(String apartmentNumber) { this.apartmentNumber.set(apartmentNumber); }
    public StringProperty apartmentNumberProperty() { return apartmentNumber; }

    public String getCity() { return city.get(); }
    public void setCity(String city) { this.city.set(city); }
    public StringProperty cityProperty() { return city; }

    public String getState() { return state.get(); }
    public void setState(String state) { this.state.set(state); }
    public StringProperty stateProperty() { return state; }

    public String getZipCode() { return zipCode.get(); }
    public void setZipCode(String zipCode) { this.zipCode.set(zipCode); }
    public StringProperty zipCodeProperty() { return zipCode; }

/*    public String getCountry() { return country.get(); }
    public void setCountry(String country) { this.country.set(country); }
    public StringProperty countryProperty() { return country; } */

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(streetAddress).append(", ");
        if (apartmentNumber != null && !apartmentNumber.get().isEmpty()) {
            sb.append("Apt ").append(apartmentNumber);
        }
        sb.append(city).append(", ").append(state).append(" ").append(zipCode);
 /*       if (country != null && !country.get().isEmpty()) {
            sb.append(", ").append(country);
        } */
        return sb.toString();
    }

    public String toStringLine1() {
        StringBuilder sb = new StringBuilder();
        sb.append(streetAddress);
        if (apartmentNumber != null && !apartmentNumber.get().isEmpty()) {
            sb.append(", Apt ").append(apartmentNumber);
        }
        return sb.toString();
    }
    
    public String toStringLine2() {
        StringBuilder sb = new StringBuilder();
        sb.append(city).append(", ").append(state).append(" ").append(zipCode);
/*        if (country != null && !country.get().isEmpty()) {
            sb.append(", ").append(country);
        } */
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Address address = (Address) o;
        return streetAddress.equals(address.streetAddress) &&
               city.equals(address.city) &&
               state.equals(address.state) &&
               zipCode.equals(address.zipCode) &&
               (apartmentNumber == null ? address.apartmentNumber == null : apartmentNumber.equals(address.apartmentNumber))/* &&
               (country == null ? address.country == null : country.equals(address.country)) */;
    }
}
