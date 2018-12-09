package oh.transactions;

import jtps.jTPS_Transaction;
import oh.data.OfficeHoursData;
import oh.data.Recitations;

/**
 *
 * @author McKillaGorilla
 */
public class addRecitation_Transaction implements jTPS_Transaction {
    OfficeHoursData data;
    Recitations rec;
    
    public addRecitation_Transaction(OfficeHoursData initData, Recitations initrec) {
        data = initData;
        rec = initrec;
    }

    @Override
    public void doTransaction() {
        data.addRecitation(rec);        
    }

    @Override
    public void undoTransaction() {
        data.removeRecitation(rec);
    }
}
