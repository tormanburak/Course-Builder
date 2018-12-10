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
import oh.data.ScheduleItem;

/**
 *
 * @author Burak Torman
 */
public class editScheduleItem_Transaction implements jTPS_Transaction {
    OfficeHoursApp app;
    ScheduleItem lec;
    String oldtype,type;
    String olddate,date;
    String oldtitle,title;
    String oldtopic,topic;
    
    OfficeHoursData data;
    public editScheduleItem_Transaction(ScheduleItem initLec,OfficeHoursData initdata, String inittype,String initdate,String inittitle,String inittopic){
        lec = initLec;
        oldtype = lec.getType();
        olddate = lec.getDate();
        oldtitle = lec.getTitle();
        oldtopic = lec.getTopic();
        type = inittype;
        date = initdate;
        title = inittitle;
        topic = inittopic;
        data = initdata;
        
    }

    @Override
    public void doTransaction() {
        
        lec.setType(type);
        lec.setTitle(title);
        lec.setDate(date);
        lec.setTopic(topic);
        data.refreshScheduleItems();

    }

    @Override
    public void undoTransaction() {
        lec.setDate(olddate);
        lec.setType(oldtype);
        lec.setTopic(oldtopic);
        lec.setTitle(oldtitle);
        data.refreshScheduleItems();
                
        
    }
    
    
}
