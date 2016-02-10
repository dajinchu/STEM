package com.gmail.dajinchu.stem;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.orm.SugarRecord;
import com.orm.dsl.Ignore;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by Da-Jin on 12/7/2015.
 */
public class Routine extends SugarRecord implements ParentRecord {
    private String _name;
    private String _relativity;
    private String _cue;
    private long _timeToDo;
    private int _days;
    private int _backupMins;

    @Ignore
    private static DateFormat format = DateFormat.getDateTimeInstance();
    @Ignore
    private static List<RoutineListener> subs = new ArrayList<>();
    @Ignore
    private List<Completion> cachedCompletions;
    @Ignore
    private static String[] choiceNames = new String[]{"5 minutes", "Half hour", "1 hour", "2 hours"};
    @Ignore
    private static Integer[] choiceValues = new Integer[]{5,30,60,120};

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
    public String getRelativity(){return _relativity;}
    public void setRelativity(String relativity){_relativity=relativity;}
    public String getCue(){return _cue;}
    public void setCue(String cue){_cue = cue;}
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
        this("","","",choiceValues[1],Calendar.getInstance(),new boolean[]{true,true,true,true,true,true,true});
    }

    public Routine(String name, String rel, String cue, int mins, Calendar time, boolean[] days){
        setName(name);
        setRelativity(rel);
        setCue(cue);
        setTimeToDo(time);
        setDays(days);
        setBackupMinutes(mins);
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
        Intent reminderIntent = new Intent(context, TimeToDoReceiver.class);
        reminderIntent.putExtra(NotificationPublisher.ROUTINE_ID, getId().intValue());
        PendingIntent reminderPending = PendingIntent.getBroadcast(context, getId().intValue(), reminderIntent, 0);
        am.cancel(reminderPending);

        Intent backupIntent = new Intent(context, BackupAlarmReceiver.class);
        backupIntent.putExtra(NotificationPublisher.ROUTINE_ID, getId().intValue());
        PendingIntent backupPending = PendingIntent.getBroadcast(context, getId().intValue(), backupIntent,0);
        am.cancel(backupPending);

        updateTimeToDo();
        am.setRepeating(AlarmManager.RTC_WAKEUP,getTimeToDo().getTimeInMillis(),24*60*60*1000,reminderPending);
        Log.d("Routine","backup at "+getBackupMinutes()*60*1000);
        am.setRepeating(AlarmManager.RTC_WAKEUP,
                getTimeToDo().getTimeInMillis()+getBackupMinutes()*60*1000,
                24*60*60*1000,
                backupPending);
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


    public void notifyAllSubscribers() {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                for (RoutineListener sub : subs) {//TODO use a central model class that caches
                    sub.update(Routine.this);
                }
            }
        });
    }
    public static void unsubscribe(RoutineListener subscriber) {
        subs.remove(subscriber);
    }

    public static void subscribe(RoutineListener subscriber) {
        subs.add(subscriber);
    }

    public int getBackupMinutes() {
        return _backupMins;
    }

    public void setBackupMinutes(int backupMinutes) {
        this._backupMins = backupMinutes;
    }

    public static String[] possibleBackupChoices(){
        return choiceNames;
    }

    public static int minuteStringToInt(String s){
        for(int i = 0; i<choiceNames.length; i++){
            if(s.equals(choiceNames[i])){
                return choiceValues[i];
            }
        }
        return 1;
    }
    public static String minuteIntToString(int minInt){
        for(int i = 0; i<choiceValues.length; i++){
            if(minInt==choiceValues[i]){
                return choiceNames[i];
            }
        }
        return "";
    }
}
