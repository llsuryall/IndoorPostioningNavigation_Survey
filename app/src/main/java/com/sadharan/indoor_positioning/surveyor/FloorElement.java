package com.sadharan.indoor_positioning.surveyor;

public class FloorElement {
    public long block_id;
    public int id;
    public float height, width;

    FloorElement(int id, long block_id, float height, float width) throws IllegalArgumentException {
        setBlock_id(block_id);
        this.id=id;
        setHeight(height);
        setWidth(width);
    }

    public void setBlock_id(long block_id) {
        this.block_id = block_id;
    }

    public void setHeight(float height) {
        this.height = height;
    }

    public void setWidth(float width) {
        this.width = width;
    }
}
