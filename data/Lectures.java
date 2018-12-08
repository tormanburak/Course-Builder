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
    public  StringProperty section;
    public  StringProperty room;
    public  StringProperty days;
    public  StringProperty time;

    public void setSection(StringProperty section) {
        this.section = section;
    }

    public void setRoom(StringProperty room) {
        this.room = room;
    }

    public void setDays(StringProperty days) {
        this.days = days;
    }

    public void setTime(StringProperty time) {
        this.time = time;
    }
    
    public Lectures(StringProperty initSection, StringProperty initRoom, StringProperty initTime, StringProperty initDays){
     section = initSection;  
     room = initRoom;
     days = initDays;
     time = initTime;
    }

    public StringProperty getSection() {
        return section;
    }

    public StringProperty getRoom() {
        return room;
    }

    public StringProperty getDays() {
        return days;
    }

    public StringProperty getTime() {
        return time;
    }
    
}
