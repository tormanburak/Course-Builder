package oh.workspace.dialogs;

import djf.modules.AppGUIModule;
import static djf.modules.AppGUIModule.ENABLED;
import djf.ui.AppNodesBuilder;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import oh.OfficeHoursApp;
import oh.data.OfficeHoursData;
import oh.data.TAType;
import oh.data.TeachingAssistantPrototype;
import static oh.workspace.style.OHStyle.*;
import static oh.OfficeHoursPropertyType.*;
import oh.transactions.EditTA_Transaction;
import oh.workspace.foolproof.OfficeHoursFoolproofDesign;

/**
 *
 * @author McKillaGorilla
 */
public class TADialog extends Stage {
    OfficeHoursApp app;

    TeachingAssistantPrototype taToEdit;
    TeachingAssistantPrototype editTA;

    EventHandler cancelHandler;
    EventHandler editTAOkHandler;
    
    public TADialog(OfficeHoursApp initApp) {
        // KEEP THIS FOR WHEN THE WORK IS ENTERED
        app = initApp;

        // LAYOUT THE UI
        initDialog();

        // NOW PUT THE GRID IN THE SCENE AND THE SCENE IN THE DIALOG
        GridPane gridPane = (GridPane)app.getGUIModule().getGUINode(OH_TA_DIALOG_GRID_PANE);
        Scene scene = new Scene(gridPane);
        this.setScene(scene);
        
        // SETUP THE STYLESHEET
        app.getGUIModule().initStylesheet(this);                            
        
        // MAKE IT MODAL
        this.initOwner(app.getGUIModule().getWindow());
        this.initModality(Modality.APPLICATION_MODAL);
    }    
    
    private void initDialog() {
        AppNodesBuilder guiBuilder = app.getGUIModule().getNodesBuilder();
        
        // THE NODES ABOVE GO DIRECTLY INSIDE THE GRID
        GridPane gridPane = guiBuilder.buildGridPane(OH_TA_DIALOG_GRID_PANE, null, CLASS_OH_DIALOG_GRID_PANE, ENABLED);
        guiBuilder.buildLabel(OH_TA_DIALOG_HEADER_LABEL, gridPane, 0, 0, 3, 1, CLASS_OH_DIALOG_HEADER, ENABLED);
        guiBuilder.buildLabel(OH_TA_DIALOG_NAME_LABEL, gridPane, 0, 1, 1, 1, CLASS_OH_DIALOG_PROMPT, ENABLED);
        TextField nameTextField = guiBuilder.buildTextField(OH_TA_DIALOG_NAME_TEXT_FIELD, gridPane, 1, 1, 2, 1, CLASS_OH_DIALOG_TEXT_FIELD, ENABLED);
        guiBuilder.buildLabel(OH_TA_DIALOG_EMAIL_LABEL, gridPane, 0, 2, 1, 1, CLASS_OH_DIALOG_PROMPT, ENABLED);
        TextField emailTextField = guiBuilder.buildTextField(OH_TA_DIALOG_EMAIL_TEXT_FIELD, gridPane, 1, 2, 2, 1, CLASS_OH_DIALOG_TEXT_FIELD, ENABLED);
        guiBuilder.buildLabel(OH_TA_DIALOG_TYPE_LABEL, gridPane, 0, 3, 1, 1, CLASS_OH_DIALOG_PROMPT, ENABLED);
        ToggleGroup tg = new ToggleGroup();
        RadioButton gradRadioButton = guiBuilder.buildRadioButton(OH_TA_DIALOG_GRAD_RADIO_BUTTON, gridPane, 1, 3, 1, 1, CLASS_OH_DIALOG_RADIO_BUTTON, ENABLED, tg, true);
        RadioButton undergradRadioButton = guiBuilder.buildRadioButton(OH_TA_DIALOG_UNDERGRAD_RADIO_BUTTON, gridPane, 2, 3, 1, 1, CLASS_OH_DIALOG_RADIO_BUTTON, ENABLED, tg, false);
        HBox okCancelBox = guiBuilder.buildHBox(OH_TA_DIALOG_OK_BOX, gridPane, 0, 4, 3, 1, CLASS_OH_DIALOG_BOX, ENABLED);
        okCancelBox.setAlignment(Pos.CENTER);
        Button okButton = guiBuilder.buildTextButton(OH_TA_DIALOG_OK_BUTTON, okCancelBox, CLASS_OH_DIALOG_BUTTON, !ENABLED);
        Button cancelButton = guiBuilder.buildTextButton(OH_TA_DIALOG_CANCEL_BUTTON, okCancelBox, CLASS_OH_DIALOG_BUTTON, ENABLED);
       
        // AND SETUP THE EVENT HANDLERS
        nameTextField.setOnAction(e->{
            processCompleteWork();
        });
        emailTextField.setOnAction(e->{
            processCompleteWork();
        });
        okButton.setOnAction(e->{
            processCompleteWork();
        });
        // VERIFY ENTERED TEXT
        nameTextField.textProperty().addListener(e->{
            processDataChange();
        });
        emailTextField.textProperty().addListener(e->{
            processDataChange();
        });
        gradRadioButton.setOnAction(e->{
            processDataChange();
        });
        undergradRadioButton.setOnAction(e->{
            processDataChange();
        });
        
        cancelButton.setOnAction(e->{
            editTA = null;
            this.hide();
        });   
    }
    
