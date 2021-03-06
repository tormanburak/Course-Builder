package oh.files;

import static djf.AppPropertyType.ABOUT_DIALOG_ERROR_CONTENT;
import static djf.AppPropertyType.ABOUT_DIALOG_ERROR_TITLE;
import static djf.AppPropertyType.APP_EXPORT_PAGE;
import static djf.AppPropertyType.APP_PATH_WEB;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import djf.components.AppDataComponent;
import djf.components.AppFileComponent;
import djf.modules.AppGUIModule;
import djf.ui.dialogs.AppDialogsFacade;
import djf.ui.dialogs.AppWebDialog;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.math.BigInteger;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonWriter;
import javax.json.JsonWriterFactory;
import javax.json.stream.JsonGenerator;
import oh.OfficeHoursApp;
import static oh.OfficeHoursPropertyType.OH_BANNERNUMBER_COMBOBOX;
import static oh.OfficeHoursPropertyType.OH_BANNERSEMESTER_COMBOBOX;
import static oh.OfficeHoursPropertyType.OH_BANNERSUBJECT_COMBOBOX;
import static oh.OfficeHoursPropertyType.OH_BANNERYEAR_COMBOBOX;
import static oh.OfficeHoursPropertyType.OH_BANNER_TITLEFIELD;
import static oh.OfficeHoursPropertyType.OH_DESCRIPTIONTEXT_AREA;
import static oh.OfficeHoursPropertyType.OH_DISHONESTYTEXT_AREA;
import static oh.OfficeHoursPropertyType.OH_GRADEDCOMPONENTSTEXT_AREA;
import static oh.OfficeHoursPropertyType.OH_GRADINGNOTETEXT_AREA;
import static oh.OfficeHoursPropertyType.OH_INSTEMAIL_FIELD;
import static oh.OfficeHoursPropertyType.OH_INSTHOME_FIELD;
import static oh.OfficeHoursPropertyType.OH_INSTNAME_FIELD;
import static oh.OfficeHoursPropertyType.OH_INSTROOM_FIELD;
import static oh.OfficeHoursPropertyType.OH_INSTTEXTAREA;
import static oh.OfficeHoursPropertyType.OH_OUTCOMESTEXT_AREA;
import static oh.OfficeHoursPropertyType.OH_PREREQTEXT_AREA;
import static oh.OfficeHoursPropertyType.OH_SPECASSISTANCETEXT_AREA;
import static oh.OfficeHoursPropertyType.OH_TEXTBOOKTEXT_AREA;
import static oh.OfficeHoursPropertyType.OH_TOPICSTEXT_AREA;
import oh.data.Labs;
import oh.data.Lectures;
import oh.data.OfficeHoursData;
import oh.data.Recitations;
import oh.data.ScheduleItem;
import oh.data.TAType;
import oh.data.TeachingAssistantPrototype;
import oh.data.TimeSlot;
import oh.data.TimeSlot.DayOfWeek;
import properties_manager.PropertiesManager;

/**
 * This class serves as the file component for the TA
 * manager app. It provides all saving and loading 
 * services for the application.
 * 
 * @author Richard McKenna
 */
public class OfficeHoursFiles implements AppFileComponent {
    // THIS IS THE APP ITSELF
    OfficeHoursApp app;
    
    // THESE ARE USED FOR IDENTIFYING JSON TYPES
    static final String JSON_GRAD_TAS = "grad_tas";
    static final String JSON_UNDERGRAD_TAS = "undergrad_tas";
    static final String JSON_NAME = "name";
    static final String JSON_EMAIL = "email";
    static final String JSON_TYPE = "type";
    static final String JSON_OFFICE_HOURS = "officeHours";
    static final String JSON_START_HOUR = "startHour";
    static final String JSON_END_HOUR = "endHour";
    static final String JSON_START_TIME = "time";
    static final String JSON_DAY_OF_WEEK = "day";
    static final String JSON_MONDAY = "monday";
    static final String JSON_TUESDAY = "tuesday";
    static final String JSON_WEDNESDAY = "wednesday";
    static final String JSON_THURSDAY = "thursday";
    static final String JSON_FRIDAY = "friday";
    static final String JSON_LECTURES = "lectures";
    static final String JSON_RECITATIONS ="recitations";
    static final String JSON_LABS = "labs";
    static final String JSON_SCHEDULEITEMS ="scheduleItems";
    
