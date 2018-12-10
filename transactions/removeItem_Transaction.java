/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package oh.transactions;

import jtps.jTPS_Transaction;
import oh.data.OfficeHoursData;
import oh.data.ScheduleItem;

/**
 *
 * @author Burak Torman
 */
public class removeItem_Transaction implements jTPS_Transaction{
    OfficeHoursData data;
    ScheduleItem item;
    
    public removeItem_Transaction(OfficeHoursData initData, ScheduleItem initItem){
        data = initData;
        item = initItem;
        
    }

    @Override
    public void doTransaction() {
        data.removeScheduleItem(item);
    }

    @Override
    public void undoTransaction() {
        data.addScheduleItem(item);
    }
    
}