    private void processDataChange() {
        AppGUIModule gui = app.getGUIModule();
        TextField nameTextField = ((TextField)gui.getGUINode(OH_TA_DIALOG_NAME_TEXT_FIELD));
        TextField emailTextField = ((TextField)gui.getGUINode(OH_TA_DIALOG_EMAIL_TEXT_FIELD));
        RadioButton radioButton = ((RadioButton)gui.getGUINode(OH_TA_DIALOG_GRAD_RADIO_BUTTON));
        Button okButton = ((Button)gui.getGUINode(OH_TA_DIALOG_OK_BUTTON));
        String name = nameTextField.getText();
        String email = emailTextField.getText();
        TAType type = TAType.Undergraduate;
        if (radioButton.isSelected())
            type = TAType.Graduate;
        
        // WE'LL JUST MAKE A LITTLE TEMPORARY ONE OF THESE
        // SO THAT WE CAN FOOLPROOF DESIGN THESE CONTROLS
        OfficeHoursFoolproofDesign fP = new OfficeHoursFoolproofDesign(app);

        // IF THE NAME, EMAIL, OR TYPE HAS NOT CHANGED THERE
        // IS NO REASON TO DO ANYTHING
        if (taToEdit.getName().equals(name) 
                && taToEdit.getEmail().equals(email)
                && taToEdit.getType().equals(type.toString())) {
            // DISABLE THE OK BUTTON SO THE USER DOESN'T THINK
            // CLICKING IT WILL DO ANY GOOD
            okButton.setDisable(true);
            fP.foolproofTextField(nameTextField, true);
            fP.foolproofTextField(emailTextField, true);
        }
        // OTHERWISE WE'LL HAVE TO FOOLPROOF DESIGN THIS STUFF
        // BASED ON WHETHER IT'S A LEGAL EDIT OR NOT
        else {
            OfficeHoursData data = (OfficeHoursData)app.getDataComponent();
            boolean isValidNameEdit = data.isValidNameEdit(taToEdit, name);
            fP.foolproofTextField(nameTextField, isValidNameEdit);
            boolean isValidEmailEdit = data.isValidEmailEdit(taToEdit, email);
            fP.foolproofTextField(emailTextField, isValidEmailEdit);
            
            if ((!isValidNameEdit) || (!isValidEmailEdit)) {
                okButton.setDisable(true);
            }
            else {
                okButton.setDisable(false);
            }
        }
    }

    private void processCompleteWork() {
        // GET THE SETTINGS
        String name = ((TextField)app.getGUIModule().getGUINode(OH_TA_DIALOG_NAME_TEXT_FIELD)).getText();
        String email = ((TextField)app.getGUIModule().getGUINode(OH_TA_DIALOG_EMAIL_TEXT_FIELD)).getText();
        RadioButton undergradRadioButton = ((RadioButton)app.getGUIModule().getGUINode(OH_TA_DIALOG_UNDERGRAD_RADIO_BUTTON));
        TAType type = TAType.Graduate;
        if (undergradRadioButton.isSelected()) {
            type = TAType.Undergraduate;
        }

        // IF THE NAME, EMAIL, OR TYPE HAS NOT CHANGED THERE
        // IS NO REASON TO DO ANYTHING
        if (taToEdit.getName().equals(name) 
                && taToEdit.getEmail().equals(email)
                && taToEdit.getType().equals(type.toString())) {
            return;
        }

        // WE NOW KNOW SOMETHING HAS CHANGED
        // WE NEED A TRANSACTION FOR THE EDIT
        EditTA_Transaction transaction = new EditTA_Transaction(taToEdit, name, email, type.toString());
        app.processTransaction(transaction);
        
        // MAKE SURE THE TABLES ARE USING THE CORRECT TYPES
        OfficeHoursData data = (OfficeHoursData)app.getDataComponent();
        data.updateTAs();
        
        // CLOSE THE DIALOG
        this.hide();
    }

    public void showEditDialog(TeachingAssistantPrototype initTAToEdit) {
        // WE'LL NEED THIS FOR VALIDATION
        taToEdit = initTAToEdit;
        
        // WE'LL ONLY PROCEED IF THERE IS A LINE TO EDIT
        editTA = null;
        
        // USE THE TEXT IN THE HEADER FOR EDIT
        TextField nameTextField = ((TextField)app.getGUIModule().getGUINode(OH_TA_DIALOG_NAME_TEXT_FIELD));
        TextField emailTextField = ((TextField)app.getGUIModule().getGUINode(OH_TA_DIALOG_EMAIL_TEXT_FIELD));
        nameTextField.setText(taToEdit.getName());
        emailTextField.setText(taToEdit.getEmail());
        if (initTAToEdit.getType().equals(TAType.Graduate.toString())) {
            ((RadioButton)app.getGUIModule().getGUINode(OH_TA_DIALOG_GRAD_RADIO_BUTTON)).setSelected(true);
        }
        else {
            ((RadioButton)app.getGUIModule().getGUINode(OH_TA_DIALOG_UNDERGRAD_RADIO_BUTTON)).setSelected(true);
        }
        
        // NOTHING HAS CHANGED WHEN THIS LOADS SO WE'LL
        // START WITH THE OK BUTTON DISABLED
        ((Button)app.getGUIModule().getGUINode(OH_TA_DIALOG_OK_BUTTON)).setDisable(true);
        
        // MAKE SURE OUR TEXT FIELDS START OFF PROPERLY
        OfficeHoursFoolproofDesign fP = new OfficeHoursFoolproofDesign(app);
        fP.foolproofTextField(nameTextField, true);
        fP.foolproofTextField(emailTextField, true);
                       
        // AND OPEN THE DIALOG
        showAndWait();
    }
    
    public TeachingAssistantPrototype getEditTA() {
        return editTA;
    }
}