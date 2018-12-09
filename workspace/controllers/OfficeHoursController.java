package oh.workspace.controllers;

import djf.modules.AppGUIModule;
import djf.ui.dialogs.AppDialogsFacade;
import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;
import javafx.scene.control.Button;
import javafx.scene.control.TablePosition;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import oh.OfficeHoursApp;
import static oh.OfficeHoursPropertyType.OH_EMAIL_TEXT_FIELD;
import static oh.OfficeHoursPropertyType.OH_FOOLPROOF_SETTINGS;
import static oh.OfficeHoursPropertyType.OH_LECTURETABLEVIEW;
import static oh.OfficeHoursPropertyType.OH_NAME_TEXT_FIELD;
import static oh.OfficeHoursPropertyType.OH_NO_TA_SELECTED_CONTENT;
import static oh.OfficeHoursPropertyType.OH_NO_TA_SELECTED_TITLE;
import static oh.OfficeHoursPropertyType.OH_OFFICE_HOURS_TABLE_VIEW;
import static oh.OfficeHoursPropertyType.OH_REMOVELAB_BUTTON;
import static oh.OfficeHoursPropertyType.OH_REMOVELECTURE_BUTTON;
import static oh.OfficeHoursPropertyType.OH_REMOVERECITATION_BUTTON;
import static oh.OfficeHoursPropertyType.OH_TAS_TABLE_VIEW;
import static oh.OfficeHoursPropertyType.OH_TA_EDIT_DIALOG;
import oh.data.Labs;
import oh.data.Lectures;
import oh.data.OfficeHoursData;
import oh.data.Recitations;
import oh.data.TAType;
import oh.data.TeachingAssistantPrototype;
import oh.data.TimeSlot;
import oh.data.TimeSlot.DayOfWeek;
import oh.transactions.AddTA_Transaction;
import oh.transactions.EditTA_Transaction;
import oh.transactions.ToggleOfficeHours_Transaction;
import oh.transactions.addLabs_Transaction;
import oh.transactions.addLecture_Transaction;
import oh.transactions.addRecitation_Transaction;
import oh.transactions.removeLabs_Transaction;
import oh.transactions.removeLecture_Transaction;
import oh.transactions.removeRecitation_Transaction;
import oh.workspace.dialogs.TADialog;

/**
 *
 * @author McKillaGorilla
 */
public class OfficeHoursController {

    OfficeHoursApp app;

    public OfficeHoursController(OfficeHoursApp initApp) {
        app = initApp;
    }

    public void processAddTA() {
        AppGUIModule gui = app.getGUIModule();
        TextField nameTF = (TextField) gui.getGUINode(OH_NAME_TEXT_FIELD);
        String name = nameTF.getText();
        TextField emailTF = (TextField) gui.getGUINode(OH_EMAIL_TEXT_FIELD);
        String email = emailTF.getText();
        OfficeHoursData data = (OfficeHoursData) app.getDataComponent();
        TAType type = data.getSelectedType();
        if (data.isLegalNewTA(name, email)) {
            TeachingAssistantPrototype ta = new TeachingAssistantPrototype(name.trim(), email.trim(), type);
            AddTA_Transaction addTATransaction = new AddTA_Transaction(data, ta);
            app.processTransaction(addTATransaction);

            // NOW CLEAR THE TEXT FIELDS
            nameTF.setText("");
            emailTF.setText("");
            nameTF.requestFocus();
        }
        app.getFoolproofModule().updateControls(OH_FOOLPROOF_SETTINGS);
    }

    public void processVerifyTA() {

    }

    public void processToggleOfficeHours() {
        AppGUIModule gui = app.getGUIModule();
        TableView<TimeSlot> officeHoursTableView = (TableView) gui.getGUINode(OH_OFFICE_HOURS_TABLE_VIEW);
        ObservableList<TablePosition> selectedCells = officeHoursTableView.getSelectionModel().getSelectedCells();
        if (selectedCells.size() > 0) {
            TablePosition cell = selectedCells.get(0);
            int cellColumnNumber = cell.getColumn();
            OfficeHoursData data = (OfficeHoursData)app.getDataComponent();
            if (data.isDayOfWeekColumn(cellColumnNumber)) {
                DayOfWeek dow = data.getColumnDayOfWeek(cellColumnNumber);
                TableView<TeachingAssistantPrototype> taTableView = (TableView)gui.getGUINode(OH_TAS_TABLE_VIEW);
                TeachingAssistantPrototype ta = taTableView.getSelectionModel().getSelectedItem();
                if (ta != null) {
                    TimeSlot timeSlot = officeHoursTableView.getSelectionModel().getSelectedItem();
                    ToggleOfficeHours_Transaction transaction = new ToggleOfficeHours_Transaction(data, timeSlot, dow, ta);
                    app.processTransaction(transaction);
                }
                else {
                    Stage window = app.getGUIModule().getWindow();
                    AppDialogsFacade.showMessageDialog(window, OH_NO_TA_SELECTED_TITLE, OH_NO_TA_SELECTED_CONTENT);
                }
            }
            int row = cell.getRow();
            cell.getTableView().refresh();
        }
    }

