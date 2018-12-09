package oh.data;

import javafx.collections.ObservableList;
import djf.components.AppDataComponent;
import djf.modules.AppGUIModule;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javafx.collections.FXCollections;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TableView;
import oh.OfficeHoursApp;
import static oh.OfficeHoursPropertyType.OH_ALL_RADIO_BUTTON;
import static oh.OfficeHoursPropertyType.OH_GRAD_RADIO_BUTTON;
import static oh.OfficeHoursPropertyType.OH_LABSTABLEVIEW;
import static oh.OfficeHoursPropertyType.OH_LECTURE;
import static oh.OfficeHoursPropertyType.OH_LECTURETABLEVIEW;
import static oh.OfficeHoursPropertyType.OH_OFFICE_HOURS_TABLE_VIEW;
import static oh.OfficeHoursPropertyType.OH_RECITATIONTABLEVIEW;
import static oh.OfficeHoursPropertyType.OH_TAS_TABLE_VIEW;
import oh.data.TimeSlot.DayOfWeek;

/**
 * This is the data component for TAManagerApp. It has all the data needed
 * to be set by the user via the User Interface and file I/O can set and get
 * all the data from this object
 * 
 * @author Richard McKenna
 */
public class OfficeHoursData implements AppDataComponent {

    // WE'LL NEED ACCESS TO THE APP TO NOTIFY THE GUI WHEN DATA CHANGES
    OfficeHoursApp app;
    
    // THESE ARE ALL THE TEACHING ASSISTANTS
    HashMap<TAType, ArrayList<TeachingAssistantPrototype>> allTAs;

    // NOTE THAT THIS DATA STRUCTURE WILL DIRECTLY STORE THE
    // DATA IN THE ROWS OF THE TABLE VIEW
    ObservableList<TeachingAssistantPrototype> teachingAssistants;
    ObservableList<TimeSlot> officeHours;
    ObservableList<TimeSlot> tempHours;
    ObservableList<Lectures> lectures;
    ObservableList<Recitations> recitations;
    ObservableList<Labs> labs;
    // THESE ARE THE TIME BOUNDS FOR THE OFFICE HOURS GRID. NOTE
    // THAT THESE VALUES CAN BE DIFFERENT FOR DIFFERENT FILES, BUT
    // THAT OUR APPLICATION USES THE DEFAULT TIME VALUES AND PROVIDES
    // NO MEANS FOR CHANGING THESE VALUES
    int startHour;
    int endHour;
    
    // DEFAULT VALUES FOR START AND END HOURS IN MILITARY HOURS
    public static final int MIN_START_HOUR = 9;
    public static final int MAX_END_HOUR = 20;

    /**
     * This constructor will setup the required data structures for
     * use, but will have to wait on the office hours grid, since
     * it receives the StringProperty objects from the Workspace.
     * 
     * @param initApp The application this data manager belongs to. 
     */
    public OfficeHoursData(OfficeHoursApp initApp) {
        // KEEP THIS FOR LATER
        app = initApp;
        AppGUIModule gui = app.getGUIModule();

        // SETUP THE DATA STRUCTURES
        allTAs = new HashMap();
        allTAs.put(TAType.Graduate, new ArrayList());
        allTAs.put(TAType.Undergraduate, new ArrayList());
        
        // GET THE LIST OF TAs FOR THE LEFT TABLE
        TableView<TeachingAssistantPrototype> taTableView = (TableView)gui.getGUINode(OH_TAS_TABLE_VIEW);
        teachingAssistants = taTableView.getItems();
        
        TableView<Lectures> lectureTableView = (TableView<Lectures>) gui.getGUINode(OH_LECTURETABLEVIEW);
        lectures = lectureTableView.getItems();
        
        TableView<Recitations> recTableView = (TableView<Recitations>) gui.getGUINode(OH_RECITATIONTABLEVIEW);
        recitations = recTableView.getItems();
        
        TableView<Labs> labsTableView = (TableView<Labs>) gui.getGUINode(OH_LABSTABLEVIEW);
        labs = labsTableView.getItems();

        // THESE ARE THE DEFAULT OFFICE HOURS
        startHour = MIN_START_HOUR;
        endHour = MAX_END_HOUR;
        
        resetOfficeHours();
    }
    
