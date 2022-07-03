package com.sadharan.indoor_positioning.surveyor;

public class BlockElement {
    public long id, building_id;
    public String name;
    public float latitude, longitude;

    BlockElement(long id, long building_id, String name, float latitude, float longitude) throws IllegalArgumentException {
        this.setId(id);
        this.building_id = building_id;
        setName(name);
        setLatitude(latitude);
        setLongitude(longitude);
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

    public void setLatitude(float latitude) throws IllegalArgumentException {
        if (latitude < -180 || latitude > 180) {
            throw new IllegalArgumentException("Invalid latitude!");
        } else {
            this.latitude = latitude;
        }
    }

    public void setLongitude(float longitude) throws IllegalArgumentException {
        if (latitude < -90 || latitude > 90) {
            throw new IllegalArgumentException("Invalid longitude!");
        } else {
            this.longitude = longitude;
        }
    }
}
