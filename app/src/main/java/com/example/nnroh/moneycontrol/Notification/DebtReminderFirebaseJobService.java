package com.example.nnroh.moneycontrol.Notification;

import android.content.Context;
import android.os.AsyncTask;

import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;

public class DebtReminderFirebaseJobService extends JobService {

    private AsyncTask mBackgroundTask;


    @Override
    public boolean onStartJob(final JobParameters jobParameters) {

        mBackgroundTask = new AsyncTask() {

            @Override
            protected Object doInBackground(Object[] params) {
                Context context = DebtReminderFirebaseJobService.this;
                ReminderTasks.executeTask(context, ReminderTasks.ACTION_DEBT_REVIEW_REMINDER);
                ReminderTasks.executeTask(context, ReminderTasks.ACTION_DEBT_REMINDER);
                return null;
            }

            @Override
            protected void onPostExecute(Object o) {

                jobFinished(jobParameters, false);
            }
        };

        mBackgroundTask.execute();
        return true;
    }


    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        if (mBackgroundTask != null) mBackgroundTask.cancel(true);
        return true;
    }
}
