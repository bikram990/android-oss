package com.kickstarter.libs;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;

import com.kickstarter.libs.gcm.RegisterService;
import com.kickstarter.libs.gcm.UnregisterService;
import com.kickstarter.libs.utils.PlayServicesUtils;
import com.kickstarter.models.Activity;
import com.kickstarter.models.pushdata.ActivityPushData;
import com.kickstarter.models.pushdata.GCMPushData;
import com.kickstarter.ui.activities.DiscoveryActivity;

import timber.log.Timber;

public class Notifications {
  @ForApplication final Context context;

  public Notifications(@ForApplication final Context context) {
    this.context = context;
  }

  public void initialize() {
    registerDevice();
  }

  public void registerDevice() {
    if (!PlayServicesUtils.isAvailable(context)) {
      return;
    }

    final Intent intent = new Intent(context, RegisterService.class);
    context.startService(intent);
  }

  public void unregisterDevice() {
    if (!PlayServicesUtils.isAvailable(context)) {
      return;
    }

    final Intent intent = new Intent(context, UnregisterService.class);
    context.startService(intent);
  }

  public void show(@NonNull final GCMPushData gcm, @NonNull final ActivityPushData activity) {
    if (activity.category().equals(Activity.CATEGORY_SUCCESS)) {
      notify(gcm.alert());
    }
  }

  /**
   * Create and show a simple notification containing the received GCM message.
   *
   * @param message GCM message received.
   */
  private void notify(@NonNull final String message) {
    Timber.d("Sending new notification: " + message);
    // TODO: Switch intent appropriately
    final Intent intent = new Intent(context, DiscoveryActivity.class);
    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
    final PendingIntent pendingIntent = PendingIntent.getActivity(context, 0 /* Request code */, intent,
      PendingIntent.FLAG_ONE_SHOT);

    final Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
    final NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context)
      .setSmallIcon(android.R.drawable.btn_star)
      .setContentTitle("Notification")
      .setContentText(message)
      .setAutoCancel(true)
      .setSound(defaultSoundUri)
      .setContentIntent(pendingIntent);

    final NotificationManager notificationManager =
      (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

    notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
  }
}
