/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package oh.transactions;

import jtps.jTPS_Transaction;
import oh.data.Lectures;
import oh.data.OfficeHoursData;

/**
 *
 * @author Burak Torman
 */
public class removeLecture_Transaction implements jTPS_Transaction{
    OfficeHoursData data;
    Lectures lec;
    
    public removeLecture_Transaction(OfficeHoursData initData, Lectures initLec){
        data = initData;
        lec = initLec;
        
    }

    @Override
    public void doTransaction() {
        data.removeLecture(lec);
    }

    @Override
    public void undoTransaction() {
        data.addLecture(lec);
    }
    
}
