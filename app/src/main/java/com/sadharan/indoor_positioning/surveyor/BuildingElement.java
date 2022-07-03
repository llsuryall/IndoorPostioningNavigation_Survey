package com.sadharan.indoor_positioning.surveyor;

public class BuildingElement {
    public long id;
    public String name;
    public String address;

    BuildingElement(long id, String name, String address) throws IllegalArgumentException {
        this.setId(id);
        this.setName(name);
        this.setAddress(address);
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setName(String name) throws IllegalArgumentException {
        if (name.length() > 0) {
            this.name = name;
        } else {
            throw new IllegalArgumentException("Empty string 'name'!");
        }
    }

    public void setAddress(String address) throws IllegalArgumentException {
        if (name.length() > 0) {
            this.address = address;
        } else {
            throw new IllegalArgumentException("Empty string 'address'!");
        }
    }
}
