/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package oh.transactions;

import djf.modules.AppGUIModule;
import jtps.jTPS_Transaction;
import oh.OfficeHoursApp;
import oh.data.Lectures;
import oh.data.OfficeHoursData;

/**
 *
 * @author Burak Torman
 */
public class editLecture_Transaction implements jTPS_Transaction {
      OfficeHoursApp app;
    Lectures lec;
    String oldsection,newsection;
    String oldroom,newroom;
    String olddays,newdays;
    String oldtime,newtime;
    OfficeHoursData data;
    public editLecture_Transaction(Lectures initLec,OfficeHoursData initdata, String initsec,String initroom,String initdays,String inittime){
        lec = initLec;
        oldsection = initLec.getSection();
        oldroom = initLec.getRoom();
        olddays = initLec.getDays();
        oldtime = initLec.getTime();
        newsection = initsec;
        newroom = initroom;
        newdays = initdays;
        newtime = inittime;
        data = initdata;
        
    }

    @Override
    public void doTransaction() {
        lec.setSection(newsection);
        lec.setDays(newdays);
        lec.setRoom(newroom);
        lec.setTime(newtime);
        data.refreshLectures();

    }

    @Override
    public void undoTransaction() {
        lec.setSection(oldsection);
        lec.setDays(olddays);
        lec.setRoom(oldroom);
        lec.setTime(oldtime);

        data.refreshLectures();
                
        
    }
    
    
}
