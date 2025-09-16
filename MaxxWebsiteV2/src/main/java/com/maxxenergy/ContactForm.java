package com.maxxenergy;

public class ContactForm {
    private String name;
    private String email;
    private String phone;
    private String address;
    private String message;
    private String energyBill;

    // Constructors
    public ContactForm() {}

    public ContactForm(String name, String email, String phone, String address, String message, String energyBill) {
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.address = address;
        this.message = message;
        this.energyBill = energyBill;
    }

    // Getters and Setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public String getEnergyBill() { return energyBill; }
    public void setEnergyBill(String energyBill) { this.energyBill = energyBill; }
}