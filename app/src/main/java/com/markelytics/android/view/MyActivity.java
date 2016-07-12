package com.markelytics.android.view;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.markelytics.android.R;
import com.markelytics.android.database.DatabaseHandler;
import com.markelytics.android.utils.Constants;

public class MyActivity extends FragmentActivity implements View.OnClickListener {
    String panel_id, panelist_id;
    ImageView imgLogo;
    TextView heading, txtSurvey,txtProfile, txtRewards, txtInvite, txtAccount;
    public ImageView btnSurvey, btnInvite,btnMyAccount, btnProfile, btnRedeemRewards;
    Dialog activationDialog;
    FontChangeCrawler fontChanger;
    RelativeLayout relativeSurvey, relativeProfile, relativeRewards, relativeInvite, relativeAccount;
    Fragment fragment ;
    FragmentManager fragmentManager;
    SharedPreferences sh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);

        panel_id = getIntent().getStringExtra("panel_id");
        panelist_id = getIntent().getStringExtra("panelist_id");
       // Toast.makeText(this, "panel_id" + panel_id + "++++++++" + "panelist_id" + panelist_id, Toast.LENGTH_SHORT).show();

        Constants.PANEL_ID = panel_id;
        Constants.PANELIST_ID = panelist_id;


        if (getIntent().getBooleanExtra("LOGOUT", false))
        {

            finish();
            System.exit(0);
        }

        sh = getSharedPreferences("dbclear", MODE_PRIVATE);
        String panelid = sh.getString("panelist_id", null);
        if(panelid != null){
            if(!panelid.equalsIgnoreCase(Constants.PANELIST_ID)){
                DatabaseHandler db = new DatabaseHandler(MyActivity.this);
                db.Delete_ALL();
               SharedPreferences sharedPreferences = getSharedPreferences("points", Context.MODE_PRIVATE);
                sharedPreferences.edit().clear().commit();
            }
        }

        init();

        SharedPreferences.Editor ed = sh.edit();
        ed.putString("panelist_id",Constants.PANELIST_ID);
        ed.commit();

        btnSurvey.setBackgroundDrawable(getResources().getDrawable(R.drawable.survey_selected));
        btnMyAccount.setBackgroundDrawable(getResources().getDrawable(R.drawable.acct));
        btnInvite.setBackgroundDrawable(getResources().getDrawable(R.drawable.invite));
        btnRedeemRewards.setBackgroundDrawable(getResources().getDrawable(R.drawable.rewards));
        btnProfile.setBackgroundDrawable(getResources().getDrawable(R.drawable.profile));

       // Fragment fragment = null;

        Bundle bundle1 = new Bundle();
        bundle1.putString("panel_id", panel_id);
        bundle1.putString("panelist_id", panelist_id);

        fragment = new YourSurveyFragment();
        fragment.setArguments(bundle1);
        fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.frame_container, fragment).addToBackStack("my_fragment").commit();

    }

    private void init() {

        btnInvite = (ImageView)findViewById(R.id.btnInvite);
        btnSurvey = (ImageView)findViewById(R.id.btnSurvey);
        btnMyAccount = (ImageView)findViewById(R.id.btnMyAccount);
        btnProfile = (ImageView)findViewById(R.id.btnProfile);
        btnRedeemRewards = (ImageView)findViewById(R.id.btnRedeemRewards);

        imgLogo = (ImageView)findViewById(R.id.imgLogo);
        heading = (TextView)findViewById(R.id.heading);
        txtAccount = (TextView)findViewById(R.id.txtAccount);
        txtInvite = (TextView)findViewById(R.id.txtInvite);
        txtProfile = (TextView)findViewById(R.id.txtProfile);
        txtRewards = (TextView)findViewById(R.id.txtRewards);
        txtSurvey = (TextView)findViewById(R.id.txtSurvey);

        relativeSurvey = (RelativeLayout) findViewById(R.id.relativeSurvey);
        relativeAccount = (RelativeLayout) findViewById(R.id.relativeAccount);
        relativeInvite = (RelativeLayout) findViewById(R.id.relativeInvite);
        relativeRewards = (RelativeLayout) findViewById(R.id.relativeRewards);
        relativeProfile = (RelativeLayout) findViewById(R.id.relativeProfile);

        relativeSurvey.setOnClickListener(this);
        relativeAccount.setOnClickListener(this);
        relativeInvite.setOnClickListener(this);
        relativeRewards.setOnClickListener(this);
        relativeProfile.setOnClickListener(this);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_my, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {

        //FragmentManager fragmentManager;
        Bundle bundle = new Bundle();
        bundle.putString("panel_id", panel_id);
        bundle.putString("panelist_id", panelist_id);

        switch (v.getId()) {

            case R.id.relativeInvite:

                btnInvite.setBackgroundDrawable(getResources().getDrawable(R.drawable.invite_selected));
                btnMyAccount.setBackgroundDrawable(getResources().getDrawable(R.drawable.acct));
                btnSurvey.setBackgroundDrawable(getResources().getDrawable(R.drawable.survey));
                btnRedeemRewards.setBackgroundDrawable(getResources().getDrawable(R.drawable.rewards));
                btnProfile.setBackgroundDrawable(getResources().getDrawable(R.drawable.profile));

                fragment = new InviteFriendFragment();
                fragment.setArguments(bundle);
                fragmentManager = getSupportFragmentManager();
                fragmentManager.beginTransaction()
                        .replace(R.id.frame_container, fragment).addToBackStack("my_fragment").commit();
                break;

            case R.id.relativeSurvey:

                btnSurvey.setBackgroundDrawable(getResources().getDrawable(R.drawable.survey_selected));
                btnMyAccount.setBackgroundDrawable(getResources().getDrawable(R.drawable.acct));
                btnInvite.setBackgroundDrawable(getResources().getDrawable(R.drawable.invite));
                btnRedeemRewards.setBackgroundDrawable(getResources().getDrawable(R.drawable.rewards));
                btnProfile.setBackgroundDrawable(getResources().getDrawable(R.drawable.profile));

                fragment = new YourSurveyFragment();
                bundle.putString("calledFrom","my");
                fragment.setArguments(bundle);
                fragmentManager = getSupportFragmentManager();
                fragmentManager.beginTransaction()
                        .replace(R.id.frame_container, fragment).addToBackStack("my_fragment").commit();

                break;

            case R.id.relativeRewards:

                btnRedeemRewards.setBackgroundDrawable(getResources().getDrawable(R.drawable.rewards_selected));
                btnMyAccount.setBackgroundDrawable(getResources().getDrawable(R.drawable.acct));
                btnSurvey.setBackgroundDrawable(getResources().getDrawable(R.drawable.survey));
                btnInvite.setBackgroundDrawable(getResources().getDrawable(R.drawable.invite));
                btnProfile.setBackgroundDrawable(getResources().getDrawable(R.drawable.profile));

                fragment = new RedeemPointsFragment();
                fragment.setArguments(bundle);
                fragmentManager = getSupportFragmentManager();
                fragmentManager.beginTransaction()
                        .replace(R.id.frame_container, fragment).addToBackStack("my_fragment").commit();
                break;

            case R.id.relativeProfile:

                btnProfile.setBackgroundDrawable(getResources().getDrawable(R.drawable.profile_selected));
                btnMyAccount.setBackgroundDrawable(getResources().getDrawable(R.drawable.acct));
                btnSurvey.setBackgroundDrawable(getResources().getDrawable(R.drawable.survey));
                btnRedeemRewards.setBackgroundDrawable(getResources().getDrawable(R.drawable.rewards));
                btnInvite.setBackgroundDrawable(getResources().getDrawable(R.drawable.invite));

                fragment = new ProfilingFragment();
                fragment.setArguments(bundle);
                fragmentManager = getSupportFragmentManager();
                fragmentManager.beginTransaction()
                        .replace(R.id.frame_container, fragment).addToBackStack("my_fragment").commit();
                break;

            case R.id.relativeAccount:

                btnMyAccount.setBackgroundDrawable(getResources().getDrawable(R.drawable.acct_selected));
                btnInvite.setBackgroundDrawable(getResources().getDrawable(R.drawable.invite));
                btnSurvey.setBackgroundDrawable(getResources().getDrawable(R.drawable.survey));
                btnRedeemRewards.setBackgroundDrawable(getResources().getDrawable(R.drawable.rewards));
                btnProfile.setBackgroundDrawable(getResources().getDrawable(R.drawable.profile));

                Bundle bundle1 = new Bundle();
                bundle1.putString("panel_id", panel_id);
                bundle1.putString("panelist_id", panelist_id);

                fragment = new ViewProfileFragment();
                fragment.setArguments(bundle);
                fragmentManager = getSupportFragmentManager();
                fragmentManager.beginTransaction()
                        .replace(R.id.frame_container, fragment).addToBackStack("my_fragment").commit();

                break;

            default:
                break;
        }
    }


   //onBack press the exit dialog popup
    @Override
    public void onBackPressed() {


        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.frame_container);
        if (currentFragment instanceof YourSurveyFragment ) {
            activationDialog = new Dialog(MyActivity.this);
            activationDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            //for transparent dialog parent layout
            activationDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.R.color.transparent));
            activationDialog.setContentView(R.layout.exit);

            fontChanger = new FontChangeCrawler(getAssets());
            fontChanger.replaceFonts((ViewGroup) activationDialog.findViewById(android.R.id.content));

            TextView txtMesssage = (TextView)activationDialog.findViewById(R.id.txtMesssage);

            txtMesssage.setText("Do you want to exit?");

            Button btn_ok = (Button) activationDialog.findViewById(R.id.btnYes);
            Button btn_cancel = (Button) activationDialog.findViewById(R.id.btnCancel);

            btn_cancel.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    activationDialog.cancel();
                }
            });

            btn_ok.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub

                    Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.putExtra("EXIT", true);
                    startActivity(intent);
                    finish();
                }
            });

            activationDialog.show();
            activationDialog.setCancelable(false);

        }
        else  if (currentFragment instanceof RecentSurveyFragment && !Constants.EXIT) {
            activationDialog = new Dialog(MyActivity.this);
            activationDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            //for transparent dialog parent layout
            activationDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.R.color.transparent));
            activationDialog.setContentView(R.layout.exit);

            fontChanger = new FontChangeCrawler(getAssets());
            fontChanger.replaceFonts((ViewGroup) activationDialog.findViewById(android.R.id.content));

            TextView txtMesssage = (TextView)activationDialog.findViewById(R.id.txtMesssage);

            txtMesssage.setText("Do you want to exit?");

            Button btn_ok = (Button) activationDialog.findViewById(R.id.btnYes);
            Button btn_cancel = (Button) activationDialog.findViewById(R.id.btnCancel);

            btn_cancel.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    activationDialog.cancel();
                }
            });

            btn_ok.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub

                    Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.putExtra("EXIT", true);
                    startActivity(intent);
                    finish();
                }
            });

            activationDialog.show();
            activationDialog.setCancelable(false);

        }else  if (currentFragment instanceof GetMoreSurveysFragment) {
            activationDialog = new Dialog(MyActivity.this);
            activationDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            //for transparent dialog parent layout
            activationDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.R.color.transparent));
            activationDialog.setContentView(R.layout.exit);

            fontChanger = new FontChangeCrawler(getAssets());
            fontChanger.replaceFonts((ViewGroup) activationDialog.findViewById(android.R.id.content));

            TextView txtMesssage = (TextView)activationDialog.findViewById(R.id.txtMesssage);

            txtMesssage.setText("Do you want to exit?");

            Button btn_ok = (Button) activationDialog.findViewById(R.id.btnYes);
            Button btn_cancel = (Button) activationDialog.findViewById(R.id.btnCancel);

            btn_cancel.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    activationDialog.cancel();
                }
            });

            btn_ok.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub

                    Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.putExtra("EXIT", true);
                    startActivity(intent);
                    finish();
                }
            });

            activationDialog.show();
            activationDialog.setCancelable(false);

        }



        if (getSupportFragmentManager().getBackStackEntryCount() > 1)
        {
            System.out.println("++++++++++OnBack if+++++++++++++");
            getSupportFragmentManager().popBackStack();
            //finish();
        }
        else if(getSupportFragmentManager().getBackStackEntryCount() == 1)
        {
            System.out.println("++++++++++OnBack else+++++++++++++");
            activationDialog = new Dialog(MyActivity.this);
            activationDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            //for transparent dialog parent layout
            activationDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.R.color.transparent));
            activationDialog.setContentView(R.layout.exit);

            fontChanger = new FontChangeCrawler(getAssets());
            fontChanger.replaceFonts((ViewGroup) activationDialog.findViewById(android.R.id.content));

            TextView txtMesssage = (TextView)activationDialog.findViewById(R.id.txtMesssage);

            txtMesssage.setText("Do you want to exit?");

            Button btn_ok = (Button) activationDialog.findViewById(R.id.btnYes);
            Button btn_cancel = (Button) activationDialog.findViewById(R.id.btnCancel);

            btn_cancel.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    activationDialog.cancel();
                }
            });

            btn_ok.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub

                    Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.putExtra("EXIT", true);
                    startActivity(intent);
                    finish();
                }
            });

            activationDialog.show();
            activationDialog.setCancelable(false);

        }
    }

}
