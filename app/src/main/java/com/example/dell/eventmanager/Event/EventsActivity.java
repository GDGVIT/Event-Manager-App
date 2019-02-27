package com.example.dell.eventmanager.Event;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.example.dell.eventmanager.MainActivity;
import com.example.dell.eventmanager.R;
import com.example.dell.eventmanager.SwipeController;
import com.example.dell.eventmanager.SwipeControllerActions;
import com.example.dell.eventmanager.Task.TasksActivity;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
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

import static android.support.v7.widget.helper.ItemTouchHelper.ACTION_STATE_SWIPE;
import static android.support.v7.widget.helper.ItemTouchHelper.LEFT;
import static android.support.v7.widget.helper.ItemTouchHelper.RIGHT;

//import com.daimajia.swipe.SwipeLayout;

public class EventsActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "MainActivity";
    public static final String ANONYMOUS = "anonymous";
    private static final int RC_SIGN_IN = 1;

    private RecyclerView mEventListView;
    private RecyclerView mCompletedEventListView;

    private EventAdapter mEventAdapter;
    private EventAdapter mCompletedEventAdapter;

    private Button mButtonAddEvent;

    private String mGoogleUserName;
    private String mGoogleUserMail;
    private String mGoogleUserPhoto;
    Intent intentFromMainActivity;
    private TextView mNavUserName;
    private TextView mNavUserMail;
    private ImageView mNavUserImage;


    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mMessagesDatabaseReference;
    private ChildEventListener mChildEventListener;
    private ProgressBar progressBar;

    private ArrayList<Events> event_list = new ArrayList<Events>();
    private ArrayList<Events> completed_event_list = new ArrayList<Events>();

    SwipeMenuListView eventSwipeListView;
    SwipeMenuListView completedSwipeListView;

    SwipeController swipeController = null;
    SwipeController completedswipeController = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation_drawer);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_ma);
        toolbar.setTitle("Event Manager");
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        toggle.getDrawerArrowDrawable().setColor(getResources().getColor(R.color.colorBlack));

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        intentFromMainActivity = getIntent();
        mGoogleUserName = intentFromMainActivity.getStringExtra("userName");
        mGoogleUserMail = intentFromMainActivity.getStringExtra("userMail");
        mGoogleUserPhoto = intentFromMainActivity.getStringExtra("userImage");

        mNavUserName = (TextView) navigationView.getHeaderView(0).findViewById(R.id.name_google_user);
        mNavUserName.setText(mGoogleUserName);
        mNavUserMail = (TextView) navigationView.getHeaderView(0).findViewById(R.id.mail_google_user);
        mNavUserMail.setText(mGoogleUserMail);
        mNavUserImage = (ImageView) navigationView.getHeaderView(0).findViewById(R.id.image_google_user);
        //Picasso.with(getBaseContext()).load(mGoogleUserPhoto).transform(new RoundedCornersTransformation(10)).into(mNavUserImage);
        Picasso.with(getBaseContext()).load(mGoogleUserPhoto).transform(new CircleTransform()).into(mNavUserImage);


        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mMessagesDatabaseReference = mFirebaseDatabase.getReference();

        mEventListView = findViewById(R.id.eventListView);
        mCompletedEventListView = findViewById(R.id.completedEventsListView);
        mEventAdapter = new EventAdapter(event_list);
        mEventListView.setAdapter(mEventAdapter);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        mEventListView.setLayoutManager(mLayoutManager);


        swipeController = new SwipeController(new SwipeControllerActions() {
            @Override
            public void onRightClicked(int position) {
                Events ev;
                ev = event_list.get(position);
                String eventDeleteKey = ev.getEventKey();
                mEventAdapter.eventList.remove(position);
                mEventAdapter.notifyDataSetChanged();
                mMessagesDatabaseReference.child("toDoList").child(eventDeleteKey).removeValue();
            }

            @Override
            public void onLeftClicked(int position) {
                Events e;
                e = event_list.get(position);
                String eventKey;
                eventKey = e.getEventKey();

                mMessagesDatabaseReference.child("toDoList").child(eventKey).child("completed").setValue("true");
                completed_event_list.add(mEventAdapter.eventList.get(position));
                mCompletedEventAdapter.notifyDataSetChanged();
                event_list.remove(mEventAdapter.eventList.get(position));
                mEventAdapter.notifyDataSetChanged();


            }

        });

        ItemTouchHelper itemTouchhelper = new ItemTouchHelper(swipeController);
        itemTouchhelper.attachToRecyclerView(mEventListView);
        mEventListView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
                swipeController.onDraw(c);
            }
        });


        completedswipeController = new SwipeController(new SwipeControllerActions() {
            @Override
            public void onRightClicked(int position) {
                Events ev;
                ev = completed_event_list.get(position);
                String eventDeleteKey = ev.getEventKey();
                mCompletedEventAdapter.eventList.remove(position);
                mCompletedEventAdapter.notifyDataSetChanged();
                mMessagesDatabaseReference.child("toDoList").child(eventDeleteKey).removeValue();
            }

            @Override
            public void onLeftClicked(int position) {
                Events e;
                e = completed_event_list.get(position);
                String eventKey;
                eventKey = e.getEventKey();

                event_list.add(mCompletedEventAdapter.eventList.get(position));
                mEventAdapter.notifyDataSetChanged();

                completed_event_list.remove(mCompletedEventAdapter.eventList.get(position));
                mCompletedEventAdapter.notifyDataSetChanged();

                mMessagesDatabaseReference.child("toDoList").child(eventKey).child("completed").setValue("false");


            }

        });


        ItemTouchHelper completeditemTouchhelper = new ItemTouchHelper(completedswipeController);
        completeditemTouchhelper.attachToRecyclerView(mCompletedEventListView);
        mCompletedEventListView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
                completedswipeController.onDraw(c);
            }
        });


        mCompletedEventAdapter = new EventAdapter(completed_event_list);
        mCompletedEventListView.setAdapter(mCompletedEventAdapter);
        RecyclerView.LayoutManager mCompletedLayoutManager = new LinearLayoutManager(getApplicationContext());
        mCompletedEventListView.setLayoutManager(mCompletedLayoutManager);

