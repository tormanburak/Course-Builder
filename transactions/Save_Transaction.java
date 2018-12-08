package oh.transactions;

import jtps.jTPS_Transaction;
import oh.data.OfficeHoursData;
import oh.data.TeachingAssistantPrototype;

/**
 *
 * @author McKillaGorilla
 */
public class Save_Transaction implements jTPS_Transaction {
    OfficeHoursData data;
    TeachingAssistantPrototype ta;
    
    public Save_Transaction() {
        
    }

    @Override
    public void doTransaction() {
    }

    @Override
    public void undoTransaction() {
    }
}
