package com.gmail.dajinchu.stem;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.orm.SugarRecord;
import com.orm.dsl.Ignore;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.List;

/**
 * Created by Da-Jin on 12/7/2015.
 */
public class Habit extends SugarRecord{
    private String _name;
    private long _timeToDo;
    private int _days;

    @Ignore
    private static DateFormat format = DateFormat.getDateTimeInstance();

    //Getters and Setters
    public List<Completion> getCompletions(){
        return Completion.find(Completion.class,"habit = ?", String.valueOf(getId()));
    }
    public String getName(){
        return _name;
    }
    public void setName(String name){
        _name = name;
    }
    public Calendar getTimeToDo(){
        Calendar temp = Calendar.getInstance();
        temp.setTimeInMillis(_timeToDo);
        return temp;
    }
    public void setTimeToDo(Calendar timeToDo){
        _timeToDo = timeToDo.getTimeInMillis();
    }
    public boolean[] getDays(){
        boolean[] temp = new boolean[7];
        for(int i = 0; i < 7; i++){
            temp[6-i] = (_days & (1<<i))!=0;
        }
        return temp;
    }
    public void setDays(boolean[] days){
        for(int i = 0; i < 7; i++){
            _days = (_days << 1) + (days[i] ? 1 : 0);
        }
    }


    public Habit(){

    }

    public Habit(String name, Calendar time, boolean[] days){
        setName(name);
        setTimeToDo(time);
        setDays(days);
    }

    public void addCompletionNow(){
        Calendar c = Calendar.getInstance();

        new Completion(c,Completion.SUCCESSFUL,this).save();

        updateTimeToDo();
        save();

        //TODO unsure if this will be needed
        /*c.add(Calendar.DATE, 1);
        nextIncomplete = c.getTimeInMillis();
        save();*/
    }

    public boolean isCompletedNow(){
        if(getCompletions().size()==0)return false;
        int dayIndex = (Calendar.getInstance().get(Calendar.DAY_OF_WEEK)-2)%7;
        int offset = 0;
        while(!getDays()[(dayIndex+offset)%7]){
            offset--;
        }
        Calendar lastOccurence = Calendar.getInstance();
        lastOccurence.set(Calendar.MILLISECOND, 0);
        lastOccurence.set(Calendar.SECOND, 0);
        lastOccurence.set(Calendar.MINUTE, 0);
        lastOccurence.set(Calendar.HOUR_OF_DAY, 0);
        lastOccurence.add(Calendar.DATE, offset);

        Log.d("Habit","last completed "+lastOccurence.toString());
        return getCompletions().get(getCompletions().size()-1).getCompletionTime()
                .after(lastOccurence);
    }

    public int daysToNextOccurence(){
        int todayIndex = (Calendar.getInstance().get(Calendar.DAY_OF_WEEK)-2)%7;
        int offset = 1;
        while(!getDays()[(todayIndex+offset)%7]){
            offset++;
        }
        return offset;
    }

    public int successfulCompletions(){
        int total = 0;
        for(Completion comp : getCompletions()){
            if(comp.getSuccessCode() ==Completion.SUCCESSFUL){
                total++;
            }
        }
        return total;
    }

    public void setDayOfWeek(int dayNum, boolean isOnThisDay){
        boolean[] days = getDays();
        days[dayNum]=isOnThisDay;
        setDays(days);
    }

    public void updateTimeToDo(){
        Calendar now = Calendar.getInstance();
        Calendar timeToDo = getTimeToDo();
        timeToDo.set(now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DATE));
        setTimeToDo(timeToDo);
        Log.d("Habit", format.format(getTimeToDo().getTime()));

        if(timeToDo.before(now)){
            //timeToDo already happened today, set to next
            timeToDo.add(Calendar.DATE, daysToNextOccurence());
            Log.d("Habit",format.format(timeToDo.getTime()));
        }
    }

    public void updateNotification(Context context){
        AlarmManager am = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, TimeToDoReceiver.class);
        intent.putExtra(NotificationPublisher.HABIT_ID, getId().intValue());
        PendingIntent alarmIntent = PendingIntent.getBroadcast(context, getId().intValue(), intent, 0);
        am.cancel(alarmIntent);

        updateTimeToDo();

        am.setRepeating(AlarmManager.RTC_WAKEUP,getTimeToDo().getTimeInMillis(),24*60*60*1000,alarmIntent);
    }
}
