package oh.workspace;

import djf.components.AppWorkspaceComponent;
import djf.modules.AppFoolproofModule;
import djf.modules.AppGUIModule;
import static djf.modules.AppGUIModule.ENABLED;
import djf.ui.AppNodesBuilder;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.SplitPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import properties_manager.PropertiesManager;
import oh.OfficeHoursApp;
import oh.OfficeHoursPropertyType;
import static oh.OfficeHoursPropertyType.*;
import oh.data.OfficeHoursData;
import oh.data.TeachingAssistantPrototype;
import oh.data.TimeSlot;
import oh.transactions.Save_Transaction;
import oh.workspace.controllers.OfficeHoursController;
import oh.workspace.dialogs.TADialog;
import oh.workspace.foolproof.OfficeHoursFoolproofDesign;
import static oh.workspace.style.OHStyle.*;

/**
 *
 * @author McKillaGorilla
 */
public class OfficeHoursWorkspace extends AppWorkspaceComponent {

    public OfficeHoursWorkspace(OfficeHoursApp app) {
        super(app);

        // LAYOUT THE APP
        initLayout();

        // INIT THE EVENT HANDLERS
        initControllers();

        // 
        initFoolproofDesign();

        // INIT DIALOGS
        initDialogs();
    }

    private void initDialogs() {
        TADialog taDialog = new TADialog((OfficeHoursApp) app);
        app.getGUIModule().addDialog(OH_TA_EDIT_DIALOG, taDialog);
    }

