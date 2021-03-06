package com.pingala.parkeasy.models;

import java.util.ArrayList;

/**
 * Created by Habeeb on 1/24/2017.
 */

public class Slot extends ArrayList<Slot> {

    boolean booked=false;
    String carNo;
    String slotName;
    String time;
    String admins;
    String timestamp;

    public String getAdmins() {
        return admins;
    }

    public void setAdmins(String admins) {
        this.admins = admins;
    }

    public Slot() {
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public Slot(boolean booked, String carNo, String slotName, String time) {
        this.booked = booked;
        this.carNo = carNo;
        this.slotName = slotName;
        this.time = time;
    }

    public boolean isBooked() {
        return booked;
    }

    public void setBooked(boolean booked) {
        this.booked = booked;
    }

    public String getCarNo() {
        return carNo;
    }

    public void setCarNo(String carNo) {
        this.carNo = carNo;
    }

    public String getSlotName() {
        return slotName;
    }

    public void setSlotName(String slotName) {
        this.slotName = slotName;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