    // ACCESSOR METHODS

    public int getStartHour() {
        return startHour;
    }

    public int getEndHour() {
        return endHour;
    }
    
    // PRIVATE HELPER METHODS
    
    private void sortTAs() {
        Collections.sort(teachingAssistants);
    }
    
    private void resetOfficeHours() {
        //THIS WILL STORE OUR OFFICE HOURS
        AppGUIModule gui = app.getGUIModule();
        TableView<TimeSlot> officeHoursTableView = (TableView)gui.getGUINode(OH_OFFICE_HOURS_TABLE_VIEW);
        officeHours = officeHoursTableView.getItems(); 
        officeHours.clear();
        for (int i = startHour; i <= endHour; i++) {
            TimeSlot timeSlot = new TimeSlot(   this.getTimeString(i, true),
                                                this.getTimeString(i, false));
            officeHours.add(timeSlot);
            
            TimeSlot halfTimeSlot = new TimeSlot(   this.getTimeString(i, false),
                                                    this.getTimeString(i+1, true));
            officeHours.add(halfTimeSlot);
        }
    }
    
    private String getTimeString(int militaryHour, boolean onHour) {
        String minutesText = "00";
        if (!onHour) {
            minutesText = "30";
        }

        // FIRST THE START AND END CELLS
        int hour = militaryHour;
        if (hour > 12) {
            hour -= 12;
        }
        String cellText = "" + hour + ":" + minutesText;
        if (militaryHour < 12) {
            cellText += "am";
        } else {
            cellText += "pm";
        }
        return cellText;
    }
    
    // METHODS TO OVERRIDE
        
    /**
     * Called each time new work is created or loaded, it resets all data
     * and data structures such that they can be used for new values.
     */
    @Override
    public void reset() {
        startHour = MIN_START_HOUR;
        endHour = MAX_END_HOUR;
        teachingAssistants.clear();
        
        for (TimeSlot timeSlot : officeHours) {
            timeSlot.reset();
        }
    }
    
    // SERVICE METHODS
    
    public void initHours(String startHourText, String endHourText) {
        int initStartHour = Integer.parseInt(startHourText);
        int initEndHour = Integer.parseInt(endHourText);
        if (initStartHour <= initEndHour) {
            // THESE ARE VALID HOURS SO KEEP THEM
            // NOTE THAT THESE VALUES MUST BE PRE-VERIFIED
            startHour = initStartHour;
            endHour = initEndHour;
        }
        resetOfficeHours();
    }
    
    public void addTA(TeachingAssistantPrototype ta) {
        if (!hasTA(ta)) {
            TAType taType = TAType.valueOf(ta.getType());
            ArrayList<TeachingAssistantPrototype> tas = allTAs.get(taType);
            tas.add(ta);
            this.updateTAs();
        }
    }

    public void addTA(TeachingAssistantPrototype ta, HashMap<TimeSlot, ArrayList<DayOfWeek>> officeHours) {
        addTA(ta);
        for (TimeSlot timeSlot : officeHours.keySet()) {
            ArrayList<DayOfWeek> days = officeHours.get(timeSlot);
            for (DayOfWeek dow : days) {
                timeSlot.addTA(dow, ta);
            }
        }
    }
    
    public void removeTA(TeachingAssistantPrototype ta) {
        // REMOVE THE TA FROM THE LIST OF TAs
        TAType taType = TAType.valueOf(ta.getType());
        allTAs.get(taType).remove(ta);
        
        // REMOVE THE TA FROM ALL OF THEIR OFFICE HOURS
        for (TimeSlot timeSlot : officeHours) {
            timeSlot.removeTA(ta);
        }
        
        // AND REFRESH THE TABLES
        this.updateTAs();
    }

    public void removeTA(TeachingAssistantPrototype ta, HashMap<TimeSlot, ArrayList<DayOfWeek>> officeHours) {
        removeTA(ta);
        for (TimeSlot timeSlot : officeHours.keySet()) {
            ArrayList<DayOfWeek> days = officeHours.get(timeSlot);
            for (DayOfWeek dow : days) {
                timeSlot.removeTA(dow, ta);
            }
        }    
    }
    
