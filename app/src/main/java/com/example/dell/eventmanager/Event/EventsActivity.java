package com.example.dell.eventmanager.Event;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.ActionBarDrawerToggle;
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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.example.dell.eventmanager.MainActivity;
import com.example.dell.eventmanager.R;
import com.example.dell.eventmanager.SwipeController;
import com.example.dell.eventmanager.SwipeControllerActions;
import com.example.dell.eventmanager.SwipeControllerNC;
import com.example.dell.eventmanager.Task.TasksActivity;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
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
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private ChildEventListener mChildEventListener;

    private NestedScrollView nestedScrollView;
    private ProgressBar progressBar;

    private ArrayList<Events> event_list = new ArrayList<Events>();
    private ArrayList<Events> completed_event_list = new ArrayList<Events>();

    SwipeMenuListView eventSwipeListView;
    SwipeMenuListView completedSwipeListView;

    SwipeController swipeController = null;
    SwipeControllerNC completedswipeController = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation_drawer);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_ma);
        toolbar.setTitle("Event Manager");
        setSupportActionBar(toolbar);

        nestedScrollView = findViewById(R.id.nested_scroll_view);
        nestedScrollView.setVisibility(View.GONE);
        progressBar = findViewById(R.id.progress_bar);
        progressBar.getIndeterminateDrawable().setColorFilter(0xFF00BFFF, android.graphics.PorterDuff.Mode.MULTIPLY);
        progressBar.setVisibility(View.VISIBLE);
        //progressBar.setScrollBarFadeDuration(10000);

        //progressBar.setVisibility(View.GONE);

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
        Picasso.with(getBaseContext()).load(mGoogleUserPhoto).transform(new CircleTransform()).into(mNavUserImage);


        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mMessagesDatabaseReference = mFirebaseDatabase.getReference();

        mEventListView = findViewById(R.id.eventListView);
        mCompletedEventListView = findViewById(R.id.completedEventsListView);
        mEventAdapter = new EventAdapter(event_list);
        mEventListView.setAdapter(mEventAdapter);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        mEventListView.setLayoutManager(mLayoutManager);

        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();

        swipeController = new SwipeController(new SwipeControllerActions() {
            @Override
            public void onRightClicked(int position) {
                if ((mFirebaseUser.getEmail().compareTo("dscvitvellore@gmail.com") != 0)) {
                    Toast.makeText(EventsActivity.this, "You are not allowed to delete the event", Toast.LENGTH_SHORT).show();
                } else {
                    Events ev;
                    ev = event_list.get(position);
                    String eventDeleteKey = ev.getEventKey();
                    mEventAdapter.eventList.remove(position);
                    mEventAdapter.notifyDataSetChanged();
                    mMessagesDatabaseReference.child("toDoList").child(eventDeleteKey).removeValue();
                }
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


        completedswipeController = new SwipeControllerNC(new SwipeControllerActions() {
            @Override
            public void onRightClicked(int position) {
                if ((mFirebaseUser.getEmail().compareTo("dscvitvellore@gmail.com") != 0)) {
                    Toast.makeText(EventsActivity.this, "You are not allowed to delete the event", Toast.LENGTH_SHORT).show();
                } else {
                    Events ev;
                    ev = completed_event_list.get(position);
                    String eventDeleteKey = ev.getEventKey();
                    mCompletedEventAdapter.eventList.remove(position);
                    mCompletedEventAdapter.notifyDataSetChanged();
                    mMessagesDatabaseReference.child("toDoList").child(eventDeleteKey).removeValue();
                }
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

        mButtonAddEvent = findViewById(R.id.btn_add_event);
        mButtonAddEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                Intent intentToAddEvent = new Intent(EventsActivity.this, AddEventActivity.class);
                startActivity(intentToAddEvent);
            }
        });


        mMessagesDatabaseReference.child("toDoList").addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        progressBar.setVisibility(View.GONE);
                        nestedScrollView.setVisibility(View.VISIBLE);
                        for (DataSnapshot data : dataSnapshot.getChildren()) {
                            if (data.child("completed").getValue().equals("false")) {

                                String date = data.child("date").getValue().toString();
                                String name = data.child("name").getValue().toString();
                                String key = data.getKey();
                                event_list.add(new Events(name, date, key, "false"));


                                mEventAdapter.notifyDataSetChanged();
                            }
                            if (data.child("completed").getValue().equals("true")) {

                                String date = data.child("date").getValue().toString();
                                String name = data.child("name").getValue().toString();
                                String key = data.getKey();
                                completed_event_list.add(new Events(name, date, key, "true"));
                                mCompletedEventAdapter.notifyDataSetChanged();
                            }
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                }
        );
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
        }
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.navigation_drawer, menu);
//        return true;
//    }

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
}
