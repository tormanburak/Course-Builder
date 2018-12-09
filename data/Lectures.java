/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package oh.data;

import javafx.beans.property.StringProperty;

/**
 *
 * @author Burak Torman
 */
public class Lectures {
    public  String section;
    public  String room;
    public  String days;
    public  String time;

    public void setSection(String section) {
        this.section = section;
    }

    public void setRoom(String room) {
        this.room = room;
    }

    public void setDays(String days) {
        this.days = days;
    }

    public void setTime(String time) {
        this.time = time;
    }
    
    public Lectures(String initSection, String initRoom, String initTime, String initDays){
     section = initSection;  
     room = initRoom;
     days = initDays;
     time = initTime;
    }

    public String getSection() {
        return section;
    }

    public String getRoom() {
        return room;
    }

    public String getDays() {
        return days;
    }

    public String getTime() {
        return time;
    }
    
}
