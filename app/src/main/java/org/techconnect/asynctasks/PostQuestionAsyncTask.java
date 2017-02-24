package org.techconnect.asynctasks;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import org.techconnect.sql.TCDatabaseHelper;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by doranwalsten on 2/23/17.
 */

public class PostQuestionAsyncTask extends AsyncTask<String, Void, Integer> {

    private Context context;

    public PostQuestionAsyncTask(Context context) {
        this.context  = context;
    }

    @Override
    protected Integer doInBackground(String... strings) {
        //Prepare phone to build a file in storage
        File exportDir = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
            exportDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), "TechConnect");
        } else {
            exportDir = new File(Environment.getExternalStorageDirectory() + "/Documents/TechConnect");

        }

        boolean present = exportDir.exists();
        if (!present) {
            present = exportDir.mkdirs();
        }

        if (present) {
            //Iterate through all session ID strings provided to get list of strings to send
            ArrayList<Uri> attachments = new ArrayList<Uri>();
            for (String s : strings) {
                String resp = TCDatabaseHelper.get(context).writeResponsesToString(s);
                File file = new File(exportDir, String.format("Session_%s.txt",s));
                try {

                    file.createNewFile();
                    FileWriter writer = new FileWriter(file);

                    //Write to file
                    writer.write(resp);
                    //close the writer
                    writer.close();

                    //Add file to list of files
                    attachments.add(Uri.fromFile(file));
                } catch (IOException e) {
                    Log.e(getClass().toString(), e.getMessage(), e);
                    return 0;
                }
            }

            //Send email based on String arguments
            Intent emailIntent = new Intent(Intent.ACTION_SEND_MULTIPLE);
            emailIntent.setType("message/rfc822");
            emailIntent.putExtra(Intent.EXTRA_EMAIL,new String[]{"jhu.techconnect@gmail.com"});
            emailIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, attachments);
            emailIntent.setType("text/plain");
            context.startActivity(Intent.createChooser(emailIntent, "Send email using"));
            return 1;
        } else {
            return 0;
        }
    }
}
