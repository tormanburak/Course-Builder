/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package oh.transactions;

import jtps.jTPS_Transaction;
import oh.data.Labs;
import oh.data.OfficeHoursData;

/**
 *
 * @author Burak Torman
 */
public class removeLabs_Transaction implements jTPS_Transaction{
    OfficeHoursData data;
    Labs lab;
    
    public removeLabs_Transaction(OfficeHoursData initData, Labs initlab){
        data = initData;
        lab = initlab;
        
    }

    @Override
    public void doTransaction() {
        data.removeLab(lab);
    }

    @Override
    public void undoTransaction() {
        data.addLab(lab);
    }
    
}
