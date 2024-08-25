
package com.gypsee.sdk.io;

import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;

import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import com.github.pires.obd.commands.SpeedCommand;
import com.github.pires.obd.commands.control.DistanceSinceCCCommand;
import com.github.pires.obd.commands.control.PendingTroubleCodesCommand;
import com.github.pires.obd.commands.control.PermanentTroubleCodesCommand;
import com.github.pires.obd.commands.control.TroubleCodesCommand;
import com.github.pires.obd.commands.control.VinCommand;
import com.github.pires.obd.commands.engine.RPMCommand;
import com.github.pires.obd.commands.fuel.FindFuelTypeCommand;
import com.github.pires.obd.commands.protocol.EchoOffCommand;
import com.github.pires.obd.commands.protocol.HeadersOffCommand;
import com.github.pires.obd.commands.protocol.LineFeedOffCommand;
import com.github.pires.obd.commands.protocol.ObdResetCommand;
import com.github.pires.obd.commands.protocol.ResetTroubleCodesCommand;
import com.github.pires.obd.commands.protocol.SelectProtocolCommand;
import com.github.pires.obd.commands.protocol.SpacesOffCommand;
import com.github.pires.obd.commands.protocol.TimeoutCommand;
import com.github.pires.obd.enums.ObdProtocols;
import com.github.pires.obd.exceptions.UnsupportedCommandException;

import java.io.File;
import java.io.IOException;

import com.gypsee.sdk.ObdCommands.SetAlltoDefaultCommand;
import com.gypsee.sdk.activities.ConfigActivity;


/**
 * This service is primarily responsible for establishing and maintaining a
 * permanent connection between the device where the application runs and a more
 * OBD Bluetooth interface.
 * <p/>
 * Secondarily, it will serve as a repository of ObdCommandJobs and at the same
 * time the application state-machine.
 */
public class ObdGatewayService extends AbstractGatewayService {

    private static final String TAG = ObdGatewayService.class.getName();

    private BluetoothSocket sock = null;

    public void startService(BluetoothSocket sock1, boolean isScanCar, boolean isClearCode) {
        sock = sock1;
        Log.e(TAG, "Starting service..");
        startObdConnection(isScanCar, isClearCode);
    }

