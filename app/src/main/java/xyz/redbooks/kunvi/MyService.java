package xyz.redbooks.kunvi;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.telephony.SmsManager;
import android.util.Log;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import xyz.redbooks.kunvi.database.AppDatabase;

public class MyService extends Service {

    static int count = 0;
    private static BroadcastReceiver mybroadcast;
    public MyService() {

    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate(){
        registerReceiver();
    }

    @Override
    public void onDestroy(){
        unregisterReceiver(mybroadcast);
    }

    private void registerReceiver(){
        mybroadcast = new BroadcastReceiver() {
            //When Event is published, onReceive method is called
            @Override
            public void onReceive(Context context, Intent intent) {

                if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {
                    Log.i("[BroadcastReceiver]", "Screen ON");
                    count++;
                }
                else if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
                    Log.i("[BroadcastReceiver]", "Screen OFF");
                    count++;
                }

                Log.d("Count", Integer.toString(count));

                if(count == 2) {
                    new Timer().schedule(new TimerTask() {
                        @Override
                        public void run() {
                            // this code will be executed after 2 seconds
                            count = 0;
                        }
                    }, 3000);
                }

                if(count == 4) {
                    Log.i("[BroadcastReceiver]", "Power button clicked Four times");
                    AppDatabase db = AppDatabase.getAppDatabase(getApplicationContext());

                    List<String> mobNumber = db.contactDao().getAllContactsNumber();
                    SmsManager smsManager = SmsManager.getDefault();

                    for(String number : mobNumber) {
                        smsManager.sendTextMessage(number,null, "This is a test text", null, null);
                        Log.d("MSG", "sent message to " + number);
                    }
                    count = 0;
                }

                // do your stuff with 2 counts(four presses) and set it to 0 again
                // after 3 seconds set it to 0

            }
        };

                /// Register the broadcast receiver
        registerReceiver(mybroadcast, new IntentFilter(Intent.ACTION_SCREEN_ON));
        registerReceiver(mybroadcast, new IntentFilter(Intent.ACTION_SCREEN_OFF));
    }
}
