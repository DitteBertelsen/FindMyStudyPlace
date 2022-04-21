package dk.au.mad22spring.appproject.group7;

import android.app.Application;
import android.content.Context;

//This class is made based on the code from Counter Tracker from lesson 8
public class FMSPApplication extends Application {

    //This is  the one application object for the application's life time
    private static FMSPApplication instance;

    @Override
    public void onCreate() {
        super.onCreate();
        //Android specifies that this is the first code to run in app, so should no be null from here on
        instance = this;
    }

    //Remember this method only can be used outside the UI
    public static Context getAppContext(){
        return instance.getApplicationContext();
    }

}
