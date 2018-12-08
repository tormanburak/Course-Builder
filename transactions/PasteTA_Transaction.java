package oh.transactions;

import jtps.jTPS_Transaction;
import oh.OfficeHoursApp;
import oh.data.OfficeHoursData;
import oh.data.TeachingAssistantPrototype;

public class PasteTA_Transaction implements jTPS_Transaction {
    OfficeHoursApp app;
    TeachingAssistantPrototype taToPaste;

    public PasteTA_Transaction(  OfficeHoursApp initApp, 
                                 TeachingAssistantPrototype initTAToPaste) {
        app = initApp;
        taToPaste = initTAToPaste;
    }

    @Override
    public void doTransaction() {
        OfficeHoursData data = (OfficeHoursData)app.getDataComponent();
        data.addTA(taToPaste);
    }

    @Override
    public void undoTransaction() {
        OfficeHoursData data = (OfficeHoursData)app.getDataComponent();
        data.removeTA(taToPaste);
    }   
}