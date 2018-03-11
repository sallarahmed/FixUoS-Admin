package com.example.sallarkhan.fixuos_admin;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.NotificationCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.measurement.AppMeasurement;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.mingle.entity.MenuEntity;
import com.mingle.sweetpick.BlurEffect;
import com.mingle.sweetpick.DimEffect;
import com.mingle.sweetpick.RecyclerViewDelegate;
import com.mingle.sweetpick.SweetSheet;
import com.mingle.sweetpick.ViewPagerDelegate;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private static final String TAG = "MainActivity";
    DatabaseReference mDatabase;
    DatabaseReference mDatabaseUser;
    StorageReference mStorage;
    FirebaseAuth mAuth;
    FirebaseAuth.AuthStateListener mAuthListener;
    public static final int CAMERA_IMAGE = 1;
    private String title = null;
    private String desc = null;
    private RecyclerView mComplaintList;
    private String complaintUserId;
    private AlertDialog.Builder builder;
    private String current_uid ;
    private RelativeLayout rl;
    private CoordinatorLayout cl;
    private LinearLayout ll;
    private MotionEvent event;
    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLayoutManager;
    private DatabaseReference mFireDatabase;
    private Context context;
    SweetSheet mSweetSheet1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = this;
        rl = (RelativeLayout) findViewById(R.id.relativeLayout);
        builder = new AlertDialog.Builder(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mDatabase = FirebaseDatabase.getInstance().getReference().child("blog");
        mDatabaseUser = FirebaseDatabase.getInstance().getReference().child("user");
        mDatabase.keepSynced(true);
        mDatabaseUser.keepSynced(true);
        mStorage = FirebaseStorage.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        mComplaintList = (RecyclerView) findViewById(R.id.complaint_list);
        mComplaintList.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(MainActivity.this);
        mComplaintList.setLayoutManager(mLayoutManager);
        mLayoutManager.setReverseLayout(true);
        mLayoutManager.setStackFromEnd(true);



        mAuthListener = new FirebaseAuth.AuthStateListener(){
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser() == null){
                    Intent loginIntent = new Intent(MainActivity.this,LoginActivity.class);
                    loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(loginIntent);

                }
            }
        };

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        checkUserExist();
        GetUserName();

    }





    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
        FirebaseRecyclerAdapter<Blog,BlogViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Blog, BlogViewHolder>(


                Blog.class,
                R.layout.blog_row,
                BlogViewHolder.class,
                mDatabase
        ) {
            @Override
            protected void populateViewHolder(final BlogViewHolder viewHolder, final Blog blog, int position) {
                final String postKey = getRef(position).toString().replaceFirst("https://fixuos.firebaseio.com/blog/","");

// And now set it to the RecyclerView
             //   mRecyclerView.setAdapter();

                viewHolder.setUid(blog.getUid());
               viewHolder.setTitle(blog.getTitle());
                viewHolder.setDesc(blog.getDesc());
                viewHolder.setImage(getApplicationContext(),blog.getImage());
                TextView tvForward = viewHolder.mView.findViewById(R.id.tvForward);
                tvForward.setOnClickListener(new View.OnClickListener() {
                  //  PopupMenu popup;
                    @Override
                    public void onClick(View view) {
                   //     Toast.makeText(getApplicationContext(),postKey,Toast.LENGTH_LONG).show();
                     /*   popup = new PopupMenu(MainActivity.this, view);
                        Menu m = popup.getMenu();
                        MenuInflater inflater = popup.getMenuInflater();
                        inflater.inflate(R.menu.forward_menu, popup.getMenu());
                      //  popup.show();*/
                        mSweetSheet1 = new SweetSheet(rl);

                        //从menu 中设置数据源
                        mSweetSheet1.setMenuList(R.menu.forward_menu);
                        mSweetSheet1.setDelegate(new ViewPagerDelegate());
                        mSweetSheet1.setBackgroundEffect(new DimEffect(0.8f));
                        mSweetSheet1.setOnMenuItemClickListener(new SweetSheet.OnMenuItemClickListener() {
                            @Override
                            public boolean onItemClick(int position, MenuEntity menuEntity1) {
                                String vcEmail = "mrvcuos@gmail.com";
                                String deanEmail = "mrdeaniict@gmail.com";
                                String subject = "Hello sir\nHere is some problem Please Point out this problem\n"+blog.getTitle();
                                String desc = blog.getDesc()+"\nPlease click the link below\n"+blog.getImage();
                               // int id = menuEntity1.getItemId();
                                if (position == 1){
                                    mDatabase.child(postKey).child("forwarded").setValue("Mr Dean");
                                    emailIntentCalling(deanEmail,subject,desc);
                                }else if (position == 0){
                                    mDatabase.child(postKey).child("forwarded").setValue("Mr V.C");
                                    emailIntentCalling(vcEmail,subject,desc);
                                }

                                return false;
                             //   Toast.makeText(MainActivity.this, menuEntity1.title + "  " + position, Toast.LENGTH_SHORT).show();
                             //   return true;
                            }
                        });
                        mSweetSheet1.toggle();


               /*     popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            String vcEmail = "mrvcuos@gmail.com";
                            String deanEmail = "mrdeaniict@gmail.com";
                            String subject = "Hello sir\nHere is some problem Please Point out this problem\n"+blog.getTitle();
                            String desc = blog.getDesc()+"\nPlease click the link below\n"+blog.getImage();
                            int id = item.getItemId();
                            if (id == R.id.action_forward_dean){
                                mDatabase.child(postKey).child("forwarded").setValue(deanEmail);
                                emailIntentCalling(deanEmail,subject,desc);
                            }else if (id == R.id.action_forward_vc){
                                mDatabase.child(postKey).child("forwarded").setValue(vcEmail);
                                emailIntentCalling(vcEmail,subject,desc);
                            }

                            return false;
                        }
                    });*/


                    }

                });



                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(final View view) {


/*
// SweetSheet control, according to rl to confirm the location
                      SweetSheet  mSweetSheet2 = new SweetSheet(rl);


// set the data source (data source support set list array, also support from the menu to get)
                        mSweetSheet2.setMenuList(R.menu.card_view_menu);

// Show different styles according to the different Delegate settings.
                        mSweetSheet2.setDelegate(new ViewPagerDelegate());
// Show different effects according to set the effect of the background BlurEffect: blur effect. DimEffect dim effect.
                        mSweetSheet2.setBackgroundEffect(new BlurEffect(8));
// set the click event.
                        mSweetSheet2.setOnMenuItemClickListener(new SweetSheet.OnMenuItemClickListener() {
                            @Override
                            public boolean onItemClick(int position, MenuEntity menuEntity1) {
// Instantly change the color of the current item.
                                //  list.get(position).titleColor = 0xff5823ff;
                                //  ((RecyclerViewDelegate) mSweetSheet.getDelegate()).notifyDataSetChanged();


                                Toast.makeText(MainActivity.this, menuEntity1.title + "  " + position, Toast.LENGTH_SHORT).show();
                                return false;
                            }
                        });*/
/*
                        final int index = event.getActionIndex();
                    float[] touchLocation = new float[] {
                                event.getX(index), event.getY(index)
                        };*/
                        PopupMenu popup = new PopupMenu(MainActivity.this, view);
                        Menu m = popup.getMenu();
                        MenuInflater inflater = popup.getMenuInflater();
                        inflater.inflate(R.menu.card_view_menu, popup.getMenu());
                       // popup.showAsDropDown(view,x,y);
                        popup.setGravity(Gravity.AXIS_Y_SHIFT);
                        popup.setGravity(Gravity.AXIS_X_SHIFT);
                        popup.show();


                        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem item) {
                                int id = item.getItemId();
                                if (id == R.id.action_view_complaint){
                                    Intent browserIntent = new Intent(MainActivity.this,BrowserActivity.class);
                                    browserIntent.putExtra("url",blog.getImage());
                                    startActivity(browserIntent);

                                }else if (id == R.id.action_view_profile){

                                    mDatabaseUser.child(blog.getUid()).addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            String name  = (String) dataSnapshot.child("name").getValue();
                                            String cell = (String) dataSnapshot.child("cell").getValue();
                                            String roll = (String) dataSnapshot.child("roll").getValue();


                                            builder.setTitle("Student Details");
                                            builder.setMessage("Name: "+name+"\nCell No: "+cell+"\nRoll No: "+roll);
                                            builder.show();
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {
                                            Snackbar.make(view, "Something Went Wrong", Snackbar.LENGTH_LONG)
                                                    .setAction("Action", null).show();
                                        }
                                    });

                                }else if (id == R.id.action_view_status){
                                    mDatabase.child(postKey).addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            if (dataSnapshot.hasChild("forwarded")){
                                                String forward = (String) dataSnapshot.child("forwarded").getValue();
                                                builder.setTitle("Complaint Status");
                                                builder.setMessage("Complaint is forwarded to :"+forward);
                                                builder.show();

                                            }else{
                                                builder.setTitle("Complaint Status");
                                                builder.setMessage("Complaint is not forwarded");
                                                builder.show();
                                            }

                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {
                                            Snackbar.make(view, "Something Went Wrong", Snackbar.LENGTH_LONG)
                                                    .setAction("Action", null).show();
                                        }
                                    });

                                }

                                return false;
                            }
                        });

                    }
                });

            }
        };
            mComplaintList.setAdapter(firebaseRecyclerAdapter);
    }


    public final void emailIntentCalling(String email_receiver,String subject ,String extra){


//3rd attempt
        Intent intent = new Intent(Intent.ACTION_SENDTO); // it's not ACTION_SEND
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        intent.putExtra(Intent.EXTRA_TEXT, extra);
        intent.setData(Uri.parse("mailto:"+email_receiver)); // or just "mailto:" for blank
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // this will make such that when user returns to your app, your app is displayed, instead of the email app.
        startActivity(intent);


    }
    public static class BlogViewHolder extends RecyclerView.ViewHolder{
        private static final String TAG = "MainActivity";
        View mView;
        String user_id;
        String t,d;

        //   String complaintUserId;
        public BlogViewHolder(View itemView) {
            super(itemView);
            mView = itemView;

        }


        public void setTitle(String title){
            t = title;
            TextView post_title = mView.findViewById(R.id.tv_title);
            post_title.setText(title);

        }
        public void setUid(String username){
            user_id = username;
            TextView post_username = mView.findViewById(R.id.tv_username_blogrow);
            post_username.setText("");
        }
        public void setDesc(String desc){
            d = desc;
            TextView post_discription = mView.findViewById(R.id.tv_discryption);
            post_discription.setText(desc);
        }



        public void setImage(Context context,String image){
            ImageView post_image = mView.findViewById(R.id.post_img);
            //    Picasso.with(context).load(image).resize(640,480).into(post_image);
            Picasso.with(context)
                    .load(image)
                    .resize(640,480)
                    .placeholder(R.drawable.loading)
                    .error(R.drawable.play)
                    .into(post_image);
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }else if (mSweetSheet1.isShow()) {
            mSweetSheet1.dismiss();
        }else {
            super.onBackPressed();
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
        if (id == R.id.action_settings) {
            return true;
        }else if(id== R.id.action_logout){
            logOut();
        }

        return super.onOptionsItemSelected(item);
    }

    private void logOut() {
        mAuth.signOut();
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action

        } else if (id == R.id.nav_gallery) {

        }  else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    private void checkUserExist() {
        if(mAuth.getCurrentUser() != null) {
            current_uid = mAuth.getCurrentUser().getUid();
            mDatabaseUser.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (!dataSnapshot.hasChild(current_uid)) {
                        Log.e("LoginActivity", "onDataChange: if");
                        Intent setupIntent = new Intent(MainActivity.this, SetupAccountActivity.class);
                        setupIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(setupIntent);

                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Snackbar.make(findViewById(R.id.drawer_layout), "Server Denied :"+databaseError,
                            Snackbar.LENGTH_LONG).setAction("Action", null).show();
                }
            });
        }

    }
    public void GetUserName() {
        if (current_uid != null) {
           // final String[] name = new String[1];
            Log.e(TAG, "GetUserName: " + current_uid);
            mDatabaseUser.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.hasChild(current_uid)){

                        mDatabaseUser.child(current_uid).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                String name = (String) dataSnapshot.child("name").getValue();


                                NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);

                                View headerView = navigationView.getHeaderView(0);
                                //    ImageView drawerImage = headerView.findViewById(R.id.drawer_image);
                                TextView drawerUsername =  headerView.findViewById(R.id.nav_username);
                                //     TextView drawerAccount = headerView.findViewById(R.id.drawer_account);
                                //     drawerImage.setImageDrawable(R.drawable.ic_user);
                                drawerUsername.setText(name);
                                //      drawerAccount.setText("user@gmail.com");
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                Snackbar.make(findViewById(R.id.drawer_layout), "Server Denied :"+databaseError,
                                        Snackbar.LENGTH_LONG).setAction("Action", null).show();

                            }
                        });

                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Snackbar.make(findViewById(R.id.drawer_layout), "Server Denied :"+databaseError,
                            Snackbar.LENGTH_LONG).setAction("Action", null).show();
                }
            });

         //   Log.e(TAG, "GetUserName: " + name);


        }

    }


}
