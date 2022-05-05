package dk.au.mad22spring.appproject.group7;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LifecycleService;
import androidx.lifecycle.Observer;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import dk.au.mad22spring.appproject.group7.models.NotificationModel;

//Most of the code in this Foreground-service-class is based on: Services + async processing demo from lesson 5.
public class NotificationService extends LifecycleService {

    private Repository repository;
    private ExecutorService executorService;

    public NotificationService() {
        repository = Repository.getInstance();
    }

   @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
       super.onStartCommand(intent, flags, startId);
       //Creates a notification channel
       if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
           NotificationChannel channel = new NotificationChannel(Constants.SERVICE_CHANNEL, "Find my study place", NotificationManager.IMPORTANCE_HIGH);
           NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
           notificationManager.createNotificationChannel(channel);
       }

       //NotificationModel m = new NotificationModel();
       //m.setFriendName("Freja");

       //createNotification(m);

       repository.getNotifications().observe(this, new Observer<NotificationModel>() {
           @Override
           public void onChanged(NotificationModel notification) {
               createNotification(notification);
           }
       });


       return START_STICKY;
   }

   private void createNotification(NotificationModel notificationModel){
        if (executorService == null) {
            executorService = Executors.newSingleThreadExecutor();
        }
        executorService.submit(new Runnable() {
            @Override
            public void run() {
                notifyWithLocation(notificationModel);
            }
        });
   }

    private void notifyWithLocation(NotificationModel notificationObject) {
        //Todo tilføj lille notofications item
        Notification notification = new NotificationCompat.Builder(this, Constants.SERVICE_CHANNEL)
                .setContentTitle(notificationObject.getFriendName() + " " + getString(R.string.notificationTitle))
                .setContentText(getString(R.string.notificationText) + " " + notificationObject.getBuilding())
                .setStyle(new NotificationCompat.BigTextStyle().bigText(notificationObject.getComment()))
                //.setSmallIcon(R.drawable.ic_notification_service_24)
                .build();

        startForeground(Constants.NOTIFICATION_ID, notification);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        super.onBind(intent);
        return null;
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
