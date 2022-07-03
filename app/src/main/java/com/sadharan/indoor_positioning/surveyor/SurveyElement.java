package com.sadharan.indoor_positioning.surveyor;

import java.util.LinkedList;

public class SurveyElement {
    public int floor_id;
    public long block_id;
    public float x_coordinate, y_coordinate;
    public LinkedList<APSignalStrength> apSignalStrengths;

    SurveyElement(int floor_id, long block_id, float x_coordinate, float y_coordinate, LinkedList<APSignalStrength> apSignalStrengths) throws IllegalArgumentException {
        this.floor_id=floor_id;
        setBlock_id(block_id);
        setX_coordinate(x_coordinate);
        setY_coordinate(y_coordinate);
        setAPSignalStrengths(apSignalStrengths);
    }

    public void setBlock_id(long block_id) {
        this.block_id = block_id;
    }

    public void setX_coordinate(float x_coordinate) {
        this.x_coordinate = x_coordinate;
    }

    public void setY_coordinate(float y_coordinate) {
        this.y_coordinate = y_coordinate;
    }

    public void setAPSignalStrengths(LinkedList<APSignalStrength> apSignalStrengths) throws IllegalArgumentException {
        if (apSignalStrengths.size() > 0) {
            this.apSignalStrengths = apSignalStrengths;
        } else {
            throw new IllegalArgumentException("Empty Signal Strength List!");
        }
    }
}
