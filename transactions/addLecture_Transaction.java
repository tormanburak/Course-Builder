package oh.transactions;

import jtps.jTPS_Transaction;
import oh.data.Lectures;
import oh.data.OfficeHoursData;
import oh.data.TeachingAssistantPrototype;

/**
 *
 * @author McKillaGorilla
 */
public class addLecture_Transaction implements jTPS_Transaction {
    OfficeHoursData data;
    Lectures lec;
    
    public addLecture_Transaction(OfficeHoursData initData, Lectures initlec) {
        data = initData;
        lec = initlec;
    }

    @Override
    public void doTransaction() {
        data.addLecture(lec);        
    }

    @Override
    public void undoTransaction() {
        data.removeLecture(lec);
    }
}