    public DayOfWeek getColumnDayOfWeek(int columnNumber) {
        return TimeSlot.DayOfWeek.values()[columnNumber-2];
    }

    public TeachingAssistantPrototype getTAWithName(String name) {
        Iterator<TeachingAssistantPrototype> taIterator = teachingAssistants.iterator();
        while (taIterator.hasNext()) {
            TeachingAssistantPrototype ta = taIterator.next();
            if (ta.getName().equals(name))
                return ta;
        }
        return null;
    }

    public TeachingAssistantPrototype getTAWithEmail(String email) {
        Iterator<TeachingAssistantPrototype> taIterator = teachingAssistants.iterator();
        while (taIterator.hasNext()) {
            TeachingAssistantPrototype ta = taIterator.next();
            if (ta.getEmail().equals(email))
                return ta;
        }
        return null;
    }

    public TimeSlot getTimeSlot(String startTime) {
        Iterator<TimeSlot> timeSlotsIterator = officeHours.iterator();
        while (timeSlotsIterator.hasNext()) {
            TimeSlot timeSlot = timeSlotsIterator.next();
            String timeSlotStartTime = timeSlot.getStartTime().replace(":", "_");
            if (timeSlotStartTime.equals(startTime))
                return timeSlot;
        }
        return null;
    }

    public TAType getSelectedType() {
        RadioButton allRadio = (RadioButton)app.getGUIModule().getGUINode(OH_ALL_RADIO_BUTTON);
        if (allRadio.isSelected())
            return TAType.All;
        RadioButton gradRadio = (RadioButton)app.getGUIModule().getGUINode(OH_GRAD_RADIO_BUTTON);
        if (gradRadio.isSelected())
            return TAType.Graduate;
        else
            return TAType.Undergraduate;
    }

    public TeachingAssistantPrototype getSelectedTA() {
        AppGUIModule gui = app.getGUIModule();
        TableView<TeachingAssistantPrototype> tasTable = (TableView)gui.getGUINode(OH_TAS_TABLE_VIEW);
        return tasTable.getSelectionModel().getSelectedItem();
    }
    
    public HashMap<TimeSlot, ArrayList<DayOfWeek>> getTATimeSlots(TeachingAssistantPrototype ta) {
        HashMap<TimeSlot, ArrayList<DayOfWeek>> timeSlots = new HashMap();
        for (TimeSlot timeSlot : officeHours) {
            if (timeSlot.hasTA(ta)) {
                ArrayList<DayOfWeek> daysForTA = timeSlot.getDaysForTA(ta);
                timeSlots.put(timeSlot, daysForTA);
            }
        }
        return timeSlots;
    }
    
    private boolean hasTA(TeachingAssistantPrototype testTA) {
        return allTAs.get(TAType.Graduate).contains(testTA)
                ||
                allTAs.get(TAType.Undergraduate).contains(testTA);
    }
    
    public boolean isTASelected() {
        AppGUIModule gui = app.getGUIModule();
        TableView tasTable = (TableView)gui.getGUINode(OH_TAS_TABLE_VIEW);
        return tasTable.getSelectionModel().getSelectedItem() != null;
    }

    public boolean isLegalNewTA(String name, String email) {
        if ((name.trim().length() > 0)
                && (email.trim().length() > 0)) {
            // MAKE SURE NO TA ALREADY HAS THE SAME NAME
            TAType type = this.getSelectedType();
            TeachingAssistantPrototype testTA = new TeachingAssistantPrototype(name, email, type);
            if (this.teachingAssistants.contains(testTA))
                return false;
            if (this.isLegalNewEmail(email)) {
                return true;
            }
        }
        return false;
    }
    
    public boolean isLegalNewName(String testName) {
        if (testName.trim().length() > 0) {
            for (TeachingAssistantPrototype testTA : this.teachingAssistants) {
                if (testTA.getName().equals(testName))
                    return false;
            }
            return true;
        }
        return false;
    }
    
