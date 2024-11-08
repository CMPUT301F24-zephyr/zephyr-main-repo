//package com.example.plannet.Notification;
//
//import android.app.Notification;
//import android.app.NotificationChannel;
//import android.app.NotificationManager;
//import android.content.Context;
//import android.os.Build;
//
//import androidx.core.app.NotificationCompat;
//import androidx.core.app.NotificationManagerCompat;
//
//public class AppNotificationManager {
//    private static final String CHANNEL_ID = "event_notifications";
//    private static AppNotificationManager instance;
//
//    private AppNotificationManager(Context context) {
//        createNotificationChannel(context);
//    }
//
//    public static AppNotificationManager getInstance(Context context) {
//        if (instance == null) {
//            instance = new AppNotificationManager(context);
//        }
//        return instance;
//    }
//
//    private void createNotificationChannel(Context context) {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            CharSequence name = "Event Notifications";
//            String description = "Notifications related to event status changes";
//            int importance = NotificationManager.IMPORTANCE_DEFAULT;
//            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
//            channel.setDescription(description);
//            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
//            notificationManager.createNotificationChannel(channel);
//        }
//    }
//
//    public void sendNotification(Context context, String title, String message) {
//        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
//                .setSmallIcon(R.drawable.ic_notification) // ensure this drawable exists
//                .setContentTitle(title)
//                .setContentText(message)
//                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
//
//        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
//        notificationManager.notify((int) System.currentTimeMillis(), builder.build());
//    }
//}
