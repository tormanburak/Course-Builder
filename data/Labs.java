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
public class Labs extends Lectures {
    public StringProperty daysTime;
    public StringProperty ta1;
    public StringProperty ta2;
    
    public Labs(StringProperty initta1,StringProperty initta2,StringProperty initDaysTime, StringProperty initSection, StringProperty initRoom, StringProperty initTime, StringProperty initDays){
       super(initSection,initRoom,initTime,initDays);
        section = initSection;
        room = initRoom;
        daysTime = initDaysTime;
        ta1 = initta1;
        ta2 = initta2;
    }

    public StringProperty getDaysTime() {
        return daysTime;
    }

    public StringProperty getTa1() {
        return ta1;
    }

    public StringProperty getTa2() {
        return ta2;
    }

    public void setDaysTime(StringProperty daysTime) {
        this.daysTime = daysTime;
    }

    public void setTa1(StringProperty ta1) {
        this.ta1 = ta1;
    }

    public void setTa2(StringProperty ta2) {
        this.ta2 = ta2;
    }
    
    
}
