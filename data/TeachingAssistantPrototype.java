package oh.data;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * This class represents a Teaching Assistant for the table of TAs.
 * 
 * @author Richard McKenna
 */
public class TeachingAssistantPrototype<E extends Comparable<E>> implements Comparable<E>  {
    // THE TABLE WILL STORE TA NAMES AND EMAILS
    private final StringProperty name;
    private final StringProperty email;
    private final StringProperty slots;
    private final StringProperty type;

    /**
     * Constructor initializes both the TA name and email.
     */
    public TeachingAssistantPrototype(String initName, String initEmail, TAType initType) {
        name = new SimpleStringProperty(initName);
        email = new SimpleStringProperty(initEmail);
        slots = new SimpleStringProperty("" + 0);
        type = new SimpleStringProperty(initType.toString());
    }

    // ACCESSORS AND MUTATORS FOR THE PROPERTIES

    public String getName() {
        return name.get();
    }

    public void setName(String initName) {
        name.set(initName);
    }

    public String getEmail() {
        return email.get();
    }

    public void setEmail(String initEmail) {
        email.set(initEmail);
    }
    
    public String getSlots() {
        return slots.get();
    }
    
    public void setSlots(String initSlots) {
        slots.setValue(initSlots);
    }
    
    public StringProperty slotsProperty() {
        return slots;
    }
    
    public String getType() {
        return type.get();
    }
    
    public void setType(String initType) {
        type.setValue(initType);
    }
    
    public StringProperty typeProperty() {
        return type;
    }

    @Override
    public int compareTo(E otherTA) {
        return getName().compareTo(((TeachingAssistantPrototype)otherTA).getName());
    }
    
    @Override
    public String toString() {
        return name.getValue();
    }
    
    @Override
    public boolean equals(Object testTA) {
        return name.getValue().toLowerCase().equals(((TeachingAssistantPrototype)testTA).name.getValue().toLowerCase().trim())
                || email.getValue().toLowerCase().equals(((TeachingAssistantPrototype)testTA).email.getValue().toLowerCase().trim());
    }

    public void changeTimeSlotCount(int i) {
        int count = Integer.parseInt(this.slots.get());
        count += i;
        this.setSlots("" + count);
    }
    
    public void changeType(TAType initType) {
        type.set(initType.toString());
    }
    
    public E clone() {
        TeachingAssistantPrototype ta = new TeachingAssistantPrototype(
                this.getName(), this.getEmail(), TAType.valueOf(this.getType()));
        ta.setSlots(this.getSlots());
        return (E)ta;
    }
}