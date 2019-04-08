package com.example.dell.eventmanager.Task;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.example.dell.eventmanager.Legend.Legend;
import com.example.dell.eventmanager.Legend.LegendAdapter;
import com.example.dell.eventmanager.R;
import com.example.dell.eventmanager.SwipeController;
import com.example.dell.eventmanager.SwipeControllerActions;
import com.example.dell.eventmanager.SwipeControllerNC;
import com.example.dell.eventmanager.User.Users;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import java.util.ArrayList;
import java.util.List;

public class TasksActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {


    public static final String ANONYMOUS = "anonymous";

    private TaskAdapter mTaskAdapter;
    private TaskAdapter mCompletedTaskAdapter;

    private RecyclerView taskRecyclerView;
    private RecyclerView.LayoutManager taskLayoutManager;

    private RecyclerView completedTaskRecyclerView;
    private RecyclerView.LayoutManager completedTaskLayoutManager;

    private GridView mLegendListView;
    private LegendAdapter mLegendAdapter;

    private Button mButtonAddTask;
    private FirebaseDatabase mFirebaseDatabase;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private DatabaseReference mMessagesDatabaseReference;
    private DatabaseReference mMessagesDatabaseReferenceToLegend;
    private ChildEventListener mChildEventListener;


    private ArrayList<Tasks> task_list = new ArrayList<Tasks>();
    private ArrayList<Tasks> completed_task_list = new ArrayList<Tasks>();
    private ArrayList<Legend> legend_list = new ArrayList<Legend>();

    private ArrayList<Users> selected_user_list = new ArrayList<Users>();
    GridView mSelectedUserGridView;

    Intent intentFromEventsActivity;
    String eventKey;

    ImageView imageBookmark;

    SwipeController swipeController;
    SwipeControllerNC completedswipeController;

    ImageView backButton;
    TextView toolbarName;

    Dialog legendDialog;
    Button legendClose;
    FloatingActionButton legendDelete;

   // View emptyView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_task_activity);

//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        toolbar.setTitle("Tasks");
//        setSupportActionBar(toolbar);


        if (Build.VERSION.SDK_INT >= 21) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.parseColor("#FF6961"));
        }
//        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
//        actionBar.setHomeButtonEnabled(true);
//        actionBar.setDisplayHomeAsUpEnabled(true);

        intentFromEventsActivity = getIntent();
        eventKey = intentFromEventsActivity.getStringExtra("eventKey");

        toolbarName = findViewById(R.id.toolbar_name_task);
        toolbarName.setText("Tasks");
        backButton = findViewById(R.id.back_button_task);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