    public void processTypeTA() {
        app.getFoolproofModule().updateControls(OH_FOOLPROOF_SETTINGS);
    }

    public void processEditTA() {
        OfficeHoursData data = (OfficeHoursData)app.getDataComponent();
        if (data.isTASelected()) {
            TeachingAssistantPrototype taToEdit = data.getSelectedTA();
            TADialog taDialog = (TADialog)app.getGUIModule().getDialog(OH_TA_EDIT_DIALOG);
            taDialog.showEditDialog(taToEdit);
            TeachingAssistantPrototype editTA = taDialog.getEditTA();
            if (editTA != null) {
                EditTA_Transaction transaction = new EditTA_Transaction(taToEdit, editTA.getName(), editTA.getEmail(), editTA.getType());
                app.processTransaction(transaction);
            }
        }
    }

    public void processSelectAllTAs() {
        OfficeHoursData data = (OfficeHoursData)app.getDataComponent();
        data.selectTAs(TAType.All);
    }

    public void processSelectGradTAs() {
        OfficeHoursData data = (OfficeHoursData)app.getDataComponent();
        data.selectTAs(TAType.Graduate);
    }

    public void processSelectUndergradTAs() {
        OfficeHoursData data = (OfficeHoursData)app.getDataComponent();
        data.selectTAs(TAType.Undergraduate);
    }

    public void processSelectTA() {
        AppGUIModule gui = app.getGUIModule();
        TableView<TimeSlot> officeHoursTableView = (TableView) gui.getGUINode(OH_OFFICE_HOURS_TABLE_VIEW);
        officeHoursTableView.refresh();
    }
    public void processSelectLecture(){
            AppGUIModule gui = app.getGUIModule();
            OfficeHoursData data = (OfficeHoursData)app.getDataComponent();
            Lectures lec = data.getSelectedLecture();
            Button b1 = (Button) gui.getGUINode(OH_REMOVELECTURE_BUTTON);
            b1.setDisable(false);
            b1.setOnAction(e->{
              removeLecture_Transaction transaction = new removeLecture_Transaction(data,lec);
                app.processTransaction(transaction);
                           b1.setDisable(true);

            });
               
        
    }
    public void processSelectRecitation(){
            AppGUIModule gui = app.getGUIModule();
            OfficeHoursData data = (OfficeHoursData)app.getDataComponent();
            Recitations lec = data.getSelectedRecitation();
            Button b1 = (Button) gui.getGUINode(OH_REMOVERECITATION_BUTTON);
            b1.setDisable(false);
            b1.setOnAction(e->{
              removeRecitation_Transaction transaction = new removeRecitation_Transaction(data,lec);
                app.processTransaction(transaction);
                           b1.setDisable(true);

            });
               
        
    }
    public void processSelectLab(){
            AppGUIModule gui = app.getGUIModule();
            OfficeHoursData data = (OfficeHoursData)app.getDataComponent();
            Labs lec = data.getSelectedLab();
            Button b1 = (Button) gui.getGUINode(OH_REMOVELAB_BUTTON);
            b1.setDisable(false);
            b1.setOnAction(e->{
              removeLabs_Transaction transaction = new removeLabs_Transaction(data,lec);
                app.processTransaction(transaction);
                           b1.setDisable(true);

            });
               
        
    }    
    public void processOfficeHoursTimeRange(int start, int end){
        OfficeHoursData data = (OfficeHoursData) app.getDataComponent();
        data.updateTimeSlot(start, end);
        
    }
    public void processAddLecture(){
        AppGUIModule gui = app.getGUIModule();
        OfficeHoursData data = (OfficeHoursData) app.getDataComponent();
            Lectures lec = new Lectures("?","?","?","?");
            addLecture_Transaction addlectureTransaction = new addLecture_Transaction(data, lec);
            app.processTransaction(addlectureTransaction);

           
        
        app.getFoolproofModule().updateControls(OH_FOOLPROOF_SETTINGS);
    }
    public void processAddRecitation(){
                AppGUIModule gui = app.getGUIModule();
        OfficeHoursData data = (OfficeHoursData) app.getDataComponent();
            Recitations rec = new Recitations("?","?","?","?","?","?","?");
            addRecitation_Transaction addrecTransaction = new addRecitation_Transaction(data, rec);
            app.processTransaction(addrecTransaction);

           
        
        app.getFoolproofModule().updateControls(OH_FOOLPROOF_SETTINGS);
    }
       public void processAddLab(){
                AppGUIModule gui = app.getGUIModule();
        OfficeHoursData data = (OfficeHoursData) app.getDataComponent();
            Labs rec = new Labs("?","?","?","?","?","?","?");
            addLabs_Transaction addrecTransaction = new addLabs_Transaction(data, rec);
            app.processTransaction(addrecTransaction);

           
        
        app.getFoolproofModule().updateControls(OH_FOOLPROOF_SETTINGS);
    }
}