package oh.transactions;

import jtps.jTPS_Transaction;
import oh.data.Labs;
import oh.data.OfficeHoursData;
import oh.data.ScheduleItem;

/**
 *
 * @author McKillaGorilla
 */
public class addScheduleItem_Transaction implements jTPS_Transaction {
    OfficeHoursData data;
    ScheduleItem item;
    
    public addScheduleItem_Transaction(OfficeHoursData initData, ScheduleItem initItem) {
        data = initData;
        item = initItem;
    }

    @Override
    public void doTransaction() {
        data.addScheduleItem(item);
    }

    @Override
    public void undoTransaction() {
        data.removeScheduleItem(item);
    }
}