    static final String JSON_SUBJECT= "subject";
    static final String JSON_NUMBER= "number";
    static final String JSON_SEMESTER= "semester";
    static final String JSON_YEAR= "year";
    static final String JSON_TITLE= "title";
    static final String JSON_INSTNAME= "name";
    static final String JSON_INSTEMAIL= "email";
    static final String JSON_INSTROOM= "room";
    static final String JSON_INSTHOME= "home";    
    static final String JSON_INSTAREA = "hours";
    static final String JSON_TITLEFIELD ="title";
    
    static final String JSON_DESCRIPTION= "description";
    static final String JSON_TOPICS= "topics";
    static final String JSON_PREREQ= "prerequisites";
    static final String JSON_OUTCOMES= "outcomes";
    static final String JSON_TEXTBOOKS= "textbooks";
    static final String JSON_GRADEDCOMPONENTS= "gradedcomponents";
    static final String JSON_GRADINGNOTE= "gradingnote";
    static final String JSON_DISHONESTY= "dishonesty";    
    static final String JSON_SPEC = "special assistance";  
    
    static final String JSON_LECSECTION = "section";
    static final String JSON_LECROOM = "room";
    static final String JSON_LECDAYS = "days";
    static final String JSON_LECTIME = "time";
    
    static final String JSON_RECSECTION = "section";
    static final String JSON_RECROOM = "room";
    static final String JSON_RECDAYS = "days";
    static final String JSON_RECTA1 = "ta1";
    static final String JSON_RECTA2 = "ta2";
    
    static final String JSON_LABSECTION = "section";
    static final String JSON_LABROOM = "room";
    static final String JSON_LABDAYS = "days";
    static final String JSON_LABTA1 = "ta1";
    static final String JSON_LABTA2 = "ta2";    
    
    static final String JSON_SCHEDULETYPE = "schedule";
    static final String JSON_SCHEDULEDATE = "date";
    static final String JSON_SCHEDULETITLE = "title";
    static final String JSON_SCHEDULETOPIC = "topic";

    
    public OfficeHoursFiles(OfficeHoursApp initApp) {
        app = initApp;
    }

