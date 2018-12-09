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
    public String daysTime;
    public String ta1;
    public String ta2;
    
    public Labs(String initta1,String initta2,String initDaysTime, String initSection,
            String initRoom, String initTime, String initDays){
       super(initSection,initRoom,initTime,initDays);
        section = initSection;
        room = initRoom;
        daysTime = initDaysTime;
        ta1 = initta1;
        ta2 = initta2;
    }

    public String getDaysTime() {
        return daysTime;
    }

    public String getTa1() {
        return ta1;
    }

    public String getTa2() {
        return ta2;
    }

    public void setDaysTime(String daysTime) {
        this.daysTime = daysTime;
    }

    public void setTa1(String ta1) {
        this.ta1 = ta1;
    }

    public void setTa2(String ta2) {
        this.ta2 = ta2;
    }
    
    
}
