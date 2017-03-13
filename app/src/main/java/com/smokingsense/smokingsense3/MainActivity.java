package com.smokingsense.smokingsense3;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.nhn.android.maps.overlay.NMapPOIdata;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    NavigationView navigationView;

    //firebase Auth
    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private FirebaseUser user;

    private String mUsername = "ANONYMOUS";
    private static final int RC_SIGN_IN = 123;




    LinearLayout loginLinearLayout;

    TextView userName;
    CircularImageView userImage;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if(savedInstanceState == null){
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.content_fragment_layout, new MapFragment())
                    .commit();
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);



        /*
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        */

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        userName = (TextView)navigationView.getHeaderView(0).findViewById(R.id.login_and_username);
        userImage = (CircularImageView) navigationView.getHeaderView(0).findViewById(R.id.user_imageView);

        loginLinearLayout = (LinearLayout)navigationView.getHeaderView(0).findViewById(R.id.header_linearlayout);
        loginLinearLayout.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                if(userName.getText().toString().equals("로그인 하기")) {
                    Login();
                }
                else{

                    AlertDialog.Builder mDialogBuilder = new AlertDialog.Builder(MainActivity.this);
                    mDialogBuilder.setMessage("로그아웃 하시겠습니까?");

                    mDialogBuilder.setPositiveButton("확인", new DialogInterface.OnClickListener(){
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            AuthUI.getInstance().signOut(MainActivity.this);
                            userName.setText("로그인 하기");
                            mUsername = "ANONYMOUS";
                            userImage.setVisibility(View.GONE);
                        }
                    });
                    mDialogBuilder.setNegativeButton("취소", new DialogInterface.OnClickListener(){
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    });

                    AlertDialog mDialog = mDialogBuilder.create();
                    mDialog.show();


                }
            }
        });


        //firebase auth
        mFirebaseAuth = FirebaseAuth.getInstance();
        mAuthStateListener = new FirebaseAuth.AuthStateListener(){
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                user = firebaseAuth.getCurrentUser();

                if(user != null){
                    mUsername = user.getDisplayName();
                   // ImageView userImage = (ImageView)findViewById(R.id.user_imageView);
                    Log.v("test firebase Auth", mUsername);
                    Log.v("testFirebase Auth 1", user.getDisplayName());
                    userName.setText(mUsername);


                    userImage.setVisibility(View.VISIBLE);
                    Glide.with(userImage.getContext())
                            .load(user.getPhotoUrl())
                            .into(userImage);

                }else{

                }
            }
        };




    }



    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


/*
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.search, menu);

        MenuItem searchMenu = menu.findItem(R.id.menu_search);
        SearchView mSearchView = (SearchView) searchMenu.getActionView();
        mSearchView.setQueryHint("흡연 장소 검색");
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener(){
            @Override
            public boolean onQueryTextSubmit(String query) {
                Toast.makeText(MainActivity.this, "검색 완료", Toast.LENGTH_SHORT).show();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                Toast.makeText(MainActivity.this, "검색 입력", Toast.LENGTH_SHORT).show();
                return false;
            }
        });
        return true;
    }*/

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == RC_SIGN_IN){
            if(resultCode == RESULT_OK){
                Toast.makeText(this, "로그인 완료!", Toast.LENGTH_SHORT).show();
            }else if(resultCode == RESULT_CANCELED){
                Toast.makeText(this, "로그인에 실패했습니다.", Toast.LENGTH_SHORT).show();
            }
        }
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

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            if(mUsername.equals("ANONYMOUS")){
                AlertDialog.Builder mDialogBuilder = new AlertDialog.Builder(MainActivity.this);
                mDialogBuilder.setMessage("로그인이 필요합니다. 로그인 하시겠습니까?");

                mDialogBuilder.setPositiveButton("확인", new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Login();
                    }
                });
                mDialogBuilder.setNegativeButton("취소", new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });

                AlertDialog mDialog = mDialogBuilder.create();
                mDialog.show();

            }else {
                Intent intent = new Intent(this, MyCount.class);
                intent.putExtra("username", mUsername);
                intent.putExtra("userimage",user.getPhotoUrl().toString());
                startActivity(intent);
            }

        } else if (id == R.id.nav_gallery) {
            AlertDialog.Builder mDialogBuilder = new AlertDialog.Builder(MainActivity.this);
            mDialogBuilder.setMessage("추후 개발 예정입니다.");

            mDialogBuilder.setPositiveButton("확인", new DialogInterface.OnClickListener(){
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                }
            });

            AlertDialog mDialog = mDialogBuilder.create();
            mDialog.show();

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {
            AlertDialog.Builder mDialogBuilder = new AlertDialog.Builder(MainActivity.this);
            mDialogBuilder.setMessage("추후 개발 예정입니다.");

            mDialogBuilder.setPositiveButton("확인", new DialogInterface.OnClickListener(){
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                }
            });

            AlertDialog mDialog = mDialogBuilder.create();
            mDialog.show();

        } else if (id == R.id.nav_share) {
            AlertDialog.Builder mDialogBuilder = new AlertDialog.Builder(MainActivity.this);
            mDialogBuilder.setMessage("추후 개발 예정입니다. ");


            mDialogBuilder.setPositiveButton("확인", new DialogInterface.OnClickListener(){
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                }
            });

            AlertDialog mDialog = mDialogBuilder.create();
            mDialog.show();

        } else if (id == R.id.nav_send) {
            AlertDialog.Builder mDialogBuilder = new AlertDialog.Builder(MainActivity.this);
            mDialogBuilder.setMessage("추후 개발 예정입니다. ");


            mDialogBuilder.setPositiveButton("확인", new DialogInterface.OnClickListener(){
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                }
            });

            AlertDialog mDialog = mDialogBuilder.create();
            mDialog.show();

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        mFirebaseAuth.addAuthStateListener(mAuthStateListener);
    }

    @Override
    protected void onPause() {
        if(mAuthStateListener != null){
            mFirebaseAuth.removeAuthStateListener(mAuthStateListener);
        }
        super.onPause();
    }

    public void Login(){
        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setIsSmartLockEnabled(false)
                        .setProviders(
                                AuthUI.EMAIL_PROVIDER,
                                AuthUI.GOOGLE_PROVIDER)
                        .build(),RC_SIGN_IN);
    }
}