    public boolean isLegalNewEmail(String email) {
        Pattern VALID_EMAIL_ADDRESS_REGEX = Pattern.compile(
                "^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);
        Matcher matcher = VALID_EMAIL_ADDRESS_REGEX .matcher(email);
        if (matcher.find()) {
            for (TeachingAssistantPrototype ta : this.teachingAssistants) {
                if (ta.getEmail().equals(email.trim()))
                    return false;
            }
            return true;
        }
        else return false;
    }
    
    public boolean isDayOfWeekColumn(int columnNumber) {
        return columnNumber >= 2;
    }
    
    public boolean isTATypeSelected() {
        AppGUIModule gui = app.getGUIModule();
        RadioButton allRadioButton = (RadioButton)gui.getGUINode(OH_ALL_RADIO_BUTTON);
        return !allRadioButton.isSelected();
    }
    
    public boolean isValidTAEdit(TeachingAssistantPrototype taToEdit, String name, String email) {
        if (!taToEdit.getName().equals(name)) {
            if (!this.isLegalNewName(name))
                return false;
        }
        if (!taToEdit.getEmail().equals(email)) {
            if (!this.isLegalNewEmail(email))
                return false;
        }
        return true;
    }

    public boolean isValidNameEdit(TeachingAssistantPrototype taToEdit, String name) {
        if (!taToEdit.getName().equals(name)) {
            if (!this.isLegalNewName(name))
                return false;
        }
        return true;
    }

    public boolean isValidEmailEdit(TeachingAssistantPrototype taToEdit, String email) {
        if (!taToEdit.getEmail().equals(email)) {
            if (!this.isLegalNewEmail(email))
                return false;
        }
        return true;
    }    

    public void updateTAs() {
        TAType type = getSelectedType();
        selectTAs(type);
    }
    
    public void selectTAs(TAType type) {
        teachingAssistants.clear();
        Iterator<TeachingAssistantPrototype> tasIt = this.teachingAssistantsIterator();
        while (tasIt.hasNext()) {
            TeachingAssistantPrototype ta = tasIt.next();
            if (type.equals(TAType.All)) {
                teachingAssistants.add(ta);
            }
            else if (ta.getType().equals(type.toString())) {
                teachingAssistants.add(ta);
            }
        }
        
        // SORT THEM BY NAME
        sortTAs();

        // CLEAR ALL THE OFFICE HOURS
        Iterator<TimeSlot> officeHoursIt = officeHours.iterator();
        while (officeHoursIt.hasNext()) {
            TimeSlot timeSlot = officeHoursIt.next();
            timeSlot.filter(type);
        }
        
        app.getFoolproofModule().updateAll();
    }
    
    public Iterator<TimeSlot> officeHoursIterator() {
        return officeHours.iterator();
    }

    public Iterator<TeachingAssistantPrototype> teachingAssistantsIterator() {
        return new AllTAsIterator();
    }
    
    private class AllTAsIterator implements Iterator {
        Iterator gradIt = allTAs.get(TAType.Graduate).iterator();
        Iterator undergradIt = allTAs.get(TAType.Undergraduate).iterator();

        public AllTAsIterator() {}
        
        @Override
        public boolean hasNext() {
            if (gradIt.hasNext() || undergradIt.hasNext())
                return true;
            else
                return false;                
        }

        @Override
        public Object next() {
            if (gradIt.hasNext())
                return gradIt.next();
            else if (undergradIt.hasNext())
                return undergradIt.next();
            else
                return null;
        }
    }
    public ObservableList<TimeSlot> getTimeSlot(){
        return officeHours;
    }
    public void updateTimeSlot(int a , int b){
                 AppGUIModule gui = app.getGUIModule();

               tempHours = FXCollections.observableArrayList();
               
           for(int i = a; i < b+1; i++){
               TimeSlot ts = officeHours.get(i);
               tempHours.add(ts);

               
           }
                   TableView<TimeSlot> officeHoursTableView = (TableView)gui.getGUINode(OH_OFFICE_HOURS_TABLE_VIEW);
                   officeHoursTableView.setItems(tempHours);
            
            
    }
    public void addLecture(Lectures lecture){
         lectures.add(lecture);
            }
    public void removeLecture(Lectures lecture){
        lectures.remove(lecture);
    }
    public void addRecitation(Recitations rec){
        recitations.add(rec);
    }
    public void removeRecitation(Recitations rec){
        recitations.remove(rec);
    }
    public void addLab(Labs lab){
        labs.add(lab);
    }
    public void removeLab(Labs lab){
        labs.remove(lab);
    }
}