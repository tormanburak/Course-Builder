/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package oh.transactions;

import djf.modules.AppGUIModule;
import jtps.jTPS_Transaction;
import oh.OfficeHoursApp;
import oh.data.Labs;
import oh.data.Lectures;
import oh.data.OfficeHoursData;
import oh.data.Recitations;

/**
 *
 * @author Burak Torman
 */
public class editLab_Transaction implements jTPS_Transaction {
      OfficeHoursApp app;
    Labs lec;
    String oldsection,newsection;
    String oldroom,newroom;
    String olddays,newdays;
    String oldta1,newta1;
    String oldta2,newta2;
    OfficeHoursData data;
    public editLab_Transaction(Labs initLec,OfficeHoursData initdata, String initsec,String initroom,String initdays,String initta1,String initta2){
        lec = initLec;
        oldsection = initLec.getSection();
        oldroom = initLec.getRoom();
        olddays = initLec.getDaysTime();
        oldta1 = initLec.getTa1();
        oldta2 = initLec.getTa2();
        newsection = initsec;
        newroom = initroom;
        newdays = initdays;
        newta1 = initta1;
        newta2 = initta2;
        data = initdata;
        
    }

    @Override
    public void doTransaction() {
        lec.setSection(newsection);
        lec.setDaysTime(newdays);
        lec.setRoom(newroom);
        lec.setTa1(newta1);
        lec.setTa2(newta2);
        data.refreshLabs();

    }

    @Override
    public void undoTransaction() {
        lec.setSection(oldsection);
        lec.setDaysTime(olddays);
        lec.setRoom(oldroom);
        lec.setTa1(oldta1);
        lec.setTa2(oldta2);

        data.refreshLabs();
                
        
    }
    
    
}
