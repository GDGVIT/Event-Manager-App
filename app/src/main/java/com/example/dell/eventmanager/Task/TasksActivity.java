package com.example.dell.eventmanager.Task;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.example.dell.eventmanager.Event.Events;
import com.example.dell.eventmanager.Legend.Legend;
import com.example.dell.eventmanager.Legend.LegendAdapter;
import com.example.dell.eventmanager.R;
import com.example.dell.eventmanager.SwipeController;
import com.example.dell.eventmanager.SwipeControllerActions;
import com.example.dell.eventmanager.User.SelectedUserAdapter;
import com.example.dell.eventmanager.User.Users;
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

    // private ListView mTaskListView;
    private TaskAdapter mTaskAdapter;

    //private ListView mCompletedTaskListView;
    private TaskAdapter mCompletedTaskAdapter;

    private RecyclerView taskRecyclerView;
    private RecyclerView.LayoutManager taskLayoutManager;

    private RecyclerView completedTaskRecyclerView;
    private RecyclerView.LayoutManager completedTaskLayoutManager;

    private GridView mLegendListView;
    private LegendAdapter mLegendAdapter;

    private Button mButtonAddTask;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mMessagesDatabaseReference;
    private DatabaseReference mMessagesDatabaseReferenceToLegend;
    private ChildEventListener mChildEventListener;


    private ArrayList<Tasks> task_list = new ArrayList<Tasks>();
    private ArrayList<Tasks> completed_task_list = new ArrayList<Tasks>();
    private ArrayList<Legend> legend_list = new ArrayList<Legend>();


    private SelectedUserAdapter mSelectedUserAdapter;
    private ArrayList<Users> selected_user_list = new ArrayList<Users>();
    GridView mSelectedUserGridView;

    Intent intentFromEventsActivity;
    String eventKey;

    ImageView imageBookmark;

    SwipeController swipeController;
    SwipeController completedswipeController;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_task_activity);

        // imageBookmark = (ImageView) findViewById(R.id.image_bookmark);

        final Drawable bookmarkDrawable = ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_bookmark_red_24dp);
        // bookmarkDrawable.setTint(Color.rgb(255,228,196));


        // ivVectorImage.setColorFilter(getResources().getColor(R.color.colorLime));

        //DrawableCompat.setTint(imageBookmark.getDrawable(), ContextCompat.getColor(TasksActivity.this, R.color.colorLime));
        // imageBookmark.setImageTintList(  Color.BLUE);

        //   Toast.makeText(TasksActivity.this,,Toast.LENGTH_SHORT).show();
        //DrawableCompat.setTint(imageBookmark.getDrawable(), ContextCompat.getColor(this, R.color.colorLime));


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Tasks");
        setSupportActionBar(toolbar);


        if (Build.VERSION.SDK_INT >= 21) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.parseColor("#FF6961"));
        }
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

/*
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        toggle.getDrawerArrowDrawable().setColor(getResources().getColor(R.color.colorBlack));

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);*/

        intentFromEventsActivity = getIntent();
        eventKey = intentFromEventsActivity.getStringExtra("eventKey");

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mMessagesDatabaseReference = mFirebaseDatabase.getReference().child("toDoList").child(eventKey).child("tasksList");
        mMessagesDatabaseReferenceToLegend = mFirebaseDatabase.getReference().child("toDoList").child(eventKey).child("legend");


