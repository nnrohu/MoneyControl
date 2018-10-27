package com.example.nnroh.moneycontrol.Notification;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;

public class DebtReminderIntentService extends IntentService {


    public DebtReminderIntentService() {
        super("DebtReminderIntentService");
    }


    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        String action = intent.getAction();
        ReminderTasks.executeTask(this, action);
    }
}