    @Override
    public void loadData(AppDataComponent data, String filePath) throws IOException {
	// CLEAR THE OLD DATA OUT
	OfficeHoursData dataManager = (OfficeHoursData)data;
        AppGUIModule gui = app.getGUIModule();

        dataManager.reset();

	// LOAD THE JSON FILE WITH ALL THE DATA
	JsonObject json = loadJSONFile(filePath);

	// LOAD THE START AND END HOURS
	String startHour = json.getString(JSON_START_HOUR);
        String endHour = json.getString(JSON_END_HOUR);
        dataManager.initHours(startHour, endHour);
        
        // LOAD ALL THE GRAD TAs
        loadTAs(dataManager, json, JSON_GRAD_TAS);
        loadTAs(dataManager, json, JSON_UNDERGRAD_TAS);
        loadLectures(dataManager,json,JSON_LECTURES);
        loadRecitations(dataManager,json,JSON_RECITATIONS);
        loadLabs(dataManager,json,JSON_LABS);
        loadScheduleItem(dataManager,json,JSON_SCHEDULEITEMS);
        String s1 = json.getString(JSON_SUBJECT);
        String s2 = json.getString(JSON_YEAR);
        String s3 = json.getString(JSON_SEMESTER);
        String s4 = json.getString(JSON_NUMBER);
        
        ComboBox cb = (ComboBox) gui.getGUINode(OH_BANNERSUBJECT_COMBOBOX);
        cb.setValue(s1);
        
        ComboBox cb1 = (ComboBox) gui.getGUINode(OH_BANNERYEAR_COMBOBOX);
        cb1.setValue(s2);
        
        ComboBox cb2 = (ComboBox) gui.getGUINode(OH_BANNERSEMESTER_COMBOBOX);
        cb2.setValue(s3);
        
        ComboBox cb3 = (ComboBox) gui.getGUINode(OH_BANNERNUMBER_COMBOBOX);
        cb3.setValue(s4);
        
        String title = json.getString(JSON_TITLEFIELD);
        TextField fieldTitle = (TextField) gui.getGUINode(OH_BANNER_TITLEFIELD);
        fieldTitle.setText(title);
        
        String str1 = json.getString(JSON_INSTNAME);
        TextField tf = (TextField) gui.getGUINode(OH_INSTNAME_FIELD);
        tf.setText(str1);
        
        String str2 = json.getString(JSON_INSTEMAIL);
        TextField tf1 = (TextField) gui.getGUINode(OH_INSTEMAIL_FIELD);
        tf1.setText(str2);
        
        String str3 = json.getString(JSON_INSTROOM);
        TextField tf2 = (TextField) gui.getGUINode(OH_INSTROOM_FIELD);
        tf2.setText(str3);
        
        String str4 = json.getString(JSON_INSTHOME);
        TextField tf3 = (TextField) gui.getGUINode(OH_INSTHOME_FIELD);
        tf3.setText(str4);
        
        String str5 = json.getString(JSON_INSTAREA);
        TextArea area = (TextArea) gui.getGUINode(OH_INSTTEXTAREA);
        area.setText(str5);
        
        String  strarea1 = json.getString(JSON_DESCRIPTION);
        TextArea area1 = (TextArea) gui.getGUINode(OH_DESCRIPTIONTEXT_AREA);
        area1.setText(strarea1);
        
        String strarea2 = json.getString(JSON_TOPICS);
        TextArea area2 = (TextArea) gui.getGUINode(OH_TOPICSTEXT_AREA);
        area2.setText(strarea2);
        
        String strarea3 = json.getString(JSON_PREREQ);
        TextArea area3 = (TextArea) gui.getGUINode(OH_PREREQTEXT_AREA);
        area3.setText(strarea3);
        
        String strarea4 = json.getString(JSON_OUTCOMES);
        TextArea area4 = (TextArea) gui.getGUINode(OH_OUTCOMESTEXT_AREA);
        area4.setText(strarea4);
        
        String strarea5 = json.getString(JSON_TEXTBOOKS);
        TextArea area5 = (TextArea) gui.getGUINode(OH_TEXTBOOKTEXT_AREA);
        area5.setText(strarea5);
        
        String strarea6 = json.getString(JSON_GRADEDCOMPONENTS);
        TextArea area6 = (TextArea) gui.getGUINode(OH_GRADEDCOMPONENTSTEXT_AREA);
        area6.setText(strarea6);
        
        String strarea7 = json.getString(JSON_GRADINGNOTE);
        TextArea area7 = (TextArea) gui.getGUINode(OH_GRADINGNOTETEXT_AREA);
        area7.setText(strarea7);
        
        String strarea8 = json.getString(JSON_DISHONESTY);
        TextArea area8 = (TextArea) gui.getGUINode(OH_DISHONESTYTEXT_AREA);
        area8.setText(strarea8);

        String strarea9 = json.getString(JSON_SPEC);
        TextArea area9 = (TextArea) gui.getGUINode(OH_SPECASSISTANCETEXT_AREA);
        area9.setText(strarea9);
        // AND THEN ALL THE OFFICE HOURS
        JsonArray jsonOfficeHoursArray = json.getJsonArray(JSON_OFFICE_HOURS);
        for (int i = 0; i < jsonOfficeHoursArray.size(); i++) {
            JsonObject jsonOfficeHours = jsonOfficeHoursArray.getJsonObject(i);
            String startTime = jsonOfficeHours.getString(JSON_START_TIME);
            DayOfWeek dow = DayOfWeek.valueOf(jsonOfficeHours.getString(JSON_DAY_OF_WEEK));
            String name = jsonOfficeHours.getString(JSON_NAME);
            TeachingAssistantPrototype ta = dataManager.getTAWithName(name);
            TimeSlot timeSlot = dataManager.getTimeSlot(startTime);
            timeSlot.toggleTA(dow, ta);
        }
    }
    