//        mTaskListView = (ListView) findViewById(R.id.taskListView);
//        mTaskAdapter = new TaskAdapter(this, R.layout.task_list_item, task_list);
//        mTaskListView.setAdapter(mTaskAdapter);
//
//
//        mCompletedTaskListView = (ListView) findViewById(R.id.completedTasksListView);
//        mCompletedTaskAdapter = new TaskAdapter(this, R.layout.task_list_item, completed_task_list);
//        mCompletedTaskListView.setAdapter(mCompletedTaskAdapter);

        taskRecyclerView = findViewById(R.id.taskListView);
        taskLayoutManager = new LinearLayoutManager(this);
        mTaskAdapter = new TaskAdapter(task_list);
        taskRecyclerView.setLayoutManager(taskLayoutManager);
        taskRecyclerView.setAdapter(mTaskAdapter);

        completedTaskRecyclerView = findViewById(R.id.completedTasksListView);
        completedTaskLayoutManager = new LinearLayoutManager(this);
        mCompletedTaskAdapter = new TaskAdapter(completed_task_list);
        completedTaskRecyclerView.setLayoutManager(completedTaskLayoutManager);
        completedTaskRecyclerView.setAdapter(mCompletedTaskAdapter);





        swipeController = new SwipeController(new SwipeControllerActions() {
            @Override
            public void onRightClicked(int position) {
                Tasks ev;
                ev = task_list.get(position);
                String eventDeleteKey = ev.getTaskKey();
                mTaskAdapter.taskList.remove(mTaskAdapter.taskList.get(position));
                mTaskAdapter.notifyDataSetChanged();
                mMessagesDatabaseReference.child(eventDeleteKey).removeValue();
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


        completedswipeController = new SwipeController(new SwipeControllerActions() {
            @Override
            public void onRightClicked(int position) {
                Tasks ev;
                ev = completed_task_list.get(position);
                String taskDeleteKey = ev.getTaskKey();
                mCompletedTaskAdapter.taskList.remove(mCompletedTaskAdapter.taskList.get(position));
                mCompletedTaskAdapter.notifyDataSetChanged();
                mMessagesDatabaseReference.child(taskDeleteKey).removeValue();
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













//        taskSwipeListView = findViewById(R.id.taskListView);
        //  completedSwipeListView = findViewById(R.id.completedTasksListView);

//        taskSwipeListView.setMenuCreator(creator);
        //      completedSwipeListView.setMenuCreator(creatorCompleted);

        mLegendListView = (GridView) findViewById(R.id.labelListView);
        mLegendAdapter = new LegendAdapter(this, R.layout.legend_list_item, legend_list);
        mLegendListView.setAdapter(mLegendAdapter);


//        mSelectedUserGridView = findViewById(R.id.grid_view_selected_user_image);
//        mSelectedUserAdapter = new SelectedUserAdapter(this, R.layout.selected_user_photo_list_item, selected_user_list);
//        mSelectedUserGridView.setAdapter(mSelectedUserAdapter);


        mButtonAddTask = findViewById(R.id.btn_add_task);
        mButtonAddTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO
                Intent intentToAddTaskActivity = new Intent(TasksActivity.this, AddTaskActivity.class);
                intentToAddTaskActivity.putExtra("eventKey", eventKey);
                startActivity(intentToAddTaskActivity);
                // String d =DataSnapshot.child("toDoList").child("tasksList").child("date").toString();
                //Toast.makeText(TasksActivity.this,""+d,Toast.LENGTH_SHORT).show();
            }
        });

      /*  mButtonAddLegend = findViewById(R.id.btn_add_legend);
        mButtonAddLegend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                String legendKey = mMessagesDatabaseReferenceToLegend.push().getKey();
                Toast.makeText(TasksActivity.this,""+ legendKey,Toast.LENGTH_SHORT).show();
                final String addLegendName = "Legend 1";

                Legend l= new Legend();
                l.setLegendName(addLegendName);
                l.setLegendKey(legendKey);


                Map<String, Object> childUpdates = new HashMap<>();
                mMessagesDatabaseReferenceToLegend.push().setValue(childUpdates.put(legendKey, l.toLegendsFirebaseObject()));

                //childUpdates.put(key, todo.toEventsFirebaseObject());
                mMessagesDatabaseReferenceToLegend.updateChildren(childUpdates, new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                        if (databaseError == null) {
                            Toast.makeText(com.example.dell.eventmanager.Task.TasksActivity.this,"New Legend Added",Toast.LENGTH_SHORT).show();

                        }
                    }
                });



            }
        });
*/


      /*  mMessagesDatabaseReferenceToLegend.addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        Log.w("TodoApp", "getUser:onCancelled " + dataSnapshot.toString());
                        Log.w("TodoApp", "count = " + String.valueOf(dataSnapshot.getChildrenCount()) + " values " + dataSnapshot.getKey());
                        for (DataSnapshot data : dataSnapshot.getChildren()) {
                            String name = data.child("name").getValue().toString();
                            legend_list.add(new Legend(name));

                        }

                        mLegendAdapter.notifyDataSetChanged();

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                }
        );
*/
        // FetchTaskListViews();
        //FetchCompletedListViews();
        FetchLegendListViews();
        //FetchCompletedListViews();


      /*  mMessagesDatabaseReference.child("toDoList").child(eventKey).child("tasksList").addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        Log.w("TodoApp", "getUser:onCancelled " + dataSnapshot.toString());
                        Log.w("TodoApp", "count = " + String.valueOf(dataSnapshot.getChildrenCount()) + " values " + dataSnapshot.getKey());
                        for (DataSnapshot data : dataSnapshot.getChildren()) {
                            String date = data.child("date").getValue().toString();
                                    //data.child("tasksList").child("date").getValue().toString();
                            String name = data.child("name").getValue().toString();
                                    //data.child("tasksList").child("name").getValue().toString();
                           // String key = data.child("name").getValue().toString();
                        String key = data.child("name").getValue().toString();
                        Toast.makeText(TasksActivity.this,key,Toast.LENGTH_SHORT).show();

                        task_list.add(new Tasks(name, date , data.getKey() ));
                         //   Toast.makeText(TasksActivity.this,dataSnapshot.child(eventKey).child("tasksList").getValue().toString(),Toast.LENGTH_SHORT).show();
                        }

                        mTaskAdapter.notifyDataSetChanged();

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                }
        );*/


        mMessagesDatabaseReference.addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
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

                     /*   for (DataSnapshot data : dataSnapshot.getChildren()) {
                            String date = data.child("date").getValue().toString();
                            String name = data.child("name").getValue().toString();
                            event_list.add(new Events(name, date));
                        }

                        mEventAdapter.notifyDataSetChanged();*/

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                }
        );

//       mTaskListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
//
//                final Tasks t = task_list.get(position);
//                final String taskKey = t.getTaskKey();
//
//
//                Intent intentToTaskDetailsActivity = new Intent(TasksActivity.this, TaskDetailsActivity.class);
//                intentToTaskDetailsActivity.putExtra("eventKey", eventKey);
//                intentToTaskDetailsActivity.putExtra("taskKey", taskKey);
//                //intentToTaskActivity.putExtra("taskKey", taskKey);
//                startActivity(intentToTaskDetailsActivity);
//            }
//        });/


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
                //for (DataSnapshot data : dataSnapshot.getChildren()) {

                if (dataSnapshot.child("completed").getValue().equals("false")) {
                    String date = dataSnapshot.child("date").getValue().toString();
                    String name = dataSnapshot.child("name").getValue().toString();
                    String key = dataSnapshot.getKey();
                    String lable = dataSnapshot.child("lable").getValue().toString();
                    String lableColor = dataSnapshot.child("lableColor").getValue().toString();
                    //Toast.makeText(TasksActivity.this, data.toString(), Toast.LENGTH_SHORT).show();
                    task_list.add(new Tasks(name, date, key, lable, lableColor, "false"));

                    // Toast.makeText(TasksActivity.this ,dataSnapshot.getValue().toString()
                    //child(eventKey).child("tasksList").toString()
                    //       , Toast.LENGTH_SHORT).show();
                    //  }
                    mTaskAdapter.notifyDataSetChanged();
                    //TODO
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                if (dataSnapshot.child("completed").getValue().equals("false")) {
                    task_list.clear();
                    // for (DataSnapshot data : dataSnapshot.getChildren()) {

                    //Events value = data.getValue(Events.class);
                    //event_list.add(value);

                    String date = dataSnapshot.child("date").getValue().toString();
                    String name = dataSnapshot.child("name").getValue().toString();
                    String key = dataSnapshot.getKey();
                    String lable = dataSnapshot.child("lable").getValue().toString();
                    String lableColor = dataSnapshot.child("lableColor").getValue().toString();
                    // Events events = dataSnapshot.child("todos").getValue(Events.class);
                    task_list.add(new Tasks(name, date, key, lable, lableColor, "false"));
                    //    event_list.add(new Events(ds.child("name").getValue().toString(),ds.child("date").getValue().toString()));
                }
                mTaskAdapter.notifyDataSetChanged();

                //TODO
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

                // String key = data.getKey();
                //Toast.makeText(TasksActivity.this, data.toString(), Toast.LENGTH_SHORT).show();
                legend_list.add(new Legend(name, color));


                // Toast.makeText(TasksActivity.this ,dataSnapshot.getValue().toString()
                //child(eventKey).child("tasksList").toString()
                //       , Toast.LENGTH_SHORT).show();
                //  }
                mLegendAdapter.notifyDataSetChanged();

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                legend_list.clear();
                for (DataSnapshot data : dataSnapshot.getChildren()) {

                    //Events value = data.getValue(Events.class);
                    //event_list.add(value);

                    String name = dataSnapshot.child("name").getValue().toString();
                    String key = data.getKey();
                    String color = dataSnapshot.child("color").getValue().toString();
                    // Events events = dataSnapshot.child("todos").getValue(Events.class);
                    legend_list.add(new Legend(name, color));
                    //    event_list.add(new Events(ds.child("name").getValue().toString(),ds.child("date").getValue().toString()));
                }
                mLegendAdapter.notifyDataSetChanged();

                //TODO
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


    private void FetchSelectedUsersListView() {

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
                    Toast.makeText(TasksActivity.this, name + "   " + email + "       " + id + "    " + photo, Toast.LENGTH_SHORT).show();
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
        });
    }

    @Override
    public void onBackPressed() {
        /*DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }*/
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.navigation_drawer, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            //TODO
            return true;
        }
        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

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

        public TaskAdapter(List<Tasks> objects) {
            taskList = objects;
        }

        @NonNull
        @Override
        public TaskAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View mItemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.task_list_item, parent, false);
            return new TaskAdapterViewHolder(mItemView);
        }

        @Override
        public void onBindViewHolder(@NonNull TaskAdapterViewHolder holder, final int position) {
            tasks = taskList.get(position);

            holder.bindViews(position);
            holder.taskNameTextView.setText(tasks.getTaskName());
            holder.taskDateTextView.setText(tasks.getTaskDate());
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    final Tasks t = taskList.get(position);
                    final String taskKey = t.getTaskKey();

                    Intent intentToTaskDetailsActivity = new Intent(TasksActivity.this, TaskDetailsActivity.class);
                    intentToTaskDetailsActivity.putExtra("eventKey", eventKey);
                    intentToTaskDetailsActivity.putExtra("taskKey", taskKey);
                    //intentToTaskActivity.putExtra("taskKey", taskKey);
                    startActivity(intentToTaskDetailsActivity);
                }
            });


            switch (tasks.getTaskLableColor()) {
                case "Bisque":
                    holder.imageView.setImageResource(R.drawable.ic_bookmark_bisque_24dp);
                    break;
                case "DeepSkyBlue":

                    holder.imageView.setImageResource(R.drawable.ic_bookmark_blue_24dp);
                    break;
                case "Lime":

                    holder.imageView.setImageResource(R.drawable.ic_bookmark_lime_24dp);
                    break;
                case "Yellow":

                    holder.imageView.setImageResource(R.drawable.ic_bookmark_yellow_24dp);
                    break;
                case "Coral":

                    holder.imageView.setImageResource(R.drawable.ic_bookmark_coral_24dp);
                    break;
                case "Purple":

                    holder.imageView.setImageResource(R.drawable.ic_bookmark_purple_24dp);
                    break;
                case "Pink":

                    holder.imageView.setImageResource(R.drawable.ic_bookmark_pink_24dp);
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
            TextView taskDateTextView;
            ImageView imageView;

            RecyclerView mSecondaryRecyclerView;

            public TaskAdapterViewHolder(View convertView) {
                super(convertView);

                taskNameTextView = convertView.findViewById(R.id.task_name);
                taskDateTextView = convertView.findViewById(R.id.task_date);
                imageView = convertView.findViewById(R.id.image_bookmark);

                mSecondaryRecyclerView = convertView.findViewById(R.id.secondary_recycler_view);
            }

            public void bindViews(int position) {

                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(TasksActivity.this,
                        LinearLayoutManager.HORIZONTAL,
                        false
                );

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
            Picasso.with(getApplicationContext()).load(photo).transform(new TasksActivity.CircleTransform()).into(selectedUsersImageInside);

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


            final SecondaryAdapter adapter = new SecondaryAdapter(selected_user_list);
            mMessagesDatabaseReference.child(task_key).child("users").addListenerForSingleValueEvent(
                    new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            for (DataSnapshot data : dataSnapshot.getChildren()) {
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
                        }
                    }
            );

        Toast.makeText(TasksActivity.this, "Length is " + selected_user_list.size(), Toast.LENGTH_SHORT).show();
            return new SecondaryAdapter(selected_user_list);

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

}
