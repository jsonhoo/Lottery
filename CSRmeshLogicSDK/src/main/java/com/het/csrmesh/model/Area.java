/******************************************************************************
 * Copyright 2015 Qualcomm Technologies International, Ltd.
 ******************************************************************************/

package com.het.csrmesh.model;

import java.io.Serializable;

/**
 *
 */
public class Area implements Serializable {

    private int id = -1;
    private String areaID;
    private String name;
    private String placeID;
    private boolean isFavorite = false;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAreaID() {
        return areaID;
    }

    public void setAreaID(String areaID) {
        this.areaID = areaID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPlaceID() {
        return placeID;
    }

    public void setPlaceID(String placeID) {
        this.placeID = placeID;
    }

    public boolean isFavorite() {
        return isFavorite;
    }

    public void setIsFavorite(boolean isFavorite) {
        this.isFavorite = isFavorite;
    }

    @Override
    public boolean equals(Object object) {
        boolean isEqual = false;

        if (object != null && object instanceof Area) {
            isEqual = (this.id == ((Area) object).id);
        }

        return isEqual;
    }

    @Override
    public int hashCode() {
        return this.id;
    }
}
