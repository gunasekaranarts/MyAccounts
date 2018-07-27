package Utils;

import android.app.Activity;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.widget.Toast;

import com.google.android.gms.drive.DriveContents;
import com.google.android.gms.drive.DriveFile;
import com.google.android.gms.drive.DriveFolder;
import com.google.android.gms.drive.DriveId;
import com.google.android.gms.drive.DriveResourceClient;
import com.google.android.gms.drive.MetadataChangeSet;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.spicasoft.myaccounts.MainActivity;
import com.spicasoft.myaccounts.Profile;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import Database.MyAccountsDatabase;
import POJO.SecurityProfile;

/**
 * Created by USER on 29-11-2017.
 */

public class UploadFile {
    Activity main;
    File currentDB;
    SecurityProfile profile;
    public UploadFile(Activity main) {
        this.main = main;
    }

    public void UploadFile()
    {
            currentDB = new File("/data/data/com.spicasoft.myaccounts/databases/MyAccountsDatabase.db");
            profile= new MyAccountsDatabase(main).getProfile();
            Date today= Calendar.getInstance().getTime();
            SimpleDateFormat formater=new SimpleDateFormat("yyyyMMdd");
            int date=Integer.parseInt(formater.format(today));
        if(AppPreferences.getInstance(main).getLastBackUp().equals("")) {
                AppPreferences.getInstance(main).setLastBackUp(formater.format(today));
                new SendFile().execute();
        }else{
            int lastdate=Integer.parseInt(AppPreferences.getInstance(main).getLastBackUp());
            if(lastdate<date){
                new SendFile().execute();
            }
        }
            if (currentDB.exists()) {
                deleteFileFromDrive();

                final Task<DriveFolder> appFolderTask = getDriveResourceClient().getAppFolder();
                final Task<DriveContents> createContentsTask = getDriveResourceClient().createContents();
                Tasks.whenAll(appFolderTask, createContentsTask)
                        .continueWithTask(new Continuation<Void, Task<DriveFile>>() {
                            @Override
                            public Task<DriveFile> then(@NonNull Task<Void> task) throws Exception {
                                DriveFolder parent = appFolderTask.getResult();
                                DriveContents contents = createContentsTask.getResult();

                                OutputStream outputStream = contents.getOutputStream();

                                file2Os(outputStream, currentDB);
                                MetadataChangeSet changeSet = new MetadataChangeSet.Builder()
                                        .setTitle(((MainActivity) main).mHelper.getDatabaseName())
                                        .setMimeType("application/x-sqlite3")
                                        .setStarred(true)
                                        .build();
                                //Task<Void> commitTask = getDriveResourceClient().commitContents(contents,changeSet);
                                // [END discard_contents]

                                return getDriveResourceClient().createFile(parent, changeSet, contents);
                            }
                        })
                        .addOnSuccessListener(main,
                                new OnSuccessListener<DriveFile>() {
                                    @Override
                                    public void onSuccess(DriveFile driveFile) {
                                        AppPreferences.getInstance(main).setDriveId(driveFile.getDriveId().encodeToString());
                                        showMessage("Data uploaded to google drive " +
                                                driveFile.getDriveId().encodeToString());
                                    }
                                })
                        .addOnFailureListener(main, new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {

                                showMessage("Unable to create file");

                            }
                        });
            }

    }
    public void deleteFileFromDrive()
    {
        final String driveIdStr = AppPreferences.getInstance(main).getDriveId();
        if(!driveIdStr.equals(""))
        {
            DriveId fileId = DriveId.decodeFromString(driveIdStr);
            DriveFile oldFile = fileId.asDriveFile();
            getDriveResourceClient().delete(oldFile)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            AppPreferences.getInstance(main).setDriveId("");
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                }
            });
        }
    }
    public DriveResourceClient getDriveResourceClient() {
        return ((MainActivity)main).mDriveResourceClient;
    }
    static boolean file2Os(OutputStream os, File file) {
        boolean bOK = false;
        InputStream is = null;
        if (file != null && os != null) try {
            byte[] buf = new byte[4096];
            is = new FileInputStream(file);
            int c;
            while ((c = is.read(buf, 0, buf.length)) > 0)
                os.write(buf, 0, c);
            bOK = true;
        } catch (Exception e) {e.printStackTrace();}
        finally {
            try {
                os.flush(); os.close();
                if (is != null )is.close();
            } catch (Exception e) {e.printStackTrace();}
        }
        return  bOK;
    }
    protected void showMessage(String message) {
        Toast.makeText(main, message, Toast.LENGTH_LONG).show();
    }


    private class SendFile extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            try {
                GMailSender gMailSender=new GMailSender();

                gMailSender.sendBackUpMail("Backup from - "+profile.getEmail(),"Please find attached file for backup","myaccappv1@gmail.com",currentDB);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return "sucess full added";
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

        }
    }


}
