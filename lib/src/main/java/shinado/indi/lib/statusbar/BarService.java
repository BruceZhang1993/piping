package shinado.indi.lib.statusbar;
import java.util.List;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.annotation.TargetApi;
import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Parcelable;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;
import android.text.TextUtils;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;

/**
 * This service class catches Toast or Notification of applications
 */
@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
public class BarService extends AccessibilityService {

    private final AccessibilityServiceInfo info = new AccessibilityServiceInfo();
    private static final String TAG = "BarService";

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {

        final int eventType = event.getEventType();
        if (eventType == AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED) {
            final String sourcePackageName = (String)event.getPackageName();
            Parcelable parcelable = event.getParcelableData();

            if (parcelable instanceof Notification) {
                // Statusbar Notification

                //Notification notification = (Notification) parcelable;
                //Log.e(TAG, "Notification -> notification.tickerText :: " + notification.tickerText);
                List<CharSequence> messages = event.getText();
                if (messages.size() > 0) {
                    final String notificationMsg = (String) messages.get(0);
                    Log.v(TAG, "Captured notification message [" + notificationMsg + "] for source [" + sourcePackageName + "]");
                    Log.v(TAG, "Broadcasting for " + Constants.ACTION_CATCH_NOTIFICATION);
                    try {
                        Intent mIntent = new Intent(Constants.ACTION_CATCH_NOTIFICATION);
                        mIntent.putExtra(Constants.EXTRA_PACKAGE, sourcePackageName);
                        mIntent.putExtra(Constants.EXTRA_MESSAGE, notificationMsg);
                        getApplicationContext().sendBroadcast(mIntent);
                    } catch (Exception e) {
                        Log.e(TAG, e.toString());
                    }
                } else {
                    Log.e(TAG, "Notification Message is empty. Can not broadcast");
                }
            } else {
                // Something else, e.g. a Toast message
                // Read message and broadcast
                List<CharSequence> messages = event.getText();
                if (messages.size() > 0) {
                    final String toastMsg = (String) messages.get(0);
                    Log.v(TAG, "Captured message [" + toastMsg + "] for source [" + sourcePackageName + "]");
                    Log.v(TAG, "Broadcasting for " + Constants.ACTION_CATCH_TOAST);
                    try {
                        Intent mIntent = new Intent(Constants.ACTION_CATCH_TOAST);
                        mIntent.putExtra(Constants.EXTRA_PACKAGE, sourcePackageName);
                        mIntent.putExtra(Constants.EXTRA_MESSAGE, toastMsg);
                        getApplicationContext().sendBroadcast(mIntent);
                    } catch (Exception e) {
                        Log.v(TAG, e.toString());
                    }
                } else {
                    Log.e(TAG, "Message is empty. Can not broadcast");
                }
            }
        } else {
            Log.v(TAG, "Got un-handled Event");
        }
    }

    @Override
    public void onInterrupt() {

    }

    @Override
    public void onServiceConnected() {
        // Set the type of events that this service wants to listen to.
        //Others won't be passed to this service.
        info.eventTypes = AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED;

        // If you only want this service to work with specific applications, set their
        // package names here.  Otherwise, when the service is activated, it will listen
        // to events from all applications.
        //info.packageNames = new String[]
        //{"com.appone.totest.accessibility", "com.apptwo.totest.accessibility"};

        // Set the type of feedback your service will provide.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            info.feedbackType = AccessibilityServiceInfo.FEEDBACK_ALL_MASK;
        } else {
            info.feedbackType = AccessibilityServiceInfo.FEEDBACK_GENERIC;
        }

        // Default services are invoked only if no package-specific ones are present
        // for the type of AccessibilityEvent generated.  This service *is*
        // application-specific, so the flag isn't necessary.  If this was a
        // general-purpose service, it would be worth considering setting the
        // DEFAULT flag.

        // info.flags = AccessibilityServiceInfo.DEFAULT;

        info.notificationTimeout = 100;

        this.setServiceInfo(info);
    }

    public static final class Constants {
        public static final String EXTRA_MESSAGE = "extra_message";
        public static final String EXTRA_PACKAGE = "extra_package";
        public static final String ACTION_CATCH_TOAST = "shinado.indi.lib.statusbar.CATCH_TOAST";
        public static final String ACTION_CATCH_NOTIFICATION = "shinado.indi.lib.statusbar.CATCH_NOTIFICATION";
    }

}
