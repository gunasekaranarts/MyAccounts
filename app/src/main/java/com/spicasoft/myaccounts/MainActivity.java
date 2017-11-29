package com.spicasoft.myaccounts;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveClient;
import com.google.android.gms.drive.DriveResourceClient;
import com.google.android.gms.tasks.Task;

import java.util.HashSet;
import java.util.Set;

import Database.MyAccountsDatabase;
import POJO.SecurityProfile;
import Utils.UploadFile;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    Fragment fragment = null;
    TextView txtuserName,txtusermail;
    FloatingActionButton fab;
    public MyAccountsDatabase mHelper;
    SQLiteDatabase dataBase;
    Splash_Activtivity splash_activtivity;
    SecurityProfile securityProfile;

    boolean doubleBackToExitPressedOnce = false;

    public GoogleSignInClient mGoogleSignInClient;
    public DriveResourceClient mDriveResourceClient;
    public DriveClient mDriveClient;
    private static final int REQUEST_CODE_SIGN_IN = 0;
    public UploadFile uploadFile;


    @Override
    protected void onStart() {
        super.onStart();
        signIn();
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //initializeDriveClient(GoogleSignIn.getLastSignedInAccount(getApplicationContext()));
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mHelper = new MyAccountsDatabase(this);
        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fab.hide();
                fragment = new AddTransaction();
                FragmentTransaction fragmentTransaction=
                        getSupportFragmentManager().beginTransaction();
                fragmentTransaction.setCustomAnimations(R.anim.enter_from_bottom,
                        R.anim.exit_to_right);
                fragmentTransaction.replace(R.id.fragment_container,fragment);
                fragmentTransaction.commit();

            }
        });
        uploadFile=new UploadFile(this);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        final View headerView = navigationView.getHeaderView(0);
        txtuserName= (TextView) headerView.findViewById(R.id.userName);
        txtusermail= (TextView) headerView.findViewById(R.id.emailId);
        UpdateUserDetails();
        fragment = new Today();
        FragmentTransaction fragmentTransaction=
                getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.enter_from_left,
                R.anim.exit_to_right, R.anim.enter_from_right, R.anim.exit_to_left);
        fragmentTransaction.replace(R.id.fragment_container,fragment);
        fragmentTransaction.commit();

    }

    public void signIn() {
        Set<Scope> requiredScopes = new HashSet<>(2);
        requiredScopes.add(Drive.SCOPE_FILE);
        requiredScopes.add(Drive.SCOPE_APPFOLDER);
        GoogleSignInAccount signInAccount = GoogleSignIn.getLastSignedInAccount(this);
        if (signInAccount != null && signInAccount.getGrantedScopes().containsAll(requiredScopes)) {
            initializeDriveClient(signInAccount);
        } else {
            mGoogleSignInClient = buildGoogleSignInClient();
            this.startActivityForResult(mGoogleSignInClient.getSignInIntent(), REQUEST_CODE_SIGN_IN);
        }

    }
    private GoogleSignInClient buildGoogleSignInClient() {
        GoogleSignInOptions signInOptions =
                new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestScopes(Drive.SCOPE_FILE)
                        .requestScopes(Drive.SCOPE_APPFOLDER)
                        .requestEmail()
                        .build();
        return GoogleSignIn.getClient(this, signInOptions);
    }
    public FloatingActionButton getFloatingActionButton (){
        return fab;
    }

    private void UpdateUserDetails() {
        securityProfile = mHelper.getProfile();
        txtuserName.setText(securityProfile.getName());
        txtusermail.setText(securityProfile.getEmail());
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_CODE_SIGN_IN:

                if (resultCode != RESULT_OK) {
                    // Sign-in may fail or be cancelled by the user. For this sample, sign-in is
                    // required and is fatal. For apps where sign-in is optional, handle
                    // appropriately
                    return;
                }

                Task<GoogleSignInAccount> getAccountTask =
                        GoogleSignIn.getSignedInAccountFromIntent(data);
                if (getAccountTask.isSuccessful()) {
                    initializeDriveClient(getAccountTask.getResult());

                } else {

                }
                break;

        }
        fragment.onActivityResult(requestCode, resultCode, data);
    }
    public void showAlertWithCancels(String BuilderText) {
        AlertDialog.Builder builder =
                new AlertDialog.Builder(this, R.style.AppCompatAlertDialogStyle);
        builder.setTitle("My Accounts -Backup Data");
        builder.setMessage(BuilderText);
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
                dialog.dismiss();
            }
        });
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                uploadFile.UploadFile();
                dialog.dismiss();
                finish();
            }
        });
        builder.show();
    }
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
//            if (doubleBackToExitPressedOnce) {
//                super.onBackPressed();
//                return;
//            }
            if(!(fragment instanceof Today))
            {
                fragment = new Today();
                FragmentTransaction fragmentTransaction=
                        getSupportFragmentManager().beginTransaction();
                fragmentTransaction.setCustomAnimations(R.anim.enter_from_left,
                        R.anim.exit_to_right, R.anim.enter_from_right, R.anim.exit_to_left);
                fragmentTransaction.replace(R.id.fragment_container,fragment);
                fragmentTransaction.commit();
                fab.show();
            }else {
                this.doubleBackToExitPressedOnce = true;
                showAlertWithCancels("Do you want to backup data before exist?");
//                Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

//                new Handler().postDelayed(new Runnable() {
//
//                    @Override
//                    public void run() {
//                        doubleBackToExitPressedOnce = false;
//                    }
//                }, 2000);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement


        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_daily) {
            if(!(fragment instanceof Today) ){
                fragment=new Today();
                fab.show();
            }
        } else if (id == R.id.nav_statement) {
            if(!(fragment instanceof Statement) ){
                fragment=new Statement();
                fab.hide();
            }
        } else if (id == R.id.nav_persons) {
            if(!(fragment instanceof ManagePersons) ){
                fragment=new ManagePersons();
                fab.hide();
            }
        }else if (id == R.id.nav_profile) {
            if(!(fragment instanceof Profile) ){
                fragment=new Profile();
                fab.hide();
            }
        }else if (id == R.id.nav_backup) {
            if(!(fragment instanceof ManageBackup) ){
                fragment=new ManageBackup();
                fab.hide();
            }
        }else if (id == R.id.nav_pwd) {
            if(!(fragment instanceof Change_Password) ){
                fragment=new Change_Password();
                fab.hide();
            }
        }
        FragmentTransaction fragmentTransaction=
                getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.enter_from_left,
                R.anim.exit_to_right, R.anim.enter_from_right, R.anim.exit_to_left);
        fragmentTransaction.replace(R.id.fragment_container,fragment);
        fragmentTransaction.commit();


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void initializeDriveClient(GoogleSignInAccount signInAccount) {
        mDriveClient = Drive.getDriveClient(getApplicationContext(), signInAccount);
        mDriveResourceClient = Drive.getDriveResourceClient(getApplicationContext(), signInAccount);
        //onDriveClientReady();
    }




}