    private void loadTAs(OfficeHoursData data, JsonObject json, String tas) {
        JsonArray jsonTAArray = json.getJsonArray(tas);
        for (int i = 0; i < jsonTAArray.size(); i++) {
            JsonObject jsonTA = jsonTAArray.getJsonObject(i);
            String name = jsonTA.getString(JSON_NAME);
            String email = jsonTA.getString(JSON_EMAIL);
            TAType type = TAType.valueOf(jsonTA.getString(JSON_TYPE));
            TeachingAssistantPrototype ta = new TeachingAssistantPrototype(name, email, type);
            data.addTA(ta);
        }     
    }
    private void loadLectures(OfficeHoursData data, JsonObject json, String tas) {
        JsonArray jsonTAArray = json.getJsonArray(tas);
        for (int i = 0; i < jsonTAArray.size(); i++) {
            JsonObject jsonTA = jsonTAArray.getJsonObject(i);
            String section = jsonTA.getString(JSON_LECSECTION);
            String room = jsonTA.getString(JSON_LECROOM);
            String days = jsonTA.getString(JSON_LECDAYS);
            String time = jsonTA.getString(JSON_LECTIME);
            Lectures lec = new Lectures(section,room,time,days);
            data.addLecture(lec);
        }     
    } 
    private void loadRecitations(OfficeHoursData data, JsonObject json, String tas) {
        JsonArray jsonTAArray = json.getJsonArray(tas);
        for (int i = 0; i < jsonTAArray.size(); i++) {
            JsonObject jsonTA = jsonTAArray.getJsonObject(i);
            String section = jsonTA.getString(JSON_RECSECTION);
            String room = jsonTA.getString(JSON_RECROOM);
            String days = jsonTA.getString(JSON_RECDAYS);
            String ta1 = jsonTA.getString(JSON_RECTA1);
            String ta2 = jsonTA.getString(JSON_RECTA2);
            Recitations rec = new Recitations(ta1,ta2,days,section,room,days,days);
            data.addRecitation(rec);
        }     
    }      
    private void loadLabs(OfficeHoursData data, JsonObject json, String tas) {
        JsonArray jsonTAArray = json.getJsonArray(tas);
        for (int i = 0; i < jsonTAArray.size(); i++) {
            JsonObject jsonTA = jsonTAArray.getJsonObject(i);
            String section = jsonTA.getString(JSON_LABSECTION);
            String room = jsonTA.getString(JSON_LABROOM);
            String days = jsonTA.getString(JSON_LABDAYS);
            String ta1 = jsonTA.getString(JSON_LABTA1);
            String ta2 = jsonTA.getString(JSON_LABTA2);
            Labs lab = new Labs(ta1,ta2,days,section,room,days,days);
            data.addLab(lab);
        }     
    }
    private void loadScheduleItem(OfficeHoursData data, JsonObject json, String tas){
        JsonArray jsonTAArray = json.getJsonArray(tas);
        for (int i = 0; i < jsonTAArray.size(); i++) {
            JsonObject jsonTA = jsonTAArray.getJsonObject(i);
            String type = jsonTA.getString(JSON_SCHEDULETYPE);
            String date = jsonTA.getString(JSON_SCHEDULEDATE);
            String title = jsonTA.getString(JSON_SCHEDULETITLE);
            String topic = jsonTA.getString(JSON_SCHEDULETOPIC);
            ScheduleItem item = new ScheduleItem(type,date,title,topic);
            data.addScheduleItem(item);
        }             
    }
    
    // HELPER METHOD FOR LOADING DATA FROM A JSON FORMAT
    private JsonObject loadJSONFile(String jsonFilePath) throws IOException {
	InputStream is = new FileInputStream(jsonFilePath);
	JsonReader jsonReader = Json.createReader(is);
	JsonObject json = jsonReader.readObject();
	jsonReader.close();
	is.close();
	return json;
    }