    /**
     * Start and configure the connection to the OBD interface.
     * <p/>
     * See http://stackoverflow.com/questions/18657427/ioexception-read-failed-socket-might-closed-bluetooth-on-android-4-3/18786701#18786701
     *
     * @param isScanCar
     * @param isClearCode
     * @throws IOException
     */
    private void startObdConnection(final boolean isScanCar, final boolean isClearCode) {

        Log.e(TAG, "Starting OBD connection..");
        isRunning = true;

        // Let's configure the connection.
        Log.e(TAG, "Queueing jobs for connection configuration..");

        Log.e(TAG, "isScanCar in service: " + isScanCar);

        if (isScanCar || isClearCode) {
            //queueJob(new ObdCommandJob(new ObdResetCommand()));
        } else {
            Log.e(TAG, "entering reset");

            queueJob(new ObdCommandJob(new SetAlltoDefaultCommand()));
            queueJob(new ObdCommandJob(new ObdResetCommand()));
        }
        // queueJob(new ObdCommandJob(new ResetTroubleCodesCommand()));

        //Below is to give the adapter enough time to reset before sending the commands, otherwise the first startup commands could be ignored.
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Log.e(TAG, "Came after 1 sec");


        if (!isScanCar && !isClearCode) {

            Log.e(TAG, "entering init");

            queueJob(new ObdCommandJob(new EchoOffCommand()));
            queueJob(new ObdCommandJob(new LineFeedOffCommand()));

            queueJob(new ObdCommandJob(new SpacesOffCommand()));
            queueJob(new ObdCommandJob(new HeadersOffCommand()));
            queueJob(new ObdCommandJob(new TimeoutCommand(70)));
        }

        // Get protocol from preferences
        final String protocol = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString(ConfigActivity.PROTOCOLS_LIST_KEY, "AUTO");

        queueJob(new ObdCommandJob(new SelectProtocolCommand(ObdProtocols.valueOf(protocol))));

        //Below commands are for basically testing at starting to chage protocol or not.

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (isScanCar) {

                    Log.e(TAG, "entering trouble code");

                    queueJob(new ObdCommandJob(new ModifiedTroubleCodesObdCommand()));
                    queueJob(new ObdCommandJob(new ModifiedPTroubleCodesObdCommand()));
                    queueJob(new ObdCommandJob(new PermanentTroubleCodesCommand()));
                } else if (isClearCode) {
                    Log.e(TAG, "Entering clear trouble");
                    queueJob(new ObdCommandJob(new ResetTroubleCodesCommand()));

                } else {


                    queueJob(new ObdCommandJob(new SpeedCommand()));
                    queueJob(new ObdCommandJob(new FindFuelTypeCommand()));
                    queueJob(new ObdCommandJob(new DistanceSinceCCCommand()));
                    queueJob(new ObdCommandJob(new RPMCommand()));
                    queueJob(new ObdCommandJob(new VinCommand()));
                }
            }
        }, 3000);
        // Job for returning dummy data
        //queueJob(new ObdCommandJob(new AmbientAirTemperatureCommand()));

        queueCounter = 0L;
        Log.e(TAG, "Initialization jobs queued.");
    }

    public class ModifiedTroubleCodesObdCommand extends TroubleCodesCommand {
        @Override
        public String getResult() {
            // remove unwanted response from output since thisresults in erroneous error codes
            // remove unwanted response from output since thisresults in erroneous error codes
            if (rawData == null) {
                return rawData;
            } else {
                return rawData.replace("SEARCHING...", "").replace("NODATA", "");
            }
        }
    }

    public class ModifiedPTroubleCodesObdCommand extends PendingTroubleCodesCommand {
        @Override
        public String getResult() {
            // remove unwanted response from output since thisresults in erroneous error codes
            if (rawData == null) {
                return rawData;
            } else {
                return rawData.replace("SEARCHING...", "").replace("NODATA", "");
            }
        }
    }

    /**
     * This method will add a job to the queue while setting its ID to the
     * internal queue counter.
     *
     * @param job the job to queue.
     */
    @Override
    public void queueJob(ObdCommandJob job) {
        // This is a good place to enforce the imperial units option
        //job.getCommand().useImperialUnits(prefs.getBoolean(ConfigActivity.IMPERIAL_UNITS_KEY, false));
        job.getCommand().useImperialUnits(false);

        // Now we can pass it along
        super.queueJob(job);
    }


    int brokenPipe = 0;

    /**
     * Runs the queue until the service is stopped
     */
    protected void executeQueue() {
        Log.e(TAG, "Executing queue..");
        while (!Thread.currentThread().isInterrupted()) {
            ObdCommandJob job = null;
            try {
                job = jobsQueue.take();

                // log job
                Log.e(TAG, "Taking job[" + job.getId() + "] from queue..");

                if (job.getState().equals(ObdCommandJob.ObdCommandJobState.NEW)) {
                    Log.e(TAG, "Job state is NEW. Run it..");
                    job.setState(ObdCommandJob.ObdCommandJobState.RUNNING);
                    if (sock.isConnected()) {
                        job.getCommand().run(sock.getInputStream(), sock.getOutputStream());
                    } else {
                        job.setState(ObdCommandJob.ObdCommandJobState.EXECUTION_ERROR);
                        Log.e(TAG, "Can't run command on a closed socket.");
                    }
                } else
                // log not new job
                {
                    Log.e(TAG, "Job state was not new, so it shouldn't be in queue. BUG ALERT!");
                }
            } catch (InterruptedException i) {
                Thread.currentThread().interrupt();
            } catch (UnsupportedCommandException u) {
                if (job != null) {
                    job.setState(ObdCommandJob.ObdCommandJobState.NOT_SUPPORTED);
                }
                Log.e(TAG, "Command not supported. -> " + u.getMessage());
            } catch (IOException io) {
                if (job != null) {
                    if (io.getMessage().contains("Broken pipe")) {
                        if (brokenPipe > 0) {
                            return;
                        }
                        ++brokenPipe;
                        job.setState(ObdCommandJob.ObdCommandJobState.BROKEN_PIPE);
                    } else
                        job.setState(ObdCommandJob.ObdCommandJobState.EXECUTION_ERROR);
                }
                Log.e(TAG, "IO error. -> " + io.getMessage());
            } catch (Exception e) {
                if (job != null) {
                    job.setState(ObdCommandJob.ObdCommandJobState.EXECUTION_ERROR);
                }
                Log.e(TAG, "Failed to run command. -> " + e.getMessage());
            }

            if (job != null) {
                Log.e(TAG, "Job is not null");

                final ObdCommandJob finalJob = job;

                Log.e(TAG, "finaljob: " + finalJob.getCommand().getName());
                Log.e(TAG, "finaljob result: " + finalJob.getCommand().getFormattedResult());

                Intent intent = new Intent("ObdCommandUpdates");
                // You can also include some extra data.
                Bundle b = new Bundle();
                b.putParcelable("ObdCommandJob", finalJob);
                intent.putExtra("OBD", b);
                intent.putExtras(b);
                LocalBroadcastManager.getInstance(ctx).sendBroadcast(intent);
            }
        }
    }


    /**
     * Stop OBD connection and queue processing.
     */
    public void stopService() {
        Log.e(TAG, "Stopping service..");
        jobsQueue.clear();
        isRunning = false;
        if (sock != null)
            // close socket
            try {
                sock.close();
            } catch (IOException e) {
                Log.e(TAG, e.getMessage());
            }

        // kill service
        stopSelf();
    }

    public boolean isRunning() {
        return isRunning;
    }

    public static void saveLogcatToFile(Context context, String devemail) {
        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        emailIntent.setType("text/plain");
        emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{devemail});
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "OBD2 Reader Debug Logs");

        StringBuilder sb = new StringBuilder();
        sb.append("\nManufacturer: ").append(Build.MANUFACTURER);
        sb.append("\nModel: ").append(Build.MODEL);
        sb.append("\nRelease: ").append(Build.VERSION.RELEASE);

        emailIntent.putExtra(Intent.EXTRA_TEXT, sb.toString());

        String fileName = "OBDReader_logcat_" + System.currentTimeMillis() + ".txt";
        File sdCard = Environment.getExternalStorageDirectory();
        File dir = new File(sdCard.getAbsolutePath() + File.separator + "OBD2Logs");
        if (dir.mkdirs()) {
            File outputFile = new File(dir, fileName);
            Uri uri = Uri.fromFile(outputFile);
            emailIntent.putExtra(Intent.EXTRA_STREAM, uri);

            Log.e("savingFile", "Going to save logcat to " + outputFile);
            //emailIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(Intent.createChooser(emailIntent, "Pick an Email provider").addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));

            try {
                @SuppressWarnings("unused")
                Process process = Runtime.getRuntime().exec("logcat -f " + outputFile.getAbsolutePath());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