//        eventSwipeListView = findViewById(R.id.eventListView);
//        completedSwipeListView = findViewById(R.id.completedEventsListView);
//
//        eventSwipeListView.setMenuCreator(creator);
//        completedSwipeListView.setMenuCreator(creatorCompleted);

        mButtonAddEvent = findViewById(R.id.btn_add_event);
        mButtonAddEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                Intent intentToAddEvent = new Intent(EventsActivity.this, AddEventActivity.class);
                startActivity(intentToAddEvent);
            }
        });

        progressBar = findViewById(R.id.progress_bar);
        progressBar.getIndeterminateDrawable().setColorFilter(0xFF00BFFF, android.graphics.PorterDuff.Mode.MULTIPLY);
        progressBar.setVisibility(View.VISIBLE);


        // FetchListViews();
        //FetchCompletedListViews();
        progressBar.setVisibility(View.GONE);
        mMessagesDatabaseReference.child("toDoList").addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot data : dataSnapshot.getChildren()) {
                            if (data.child("completed").getValue().equals("false")) {

                                String date = data.child("date").getValue().toString();
                                String name = data.child("name").getValue().toString();
                                String key = data.getKey();
//                    Toast.makeText(EventsActivity.this," " + dataSnapshot,Toast.LENGTH_SHORT).show();

                                // Events events = dataSnapshot.child("todos").getValue(Events.class);
                                event_list.add(new Events(name, date, key, "false"));
                                //    event_list.add(new Events(ds.child("name").getValue().toString(),ds.child("date").getValue().toString()));


                                mEventAdapter.notifyDataSetChanged();
                            }
                            if (data.child("completed").getValue().equals("true")) {

                                String date = data.child("date").getValue().toString();
                                String name = data.child("name").getValue().toString();
                                String key = data.getKey();
//                    Toast.makeText(EventsActivity.this," " + dataSnapshot,Toast.LENGTH_SHORT).show();

                                // Events events = dataSnapshot.child("todos").getValue(Events.class);
                                completed_event_list.add(new Events(name, date, key, "true"));
                                //    event_list.add(new Events(ds.child("name").getValue().toString(),ds.child("date").getValue().toString()));


                                mCompletedEventAdapter.notifyDataSetChanged();
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

//
//        mEventListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
//
//                final Events e = event_list.get(position);
//                final String eventKey = e.getEventKey();
//
//
//                Intent intentToTaskActivity = new Intent(EventsActivity.this, TasksActivity.class);
//                intentToTaskActivity.putExtra("eventKey", eventKey);
//                //intentToTaskActivity.putExtra("taskKey", taskKey);
//                startActivity(intentToTaskActivity);

/*
              mMessagesDatabaseReference.child("toDoList").child(eventKey)
                        //.orderByChild("name")
                        //.equalTo( e.getEventName()

                        //mEventListView.get getItemAtPosition(position)
                        //)
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {

                                String taskKey = mMessagesDatabaseReference.child("toDoList").child(eventKey).child("tasksList").push().getKey();
                               Toast.makeText(EventsActivity.this, "" + taskKey, Toast.LENGTH_SHORT).show();
                                Intent intentToTaskActivity = new Intent(EventsActivity.this, TasksActivity.class);
                                intentToTaskActivity.putExtra("eventKey", eventKey);
                                intentToTaskActivity.putExtra("taskKey", taskKey);
                                startActivity(intentToTaskActivity);


                                final String addTaskName = "Task 1";
                                final String addTaskDetail = "Task 1";

                                Tasks t = new Tasks();
                                t.setTaskName(addTaskName);
                                t.setTaskDate(addTaskDetail);
                                t.setTaskKey(taskKey);


                                Map<String, Object> childUpdates = new HashMap<>();
                                childUpdates.put(taskKey, t.toTasksFirebaseObject());

                                //String s = childUpdates.put(taskKey, t.toTasksFirebaseObject()).toString();
                               mMessagesDatabaseReference.child("toDoList").child(eventKey).child("tasksList").push().setValue(childUpdates.put(taskKey, t.toTasksFirebaseObject()));
                                Toast.makeText(EventsActivity.this,"Done"   ,Toast.LENGTH_SHORT).show();

*/

                                /*mMessagesDatabaseReference.child("toDoList").child("tasksList").updateChildren(childUpdates, new DatabaseReference.CompletionListener() {
                                    @Override
                                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                        if (databaseError == null) {
                                            Toast.makeText(EventsActivity.this,"done" ,Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });*/

                               /* if (dataSnapshot.hasChildren()) {
                                    for (DataSnapshot data : dataSnapshot.getChildren()) {
                                        String d = data.child("tasksList").child("taskDetails").getValue().toString();
                                        //String firstChild = dataSnapshot.child("tasksList").child("taskDetails").getValue().toString();
                                        Toast.makeText(EventsActivity.this," " + d,Toast.LENGTH_SHORT).show();
                                    }

                                }*/
//            }
//
//
//        });


//        eventSwipeListView.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
//            @Override
//            public boolean onMenuItemClick(final int position, SwipeMenu menu, int index) {
//                switch (index) {
//
//                    case 0:
//
//                        Events e;
//                        e = event_list.get(position);
//                        String eventKey;
//                        eventKey = e.getEventKey();
//
//                        mMessagesDatabaseReference.child("toDoList").child(eventKey).child("completed").setValue("true");
//
//                        completed_event_list.add(mEventAdapter.getItem(position));
//                        mCompletedEventAdapter.notifyDataSetChanged();
//
//                        event_list.remove(mEventAdapter.getItem(position));
//                        mEventAdapter.notifyDataSetChanged();
//
//
//                       /* Events e;
//                        e = event_list.get(position);
//                        String eventKey;
//                        eventKey = e.getEventKey();
//
//                        Events todo = new Events();
//                        todo.setEventComplete("true");
//
//                        Map<String, Object> childUpdates = new HashMap<>();
//                        childUpdates.put(eventKey, todo.toEventsFirebaseObject());
//                        mMessagesDatabaseReference.child("toDoList").
//
//                                updateChildren(childUpdates, new DatabaseReference.CompletionListener() {
//                            @Override
//                            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
//                                if (databaseError == null) {
//                                    Toast.makeText(EventsActivity.this, "Marking event as completed", Toast.LENGTH_SHORT).show();
//                                    finish();
//                                }
//                            }
//                        });
//*/
//                        break;
//
//                    case 1:
//                        Events ev;
//                        ev = event_list.get(position);
//                        String eventDeleteKey = ev.getEventKey();
//                        mEventAdapter.remove(mEventAdapter.getItem(position));
//                        mEventAdapter.notifyDataSetChanged();
//                        mMessagesDatabaseReference.child("toDoList").child(eventDeleteKey).removeValue();
//
//                        break;
//                }
//                return false;
//            }
//        });
//
//
//        completedSwipeListView.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
//            @Override
//            public boolean onMenuItemClick(final int position, SwipeMenu menu, int index) {
//                switch (index) {
//
//                    case 0:
//                        Events e;
//                        e = completed_event_list.get(position);
//                        String eventKey;
//                        eventKey = e.getEventKey();
//
//                        event_list.add(mCompletedEventAdapter.getItem(position));
//                        mEventAdapter.notifyDataSetChanged();
//
//                        completed_event_list.remove(mCompletedEventAdapter.getItem(position));
//                        mCompletedEventAdapter.notifyDataSetChanged();
//
//                        mMessagesDatabaseReference.child("toDoList").child(eventKey).child("completed").setValue("false");
//
//
//                       /* Events e;
//                        e = completed_event_list.get(position);
//                        String eventKey;
//                        eventKey = e.getEventKey();
//
//                        Events todo = new Events();
//                        todo.setEventName(e.getEventName());
//                        todo.setEventDate(e.getEventDate());
//                        todo.setEventKey(eventKey);
//                        todo.setEventComplete("false");
//
//                        Map<String, Object> childUpdates = new HashMap<>();
//                        childUpdates.put(eventKey, todo.toEventsFirebaseObject());
//                        mMessagesDatabaseReference.child("toDoList").updateChildren(childUpdates, new DatabaseReference.CompletionListener() {
//                            @Override
//                            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
//                                if (databaseError == null) {
//                                    Toast.makeText(EventsActivity.this, "Marking event as not completed", Toast.LENGTH_SHORT).show();
//                                    finish();
//                                }
//                            }
//                        });*/
//                        break;
//
//                    case 1:
//                        Events ev;
//                        ev = completed_event_list.get(position);
//                        String eventDeleteKey = ev.getEventKey();
//                        mCompletedEventAdapter.remove(mCompletedEventAdapter.getItem(position));
//                        mCompletedEventAdapter.notifyDataSetChanged();
//                        mMessagesDatabaseReference.child("toDoList").child(eventDeleteKey).removeValue();
//
//                        break;
//                }
//                return false;
//            }
//        });
    }


    private void FetchListViews() {
        progressBar.setVisibility(View.GONE);
        mMessagesDatabaseReference.child("toDoList").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
//                for (DataSnapshot data : dataSnapshot.getChildren()) {
//
//                    // Events value = dataSnapshot.getValue(Events.class);
//                    //event_list.add(value);
//
//                    String date = data.child("date").getValue().toString();
//                    String name = data.child("name").getValue().toString();
//                    String key = data.getKey();
//                    Toast.makeText(EventsActivity.this," " + data,Toast.LENGTH_SHORT).show();
//
//                    // Events events = dataSnapshot.child("todos").getValue(Events.class);
//                    event_list.add(new Events(name, date, key));
//                    //    event_list.add(new Events(ds.child("name").getValue().toString(),ds.child("date").getValue().toString()));
//
//
//                }


                // Events value = dataSnapshot.getValue(Events.class);
                //event_list.add(value);
                if (dataSnapshot.child("completed").getValue().equals("false")) {

                    String date = dataSnapshot.child("date").getValue().toString();
                    String name = dataSnapshot.child("name").getValue().toString();
                    String key = dataSnapshot.getKey();
//                    Toast.makeText(EventsActivity.this," " + dataSnapshot,Toast.LENGTH_SHORT).show();

                    // Events events = dataSnapshot.child("todos").getValue(Events.class);
                    event_list.add(new Events(name, date, key, "false"));
                    //    event_list.add(new Events(ds.child("name").getValue().toString(),ds.child("date").getValue().toString()));


                    mEventAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                if (dataSnapshot.child("completed").getValue().equals("false")) {
                    event_list.clear();
                    // for (DataSnapshot data : dataSnapshot.getChildren()) {

                    //Events value = data.getValue(Events.class);
                    //event_list.add(value);

                    String date = dataSnapshot.child("date").getValue().toString();
                    String name = dataSnapshot.child("name").getValue().toString();
                    String key = dataSnapshot.getKey();

                    // Events events = dataSnapshot.child("todos").getValue(Events.class);
                    event_list.add(new Events(name, date, key, "false"));
                    //    event_list.add(new Events(ds.child("name").getValue().toString(),ds.child("date").getValue().toString()));
                    //}
                    mEventAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                int index = event_list.indexOf(dataSnapshot.getKey());
                event_list.remove(index);
                mEventAdapter.notifyDataSetChanged();

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                if (dataSnapshot.child("completed").getValue().equals("false")) {
                    event_list.clear();
                    // for (DataSnapshot data : dataSnapshot.getChildren()) {

                    //Events value = data.getValue(Events.class);
                    //event_list.add(value);

                    String date = dataSnapshot.child("date").getValue().toString();
                    String name = dataSnapshot.child("name").getValue().toString();
                    String key = dataSnapshot.getKey();

                    // Events events = dataSnapshot.child("todos").getValue(Events.class);
                    event_list.add(new Events(name, date, key, "false"));
                    //    event_list.add(new Events(ds.child("name").getValue().toString(),ds.child("date").getValue().toString()));
                    //}
                    mEventAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {


            }
        });

    }

    private void FetchCompletedListViews() {
        progressBar.setVisibility(View.GONE);

        mMessagesDatabaseReference.child("toDoList").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                // Events value = dataSnapshot.getValue(Events.class);
                //event_list.add(value);
                //Toast.makeText(EventsActivity.this, "" + dataSnapshot, Toast.LENGTH_SHORT).show();

                if (dataSnapshot.child("completed").getValue().equals("true")) {
                    String date = dataSnapshot.child("date").getValue().toString();
                    String name = dataSnapshot.child("name").getValue().toString();
                    String key = dataSnapshot.getKey();
                    //Toast.makeText(EventsActivity.this," Hello" + dataSnapshot,Toast.LENGTH_SHORT).show();
                    // Events events = dataSnapshot.child("todos").getValue(Events.class);
                    completed_event_list.add(new Events(name, date, key, "true"));
                    //    event_list.add(new Events(ds.child("name").getValue().toString(),ds.child("date").getValue().toString()));
                    mCompletedEventAdapter.notifyDataSetChanged();
                    //mCompletedEventAdapter.notifyDataSetChanged();

                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                if (dataSnapshot.child("completed").getValue().equals("true")) {
                    completed_event_list.clear();

                    // for (DataSnapshot data : dataSnapshot.getChildren()) {
                    //Events value = data.getValue(Events.class);
                    //event_list.add(value);

                    String date = dataSnapshot.child("date").getValue().toString();
                    String name = dataSnapshot.child("name").getValue().toString();
                    String key = dataSnapshot.getKey();

                    // Events events = dataSnapshot.child("todos").getValue(Events.class);
                    completed_event_list.add(new Events(name, date, key, "true"));
                    //    event_list.add(new Events(ds.child("name").getValue().toString(),ds.child("date").getValue().toString()));
                    // }
                    mCompletedEventAdapter.notifyDataSetChanged();
                    // mCompletedEventAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if (dataSnapshot.child("completed").getValue().equals("true")) {
                    completed_event_list.clear();

                    // for (DataSnapshot data : dataSnapshot.getChildren()) {
                    //Events value = data.getValue(Events.class);
                    //event_list.add(value);

                    String date = dataSnapshot.child("date").getValue().toString();
                    String name = dataSnapshot.child("name").getValue().toString();
                    String key = dataSnapshot.getKey();

                    // Events events = dataSnapshot.child("todos").getValue(Events.class);
                    completed_event_list.add(new Events(name, date, key, "true"));
                    //    event_list.add(new Events(ds.child("name").getValue().toString(),ds.child("date").getValue().toString()));
                    // }
                    mCompletedEventAdapter.notifyDataSetChanged();
                    // mCompletedEventAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

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
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_signout) {
            AuthUI.getInstance()
                    .signOut(this)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        public void onComplete(@NonNull Task<Void> task) {
                            // user is now signed out
                            startActivity(new Intent(EventsActivity.this, MainActivity.class));
                            finish();
                        }
                    });
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
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


    public class EventAdapter extends RecyclerView.Adapter<EventAdapter.EventAdapterViewHolder> {

        private List<Events> eventList;

        public EventAdapter(List<Events> el) {
            this.eventList = el;
        }

        @Override
        public EventAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.event_list_item, parent, false);
            return new EventAdapterViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull EventAdapter.EventAdapterViewHolder holder, final int position) {

            Events events = eventList.get(position);
            holder.eventNameTextView.setText(events.getEventName());
            holder.eventDateTextView.setText(events.getEventDate());

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    final Events e = eventList.get(position);
                    final String eventKey = e.getEventKey();


                    Intent intentToTaskActivity = new Intent(EventsActivity.this, TasksActivity.class);
                    intentToTaskActivity.putExtra("eventKey", eventKey);
                    //intentToTaskActivity.putExtra("taskKey", taskKey);
                    startActivity(intentToTaskActivity);
                }
            });

        }

        @Override
        public int getItemCount() {
            return eventList.size();
        }

        public class EventAdapterViewHolder extends RecyclerView.ViewHolder {

            TextView eventNameTextView;
            TextView eventDateTextView;

            public EventAdapterViewHolder(View itemView) {
                super(itemView);

                eventNameTextView = itemView.findViewById(R.id.event_name);
                eventDateTextView = itemView.findViewById(R.id.event_date);
            }
        }
    }

    //
