package com.betabase.models;

public class Address {
    private String streetNumber;
    private String streetName;
    private String apartmentNumber; // Optional
    private String city;
    private String state;
    private String zipCode;
    private String country; // Optional, especially for international addresses

    // Constructors
    public Address(String streetNumber, String streetName, String city, String state, String zipCode, String country) {
        this.streetNumber = streetNumber;
        this.streetName = streetName;
        this.city = city;
        this.state = state;
        this.zipCode = zipCode;
        this.country = country;
    }

    public Address(String streetNumber, String streetName, String apartmentNumber, String city, String state, String zipCode, String country) {
        this(streetNumber, streetName, city, state, zipCode, country); // Call primary constructor
        this.apartmentNumber = apartmentNumber;
    }

    // Getters and setters
    public String getStreetNumber() {
        return streetNumber;
    }
    public void setStreetNumber(String streetNumber) {
        this.streetNumber = streetNumber;
    }

    public String getStreetName() {
        return streetName;
    }
    public void setStreetName(String streetName) {
        this.streetName = streetName;
    }

    public String getStreetAddress() {
        return streetNumber + " " + streetName;
    }

    public String getApartmentNumber() {
        return apartmentNumber;
    }
    public void setApartmentNumber(String apartmentNumber) {
        this.apartmentNumber = apartmentNumber;
    }

    public String getCity() {
        return city;
    }
    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }
    public void setState(String state) {
        this.state = state;
    }

    public String getZipCode() {
        return zipCode;
    }
    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    public String getCountry() {
        return country;
    }
    public void setCountry(String country) {
        this.country = country;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(streetNumber).append(" ").append(streetName);
        if (apartmentNumber != null && !apartmentNumber.isEmpty()) {
            sb.append(", Apt ").append(apartmentNumber);
        }
        sb.append(", ").append(city).append(", ").append(state).append(" ").append(zipCode);
        if (country != null && !country.isEmpty()) {
            sb.append(", ").append(country);
        }
        return sb.toString();
    }

    public String toStringLine1() {
        StringBuilder sb = new StringBuilder();
        sb.append(streetNumber).append(" ").append(streetName);
        if (apartmentNumber != null && !apartmentNumber.isEmpty()) {
            sb.append(", Apt ").append(apartmentNumber);
        }
        return sb.toString();
    }
    
    public String toStringLine2() {
        StringBuilder sb = new StringBuilder();
        sb.append(city).append(", ").append(state).append(" ").append(zipCode);
        if (country != null && !country.isEmpty()) {
            sb.append(", ").append(country);
        }
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Address address = (Address) o;
        return streetNumber.equals(address.streetNumber) &&
               streetName.equals(address.streetName) &&
               city.equals(address.city) &&
               state.equals(address.state) &&
               zipCode.equals(address.zipCode) &&
               (apartmentNumber == null ? address.apartmentNumber == null : apartmentNumber.equals(address.apartmentNumber)) &&
               (country == null ? address.country == null : country.equals(address.country));
    }
}
