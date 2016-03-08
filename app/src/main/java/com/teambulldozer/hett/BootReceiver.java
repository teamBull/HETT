package com.teambulldozer.hett;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by yoon on 16. 3. 8..
 */
public class BootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if ("android.intent.action.BOOT_COMPLETED".equals(intent.getAction())) {

            Log.d("receiver", "The Receiver is called");
            Intent mServiceIntent = new Intent(context, AlarmOnBoot.class);
            context.startService(mServiceIntent);

        } else {
            Log.d("boot receiver ",
                    "Something went wrong with the boot complete maybe with permissions ");
        }
    }
}