//                Intent intent = new Intent(TasksActivity.this, EventsActivity.class);
//                startActivity(intent);
            }
        });


        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mMessagesDatabaseReference = mFirebaseDatabase.getReference().child("toDoList").child(eventKey).child("tasksList");
        mMessagesDatabaseReferenceToLegend = mFirebaseDatabase.getReference().child("toDoList").child(eventKey).child("legend");

        taskRecyclerView = findViewById(R.id.taskListView);
        taskLayoutManager = new LinearLayoutManager(this);
        mTaskAdapter = new TaskAdapter(task_list);
        taskRecyclerView.setLayoutManager(taskLayoutManager);
        taskRecyclerView.setAdapter(mTaskAdapter);

       // emptyView = findViewById(R.id.empty_view);

        completedTaskRecyclerView = findViewById(R.id.completedTasksListView);
        completedTaskLayoutManager = new LinearLayoutManager(this);
        mCompletedTaskAdapter = new TaskAdapter(completed_task_list);
        completedTaskRecyclerView.setLayoutManager(completedTaskLayoutManager);
        completedTaskRecyclerView.setAdapter(mCompletedTaskAdapter);

        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();

        swipeController = new SwipeController(new SwipeControllerActions() {
            @Override
            public void onRightClicked(int position) {
                if ((mFirebaseUser.getEmail().compareTo("dscvitvellore@gmail.com") != 0)) {
                    Toast.makeText(TasksActivity.this, "You are not allowed to delete the task", Toast.LENGTH_SHORT).show();
                } else {
                    Tasks ev;
                    ev = task_list.get(position);
                    String eventDeleteKey = ev.getTaskKey();
                    mTaskAdapter.taskList.remove(mTaskAdapter.taskList.get(position));
                    mTaskAdapter.notifyDataSetChanged();
                    mMessagesDatabaseReference.child(eventDeleteKey).removeValue();
                }
            }

            @Override
            public void onLeftClicked(int position) {


                Tasks t;
                t = task_list.get(position);
                String taskKey;
                taskKey = t.getTaskKey();

                mMessagesDatabaseReference.child(taskKey).child("completed").setValue("true");

                completed_task_list.add(mTaskAdapter.taskList.get(position));
                mCompletedTaskAdapter.notifyDataSetChanged();

                task_list.remove(mTaskAdapter.taskList.get(position));
                mTaskAdapter.notifyDataSetChanged();


            }

        });

        ItemTouchHelper itemTouchhelper = new ItemTouchHelper(swipeController);
        itemTouchhelper.attachToRecyclerView(taskRecyclerView);
        taskRecyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
                swipeController.onDraw(c);
            }
        });


        completedswipeController = new SwipeControllerNC(new SwipeControllerActions() {
            @Override
            public void onRightClicked(int position) {
                if ((mFirebaseUser.getEmail().compareTo("dscvitvellore@gmail.com") != 0)) {
                    Toast.makeText(TasksActivity.this, "You are not allowed to delete the task", Toast.LENGTH_SHORT).show();
                } else {
                    Tasks ev;
                    ev = completed_task_list.get(position);
                    String taskDeleteKey = ev.getTaskKey();
                    mCompletedTaskAdapter.taskList.remove(mCompletedTaskAdapter.taskList.get(position));
                    mCompletedTaskAdapter.notifyDataSetChanged();
                    mMessagesDatabaseReference.child(taskDeleteKey).removeValue();
                }
            }

            @Override
            public void onLeftClicked(int position) {

                Tasks t;
                t = completed_task_list.get(position);
                String taskKey;
                taskKey = t.getTaskKey();

                task_list.add(mCompletedTaskAdapter.taskList.get(position));
                mTaskAdapter.notifyDataSetChanged();

                completed_task_list.remove(mCompletedTaskAdapter.taskList.get(position));
                mCompletedTaskAdapter.notifyDataSetChanged();

                mMessagesDatabaseReference.child(taskKey).child("completed").setValue("false");


            }

        });


        ItemTouchHelper completeditemTouchhelper = new ItemTouchHelper(completedswipeController);
        completeditemTouchhelper.attachToRecyclerView(completedTaskRecyclerView);
        completedTaskRecyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
                completedswipeController.onDraw(c);
            }
        });

        mLegendListView = (GridView) findViewById(R.id.labelListView);
        mLegendAdapter = new LegendAdapter(this, R.layout.legend_list_item, legend_list, eventKey);
        mLegendListView.setAdapter(mLegendAdapter);


        mButtonAddTask = findViewById(R.id.btn_add_task);
        mButtonAddTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO
                Intent intentToAddTaskActivity = new Intent(TasksActivity.this, AddTaskActivity.class);
                intentToAddTaskActivity.putExtra("eventKey", eventKey);
                startActivity(intentToAddTaskActivity);
            }
        });

        FetchLegendListViews();
       // FetchTaskListViews();

        mMessagesDatabaseReference.addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        completed_task_list.clear();
                        task_list.clear();
                        for (DataSnapshot data : dataSnapshot.getChildren()) {
                            if (data.child("completed").getValue().equals("false")) {
                                String date = data.child("date").getValue().toString();
                                String name = data.child("name").getValue().toString();
                                String key = data.getKey();
                                String lable = data.child("lable").getValue().toString();
                                String lableColor = data.child("lableColor").getValue().toString();

                                task_list.add(new Tasks(name, date, key, lable, lableColor, "false"));
                                mTaskAdapter.notifyDataSetChanged();

                            }
                            if (data.child("completed").getValue().equals("true")) {

                                String date = data.child("date").getValue().toString();
                                String name = data.child("name").getValue().toString();
                                String key = data.getKey();
                                String lable = data.child("lable").getValue().toString();
                                String lableColor = data.child("lableColor").getValue().toString();

                                completed_task_list.add(new Tasks(name, date, key, lable, lableColor, "false"));
                                mCompletedTaskAdapter.notifyDataSetChanged();

                            }
                        }

                    }//TODO

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        completed_task_list.clear();
                        task_list.clear();
                    }
                }
        );

 /*       taskSwipeListView.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(final int position, SwipeMenu menu, int index) {
                switch (index) {

                    case 0:

                        Tasks t;
                        t = task_list.get(position);
                        String taskKey;
                        taskKey = t.getTaskKey();

                        mMessagesDatabaseReference.child(taskKey).child("completed").setValue("true");

                        completed_task_list.add(mTaskAdapter.getItem(position));
                        mCompletedTaskAdapter.notifyDataSetChanged();

                        task_list.remove(mTaskAdapter.getItem(position));
                        mTaskAdapter.notifyDataSetChanged();

                        break;

                    case 1:
                        Tasks ev;
                        ev = task_list.get(position);
                        String eventDeleteKey = ev.getTaskKey();
                        mTaskAdapter.remove(mTaskAdapter.getItem(position));
                        mTaskAdapter.notifyDataSetChanged();
                        mMessagesDatabaseReference.child(eventDeleteKey).removeValue();

                        break;
                }
                return false;
            }
        });


        completedSwipeListView.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(final int position, SwipeMenu menu, int index) {
                switch (index) {

                    case 0:
//                       Events e;
//                        e = completed_event_list.get(position);
//                        String eventKey;
//                        eventKey = e.getEventKey();
//
//                        completed_event_list.remove(mCompletedEventAdapter.getItem(position));
//                        mCompletedEventAdapter.notifyDataSetChanged();
//                        mMessagesDatabaseReference.child("toDoList").child(eventKey).child("completed").setValue("false");

                        Tasks t;
                        t = completed_task_list.get(position);
                        String taskKey;
                        taskKey = t.getTaskKey();

                        task_list.add(mCompletedTaskAdapter.getItem(position));
                        mTaskAdapter.notifyDataSetChanged();

                        completed_task_list.remove(mCompletedTaskAdapter.getItem(position));
                        mCompletedTaskAdapter.notifyDataSetChanged();

                        mMessagesDatabaseReference.child(taskKey).child("completed").setValue("false");
                        break;

                    case 1:
                        Tasks ev;
                        ev = completed_task_list.get(position);
                        String taskDeleteKey = ev.getTaskKey();
                        mCompletedTaskAdapter.remove(mCompletedTaskAdapter.getItem(position));
                        mCompletedTaskAdapter.notifyDataSetChanged();
                        mMessagesDatabaseReference.child(taskDeleteKey).removeValue();

                        break;
                }
                return false;
            }
        });
*/
//        mMessagesDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                for (DataSnapshot d : dataSnapshot.child("users").getChildren()) {
//
//                    String name = d.child("user_name").getValue().toString();
//                    String email = d.child("user_email").getValue().toString();
//                    String id = d.child("user_id").getValue().toString();
//                    String photo = d.child("user_photo").getValue().toString();
//                    Toast.makeText(TasksActivity.this,name + "   " + email +"       "+ id +"    "+photo, Toast.LENGTH_SHORT).show();
//                    selected_user_list.add(new Users(id, name, email, photo));
//                    mSelectedUserAdapter.notifyDataSetChanged();
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });
        /*
        mMessagesDatabaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                //for (DataSnapshot data : dataSnapshot.getChildren()) {

//
//                    String name = data.child("user_name").getValue().toString();
//                    String email = data.child("user_email").getValue().toString();
//                    String id = data.child("user_id").getValue().toString();
//                    String photo = data.child("user_photo").getValue().toString();
//
//                    selected_user_list.add(new Users(id, name, email, photo));
//
//                    mSelectedUserAdapter.notifyDataSetChanged();
//                    Toast.makeText(TasksActivity.this, dataSnapshot.child("users").getValue().toString(), Toast.LENGTH_SHORT).show();
                for (DataSnapshot d : dataSnapshot.child("users").getChildren()) {

                    String name = d.child("user_name").getValue().toString();
                    String email = d.child("user_email").getValue().toString();
                    String id = d.child("user_id").getValue().toString();
                    String photo = d.child("user_photo").getValue().toString();
                    Toast.makeText(TasksActivity.this,name + "   " + email +"       "+ id +"    "+photo, Toast.LENGTH_SHORT).show();
                    selected_user_list.add(new Users(id, name, email, photo));
                    mSelectedUserAdapter.notifyDataSetChanged();
                }

                //}
//                    String date = dataSnapshot.child("users").getValue().toString();
//                    String name = dataSnapshot.child("name").getValue().toString();
//                    String key = dataSnapshot.getKey();
//                    String lable = dataSnapshot.child("lable").getValue().toString();
//                    String lableColor = dataSnapshot.child("lableColor").getValue().toString();

                Tasks t = new Tasks();
                String taskKey = t.getTaskKey();
                //dataSnapshot.child(taskKey);


//                String name = dataSnapshot.child("users").child("user_name").getValue().toString();
//                String email = dataSnapshot.child("users").child("user_email").getValue().toString();
//                String id = dataSnapshot.child("users").child("user_id").getValue().toString();
//                String photo = dataSnapshot.child("users").child("user_photo").getValue().toString();
//
//                //   Toast.makeText(TasksActivity.this, data.child("users").getValue().toString(), Toast.LENGTH_SHORT).show();
//
//
////                   Toast.makeText(TasksActivity.this, dataSnapshot.child(taskKeyForUsers).child("users").getValue().toString(), Toast.LENGTH_SHORT).show();
//                    selected_user_list.add(new Users(id,name,email,photo));
//
//                    mSelectedUserAdapter.notifyDataSetChanged();
                //TODO

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });*/
    }


    private void FetchTaskListViews() {

        mMessagesDatabaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

               // for (DataSnapshot data : dataSnapshot.getChildren()) {
                    if (dataSnapshot.child("completed").getValue().equals("false")) {
                        String date = dataSnapshot.child("date").getValue().toString();
                        String name = dataSnapshot.child("name").getValue().toString();
                        String key = dataSnapshot.getKey();
                        String lable = dataSnapshot.child("lable").getValue().toString();
                        String lableColor = dataSnapshot.child("lableColor").getValue().toString();

                        task_list.add(new Tasks(name, date, key, lable, lableColor, "false"));
                        mTaskAdapter.notifyDataSetChanged();

                    }
                    if (dataSnapshot.child("completed").getValue().equals("true")) {

                        String date = dataSnapshot.child("date").getValue().toString();
                        String name = dataSnapshot.child("name").getValue().toString();
                        String key = dataSnapshot.getKey();
                        String lable = dataSnapshot.child("lable").getValue().toString();
                        String lableColor = dataSnapshot
                                .child("lableColor").getValue().toString();

                        completed_task_list.add(new Tasks(name, date, key, lable, lableColor, "false"));
                        mCompletedTaskAdapter.notifyDataSetChanged();

                    }
                //}




//                //for (DataSnapshot data : dataSnapshot.getChildren()) {
//
//                if (dataSnapshot.child("completed").getValue().equals("false")) {
//                    String date = dataSnapshot.child("date").getValue().toString();
//                    String name = dataSnapshot.child("name").getValue().toString();
//                    String key = dataSnapshot.getKey();
//                    String lable = dataSnapshot.child("lable").getValue().toString();
//                    String lableColor = dataSnapshot.child("lableColor").getValue().toString();
//                    //Toast.makeText(TasksActivity.this, data.toString(), Toast.LENGTH_SHORT).show();
//                    task_list.add(new Tasks(name, date, key, lable, lableColor, "false"));
//
//                    // Toast.makeText(TasksActivity.this ,dataSnapshot.getValue().toString()
//                    //child(eventKey).child("tasksList").toString()
//                    //       , Toast.LENGTH_SHORT).show();
//                    //  }
//                    mTaskAdapter.notifyDataSetChanged();
//                    //TODO
//                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {


               // for (DataSnapshot data : dataSnapshot.getChildren()) {
                    if (dataSnapshot.child("completed").getValue().equals("false")) {
                        String date = dataSnapshot.child("date").getValue().toString();
                        String name = dataSnapshot.child("name").getValue().toString();
                        String key = dataSnapshot.getKey();
                        String lable = dataSnapshot.child("lable").getValue().toString();
                        String lableColor = dataSnapshot.child("lableColor").getValue().toString();

                        task_list.add(new Tasks(name, date, key, lable, lableColor, "false"));
                        mTaskAdapter.notifyDataSetChanged();

                    }
                    if (dataSnapshot.child("completed").getValue().equals("true")) {

                        String date = dataSnapshot.child("date").getValue().toString();
                        String name = dataSnapshot.child("name").getValue().toString();
                        String key = dataSnapshot.getKey();
                        String lable = dataSnapshot.child("lable").getValue().toString();
                        String lableColor = dataSnapshot
                                .child("lableColor").getValue().toString();

                        completed_task_list.add(new Tasks(name, date, key, lable, lableColor, "false"));
                        mCompletedTaskAdapter.notifyDataSetChanged();

                    }
               // }

//                if (dataSnapshot.child("completed").getValue().equals("false")) {
//                    task_list.clear();
//                    // for (DataSnapshot data : dataSnapshot.getChildren()) {
//
//                    //Events value = data.getValue(Events.class);
//                    //event_list.add(value);
//
//                    String date = dataSnapshot.child("date").getValue().toString();
//                    String name = dataSnapshot.child("name").getValue().toString();
//                    String key = dataSnapshot.getKey();
//                    String lable = dataSnapshot.child("lable").getValue().toString();
//                    String lableColor = dataSnapshot.child("lableColor").getValue().toString();
//                    // Events events = dataSnapshot.child("todos").getValue(Events.class);
//                    task_list.add(new Tasks(name, date, key, lable, lableColor, "false"));
//                    //    event_list.add(new Events(ds.child("name").getValue().toString(),ds.child("date").getValue().toString()));
//                }
//                mTaskAdapter.notifyDataSetChanged();

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }


    private void FetchCompletedListViews() {

        mMessagesDatabaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                if (dataSnapshot.child("completed").getValue().equals("true")) {
                    String date = dataSnapshot.child("date").getValue().toString();
                    String name = dataSnapshot.child("name").getValue().toString();
                    String key = dataSnapshot.getKey();
                    String lable = dataSnapshot.child("lable").getValue().toString();
                    String lableColor = dataSnapshot.child("lableColor").getValue().toString();
                    //Toast.makeText(TasksActivity.this, data.toString(), Toast.LENGTH_SHORT).show();
                    completed_task_list.add(new Tasks(name, date, key, lable, lableColor, "false"));

                    // Toast.makeText(TasksActivity.this ,dataSnapshot.getValue().toString()
                    //child(eventKey).child("tasksList").toString()
                    //       , Toast.LENGTH_SHORT).show();
                    //  }
                    mCompletedTaskAdapter.notifyDataSetChanged();
                    //TODO
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                if (dataSnapshot.child("completed").getValue().equals("true")) {
                    if (dataSnapshot.child("completed").getValue().equals("false")) {
                        String date = dataSnapshot.child("date").getValue().toString();
                        String name = dataSnapshot.child("name").getValue().toString();
                        String key = dataSnapshot.getKey();
                        String lable = dataSnapshot.child("lable").getValue().toString();
                        String lableColor = dataSnapshot.child("lableColor").getValue().toString();
                        //Toast.makeText(TasksActivity.this, data.toString(), Toast.LENGTH_SHORT).show();
                        completed_task_list.add(new Tasks(name, date, key, lable, lableColor, "false"));

                        // Toast.makeText(TasksActivity.this ,dataSnapshot.getValue().toString()
                        //child(eventKey).child("tasksList").toString()
                        //       , Toast.LENGTH_SHORT).show();
                        //  }
                        mCompletedTaskAdapter.notifyDataSetChanged();
                        //TODO
                    }
                }
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }


    private void FetchLegendListViews() {

        mMessagesDatabaseReferenceToLegend.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                //for (DataSnapshot data : dataSnapshot.getChildren()) {

                String name = dataSnapshot.child("name").getValue().toString();
                String color = dataSnapshot.child("color").getValue().toString();
                String key = dataSnapshot.getKey();
                //Toast.makeText(TasksActivity.this, data.toString(), Toast.LENGTH_SHORT).show();
                legend_list.add(new Legend(key, name, color));


                // Toast.makeText(TasksActivity.this ,dataSnapshot.getValue().toString()
                //child(eventKey).child("tasksList").toString()
                //       , Toast.LENGTH_SHORT).show();
                //}
                mLegendAdapter.notifyDataSetChanged();

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                legend_list.clear();
                for (DataSnapshot data : dataSnapshot.getChildren()) {

                    //Events value = data.getValue(Events.class);
                    //event_list.add(value);

                    String name = dataSnapshot.child("name").getValue().toString();
                    String key = dataSnapshot.getKey();
                    String color = dataSnapshot.child("color").getValue().toString();
                    // Events events = dataSnapshot.child("todos").getValue(Events.class);
                    legend_list.add(new Legend(key, name, color));
                    //    event_list.add(new Events(ds.child("name").getValue().toString(),ds.child("date").getValue().toString()));
                }
                mLegendAdapter.notifyDataSetChanged();

                //TODO
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                legend_list.remove(dataSnapshot.getKey());
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

//
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.navigation_drawer, menu);
//        return true;
//    }

//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        int id = item.getItemId();
//        if (id == R.id.action_settings) {
//            //TODO
//            return true;
//        }
//        return super.onOptionsItemSelected(item);
//    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_signout) {
            // TODO
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    SwipeMenuCreator creator = new SwipeMenuCreator() {

        @Override
        public void create(SwipeMenu menu) {
            // create "open" item
            SwipeMenuItem openItem = new SwipeMenuItem(
                    getApplicationContext());
            // set item background
            openItem.setBackground(R.drawable.select_option_swipe);
            openItem.setIcon(R.drawable.ic_check_black_24dp);
            // set item width
            openItem.setWidth(250);
            // set item title fontsize
            openItem.setTitleSize(18);
            // set item title font color
            openItem.setTitleColor(Color.WHITE);
            // add to menu
            menu.addMenuItem(openItem);


            // create "delete" item
            SwipeMenuItem deleteItem = new SwipeMenuItem(
                    getApplicationContext());
            // set item background
            deleteItem.setBackground(R.drawable.cross_option_swipe);
            // set item width
            deleteItem.setWidth(250);
            // set a icon
            deleteItem.setIcon(R.drawable.ic_clear_black_24dp);
            // add to menu
            menu.addMenuItem(deleteItem);
        }
    };

    SwipeMenuCreator creatorCompleted = new SwipeMenuCreator() {

        @Override
        public void create(SwipeMenu menu) {
            // create "open" item
            SwipeMenuItem openItem = new SwipeMenuItem(
                    getApplicationContext());
            // set item background
            openItem.setBackground(R.drawable.select_option_swipe);
            openItem.setIcon(R.drawable.ic_replay_black_24dp);
            // set item width
            openItem.setWidth(250);
            // set item title fontsize
            openItem.setTitleSize(18);
            // set item title font color
            openItem.setTitleColor(Color.WHITE);
            // add to menu
            menu.addMenuItem(openItem);


            // create "delete" item
            SwipeMenuItem deleteItem = new SwipeMenuItem(
                    getApplicationContext());
            // set item background
            deleteItem.setBackground(R.drawable.cross_option_swipe);
            // set item width
            deleteItem.setWidth(250);
            // set a icon
            deleteItem.setIcon(R.drawable.ic_clear_black_24dp);
            // add to menu
            menu.addMenuItem(deleteItem);
        }
    };


    public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskAdapterViewHolder> {

        List<Tasks> taskList;
        Tasks tasks;

        public TaskAdapter(List<Tasks> taskList) {
            this.taskList = taskList;
            notifyDataSetChanged();
        }


        @NonNull
        @Override
        public TaskAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View mItemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.task_list_item, parent, false);
            return new TaskAdapterViewHolder(mItemView);
        }

        @Override
        public void onBindViewHolder(final @NonNull TaskAdapterViewHolder holder, final int position) {
            tasks = taskList.get(position);

            holder.bindViews(position);
            holder.taskNameTextView.setText(tasks.getTaskName());


            mMessagesDatabaseReference.child(tasks.getTaskKey()).child("users").addListenerForSingleValueEvent(
                    new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for (DataSnapshot data : dataSnapshot.getChildren()) {
                                holder.numberOfUsers.setText("+ " + (dataSnapshot.getChildrenCount() - 1));
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            selected_user_list.clear();
                        }
                    });


            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    final Tasks t = taskList.get(position);
                    final String taskKey = t.getTaskKey();

                    Intent intentToTaskDetailsActivity = new Intent(TasksActivity.this, TaskDetailsActivity.class);
                    intentToTaskDetailsActivity.putExtra("eventKey", eventKey);
                    intentToTaskDetailsActivity.putExtra("taskKey", taskKey);
                    startActivity(intentToTaskDetailsActivity);
                }
            });


            switch (tasks.getTaskLabelColor()) {
                case "Cyan":
                    holder.imageView.setImageResource(R.drawable.ic_bookmark_cyan_24dp);
                    break;
                case "Blue":
                    holder.imageView.setImageResource(R.drawable.ic_bookmark_blue_24dp);
                    break;
                case "Green":
                    holder.imageView.setImageResource(R.drawable.ic_bookmark_green_24dp);
                    break;
                case "Yellow":
                    holder.imageView.setImageResource(R.drawable.ic_bookmark_yellow_24dp);
                    break;
                case "Magenta":
                    holder.imageView.setImageResource(R.drawable.ic_bookmark_magenta_24dp);
                    break;
                case "Purple":
                    holder.imageView.setImageResource(R.drawable.ic_bookmark_purple_24dp);
                    break;
                case "Bush":
                    holder.imageView.setImageResource(R.drawable.ic_bookmark_bush_24dp);
                    break;
                case "Red":
                    holder.imageView.setImageResource(R.drawable.ic_bookmark_red_24dp);
                    break;
            }

        }

        @Override
        public int getItemCount() {
            return taskList.size();
        }

        class TaskAdapterViewHolder extends RecyclerView.ViewHolder {

            TextView taskNameTextView;
            ImageView imageView;
            TextView numberOfUsers;

            RecyclerView mSecondaryRecyclerView;

            public TaskAdapterViewHolder(View convertView) {
                super(convertView);

                taskNameTextView = convertView.findViewById(R.id.task_name);
                imageView = convertView.findViewById(R.id.image_bookmark);
                numberOfUsers = convertView.findViewById(R.id.task_number_of_users);

                mSecondaryRecyclerView = convertView.findViewById(R.id.secondary_recycler_view);
            }

            public void bindViews(int position) {

                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(TasksActivity.this,
                        LinearLayoutManager.HORIZONTAL,
                        false
                );
                mSecondaryRecyclerView.setVisibility(View.VISIBLE);
                mSecondaryRecyclerView.setLayoutManager(linearLayoutManager);
                String key = tasks.getTaskKey();
                mSecondaryRecyclerView.setAdapter(getSecondaryAdapter(position, key));

            }


        }
    }

    class SecondaryViewHolder extends RecyclerView.ViewHolder {

        private ImageView selectedUsersImageInside;


        public SecondaryViewHolder(View view) {
            super(view);
            selectedUsersImageInside = itemView.findViewById(R.id.selected_user_image_inside);

        }

        public void bindView(String name, String photo) {
            Picasso.with(TasksActivity.this).load(photo).transform(new TasksActivity.CircleTransform()).into(selectedUsersImageInside);

        }

    }

    class SecondaryAdapter extends RecyclerView.Adapter<SecondaryViewHolder> {

        ArrayList<Users> userList;


        public SecondaryAdapter(ArrayList<Users> users) {
            userList = users;
        }

        @Override
        public SecondaryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(TasksActivity.this);
            View view = inflater.inflate(R.layout.secondary_recycler_view_item, parent, false);

            return new SecondaryViewHolder(view);
        }

        @Override
        public void onBindViewHolder(SecondaryViewHolder holder, int position) {
            Users user = userList.get(position);
            String name = user.getUserName();
            String photo = user.getUserPhotoUrl();
            holder.bindView(name, photo);
        }

        @Override
        public int getItemCount() {
            return userList.size();
        }

    }

    SecondaryAdapter getSecondaryAdapter(int position, String task_key) {

        //final TextView numberOfUsers;

        final SecondaryAdapter adapter = new SecondaryAdapter(selected_user_list);
        mMessagesDatabaseReference.child(task_key).child("users").addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        selected_user_list.clear();

                        //numberOfUsers =  view.findViewById(R.id.task_number_of_users);
                        for (DataSnapshot data : dataSnapshot.getChildren()) {
                            //Toast.makeText(TasksActivity.this, "Length is " + dataSnapshot.getChildrenCount(), Toast.LENGTH_SHORT).show();
                            //numberOfUsers.setText(dataSnapshot.getChildrenCount().toString());
                            selected_user_list.clear();

                            String userName = data.child("user_name").getValue().toString();
                            String userPhoto = data.child("user_photo").getValue().toString();
                            String userEmail = data.child("user_email").getValue().toString();
                            String userId = data.child("user_id").getValue().toString();

                            selected_user_list.add(new Users(userId, userName, userEmail, userPhoto));
                            adapter.notifyDataSetChanged();


                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        selected_user_list.clear();
                    }
                }
        );
        return adapter;

    }


    public class CircleTransform implements Transformation {
        @Override
        public Bitmap transform(Bitmap source) {
            int size = Math.min(source.getWidth(), source.getHeight());

            int x = (source.getWidth() - size) / 2;
            int y = (source.getHeight() - size) / 2;

            Bitmap squaredBitmap = Bitmap.createBitmap(source, x, y, size, size);
            if (squaredBitmap != source) {
                source.recycle();
            }

            Bitmap bitmap = Bitmap.createBitmap(size, size, source.getConfig());

            Canvas canvas = new Canvas(bitmap);
            Paint paint = new Paint();
            BitmapShader shader = new BitmapShader(squaredBitmap,
                    BitmapShader.TileMode.CLAMP, BitmapShader.TileMode.CLAMP);
            paint.setShader(shader);
            paint.setAntiAlias(true);

            float r = size / 2f;
            canvas.drawCircle(r, r, r, paint);

            squaredBitmap.recycle();
            return bitmap;
        }

        @Override
        public String key() {
            return "circle";
        }
    }

    public class LegendAdapter extends ArrayAdapter<Legend> {

        Dialog userDialog;

        public LegendAdapter(Context context, int resource, List<Legend> objects, String eventKey) {
            super(context, resource, objects);
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            if (convertView == null) {


                convertView = ((Activity) getContext()).getLayoutInflater().inflate(R.layout.legend_list_item, parent, false);
            }

            TextView legendNameTextView = convertView.findViewById(R.id.legend_name);
            LinearLayout legendTextView = convertView.findViewById(R.id.legend_list_item_details);

            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final Legend legend = getItem(position);
                    //Toast.makeText(getContext(), legend.getLegendKey(), Toast.LENGTH_SHORT).show();

                    legendDialog = new Dialog(TasksActivity.this);
                    legendDialog.setContentView(R.layout.legend_dialog);
                    legendDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    legendDialog.show();

                    legendDelete = legendDialog.findViewById(R.id.legend_delete);
                    legendDelete.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View legend_view) {
                            mMessagesDatabaseReferenceToLegend.child(legend.getLegendKey()).removeValue();
                            legend_list.remove(legend);
                            notifyDataSetChanged();
                            legendDialog.cancel();
                            Toast.makeText(TasksActivity.this,"Legend deleted successfully", Toast.LENGTH_SHORT).show();
                        }
                    });

                    legendClose = legendDialog.findViewById(R.id.legend_close);
                    legendClose.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            legendDialog.cancel();
                        }
                    });


                }
            });

            Legend legend = getItem(position);
            legendNameTextView.setText(legend.getLegendName());

            final Drawable mDrawable = ContextCompat.getDrawable(getContext(), R.drawable.legend_inputs);
            switch (legend.getLegendColor()) {
                case "Red":
                    mDrawable.setTint(Color.rgb(255, 96, 127));
                    break;
                case "Yellow":
                    mDrawable.setTint(Color.rgb(255, 187, 0));
                    break;
                case "Green":
                    mDrawable.setTint(Color.rgb(3, 213, 163));
                    break;
                case "Blue":
                    mDrawable.setTint(Color.rgb(0, 154, 255));
                    break;
                case "Purple":
                    mDrawable.setTint(Color.rgb(218, 92, 240));
                    break;
                case "Cyan":
                    mDrawable.setTint(Color.rgb(103, 225, 247));
                    break;
                case "Magenta":
                    mDrawable.setTint(Color.rgb(109, 103, 247));
                    break;
                case "Bush":
                    mDrawable.setTint(Color.rgb(0, 165, 108));
                    break;
            }

            legendTextView.setBackground(mDrawable);
            return convertView;
        }
    }


}
