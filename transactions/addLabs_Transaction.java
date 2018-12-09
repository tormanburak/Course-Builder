package oh.transactions;

import jtps.jTPS_Transaction;
import oh.data.Labs;
import oh.data.Lectures;
import oh.data.OfficeHoursData;

/**
 *
 * @author McKillaGorilla
 */
public class addLabs_Transaction implements jTPS_Transaction {
    OfficeHoursData data;
    Labs lab;
    
    public addLabs_Transaction(OfficeHoursData initData, Labs initLab) {
        data = initData;
        lab = initLab;
    }

    @Override
    public void doTransaction() {
        data.addLab(lab);        
    }

    @Override
    public void undoTransaction() {
        data.removeLab(lab);
    }
}
