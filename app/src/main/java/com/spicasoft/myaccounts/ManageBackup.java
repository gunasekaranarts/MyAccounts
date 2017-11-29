package com.spicasoft.myaccounts;

import android.app.ProgressDialog;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveClient;
import com.google.android.gms.drive.DriveContents;
import com.google.android.gms.drive.DriveFile;
import com.google.android.gms.drive.DriveFolder;
import com.google.android.gms.drive.DriveId;
import com.google.android.gms.drive.DriveResource;
import com.google.android.gms.drive.DriveResourceClient;
import com.google.android.gms.drive.MetadataBuffer;
import com.google.android.gms.drive.MetadataChangeSet;
import com.google.android.gms.drive.events.OpenFileCallback;
import com.google.android.gms.drive.query.Filters;
import com.google.android.gms.drive.query.Query;
import com.google.android.gms.drive.query.SearchableField;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

import Database.MyAccountsDatabase;
import Utils.AppPreferences;
import Utils.NetworkUtil;

/**
 * Created by USER on 15-11-2017.
 */

public class ManageBackup extends Fragment {
    private static GoogleSignInClient mGoogleSignInClient;
  //  private static DriveResourceClient mDriveResourceClient;
//    private static DriveClient mDriveClient;
    MyAccountsDatabase mHelper;
    Button btn_upload,btn_restore;
    private static final int REQUEST_CODE_SIGN_IN = 0;
    private ProgressDialog progressDialog;

    DriveFile file;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view= inflater.inflate(R.layout.dbcloud, container, false);
        signIn();
        btn_upload=(Button) view.findViewById(R.id.btn_backup);
        btn_restore=(Button) view.findViewById(R.id.btn_restore);

        mHelper = new MyAccountsDatabase(getActivity());

        btn_upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(NetworkUtil.isNetworkAvailable(getActivity()))
                    createFileInAppFolder();
                else{
                    showMessage("No Network connection found, Unable to backup or restore transactions!");
                }

            }
        });
        btn_restore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(NetworkUtil.isNetworkAvailable(getActivity()))
                    getFileFromAppFolder();
                else{
                    showMessage("No Network connection found, Unable to backup or restore transactions!");
                }
            }
        });

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
       // new  RequestUserWait().execute();
    }

    private class RequestUserWait extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("Please wait we connect your drive...");
            progressDialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    showMessage("Connected");
                }
            }, 9000);
            return "sucess full added";
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            progressDialog.dismiss();
        }
    }


    public void getFileFromAppFolder() {
        final Task<DriveFolder> appFolderTask =getDriveResourceClient().getAppFolder();
        appFolderTask.addOnSuccessListener(getActivity(),new OnSuccessListener<DriveFolder>() {
            @Override
            public void onSuccess(DriveFolder driveFolder) {
                getFile(driveFolder);
            }
        });
    }

    private void getFile(DriveFolder parent) {
        Query query = new Query.Builder()
                .addFilter(Filters.contains(SearchableField.TITLE, mHelper.getDatabaseName()))
                .build();
        Task<MetadataBuffer> queryTask = getDriveResourceClient().queryChildren(parent, query);
        queryTask
                .addOnSuccessListener(getActivity(),
                        new OnSuccessListener<MetadataBuffer>() {
                            @Override
                            public void onSuccess(MetadataBuffer metadataBuffer) {
                                 file = metadataBuffer.get(0).getDriveId().asDriveFile();
                                 downloadFile(file);
                            }
                        })
                .addOnFailureListener(getActivity(), new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getContext(),"Unable to download file",
                                Toast.LENGTH_LONG).show();
                    }
                });


    }

    private void downloadFile(DriveFile file) {
        final Task<DriveContents> openFileTask =
                getDriveResourceClient().openFile(file, DriveFile.MODE_READ_ONLY);
        openFileTask.addOnCompleteListener(new OnCompleteListener<DriveContents>() {
            @Override
            public void onComplete(@NonNull Task<DriveContents> task) {
                openFileTask
                        .continueWithTask(new Continuation<DriveContents, Task<Void>>() {
                            @Override
                            public Task<Void> then(@NonNull Task<DriveContents> task) throws Exception {
                                DriveContents contents = task.getResult();
                                // Process contents...
                                // [START_EXCLUDE]
                                // [START read_as_string]
                                File NewDB = new File("/data/data/com.spicasoft.myaccounts/databases/MyAccountsDatabase.db");
                                OutputStream outStream = new FileOutputStream(NewDB);
                                try {

                                    InputStream is=contents.getInputStream();
                                    byte[] buffer = new byte[is.available()];
                                    is.read(buffer);
                                    outStream.write(buffer);
                                    outStream.close();
                                    outStream.flush();
                                    showMessage("Database Restored");

                                }catch (Exception ex){
                                    showMessage("Unable to Restore");
                                    outStream.close();
                                    outStream.flush();
                                }
                                // [END read_as_string]
                                // [END_EXCLUDE]
                                // [START discard_contents]
                                Task<Void> discardTask = getDriveResourceClient().discardContents(contents);
                                // [END discard_contents]
                                return discardTask;
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                // Handle failure
                                // [START_EXCLUDE]

                                showMessage("Unable to restore database");

                                // [END_EXCLUDE]
                            }
                        });
            }
        });
    }
public void deleteFileFromDrive()
{
    final String driveIdStr = AppPreferences.getInstance(getContext()).getDriveId();
    if(!driveIdStr.equals(""))
    {
        DriveId fileId = DriveId.decodeFromString(driveIdStr);
        DriveFile oldFile = fileId.asDriveFile();
        getDriveResourceClient().delete(oldFile)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        AppPreferences.getInstance(getContext()).setDriveId("");
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }
}

    public void signIn() {
        mGoogleSignInClient = buildGoogleSignInClient();
        getActivity().startActivityForResult(mGoogleSignInClient.getSignInIntent(), REQUEST_CODE_SIGN_IN);
    }
    private GoogleSignInClient buildGoogleSignInClient() {
        GoogleSignInOptions signInOptions =
                new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestScopes(Drive.SCOPE_FILE)
                        .requestScopes(Drive.SCOPE_APPFOLDER)
                        .requestEmail()
                        .build();
        return GoogleSignIn.getClient(getActivity(), signInOptions);
    }
    public void createFileInAppFolder() {
        final File currentDB = new File("/data/data/com.spicasoft.myaccounts/databases/MyAccountsDatabase.db");
        if(currentDB.exists()) {
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
                                    .setTitle(((MainActivity) getActivity()).mHelper.getDatabaseName())
                                    .setMimeType("application/x-sqlite3")
                                    .setStarred(true)
                                    .build();

                            //Task<Void> commitTask = getDriveResourceClient().commitContents(contents,changeSet);
                            // [END discard_contents]

                            return getDriveResourceClient().createFile(parent, changeSet, contents);
                        }
                    })
                    .addOnSuccessListener(getActivity(),
                            new OnSuccessListener<DriveFile>() {
                                @Override
                                public void onSuccess(DriveFile driveFile) {

                                    AppPreferences.getInstance(getContext()).setDriveId(driveFile.getDriveId().encodeToString());
                                    showMessage("Data uploaded to google drive " +
                                            driveFile.getDriveId().encodeToString());

                                }
                            })
                    .addOnFailureListener(getActivity(), new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                            showMessage("Unable to create file");

                        }
                    });
        }
    }
    protected void showMessage(String message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
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
    public DriveResourceClient getDriveResourceClient() {
        return ((MainActivity)getActivity()).mDriveResourceClient;
    }
}
