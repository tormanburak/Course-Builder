package oh.data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * This class stores information for a single TA-Day/Time mapping for our office
 * hours grid. It's useful to provide a list of these objects for file I/O.
 *
 * @author Richard McKenna
 */
public class TimeSlot {

    public enum DayOfWeek {
        MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY
    }
    private StringProperty startTime;
    private StringProperty endTime;
    private HashMap<DayOfWeek, ArrayList<TeachingAssistantPrototype>> tas;
    private HashMap<DayOfWeek, StringProperty> dayText;

    public TimeSlot(String initStartTime, String initEndTime) {
        startTime = new SimpleStringProperty(initStartTime);
        endTime = new SimpleStringProperty(initEndTime);
        tas = new HashMap();
        dayText = new HashMap();
        for (DayOfWeek dow : DayOfWeek.values()) {
            tas.put(dow, new ArrayList());
            dayText.put(dow, new SimpleStringProperty());
        }
    }

    // ACCESSORS AND MUTATORS
    public String getStartTime() {
        return startTime.getValue();
    }

    public void setStartTime(String initStartTime) {
        startTime.setValue(initStartTime);
    }

    public StringProperty startTimeProperty() {
        return startTime;
    }

    public String getEndTime() {
        return endTime.getValue();
    }

    public void setEndTime(String initEndTime) {
        endTime.setValue(initEndTime);
    }

    public StringProperty endTimeProperty() {
        return endTime;
    }

    public String getMonday() {
        return dayText.get(DayOfWeek.MONDAY).getValue();
    }

    public void setMonday(String initMonday) {
        dayText.get(DayOfWeek.MONDAY).setValue(initMonday);
    }

    public StringProperty mondayProperty() {
        return this.dayText.get(DayOfWeek.MONDAY);
    }

    public String getTuesday() {
        return dayText.get(DayOfWeek.TUESDAY).getValue();
    }

    public void setTuesday(String initTuesday) {
        dayText.get(DayOfWeek.TUESDAY).setValue(initTuesday);
    }

    public StringProperty tuesdayProperty() {
        return this.dayText.get(DayOfWeek.TUESDAY);
    }

    public String getWednesday() {
        return dayText.get(DayOfWeek.WEDNESDAY).getValue();
    }

    public void setWednesday(String initWednesday) {
        dayText.get(DayOfWeek.WEDNESDAY).setValue(initWednesday);
    }

    public StringProperty wednesdayProperty() {
        return this.dayText.get(DayOfWeek.WEDNESDAY);
    }

    public String getThursday() {
        return dayText.get(DayOfWeek.THURSDAY).getValue();
    }

    public void setThursday(String initThursday) {
        dayText.get(DayOfWeek.THURSDAY).setValue(initThursday);
    }

    public StringProperty thursdayProperty() {
        return this.dayText.get(DayOfWeek.THURSDAY);
    }

    public String getFriday() {
        return dayText.get(DayOfWeek.FRIDAY).getValue();
    }

    public void setFriday(String initFriday) {
        dayText.get(DayOfWeek.FRIDAY).setValue(initFriday);
    }

    public StringProperty fridayProperty() {
        return this.dayText.get(DayOfWeek.FRIDAY);
    }

    // SERVICE METHODS
    public void addTA(DayOfWeek dow, TeachingAssistantPrototype ta) {
        ArrayList<TeachingAssistantPrototype> tasList = tas.get(dow);
        TAType currentType = TAType.valueOf(ta.getType());
        if (!tasList.contains(ta)) {
            tasList.add(ta);
            Collections.sort(tasList);
            updateDayText(dow, currentType);
        }
    }
    
    public void removeTA(TeachingAssistantPrototype ta) {
        for (DayOfWeek dow : DayOfWeek.values()) {
            removeTA(dow, ta);
        }
    }

    public void removeTA(DayOfWeek dow, TeachingAssistantPrototype ta) {
        ArrayList<TeachingAssistantPrototype> tasList = tas.get(dow);
        TAType currentType = TAType.valueOf(ta.getType());
        if (tasList.contains(ta)) {
            tasList.remove(ta);
            updateDayText(dow, currentType);
        }
    }

    public void filter(TAType type) {
        for (DayOfWeek dow : DayOfWeek.values()) {
            updateDayText(dow, type);
        }
    }
    public void filter(int a, int b){
        
    }

    public void updateDayText(DayOfWeek dow, TAType currentType) {
        StringProperty text = dayText.get(dow);
        ArrayList<TeachingAssistantPrototype> taList = this.tas.get(dow);
        String taText = "";
        int counter = 0;
        for (TeachingAssistantPrototype ta : taList) {
            if (currentType.equals(TAType.All) 
                    || ta.getType().equals(currentType.toString())) {
                taText += ta.getName();
                if (counter < (taList.size() - 1)) {
                    taText += "\n";
                }
                counter++;
            }
        }
        text.set(taText);
    }

    public void reset() {
        for (DayOfWeek dow : DayOfWeek.values()) {
            tas.get(dow).clear();
            StringProperty text = dayText.get(dow);
            text.setValue("");
        }
    }

    public void toggleTA(DayOfWeek dow, TeachingAssistantPrototype ta) {
        ArrayList<TeachingAssistantPrototype> taList = tas.get(dow);
        if (taList.contains(ta)) {
            removeTA(dow, ta);
            ta.changeTimeSlotCount(-1);
        } else {
            addTA(dow, ta);
            ta.changeTimeSlotCount(1);
        }
    }

    public Iterator<TeachingAssistantPrototype> getTAsIterator(DayOfWeek dow) {
        return tas.get(dow).iterator();
    }
    
    public boolean hasTA(TeachingAssistantPrototype ta, DayOfWeek dow) {
        return tas.get(dow).contains(ta);
    }
    
    public boolean hasTA(TeachingAssistantPrototype ta) {
        for (DayOfWeek dow : DayOfWeek.values()) {
            if (hasTA(ta, dow))
                return true;
        }
        return false;
    }

    public ArrayList<DayOfWeek> getDaysForTA(TeachingAssistantPrototype ta) {
        if (!hasTA(ta)) {
            return null;
        }
        else {
            ArrayList<DayOfWeek> days = new ArrayList();
            for (DayOfWeek dow : DayOfWeek.values()) {
                if (hasTA(ta, dow)) {
                    days.add(dow);
                }
            }
            return days;
        }
    }
}
