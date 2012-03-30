package edu.stanford.mobisocial.games.wordplay;

import mobisocial.socialkit.musubi.DbObj;
import mobisocial.socialkit.musubi.Musubi;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

public class NotificationReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Uri objUri = intent.getParcelableExtra("objUri");
        if (objUri == null) {
            Log.i("WordPlayNotification", "No object found");
            return;
        }

        DbObj obj = Musubi.forIntent(context, intent).objForUri(objUri);
        if (obj.getSender().isOwned()) {
            return;
        }
        NotificationManager nm = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
        Notification notification = new Notification(R.drawable.notification, "Move made in WordPlay", System.currentTimeMillis());
        notification.flags = Notification.FLAG_AUTO_CANCEL;

        Intent notificationIntent = new Intent(context, WordPlayActivity.class);
        notificationIntent.setData(objUri);
        PendingIntent contentIntent = PendingIntent.getActivity(context, 0, notificationIntent, 0);
        notification.setLatestEventInfo(context, "Move made in WordPlay", "Someone made a move.", contentIntent);
        nm.notify(0, notification);

        // Dont notify in Musubi
        Bundle b = new Bundle();
        b.putInt("notification", 0);
        setResult(Activity.RESULT_OK, null, b);
    }
}