    @Override
    public void saveData(AppDataComponent data, String filePath) throws IOException {
	// GET THE DATA
	OfficeHoursData dataManager = (OfficeHoursData)data;
        AppGUIModule gui = app.getGUIModule();
        

	// NOW BUILD THE TA JSON OBJCTS TO SAVE
	JsonArrayBuilder gradTAsArrayBuilder = Json.createArrayBuilder();
        JsonArrayBuilder undergradTAsArrayBuilder = Json.createArrayBuilder();
	Iterator<TeachingAssistantPrototype> tasIterator = dataManager.teachingAssistantsIterator();
        while (tasIterator.hasNext()) {
            TeachingAssistantPrototype ta = tasIterator.next();
	    JsonObject taJson = Json.createObjectBuilder()
		    .add(JSON_NAME, ta.getName())
		    .add(JSON_EMAIL, ta.getEmail())
                    .add(JSON_TYPE, ta.getType().toString()).build();
            if (ta.getType().equals(TAType.Graduate.toString()))
                gradTAsArrayBuilder.add(taJson);
            else
                undergradTAsArrayBuilder.add(taJson);
	}
        JsonArray gradTAsArray = gradTAsArrayBuilder.build();
	JsonArray undergradTAsArray = undergradTAsArrayBuilder.build();

	// NOW BUILD THE OFFICE HOURS JSON OBJCTS TO SAVE
	JsonArrayBuilder officeHoursArrayBuilder = Json.createArrayBuilder();
        Iterator<TimeSlot> timeSlotsIterator = dataManager.officeHoursIterator();
        while (timeSlotsIterator.hasNext()) {
            TimeSlot timeSlot = timeSlotsIterator.next();
            for (int i = 0; i < DayOfWeek.values().length; i++) {
                DayOfWeek dow = DayOfWeek.values()[i];
                tasIterator = timeSlot.getTAsIterator(dow);
                while (tasIterator.hasNext()) {
                    TeachingAssistantPrototype ta = tasIterator.next();
                    JsonObject tsJson = Json.createObjectBuilder()
                        .add(JSON_START_TIME, timeSlot.getStartTime().replace(":", "_"))
                        .add(JSON_DAY_OF_WEEK, dow.toString())
                        .add(JSON_NAME, ta.getName()).build();
                    officeHoursArrayBuilder.add(tsJson);
                }
            }
	}
        //save lectures
        JsonArrayBuilder lecturesArrayBuilder = Json.createArrayBuilder();
        Iterator<Lectures> iteratorLectures = dataManager.lecturesIterator();
        while(iteratorLectures.hasNext()){
            Lectures lec = iteratorLectures.next();
            JsonObject lecJson = Json.createObjectBuilder()
                    .add(JSON_LECSECTION,lec.getSection())
                    .add(JSON_LECROOM,lec.getRoom())
                    .add(JSON_LECDAYS, lec.getDays())
                    .add(JSON_LECTIME, lec.getTime()).build();
                    lecturesArrayBuilder.add(lecJson);
        }
        //save recitations
        JsonArrayBuilder recitationsArrayBuilder = Json.createArrayBuilder();
        Iterator<Recitations> iteratorRecitations = dataManager.recitationsIterator();
        while(iteratorRecitations.hasNext()){
            Recitations lec = iteratorRecitations.next();
            JsonObject lecJson = Json.createObjectBuilder()
                    .add(JSON_RECSECTION,lec.getSection())
                    .add(JSON_RECROOM,lec.getRoom())
                    .add(JSON_RECDAYS, lec.getDaysTime())
                    .add(JSON_RECTA1, lec.getTa1())
                    .add(JSON_RECTA2,lec.getTa2())
                    .build();
                    recitationsArrayBuilder.add(lecJson);
        }        
        
        JsonArrayBuilder labsArrayBuilder = Json.createArrayBuilder();
        Iterator<Labs> iteratorLabs = dataManager.labsIterator();
        while(iteratorLabs.hasNext()){
            Labs lab = iteratorLabs.next();
            JsonObject lecJson = Json.createObjectBuilder()
                    .add(JSON_LABSECTION,lab.getSection())
                    .add(JSON_LABROOM,lab.getRoom())
                    .add(JSON_LABDAYS, lab.getDaysTime())
                    .add(JSON_LABTA1,lab.getTa1())
                    .add(JSON_LABTA2,lab.getTa2())
                    .build();
                    labsArrayBuilder.add(lecJson);
        }
        
        JsonArrayBuilder scheduleItemBuilder = Json.createArrayBuilder();
        Iterator<ScheduleItem> iteratorScheduleItem = dataManager.scheduleItemIterator();
        while(iteratorScheduleItem.hasNext()){
            ScheduleItem scheduleItem = iteratorScheduleItem.next();
            JsonObject lecJson = Json.createObjectBuilder()
                    .add(JSON_SCHEDULETYPE,scheduleItem.getType())
                    .add(JSON_SCHEDULEDATE,scheduleItem.getDate())
                    .add(JSON_SCHEDULETITLE,scheduleItem.getTitle())
                    .add(JSON_SCHEDULETOPIC,scheduleItem.getTopic())
                    .build();
                    scheduleItemBuilder.add(lecJson);
        }
        
	JsonArray officeHoursArray = officeHoursArrayBuilder.build();
        JsonArray lecturesArray = lecturesArrayBuilder.build();
        JsonArray recitationArray = recitationsArrayBuilder.build();
        JsonArray labsArray = labsArrayBuilder.build();
        JsonArray scheduleArray = scheduleItemBuilder.build();
        
        ComboBox cb = (ComboBox) gui.getGUINode(OH_BANNERSUBJECT_COMBOBOX);
        String str = cb.getValue().toString();
        
        ComboBox cb1 = (ComboBox) gui.getGUINode(OH_BANNERYEAR_COMBOBOX);
        String str1 = cb1.getValue().toString();
        
        ComboBox cb2 = (ComboBox) gui.getGUINode(OH_BANNERNUMBER_COMBOBOX);
        String str2 = cb2.getValue().toString();
        
        ComboBox cb3 = (ComboBox) gui.getGUINode(OH_BANNERSEMESTER_COMBOBOX);
        String str3 = cb3.getValue().toString();
        
        TextField titlefield = (TextField) gui.getGUINode(OH_BANNER_TITLEFIELD);
        String title = titlefield.getText();
        
        TextField tf = (TextField) gui.getGUINode(OH_INSTNAME_FIELD);
        String strtf = tf.getText();
        
        TextField tf1 = (TextField) gui.getGUINode(OH_INSTEMAIL_FIELD);
        String strtf1 = tf1.getText();
        
        TextField tf2 = (TextField) gui.getGUINode(OH_INSTROOM_FIELD);
        String strtf2 = tf2.getText();
        
        TextField tf3 = (TextField) gui.getGUINode(OH_INSTHOME_FIELD);
        String strtf3 = tf3.getText();
        
        TextArea area = (TextArea) gui.getGUINode(OH_INSTTEXTAREA);
        String strarea = area.getText();

        TextArea area1 = (TextArea) gui.getGUINode(OH_DESCRIPTIONTEXT_AREA);
        String strarea1 = area1.getText();

        TextArea area2 = (TextArea) gui.getGUINode(OH_TOPICSTEXT_AREA);
        String strarea2 = area2.getText();

        TextArea area3 = (TextArea) gui.getGUINode(OH_PREREQTEXT_AREA);
        String strarea3 = area3.getText();

        TextArea area4 = (TextArea) gui.getGUINode(OH_OUTCOMESTEXT_AREA);
        String strarea4 = area4.getText();

        TextArea area5 = (TextArea) gui.getGUINode(OH_TEXTBOOKTEXT_AREA);
        String strarea5 = area5.getText();

        TextArea area6 = (TextArea) gui.getGUINode(OH_GRADEDCOMPONENTSTEXT_AREA);
        String strarea6 = area6.getText();

        TextArea area7 = (TextArea) gui.getGUINode(OH_GRADINGNOTETEXT_AREA);
        String strarea7 = area7.getText();

        TextArea area8 = (TextArea) gui.getGUINode(OH_DISHONESTYTEXT_AREA);
        String strarea8 = area8.getText();

        TextArea area9 = (TextArea) gui.getGUINode(OH_SPECASSISTANCETEXT_AREA);
        String strarea9 = area9.getText();        
          
	// THEN PUT IT ALL TOGETHER IN A JsonObject
	JsonObject dataManagerJSO = Json.createObjectBuilder()
		.add(JSON_START_HOUR, "" + dataManager.getStartHour())
		.add(JSON_END_HOUR, "" + dataManager.getEndHour())
                .add(JSON_GRAD_TAS, gradTAsArray)
                .add(JSON_UNDERGRAD_TAS, undergradTAsArray)
                .add(JSON_OFFICE_HOURS, officeHoursArray)
                .add(JSON_TITLEFIELD, title)
                .add(JSON_SUBJECT, str)
                .add(JSON_YEAR, str1)
                .add(JSON_SEMESTER, str3)
                .add(JSON_NUMBER, str2)
                .add(JSON_INSTNAME, strtf)
                .add(JSON_INSTEMAIL,strtf1)
                .add(JSON_INSTROOM, strtf2)
                .add(JSON_INSTHOME, strtf3)
                .add(JSON_INSTAREA, strarea)         
                .add(JSON_DESCRIPTION, strarea1)
                .add(JSON_TOPICS, strarea2)
                .add(JSON_PREREQ, strarea3)
                .add(JSON_OUTCOMES, strarea4)
                .add(JSON_TEXTBOOKS, strarea5)
                .add(JSON_GRADEDCOMPONENTS,strarea6)
                .add(JSON_GRADINGNOTE, strarea7)
                .add(JSON_DISHONESTY, strarea8)
                .add(JSON_SPEC, strarea9)
                .add(JSON_LECTURES,lecturesArray)
                .add(JSON_RECITATIONS, recitationArray)
                .add(JSON_LABS, labsArray)
                .add(JSON_SCHEDULEITEMS, scheduleArray)
                .build();
	
	// AND NOW OUTPUT IT TO A JSON FILE WITH PRETTY PRINTING
	Map<String, Object> properties = new HashMap<>(1);
	properties.put(JsonGenerator.PRETTY_PRINTING, true);
	JsonWriterFactory writerFactory = Json.createWriterFactory(properties);
	StringWriter sw = new StringWriter();
	JsonWriter jsonWriter = writerFactory.createWriter(sw);
	jsonWriter.writeObject(dataManagerJSO);
	jsonWriter.close();

	// INIT THE WRITER
	OutputStream os = new FileOutputStream(filePath);
	JsonWriter jsonFileWriter = Json.createWriter(os);
	jsonFileWriter.writeObject(dataManagerJSO);
	String prettyPrinted = sw.toString();
	PrintWriter pw = new PrintWriter(filePath);
	pw.write(prettyPrinted);
	pw.close();
    }
    
    // IMPORTING/EXPORTING DATA IS USED WHEN WE READ/WRITE DATA IN AN
    // ADDITIONAL FORMAT USEFUL FOR ANOTHER PURPOSE, LIKE ANOTHER APPLICATION

    @Override
    public void importData(AppDataComponent data, String filePath) throws IOException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void exportData(AppDataComponent data, String filePath) throws IOException {
        AppWebDialog dialog = new AppWebDialog(app);
        try{
        PropertiesManager props = PropertiesManager.getPropertiesManager();
        String webPath = props.getProperty(APP_PATH_WEB);
         filePath = webPath + props.getProperty(APP_EXPORT_PAGE);
        dialog.showWebDialog(filePath);
        }catch(MalformedURLException murle){
             AppDialogsFacade.showMessageDialog(app.getGUIModule().getWindow(), ABOUT_DIALOG_ERROR_TITLE, ABOUT_DIALOG_ERROR_CONTENT);

        }
    }
}