//    SwipeHelper swipeHelper = new SwipeHelper(this, mCompletedEventListView) {
//        @Override
//        public void instantiateUnderlayButton(RecyclerView.ViewHolder viewHolder, List<UnderlayButton> underlayButtons) {
//            underlayButtons.add(new SwipeHelper.UnderlayButton(
//                    "Delete",
//                    0,
//                    Color.parseColor("#FF3C30"),
//                    new SwipeHelper.UnderlayButtonClickListener() {
//                        @Override
//                        public void onClick(int pos) {
//                            // TODO: onDelete
//                        }
//                    }
//            ));
//
//            underlayButtons.add(new SwipeHelper.UnderlayButton(
//                    "Transfer",
//                    0,
//                    Color.parseColor("#FF9502"),
//                    new SwipeHelper.UnderlayButtonClickListener() {
//                        @Override
//                        public void onClick(int pos) {
//                            // TODO: OnTransfer
//                        }
//                    }
//            ));
//            underlayButtons.add(new SwipeHelper.UnderlayButton(
//                    "Unshare",
//                    0,
//                    Color.parseColor("#C7C7CB"),
//                    new SwipeHelper.UnderlayButtonClickListener() {
//                        @Override
//                        public void onClick(int pos) {
//                            // TODO: OnUnshare
//                        }
//                    }
//            ));
//        }
//    };


}