    // THIS HELPER METHOD INITIALIZES ALL THE CONTROLS IN THE WORKSPACE
    private void initLayout() {
        // FIRST LOAD THE FONT FAMILIES FOR THE COMBO BOX
        PropertiesManager props = PropertiesManager.getPropertiesManager();

        // THIS WILL BUILD ALL OF OUR JavaFX COMPONENTS FOR US
        AppNodesBuilder ohBuilder = app.getGUIModule().getNodesBuilder();

        // INIT THE HEADER ON THE LEFT
        VBox leftPane = ohBuilder.buildVBox(OH_LEFT_PANE, null, CLASS_OH_PANE, ENABLED);
        HBox tasHeaderBox = ohBuilder.buildHBox(OH_TAS_HEADER_PANE, leftPane, CLASS_OH_BOX, ENABLED);
        ohBuilder.buildLabel(OfficeHoursPropertyType.OH_TAS_HEADER_LABEL, tasHeaderBox, CLASS_OH_HEADER_LABEL, ENABLED);
        HBox typeHeaderBox = ohBuilder.buildHBox(OH_GRAD_UNDERGRAD_TAS_PANE, tasHeaderBox, CLASS_OH_RADIO_BOX, ENABLED);
        ToggleGroup tg = new ToggleGroup();
        ohBuilder.buildRadioButton(OH_ALL_RADIO_BUTTON, typeHeaderBox, CLASS_OH_RADIO_BUTTON, ENABLED, tg, true);
        ohBuilder.buildRadioButton(OH_GRAD_RADIO_BUTTON, typeHeaderBox, CLASS_OH_RADIO_BUTTON, ENABLED, tg, false);
        ohBuilder.buildRadioButton(OH_UNDERGRAD_RADIO_BUTTON, typeHeaderBox, CLASS_OH_RADIO_BUTTON, ENABLED, tg, false);

        // MAKE THE TABLE AND SETUP THE DATA MODEL
        TableView<TeachingAssistantPrototype> taTable = ohBuilder.buildTableView(OH_TAS_TABLE_VIEW, leftPane, CLASS_OH_TABLE_VIEW, ENABLED);
        taTable.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        TableColumn nameColumn = ohBuilder.buildTableColumn(OH_NAME_TABLE_COLUMN, taTable, CLASS_OH_COLUMN);
        TableColumn emailColumn = ohBuilder.buildTableColumn(OH_EMAIL_TABLE_COLUMN, taTable, CLASS_OH_COLUMN);
        TableColumn slotsColumn = ohBuilder.buildTableColumn(OH_SLOTS_TABLE_COLUMN, taTable, CLASS_OH_CENTERED_COLUMN);
        TableColumn typeColumn = ohBuilder.buildTableColumn(OH_TYPE_TABLE_COLUMN, taTable, CLASS_OH_COLUMN);
        nameColumn.setCellValueFactory(new PropertyValueFactory<String, String>("name"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<String, String>("email"));
        slotsColumn.setCellValueFactory(new PropertyValueFactory<String, String>("slots"));
        typeColumn.setCellValueFactory(new PropertyValueFactory<String, String>("type"));
        nameColumn.prefWidthProperty().bind(taTable.widthProperty().multiply(1.0 / 5.0));
        emailColumn.prefWidthProperty().bind(taTable.widthProperty().multiply(2.0 / 5.0));
        slotsColumn.prefWidthProperty().bind(taTable.widthProperty().multiply(1.0 / 5.0));
        typeColumn.prefWidthProperty().bind(taTable.widthProperty().multiply(1.0 / 5.0));

        // ADD BOX FOR ADDING A TA
        HBox taBox = ohBuilder.buildHBox(OH_ADD_TA_PANE, leftPane, CLASS_OH_PANE, ENABLED);
        ohBuilder.buildTextField(OH_NAME_TEXT_FIELD, taBox, CLASS_OH_TEXT_FIELD, ENABLED);
        ohBuilder.buildTextField(OH_EMAIL_TEXT_FIELD, taBox, CLASS_OH_TEXT_FIELD, ENABLED);
        ohBuilder.buildTextButton(OH_ADD_TA_BUTTON, taBox, CLASS_OH_BUTTON, !ENABLED);

        // MAKE SURE IT'S THE TABLE THAT ALWAYS GROWS IN THE LEFT PANE
        VBox.setVgrow(taTable, Priority.ALWAYS);

        // INIT THE HEADER ON THE RIGHT
        VBox rightPane = ohBuilder.buildVBox(OH_RIGHT_PANE, null, CLASS_OH_PANE, ENABLED);
        HBox officeHoursHeaderBox = ohBuilder.buildHBox(OH_OFFICE_HOURS_HEADER_PANE, rightPane, CLASS_OH_PANE, ENABLED);
        ohBuilder.buildLabel(OH_OFFICE_HOURS_HEADER_LABEL, officeHoursHeaderBox, CLASS_OH_HEADER_LABEL, ENABLED);
        HBox officeHoursTimeBox = ohBuilder.buildHBox(OH_OFFICE_HOURS_RIGHTHEADER_PANE, officeHoursHeaderBox, CLASS_OH_PANE, ENABLED);
        ohBuilder.buildLabel(OH_STARTTIME, officeHoursTimeBox, CLASS_OH_BANNER_LABEL, ENABLED);
        ohBuilder.buildComboBox(OH_STARTTIME_COMBO, "STARTTIME_OPTIONS","DEF_STARTTIME" ,officeHoursTimeBox, EMPTY_TEXT, ENABLED);
        ohBuilder.buildLabel(OH_ENDTIME, officeHoursTimeBox, CLASS_OH_BANNER_LABEL, ENABLED);
        ohBuilder.buildComboBox(OH_ENDTIME_COMBO, "ENDTIME_OPTIONS","DEF_ENDTIME",officeHoursTimeBox, EMPTY_TEXT, ENABLED);
        

        // SETUP THE OFFICE HOURS TABLE
        TableView<TimeSlot> officeHoursTable = ohBuilder.buildTableView(OH_OFFICE_HOURS_TABLE_VIEW, rightPane, CLASS_OH_OFFICE_HOURS_TABLE_VIEW, ENABLED);
        setupOfficeHoursColumn(OH_START_TIME_TABLE_COLUMN, officeHoursTable, CLASS_OH_TIME_COLUMN, "startTime");
        setupOfficeHoursColumn(OH_END_TIME_TABLE_COLUMN, officeHoursTable, CLASS_OH_TIME_COLUMN, "endTime");
        setupOfficeHoursColumn(OH_MONDAY_TABLE_COLUMN, officeHoursTable, CLASS_OH_DAY_OF_WEEK_COLUMN, "monday");
        setupOfficeHoursColumn(OH_TUESDAY_TABLE_COLUMN, officeHoursTable, CLASS_OH_DAY_OF_WEEK_COLUMN, "tuesday");
        setupOfficeHoursColumn(OH_WEDNESDAY_TABLE_COLUMN, officeHoursTable, CLASS_OH_DAY_OF_WEEK_COLUMN, "wednesday");
        setupOfficeHoursColumn(OH_THURSDAY_TABLE_COLUMN, officeHoursTable, CLASS_OH_DAY_OF_WEEK_COLUMN, "thursday");
        setupOfficeHoursColumn(OH_FRIDAY_TABLE_COLUMN, officeHoursTable, CLASS_OH_DAY_OF_WEEK_COLUMN, "friday");

        // MAKE SURE IT'S THE TABLE THAT ALWAYS GROWS IN THE LEFT PANE
        VBox.setVgrow(officeHoursTable, Priority.ALWAYS);

        // BOTH PANES WILL NOW GO IN A SPLIT PANE
        SplitPane sPane = new SplitPane(leftPane, rightPane);
        sPane.setDividerPositions(.4);
        workspace = new BorderPane();
        TabPane tabPane = ohBuilder.buildTabPane(OH_TAB_PANE, CLASS_OH_TAB_PANE);
        Tab siteTab = ohBuilder.buildTab(OH_SITE_TAB, CLASS_OH_TABPANE_TABS, tabPane);
        Tab syllTab = ohBuilder.buildTab(OH_SYLLABUS_TAB, CLASS_OH_TABPANE_TABS, tabPane);
        Tab meetTab = ohBuilder.buildTab(OH_MEETING_TAB, CLASS_OH_TABPANE_TABS, tabPane);
        Tab ohTab = ohBuilder.buildTab(OH_OH_TAB, CLASS_OH_TABPANE_TABS, tabPane);
        Tab scheduleTab = ohBuilder.buildTab(OH_SCHEDULE_TAB, CLASS_OH_TABPANE_TABS, tabPane);
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        // AND PUT EVERYTHING IN THE WORKSPACE
        ((BorderPane) workspace).setTop(tabPane);

        // LAYOUT SITE TAB
        VBox siteContainerBox = ohBuilder.buildVBox(OH_SITE_LAYOUT, null, CLASS_OH_SITE_CONTAINER, ENABLED);
        VBox siteBannerBox = ohBuilder.buildVBox(OH_SITE_BANNERBOX, siteContainerBox, CLASS_OH_SITE, ENABLED);
        //BANNER
        ohBuilder.buildLabel(OH_SITE_BANNER, siteBannerBox, CLASS_OH_BANNERHEADER_LABEL, ENABLED);
        HBox bannerContainerBox = ohBuilder.buildHBox(OH_BANNER_CONTAINER, siteBannerBox, CLASS_OH_BANNER_CONTAINER, ENABLED);
        VBox bannerLabelsBox = ohBuilder.buildVBox(OH_BANNER_LABELS, bannerContainerBox, CLASS_OH_BANNER_LABELS, ENABLED);
        ohBuilder.buildLabel(OH_BANNER_SUBJECT, bannerLabelsBox, CLASS_OH_BANNER_LABEL, ENABLED);
        ohBuilder.buildLabel(OH_BANNER_SEMESTER, bannerLabelsBox, CLASS_OH_BANNER_LABEL, ENABLED);
        ohBuilder.buildLabel(OH_BANNER_TITLE, bannerLabelsBox, CLASS_OH_BANNER_LABEL, ENABLED);
        ohBuilder.buildLabel(OH_BANNER_EXPORT, bannerLabelsBox, CLASS_OH_BANNER_LABEL, ENABLED);
        VBox bannerComboBox = ohBuilder.buildVBox(OH_BANNER_COMBO, bannerContainerBox, CLASS_OH_BANNER_COMBO, ENABLED);
        ComboBox cb1 = ohBuilder.buildComboBox(OH_BANNERSUBJECT_COMBOBOX, "COURSE_OPTIONS", "CSE", bannerComboBox, CLASS_OH_BANNER_COMBOBOX, ENABLED);
        ComboBox cb2 = ohBuilder.buildComboBox(OH_BANNERSEMESTER_COMBOBOX, "SEMESTER_OPTIONS", "Fall", bannerComboBox, CLASS_OH_BANNER_COMBOBOX, ENABLED);
        ohBuilder.buildTextField(OH_BANNER_TITLEFIELD, bannerComboBox, CLASS_OH_TEXT_FIELD, ENABLED);
        VBox banner2ndLabels = ohBuilder.buildVBox(OH_BANNER_SECONDLABEL, bannerContainerBox, CLASS_OH_BANNER_LABELS, ENABLED);
        ohBuilder.buildLabel(OH_BANNER_NUMBER, banner2ndLabels, CLASS_OH_BANNER_LABEL, ENABLED);
        ohBuilder.buildLabel(OH_BANNER_YEAR, banner2ndLabels, CLASS_OH_BANNER_LABEL, ENABLED);
        VBox banner2ndCombobox = ohBuilder.buildVBox(OH_BANNER_COMBOBOX, bannerContainerBox, CLASS_OH_BANNER_COMBOBOX, ENABLED);
        ComboBox cb3 = ohBuilder.buildComboBox(OH_BANNERNUMBER_COMBOBOX, "NUMBER_OPTIONS", "219", banner2ndCombobox, CLASS_OH_BANNER_COMBOBOX, ENABLED);
        ComboBox cb4 = ohBuilder.buildComboBox(OH_BANNERYEAR_COMBOBOX, EMPTY_TEXT, EMPTY_TEXT, banner2ndCombobox, CLASS_OH_BANNER_COMBOBOX, ENABLED);
        
        int dynamicYear = Calendar.getInstance().get(Calendar.YEAR);
        int nextYear = dynamicYear+1;
        String thisYear = Integer.toString(dynamicYear);
        String nextConv = Integer.toString(nextYear);
        cb4.setValue(thisYear);
        cb4.getItems().addAll(thisYear,nextConv);
        
        
        String subject = props.getProperty("CSE")+"_";
        String number = props.getProperty("219")+"_";
        String term = props.getProperty("Fall")+"_";
        String year = cb4.getValue().toString();
        
        Label export = new Label(".\\export\\"+subject+number+term+year+"\\public_html");
        
        cb1.setOnAction( e->{
            String str = cb1.getValue().toString();
            String str2 = cb2.getValue().toString();
            String str3 = cb3.getValue().toString();
            String str4 = cb4.getValue().toString();
            
            export.setText(".\\export\\"+str+"_"+str3+"_"+str2+"_"+str4+"\\public_html");
           
            
        });
        cb2.setOnAction(e->{
                     String str = cb1.getValue().toString();
            String str2 = cb2.getValue().toString();
            String str3 = cb3.getValue().toString();
            String str4 = cb4.getValue().toString();
            
            export.setText(".\\export\\"+str+"_"+str3+"_"+str2+"_"+str4+"\\public_html");
        });
        cb3.setOnAction(e->{
                      String str = cb1.getValue().toString();
            String str2 = cb2.getValue().toString();
            String str3 = cb3.getValue().toString();
            String str4 = cb4.getValue().toString();
            
            export.setText(".\\export\\"+str+"_"+str3+"_"+str2+"_"+str4+"\\public_html");
        });
        cb4.setOnAction(e->{
                       String str = cb1.getValue().toString();
            String str2 = cb2.getValue().toString();
            String str3 = cb3.getValue().toString();
            String str4 = cb4.getValue().toString();
            
            export.setText(".\\export\\"+str+"_"+str3+"_"+str2+"_"+str4+"\\public_html");
        });        
        bannerComboBox.getChildren().add(export);
        
        //PAGES
        HBox sitePagesBox = ohBuilder.buildHBox(OH_SITE_PAGESBOX, siteContainerBox, CLASS_OH_PAGE, ENABLED);
        ohBuilder.buildLabel(OH_PAGES_TITLE, sitePagesBox, CLASS_OH_BANNERHEADER_LABEL, ENABLED);
        ohBuilder.buildCheckBox(OH_HOME_CHECKBOX, sitePagesBox, CLASS_OH_BANNER_LABEL, ENABLED);
        ohBuilder.buildCheckBox(OH_SYLLABUS_CHECKBOX, sitePagesBox, CLASS_OH_BANNER_LABEL, ENABLED);
        ohBuilder.buildCheckBox(OH_SCHEDULE_CHECKBOX, sitePagesBox, CLASS_OH_BANNER_LABEL, ENABLED);
        ohBuilder.buildCheckBox(OH_HWS_CHECKBOX, sitePagesBox, CLASS_OH_BANNER_LABEL, ENABLED);

        //STYLE
        HBox siteStyleBox = ohBuilder.buildHBox(OH_SITE_STYLEBOX, siteContainerBox, CLASS_OH_SITE, ENABLED);
        VBox leftSideSite = ohBuilder.buildVBox(OH_SITE_STYLEBOX2, siteStyleBox, CLASS_OH_SITE, ENABLED);
        ohBuilder.buildLabel(OH_STYLE_HEADER, leftSideSite, CLASS_OH_BANNERHEADER_LABEL, ENABLED);
        ohBuilder.buildTextButton(OH_STYLE_FAVBUTTON, leftSideSite, CLASS_OH_STYLEBUTTON, ENABLED);
        ohBuilder.buildTextButton(OH_NAVBAR_BUTTON, leftSideSite, CLASS_OH_STYLEBUTTON, ENABLED);
        ohBuilder.buildTextButton(OH_LEFTFOOTER_BUTTON, leftSideSite, CLASS_OH_STYLEBUTTON, ENABLED);
        ohBuilder.buildTextButton(OH_RIGHTFOOTER_BUTTON, leftSideSite, CLASS_OH_STYLEBUTTON, ENABLED);
        ohBuilder.buildLabel(OH_FONTS_NOTE, leftSideSite, CLASS_OH_BANNER_LABEL, ENABLED);
        ohBuilder.buildLabel(OH_STYLESHEET_NOTE, leftSideSite, CLASS_OH_BANNER_LABEL, ENABLED);
        VBox rightSideSite = ohBuilder.buildVBox(OH_SITE_STYLEBOX, siteStyleBox, CLASS_OH_SITE, ENABLED);
        String str = "file:./images/SBULogo.png";
        Image img1 = new Image(str);
        ImageView imgView = new ImageView();
        imgView.setImage(img1);
       
        String str1 = "file:./images/SBUCSLogo.png";
        Image img2 = new Image(str1);
        ImageView imgView1 = new ImageView();
        imgView1.setImage(img2);
       
        String str2 = "file:./images/SBUDarkRedShieldLogo.png";
        Image img3 = new Image(str2);
        ImageView imgView2 = new ImageView();
        imgView2.setImage(img3);
        
        String str3 = "file:./images/SBUDarkRedShieldLogo.png";
        Image img4 = new Image(str3);
        ImageView imgView3 = new ImageView();
        imgView3.setImage(img4);
        rightSideSite.getChildren().addAll(imgView,imgView1,imgView2,imgView3);
        ohBuilder.buildComboBox(OH_CSS_COMBO, "seawolf", "seawolf", rightSideSite, CLASS_OH_BANNER_COMBOBOX, ENABLED);


        VBox siteInstructorBox = ohBuilder.buildVBox(OH_SITE_INSTRUCTIONSBOX, siteContainerBox, CLASS_OH_SITE_INSTRUCTOR, ENABLED);
        ohBuilder.buildLabel(OH_INSTRUCTOR_LABEL, siteInstructorBox, CLASS_OH_BANNERHEADER_LABEL, ENABLED);
        HBox firstRow = ohBuilder.buildHBox(OH_INSTRUCTOR_FIRSTROW, siteInstructorBox, CLASS_OH_SITE_INSTRUCTOR_BOX, ENABLED);
        ohBuilder.buildLabel(OH_NAME_LABEL, firstRow, CLASS_OH_BANNER_LABEL, ENABLED);
        ohBuilder.buildTextField(OH_INSTNAME_FIELD, firstRow, CLASS_OH_TEXT_FIELD, ENABLED);
        ohBuilder.buildLabel(OH_ROOM_LABEL, firstRow, CLASS_OH_BANNER_LABEL, ENABLED);
        ohBuilder.buildTextField(OH_INSTROOM_FIELD, firstRow, CLASS_OH_TEXT_FIELD, ENABLED);

        HBox secondRow = ohBuilder.buildHBox(OH_INSTRUCTOR_SECONDROW, siteInstructorBox, CLASS_OH_SITE_INSTRUCTOR_BOX, ENABLED);
        ohBuilder.buildLabel(OH_EMAIL_LABEL, secondRow, CLASS_OH_BANNER_LABEL, ENABLED);
        ohBuilder.buildTextField(OH_INSTEMAIL_FIELD, secondRow, CLASS_OH_TEXT_FIELD, ENABLED);
        ohBuilder.buildLabel(OH_HOME_LABEL, secondRow, CLASS_OH_BANNER_LABEL, ENABLED);
        ohBuilder.buildTextField(OH_INSTHOME_FIELD, secondRow, CLASS_OH_TEXT_FIELD, ENABLED);
        TextArea t = ohBuilder.buildTextArea(OH_INSTTEXTAREA, siteInstructorBox, CLASS_OH_TEXTAREA, 10, 20, ENABLED);
        ohBuilder.buildTitledPane(OH_INST_OFFICEHOURS, t, siteInstructorBox, CLASS_OH_BANNER_LABEL, ENABLED);
        siteContainerBox.setPrefWidth(1000);
        ScrollPane sp = new ScrollPane(siteContainerBox);
        sp.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

        //SYLLABUS LAYOUT
        VBox syllabusContainer = ohBuilder.buildVBox(OH_SYLLABUS_CONTAINER, null, CLASS_OH_SYLLABUS_CONTAINER, ENABLED);
        TextArea t1 = ohBuilder.buildTextArea(OH_DESCRIPTIONTEXT_AREA, syllabusContainer, CLASS_OH_TEXTAREA, 20, 100, ENABLED);
        ohBuilder.buildTitledPane(OH_DESCRIPTION, t1, syllabusContainer, CLASS_OH_BANNERHEADER_LABEL, ENABLED);
        TextArea t2 = ohBuilder.buildTextArea(OH_TOPICSTEXT_AREA, syllabusContainer, CLASS_OH_TEXTAREA, 20, 50, ENABLED);
        ohBuilder.buildTitledPane(OH_TOPICS, t2, syllabusContainer, CLASS_OH_BANNERHEADER_LABEL, ENABLED);
        TextArea t3 = ohBuilder.buildTextArea(OH_PREREQTEXT_AREA, syllabusContainer, CLASS_OH_TEXTAREA, 20, 50, ENABLED);
        ohBuilder.buildTitledPane(OH_PREREQ, t3, syllabusContainer, CLASS_OH_BANNERHEADER_LABEL, ENABLED);
        TextArea t4 = ohBuilder.buildTextArea(OH_OUTCOMESTEXT_AREA, syllabusContainer, CLASS_OH_TEXTAREA, 20, 50, ENABLED);
        ohBuilder.buildTitledPane(OH_OUTCOMES, t4, syllabusContainer, CLASS_OH_BANNERHEADER_LABEL, ENABLED);
        TextArea t5 = ohBuilder.buildTextArea(OH_TEXTBOOKTEXT_AREA, syllabusContainer, CLASS_OH_TEXTAREA, 20, 50, ENABLED);
        ohBuilder.buildTitledPane(OH_TEXTBOOK, t5, syllabusContainer, CLASS_OH_BANNERHEADER_LABEL, ENABLED);
        TextArea t6 = ohBuilder.buildTextArea(OH_GRADEDCOMPONENTSTEXT_AREA, syllabusContainer, CLASS_OH_TEXTAREA, 20, 50, ENABLED);
        ohBuilder.buildTitledPane(OH_GRADEDCOMPONENTS, t6, syllabusContainer, CLASS_OH_BANNERHEADER_LABEL, ENABLED);
        TextArea t7 = ohBuilder.buildTextArea(OH_GRADINGNOTETEXT_AREA, syllabusContainer, CLASS_OH_TEXTAREA, 20, 50, ENABLED);
        ohBuilder.buildTitledPane(OH_GRADINGNOTE, t7, syllabusContainer, CLASS_OH_BANNERHEADER_LABEL, ENABLED);
        TextArea t8 = ohBuilder.buildTextArea(OH_DISHONESTYTEXT_AREA, syllabusContainer, CLASS_OH_TEXTAREA, 20, 50, ENABLED);
        ohBuilder.buildTitledPane(OH_DISHONESTY, t8, syllabusContainer, CLASS_OH_BANNERHEADER_LABEL, ENABLED);
        TextArea t9 = ohBuilder.buildTextArea(OH_SPECASSISTANCETEXT_AREA, syllabusContainer, CLASS_OH_TEXTAREA, 20, 50, ENABLED);
        ohBuilder.buildTitledPane(OH_SPECASSISTANCE, t9, syllabusContainer, CLASS_OH_BANNERHEADER_LABEL, ENABLED);

        ScrollPane sp1 = new ScrollPane(syllabusContainer);
        sp1.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        
        //MEETING TIMES
        VBox meetingContainer = ohBuilder.buildVBox(OH_MEETING_CONTAINER, null, CLASS_OH_MEETING, ENABLED);
        TableView tb = ohBuilder.buildTableView(OH_LECTURE, meetingContainer, CLASS_OH_TABLE_VIEW, ENABLED);
        TitledPane title1 = ohBuilder.buildTitledPane(OH_LECTURE, tb, meetingContainer, CLASS_OH_BANNERHEADER_LABEL, ENABLED);
        TableColumn sectionColumn = ohBuilder.buildTableColumn(OH_SECTION, tb, CLASS_OH_COLUMN);
        TableColumn daysColumn = ohBuilder.buildTableColumn(OH_DAYS, tb, CLASS_OH_COLUMN);
        TableColumn timeColumn = ohBuilder.buildTableColumn(OH_TIME, tb, CLASS_OH_COLUMN);
        TableColumn roomColumn = ohBuilder.buildTableColumn(OH_ROOM_LABEL2, tb, CLASS_OH_COLUMN);
        sectionColumn.prefWidthProperty().bind(tb.widthProperty().multiply(1.0 / 4.0));
        daysColumn.prefWidthProperty().bind(tb.widthProperty().multiply(1.0 / 4.0));
        timeColumn.prefWidthProperty().bind(tb.widthProperty().multiply(1.0 / 4.0));
        roomColumn.prefWidthProperty().bind(tb.widthProperty().multiply(1.0 / 4.0));
        TableView tb2 = ohBuilder.buildTableView(OH_RECITATION, null, CLASS_OH_TABLE_VIEW, ENABLED);
        TitledPane title2 = ohBuilder.buildTitledPane(OH_RECITATION, tb2, meetingContainer, CLASS_OH_BANNERHEADER_LABEL, ENABLED);
        TableColumn recSection = ohBuilder.buildTableColumn(OH_RECSECTION, tb2, CLASS_OH_COLUMN);
        TableColumn recDays = ohBuilder.buildTableColumn(OH_DAYSNTIME, tb2, CLASS_OH_COLUMN);
        TableColumn recRoom = ohBuilder.buildTableColumn(OH_RECROOM_LABEL, tb2, CLASS_OH_COLUMN);
        TableColumn recTA1 = ohBuilder.buildTableColumn(OH_TA1, tb2, CLASS_OH_COLUMN);
        TableColumn recTA2 = ohBuilder.buildTableColumn(OH_TA2, tb2, CLASS_OH_COLUMN);
        recSection.prefWidthProperty().bind(tb2.widthProperty().multiply(1.0 / 5.0));
        recDays.prefWidthProperty().bind(tb2.widthProperty().multiply(1.0 / 5.0));
        recRoom.prefWidthProperty().bind(tb2.widthProperty().multiply(1.0 / 5.0));
        recTA1.prefWidthProperty().bind(tb2.widthProperty().multiply(1.0 / 5.0));
        recTA2.prefWidthProperty().bind(tb2.widthProperty().multiply(1.0 / 5.0));
        TableView tb3 = ohBuilder.buildTableView(OH_LABS, meetingContainer, CLASS_OH_TABLE_VIEW, ENABLED);
        TitledPane title3 = ohBuilder.buildTitledPane(OH_LABS, tb3, meetingContainer, CLASS_OH_BANNERHEADER_LABEL, ENABLED);
        TableColumn labSection = ohBuilder.buildTableColumn(OH_LABSECTION, tb3, CLASS_OH_COLUMN);
        TableColumn labDays = ohBuilder.buildTableColumn(OH_LABDAYSNTIME, tb3, CLASS_OH_COLUMN);
        TableColumn labRoom = ohBuilder.buildTableColumn(OH_LABROOM_LABEL, tb3, CLASS_OH_COLUMN);
        TableColumn labTA1 = ohBuilder.buildTableColumn(OH_LABTA1, tb3, CLASS_OH_COLUMN);
        TableColumn labTA2 = ohBuilder.buildTableColumn(OH_LABTA2, tb3, CLASS_OH_COLUMN);
        labSection.prefWidthProperty().bind(tb3.widthProperty().multiply(1.0 / 5.0));
        labDays.prefWidthProperty().bind(tb3.widthProperty().multiply(1.0 / 5.0));
        labRoom.prefWidthProperty().bind(tb3.widthProperty().multiply(1.0 / 5.0));
        labTA1.prefWidthProperty().bind(tb3.widthProperty().multiply(1.0 / 5.0));
        labTA2.prefWidthProperty().bind(tb3.widthProperty().multiply(1.0 / 5.0));
        title1.setExpanded(ENABLED);
        title2.setExpanded(ENABLED);
        title3.setExpanded(ENABLED);
        
        
        
        //SCHEDULE LAYOUT
        VBox scheduleContainer = ohBuilder.buildVBox(OH_SCHEDULE_CONTAINER, null, CLASS_OH_SCHEDULE_CONTAINER, ENABLED);
        ohBuilder.buildLabel(OH_CALENDAR_BOUNDARIES, scheduleContainer, CLASS_OH_BANNERHEADER_LABEL, ENABLED);
        HBox calendarContainer = ohBuilder.buildHBox(OH_CALENDAR_CONTAINER, scheduleContainer, CLASS_OH_SCHEDULE_CONTAINER, ENABLED);
        ohBuilder.buildLabel(OH_STARTINGMONDAY, calendarContainer, CLASS_OH_BANNER_LABEL, ENABLED);
        DatePicker date1 = new DatePicker();
        calendarContainer.getChildren().add(date1);
        ohBuilder.buildLabel(OH_ENDINGFRIDAY, calendarContainer, CLASS_OH_BANNER_LABEL, ENABLED);
        DatePicker date2 = new DatePicker();
        calendarContainer.getChildren().add(date2);
        TableView scheduleItems = ohBuilder.buildTableView(OH_SCHEDULEITEMS, scheduleContainer, CLASS_OH_TABLE_VIEW, ENABLED);
        TitledPane scheduleTitle = ohBuilder.buildTitledPane(OH_SCHEDULETITLE, scheduleItems, scheduleContainer, CLASS_OH_BANNERHEADER_LABEL, ENABLED);
        TableColumn schedType = ohBuilder.buildTableColumn(OH_SCHEDTYPE, scheduleItems, CLASS_OH_COLUMN);
        TableColumn schedDate = ohBuilder.buildTableColumn(OH_SCHEDDATE, scheduleItems, CLASS_OH_COLUMN);
        TableColumn schedTitle = ohBuilder.buildTableColumn(OH_SCHEDTITLE, scheduleItems, CLASS_OH_COLUMN);
        TableColumn schedTopic = ohBuilder.buildTableColumn(OH_SCHEDTOPIC, scheduleItems, CLASS_OH_COLUMN);
        scheduleTitle.setExpanded(ENABLED);
        schedType.prefWidthProperty().bind(scheduleTitle.widthProperty().multiply(1.0 / 4.0));
        schedDate.prefWidthProperty().bind(scheduleTitle.widthProperty().multiply(1.0 / 4.0));
        schedTitle.prefWidthProperty().bind(scheduleTitle.widthProperty().multiply(1.0 / 4.0));
        schedTopic.prefWidthProperty().bind(scheduleTitle.widthProperty().multiply(1.0 / 4.0));
        ohBuilder.buildLabel(OH_ADDEDIT, scheduleContainer, CLASS_OH_BANNERHEADER_LABEL, ENABLED);
        HBox typeBox = ohBuilder.buildHBox(OH_TYPEBOX, scheduleContainer, CLASS_OH_SCHEDULE_CONTAINER, ENABLED);
        ohBuilder.buildLabel(OH_TYPE, typeBox, CLASS_OH_BANNER_LABEL, ENABLED);
        ohBuilder.buildComboBox(OH_TYPECOMBO, OH_DISHONESTY, OH_OPTIONS, typeBox, CLASS_OH_BANNER_COMBOBOX, ENABLED);
        HBox dateBox = ohBuilder.buildHBox(OH_DATEBOX, scheduleContainer, CLASS_OH_SCHEDULE_CONTAINER, ENABLED);
        ohBuilder.buildLabel(OH_DATE, dateBox, CLASS_OH_BANNER_LABEL, ENABLED);
        DatePicker dp2 = new DatePicker();
        dateBox.getChildren().add(dp2);
        HBox titleBox = ohBuilder.buildHBox(OH_TITLEBOX, scheduleContainer, CLASS_OH_SCHEDULE_CONTAINER, ENABLED);
        ohBuilder.buildLabel(OH_TITLE, titleBox, CLASS_OH_BANNER_LABEL, ENABLED);
        ohBuilder.buildTextField(OH_TITLEFIELD, titleBox, CLASS_OH_TEXTFIELD, ENABLED);
        HBox topicBox = ohBuilder.buildHBox(OH_TOPICBOX, scheduleContainer, CLASS_OH_SCHEDULE_CONTAINER, ENABLED);
        ohBuilder.buildLabel(OH_TOPIC, topicBox, CLASS_OH_BANNER_LABEL, ENABLED);
        ohBuilder.buildTextField(OH_TOPICFIELD, topicBox, CLASS_OH_TEXTFIELD, ENABLED);
        HBox linkBox = ohBuilder.buildHBox(OH_LINKBOX, scheduleContainer, CLASS_OH_SCHEDULE_CONTAINER, ENABLED);
        ohBuilder.buildLabel(OH_LINK, linkBox, CLASS_OH_BANNER_LABEL, ENABLED);
        ohBuilder.buildTextField(OH_LINKFIELD, linkBox, CLASS_OH_TEXTFIELD, ENABLED);
        HBox buttonBox = ohBuilder.buildHBox(OH_BUTTONBOX, scheduleContainer, CLASS_OH_SCHEDULE_CONTAINER, ENABLED);
        ohBuilder.buildTextButton(OH_ADDUPDATE, buttonBox, CLASS_OH_BUTTON, ENABLED);
        ohBuilder.buildTextButton(OH_CLEAR, buttonBox, CLASS_OH_BUTTON, ENABLED);
        
                ((BorderPane) workspace).setCenter(sp);

        siteTab.setOnSelectionChanged(e -> {
            ((BorderPane) workspace).setCenter(sp);
            
        });

        syllTab.setOnSelectionChanged(e -> {
            ((BorderPane) workspace).setCenter(sp1);

        });

        meetTab.setOnSelectionChanged(e -> {
            ((BorderPane) workspace).setCenter(meetingContainer);

        });

        ohTab.setOnSelectionChanged(e -> {
            ((BorderPane) workspace).setCenter(sPane);

        });

        scheduleTab.setOnSelectionChanged(e -> {
            ((BorderPane) workspace).setCenter(scheduleContainer);

        });

    }

    private void setupOfficeHoursColumn(Object columnId, TableView tableView, String styleClass, String columnDataProperty) {
        AppNodesBuilder builder = app.getGUIModule().getNodesBuilder();
        TableColumn<TeachingAssistantPrototype, String> column = builder.buildTableColumn(columnId, tableView, styleClass);
        column.setCellValueFactory(new PropertyValueFactory<TeachingAssistantPrototype, String>(columnDataProperty));
        column.prefWidthProperty().bind(tableView.widthProperty().multiply(1.0 / 7.0));
        column.setCellFactory(col -> {
            return new TableCell<TeachingAssistantPrototype, String>() {
                @Override
                protected void updateItem(String text, boolean empty) {
                    super.updateItem(text, empty);
                    if (text == null || empty) {
                        setText(null);
                        setStyle("");
                    } else {
                        // CHECK TO SEE IF text CONTAINS THE NAME OF
                        // THE CURRENTLY SELECTED TA
                        setText(text);
                        TableView<TeachingAssistantPrototype> tasTableView = (TableView) app.getGUIModule().getGUINode(OH_TAS_TABLE_VIEW);
                        TeachingAssistantPrototype selectedTA = tasTableView.getSelectionModel().getSelectedItem();
                        if (selectedTA == null) {
                            setStyle("");
                        } else if (text.contains(selectedTA.getName())) {
                            setStyle("-fx-background-color: yellow");
                        } else {
                            setStyle("");
                        }
                    }
                }
            };
        });
    }

    public void initControllers() {
        OfficeHoursController controller = new OfficeHoursController((OfficeHoursApp) app);
        AppGUIModule gui = app.getGUIModule();

        // FOOLPROOF DESIGN STUFF
        TextField nameTextField = ((TextField) gui.getGUINode(OH_NAME_TEXT_FIELD));
        TextField emailTextField = ((TextField) gui.getGUINode(OH_EMAIL_TEXT_FIELD));

        nameTextField.textProperty().addListener(e -> {
            controller.processTypeTA();
        });
        emailTextField.textProperty().addListener(e -> {
            controller.processTypeTA();
        });

        // FIRE THE ADD EVENT ACTION
        nameTextField.setOnAction(e -> {
            controller.processAddTA();
        });
        emailTextField.setOnAction(e -> {
            controller.processAddTA();
        });
        ((Button) gui.getGUINode(OH_ADD_TA_BUTTON)).setOnAction(e -> {
            controller.processAddTA();
        });

        TableView officeHoursTableView = (TableView) gui.getGUINode(OH_OFFICE_HOURS_TABLE_VIEW);
        officeHoursTableView.getSelectionModel().setCellSelectionEnabled(true);
        officeHoursTableView.setOnMouseClicked(e -> {
            controller.processToggleOfficeHours();
        });

        // DON'T LET ANYONE SORT THE TABLES
        TableView tasTableView = (TableView) gui.getGUINode(OH_TAS_TABLE_VIEW);
        for (int i = 0; i < officeHoursTableView.getColumns().size(); i++) {
            ((TableColumn) officeHoursTableView.getColumns().get(i)).setSortable(false);
        }
        for (int i = 0; i < tasTableView.getColumns().size(); i++) {
            ((TableColumn) tasTableView.getColumns().get(i)).setSortable(false);
        }

        tasTableView.setOnMouseClicked(e -> {
            app.getFoolproofModule().updateAll();
            if (e.getClickCount() == 2) {
                controller.processEditTA();
            }
            controller.processSelectTA();
        });

        RadioButton allRadio = (RadioButton) gui.getGUINode(OH_ALL_RADIO_BUTTON);
        allRadio.setOnAction(e -> {
            controller.processSelectAllTAs();
        });
        RadioButton gradRadio = (RadioButton) gui.getGUINode(OH_GRAD_RADIO_BUTTON);
        gradRadio.setOnAction(e -> {
            controller.processSelectGradTAs();
        });
        RadioButton undergradRadio = (RadioButton) gui.getGUINode(OH_UNDERGRAD_RADIO_BUTTON);
        undergradRadio.setOnAction(e -> {
            controller.processSelectUndergradTAs();
        });
        
        ComboBox cb1 = (ComboBox) gui.getGUINode(OH_STARTTIME_COMBO);
        ComboBox cb2 = (ComboBox) gui.getGUINode(OH_ENDTIME_COMBO);
        cb1.setOnAction(e->{
            int indexOfStart = cb1.getItems().indexOf(cb1.getValue());
            int indexOfEnd = cb2.getItems().indexOf(cb2.getValue());
            
            if(indexOfStart > indexOfEnd){
                Alert alert = new Alert(AlertType.ERROR);
                alert.setContentText("Invalid Time Error: End time cannot be same or earlier than Start time");
                alert.showAndWait();
            }else{
                controller.processOfficeHoursTimeRange(indexOfStart, indexOfEnd);
            }
                
            
            
        });
        cb2.setOnAction(e->{
            int indexOfStart = cb1.getItems().indexOf(cb1.getValue());
            int indexOfEnd = cb2.getItems().indexOf(cb2.getValue());
            
            if(indexOfStart > indexOfEnd){
                Alert alert = new Alert(AlertType.ERROR);
                alert.setContentText("Invalid Time Error: End time cannot be same or earlier than Start time");
                alert.showAndWait();
            }else{
                controller.processOfficeHoursTimeRange(indexOfStart, indexOfEnd);
            } 
        });
        
        TextField tf1 = (TextField) gui.getGUINode(OH_INSTNAME_FIELD);
        TextField tf2 = (TextField) gui.getGUINode(OH_INSTEMAIL_FIELD);
        TextField tf3 = (TextField) gui.getGUINode(OH_INSTROOM_FIELD);
        TextField tf4 = (TextField) gui.getGUINode(OH_INSTHOME_FIELD);
        TextArea tarea = (TextArea) gui.getGUINode(OH_INSTTEXTAREA);
        ComboBox com1 = (ComboBox) gui.getGUINode(OH_BANNERSUBJECT_COMBOBOX);
        ComboBox com2 = (ComboBox) gui.getGUINode(OH_BANNERSEMESTER_COMBOBOX);
        ComboBox com3 = (ComboBox) gui.getGUINode(OH_BANNERYEAR_COMBOBOX);
        ComboBox com4 = (ComboBox) gui.getGUINode(OH_BANNERNUMBER_COMBOBOX);
        
        TextArea a1 = (TextArea) gui.getGUINode(OH_DESCRIPTIONTEXT_AREA);
        TextArea a2 = (TextArea) gui.getGUINode(OH_TOPICSTEXT_AREA);
        TextArea a3 = (TextArea) gui.getGUINode(OH_PREREQTEXT_AREA);
        TextArea a4 = (TextArea) gui.getGUINode(OH_OUTCOMESTEXT_AREA);
        TextArea a5 = (TextArea) gui.getGUINode(OH_TEXTBOOKTEXT_AREA);
        TextArea a6 = (TextArea) gui.getGUINode(OH_GRADEDCOMPONENTSTEXT_AREA);
        TextArea a7 = (TextArea) gui.getGUINode(OH_GRADINGNOTETEXT_AREA);
        TextArea a8 = (TextArea) gui.getGUINode(OH_DISHONESTYTEXT_AREA);
        TextArea a9 = (TextArea) gui.getGUINode(OH_SPECASSISTANCETEXT_AREA);
        
        tf1.onKeyPressedProperty().set(e->{
             Save_Transaction save = new Save_Transaction();
            app.processTransaction(save);
        });
        tf2.onKeyPressedProperty().set(e->{
             Save_Transaction save = new Save_Transaction();
            app.processTransaction(save);
        });
        tf3.onKeyPressedProperty().set(e->{
             Save_Transaction save = new Save_Transaction();
            app.processTransaction(save);
        });
        tf4.onKeyPressedProperty().set(e->{
             Save_Transaction save = new Save_Transaction();
            app.processTransaction(save);
        });
        tarea.onKeyPressedProperty().set(e->{
             Save_Transaction save = new Save_Transaction();
            app.processTransaction(save);            
        });
        a1.onKeyPressedProperty().set(e->{
            Save_Transaction save = new Save_Transaction();
            app.processTransaction(save);               
        });
        a2.onKeyPressedProperty().set(e->{
            Save_Transaction save = new Save_Transaction();
            app.processTransaction(save);               
        });
                a3.onKeyPressedProperty().set(e->{
            Save_Transaction save = new Save_Transaction();
            app.processTransaction(save);               
        });
        a4.onKeyPressedProperty().set(e->{
            Save_Transaction save = new Save_Transaction();
            app.processTransaction(save);               
        });
        a5.onKeyPressedProperty().set(e->{
            Save_Transaction save = new Save_Transaction();
            app.processTransaction(save);               
        });
        a6.onKeyPressedProperty().set(e->{
            Save_Transaction save = new Save_Transaction();
            app.processTransaction(save);               
        });
        a7.onKeyPressedProperty().set(e->{
            Save_Transaction save = new Save_Transaction();
            app.processTransaction(save);               
        });
        a8.onKeyPressedProperty().set(e->{
            Save_Transaction save = new Save_Transaction();
            app.processTransaction(save);               
        });
        a9.onKeyPressedProperty().set(e->{
            Save_Transaction save = new Save_Transaction();
            app.processTransaction(save);               
        });
        
        
/*        com1.onMouseClickedProperty().set(e->{
                         Save_Transaction save = new Save_Transaction();
            app.processTransaction(save);     
        });
        com2.onMouseClickedProperty().set(e->{
                         Save_Transaction save = new Save_Transaction();
            app.processTransaction(save);     
        });
        com3.onMouseClickedProperty().set(e->{
                         Save_Transaction save = new Save_Transaction();
            app.processTransaction(save);     
        });
        com4.onMouseClickedProperty().set(e->{
                         Save_Transaction save = new Save_Transaction();
            app.processTransaction(save);     
        });
      */      
        
        
    }

    public void initFoolproofDesign() {
        AppGUIModule gui = app.getGUIModule();
        AppFoolproofModule foolproofSettings = app.getFoolproofModule();
        foolproofSettings.registerModeSettings(OH_FOOLPROOF_SETTINGS,
                new OfficeHoursFoolproofDesign((OfficeHoursApp) app));
    }

    @Override
    public void processWorkspaceKeyEvent(KeyEvent ke) {
        // WE AREN'T USING THIS FOR THIS APPLICATION
    }

    @Override
    public void showNewDialog() {
        // WE AREN'T USING THIS FOR THIS APPLICATION
    }
}
