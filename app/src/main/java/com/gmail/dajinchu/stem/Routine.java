package com.gmail.dajinchu.stem;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.orm.dsl.Ignore;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.List;

/**
 * Created by Da-Jin on 12/7/2015.
 */
public class Routine extends SubscribableSugarRecord implements ParentRecord {
    private String _name;
    private long _timeToDo;
    private int _days;

    @Ignore
    private static DateFormat format = DateFormat.getDateTimeInstance();
    @Ignore
    private List<Completion> cachedCompletions;

    //Getters and Setters
    public List<Completion> getCompletions(){
        if(cachedCompletions==null){
            refreshCompletionCache();
        }
        return cachedCompletions;
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


    public Routine(){
        this("",Calendar.getInstance(),new boolean[]{true,true,true,true,true,true,true});
    }

    public Routine(String name, Calendar time, boolean[] days){
        setName(name);
        setTimeToDo(time);
        setDays(days);
    }

    public void addCompletionNow(){
        Calendar c = Calendar.getInstance();

        new Completion(c,Completion.SUCCESSFUL,this).save();

        //TODO unsure if this will be needed
        /*c.add(Calendar.DATE, 1);
        nextIncomplete = c.getTimeInMillis();
        save();*/
    }

    public boolean isCompletedNow(){
        return isCompletedAtTime(Calendar.getInstance());
    }

    public boolean isCompletedAtTime(Calendar checkTime){
        Bench.start("isCompletedAtTime");
        Bench.start("getCompletions");
        List<Completion> completions = getCompletions();
        Bench.end("getCompletions");
        if(completions.size()==0)return false;

        Calendar dayBegin = Calendar.getInstance();
        dayBegin.setTimeInMillis(checkTime.getTimeInMillis());
        dayBegin.set(Calendar.MILLISECOND, 0);
        dayBegin.set(Calendar.SECOND, 0);
        dayBegin.set(Calendar.MINUTE, 0);
        dayBegin.set(Calendar.HOUR_OF_DAY, 0);

        for(Completion c : completions){
            //See if there is a completion between when this day began, and the 'current' time
            //If there is then, the routine is 'currently' completed
            if(c.getSuccessCode()==Completion.SUCCESSFUL
                    &&dayBegin.before(c.getCompletionTime())
                    &&checkTime.after(c.getCompletionTime())){
                Bench.end("isCompletedAtTime");
                return true;
            }
        }
        Bench.end("isCompletedAtTime");

        return false;
    }

    //TODO figure out how to not need this stupid shit
    public static int calendarDayWeekToDisplay(int index){
        int r = (index-2)%7;
        return r<0 ? r+7 : r;
    }

    public int daysToNextOccurence(){
        int todayIndex = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
        int offset = 1;
        while(!getDays()[calendarDayWeekToDisplay(todayIndex+offset)]){
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
        timeToDo.set(Calendar.SECOND,0);
        setTimeToDo(timeToDo);

        if(timeToDo.before(now)){
            //timeToDo already happened today, set to next
            timeToDo.add(Calendar.DATE, daysToNextOccurence());
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

    @Override
    public void childUpdated() {
        refreshCompletionCache();
        notifyAllSubscribers();
    }

    @Override
    public long save() {
        long save = super.save();
        notifyAllSubscribers();
        return save;
    }

    private void refreshCompletionCache(){
        cachedCompletions = Completion.find(Completion.class,"routine = ?", String.valueOf(getId()));
    }
}
