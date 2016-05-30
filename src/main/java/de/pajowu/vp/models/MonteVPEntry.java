package de.pajowu.vp.models;

public class MonteVPEntry {

    public String changed_class, hour, subject, teacher, room, desc;
    public Boolean real = true;

    MonteVPEntry(String cc, String d) {
        changed_class = cc;
        desc = d;
        real = false;
    }
}