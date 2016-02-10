package com.gmail.dajinchu.stem.view;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;

import com.facebook.appevents.AppEventsLogger;
import com.gmail.dajinchu.stem.receivers.MidnightFailureChecker;
import com.gmail.dajinchu.stem.R;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    private ViewPager viewPager;
    private TabLayout tabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        viewPager = (ViewPager) findViewById(R.id.main_view_pager);
        setupViewPager(viewPager);

        tabLayout = (TabLayout) findViewById(R.id.main_tabs);
        tabLayout.setupWithViewPager(viewPager);

        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener(){

            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        AlarmManager am = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, MidnightFailureChecker.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this,0,intent,0);
        am.cancel(pendingIntent);

        Calendar triggerTime = Calendar.getInstance();
        triggerTime.set(Calendar.HOUR_OF_DAY,23);
        triggerTime.set(Calendar.MINUTE,59);
        triggerTime.set(Calendar.SECOND,50);

        am.setRepeating(AlarmManager.RTC_WAKEUP,triggerTime.getTimeInMillis(),24*60*60*1000,pendingIntent);
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFrag(new CheckInFragment(),"CHECK IN");
        adapter.addFrag(new ViewProgressFragment(),"PROGRESS");
        viewPager.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        return false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        AppEventsLogger.activateApp(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        AppEventsLogger.deactivateApp(this);

    }
}
