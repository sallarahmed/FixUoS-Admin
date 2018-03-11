package com.example.sallarkhan.fixuos_admin;

import android.app.Notification;

import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Sallar Khan on 10/28/2017.
 */

public class Notifications {

    String title;
    String desc;


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public Notifications() {
    }


    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("title", title);
        result.put("description", desc);
        return result;
    }

    public static void sendNotification(String user_id,String message,String description,String type){
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("notifications").child(user_id);
        String pushKey = databaseReference.push().getKey();

        Notifications notification = new Notifications();
        notification.setDesc(description);
        notification.setTitle(message);
      //  notification.setUser_id(user_id);
      //  notification.setType(type);

        Map<String, Object> forumValues = notification.toMap();
        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put(pushKey, forumValues);
        databaseReference.setPriority(ServerValue.TIMESTAMP);
        databaseReference.updateChildren(childUpdates, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                if(databaseError == null){

                }
            }
        });
    }



}
