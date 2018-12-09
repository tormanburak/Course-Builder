/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package oh.transactions;

import jtps.jTPS_Transaction;
import oh.data.OfficeHoursData;
import oh.data.Recitations;

/**
 *
 * @author Burak Torman
 */
public class removeRecitation_Transaction implements jTPS_Transaction{
    OfficeHoursData data;
    Recitations rec;
    
    public removeRecitation_Transaction(OfficeHoursData initData, Recitations initrec){
        data = initData;
        rec = initrec;
        
    }

    @Override
    public void doTransaction() {
        data.removeRecitation(rec);
    }

    @Override
    public void undoTransaction() {
        data.addRecitation(rec);
    }
    
}
