/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package oh.data;

/**
 *
 * @author Burak Torman
 */
public class ScheduleItem {
    public String type;
    public String date;
    public String title;
    public String topic;
    
    public ScheduleItem(String initType, String initDate, String initTitle, String initTopic){
        type = initType;
        date = initDate;
        title = initTitle;
        topic = initTopic;
    }

    public String getType() {
        return type;
    }

    public String getDate() {
        return date;
    }

    public String getTitle() {
        return title;
    }

    public String getTopic() {
        return topic;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }
    
       
    
}
