package com.example.sallarkhan.fixuos_admin;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.TaskStackBuilder;
import android.bluetooth.BluetoothClass;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v7.app.NotificationCompat;
import android.text.Html;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by Sallar Khan on 10/28/2017.
 */
public class FirebaseNotificationServices extends Service{

    public FirebaseDatabase mDatabase;
    FirebaseAuth firebaseAuth;
    Context context;
    static String TAG = "FirebaseService";

    @Override
    public void onCreate() {
        super.onCreate();
        context = this;



        mDatabase = FirebaseDatabase.getInstance();
      //  firebaseAuth = FirebaseAuth.getInstance();

        setupNotificationListener();
    }



    private void setupNotificationListener() {

        mDatabase.getReference().child("fixuos").child("blog")
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        if(dataSnapshot != null){
                            Notifications notification = dataSnapshot.getValue(Notifications.class);

                            showNotification(context,notification,dataSnapshot.getKey());
                        }
                    }

                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                   //     Utilities.log("onChildChanged",dataSnapshot);
                    }

                    @Override
                    public void onChildRemoved(DataSnapshot dataSnapshot) {
                    //    Utilities.log("onChildRemoved",dataSnapshot);
                    }

                    @Override
                    public void onChildMoved(DataSnapshot dataSnapshot, String s) {
//                        Utilities.log("onChildMoved",dataSnapshot);

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
  //                      Utilities.log("onCancelled",databaseError);
                    }
                });


    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return Service.START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private void showNotification(Context context, Notifications notification, String notification_key){
        NotificationCompat.Builder mBuilder = (NotificationCompat.Builder) new NotificationCompat.Builder(context)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(notification.getDesc())
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setContentText(Html.fromHtml(notification.getTitle()
                ))
                .setAutoCancel(true);

        Intent backIntent = new Intent(context, MainActivity.class);
        backIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        Intent intent = new Intent(context, MainActivity.class);

       //   Use the notification type to switch activity to stack on the main activity
      /*  if(notification.getTyp().equals("chat_view")){
            intent = new Intent(context, FriendsView.class);
        }*/


        /*final PendingIntent pendingIntent = PendingIntent.getActivities(context, 900,
                new Intent[] {backIntent}, PendingIntent.FLAG_ONE_SHOT);
*/

   //     TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
   //     stackBuilder.addParentStack(MainActivity.class);

    //    mBuilder.setContentIntent(pendingIntent);


        NotificationManager mNotificationManager =  (NotificationManager)context. getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(1, mBuilder.build());

        /* Update firebase set notifcation with this key to 1 so it doesnt get pulled by our notification listener*/
        flagNotificationAsSent(notification_key);
    }

    private void flagNotificationAsSent(String notification_key) {
        mDatabase.getReference().child("notifications")
                .child(firebaseAuth.getCurrentUser().getUid())
                .child(notification_key)
                .child("status")
                .setValue(1);
    }

}