//    private void setListViewHeader() {
//        LayoutInflater inflater = getLayoutInflater();
//        View header = inflater.inflate(R.layout.header_list_view, mEventListView, false);
//        totalClassmates = (TextView) header.findViewById(R.id.total);
//        swipeLayout = (SwipeLayout)header.findViewById(R.id.swipe_layout);
//        setSwipeViewFeatures();
//        mEventListView.addHeaderView(header);
//    }
//
//    private void setSwipeViewFeatures() {
//        //set show mode.
//        swipeLayout.setShowMode(SwipeLayout.ShowMode.PullOut);
//
//        //add drag edge.(If the BottomView has 'layout_gravity' attribute, this line is unnecessary)
//        swipeLayout.addDrag(SwipeLayout.DragEdge.Left, findViewById(R.id.bottom_wrapper));
//
//        swipeLayout.addSwipeListener(new SwipeLayout.SwipeListener() {
//            @Override
//            public void onClose(SwipeLayout layout) {
//                Log.i(TAG, "onClose");
//            }
//
//            @Override
//            public void onUpdate(SwipeLayout layout, int leftOffset, int topOffset) {
//                Log.i(TAG, "on swiping");
//            }
//
//            @Override
//            public void onStartOpen(SwipeLayout layout) {
//                Log.i(TAG, "on start open");
//            }
//
//            @Override
//            public void onOpen(SwipeLayout layout) {
//                Log.i(TAG, "the BottomView totally show");
//            }
//
//            @Override
//            public void onStartClose(SwipeLayout layout) {
//                Log.i(TAG, "the BottomView totally close");
//            }
//
//            @Override
//            public void onHandRelease(SwipeLayout layout, float xvel, float yvel) {
//                //when user's hand released.
//            }
//        });
//    }
//
//    private void setListViewAdapter() {
//        mEventAdapter = new EventAdapter(this, R.layout.event_list_item, event_list);
//        mEventListView.setAdapter(mEventAdapter);
//
//        totalClassmates.setText("(" + event_list.size() + ")");
//    }
//
//    public void updateAdapter() {
//        mEventAdapter.notifyDataSetChanged(); //update adapter
//        totalClassmates.setText("(" + event_list.size() + ")"); //update total friends in list
//    }
//
//}
