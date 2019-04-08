package com.example.dell.eventmanager.Task;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dell.eventmanager.Legend.Legend;
import com.example.dell.eventmanager.Legend.LegendAdapter;
import com.example.dell.eventmanager.R;
import com.example.dell.eventmanager.User.Users;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddTaskActivity extends AppCompatActivity {

    private ArrayList<Legend> legend_list = new ArrayList<Legend>();
    private LegendAdapter mLegendAdapter;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mMessagesDatabaseReference;
    private DatabaseReference mMessagesDatabaseReferenceToUsers;

    private FirebaseUser mFirebaseUser;
    private ChildEventListener mChildEventListener;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;

    RecyclerView mUserGridView;
    RecyclerView mSelectedUserGridView;
    private UserAdapter mUserAdapter;
    private UserAdapter mSelectedUserAdapter;
    private ArrayList<Users> add_task_user_list = new ArrayList<Users>();
    private ArrayList<Users> selected_user_list = new ArrayList<Users>();

    private FloatingActionButton fab;

    Intent intentFromEventsActivity;
    String eventKey;
    String labelName = "";
    String labelColor = "";
    Dialog labelDialog;
    Dialog userAddTaskDialog;
    Button labelConfirmButton;
    Button labelCancelButton;
    Button userConfirmButton;
    Button userCancelButton;
    EditText newLabelName;
    Button colorGreen;
    Button colorYellow;
    Button colorPurple;
    Button colorRed;
    Button colorBlue;
    Button colorBush;
    Button colorCyan;
    Button colorMagenta;


    TextView toolbarName;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.add_task_activity);


        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mFirebaseAuth = FirebaseAuth.getInstance();
        mMessagesDatabaseReference = mFirebaseDatabase.getReference();

        mFirebaseUser = mFirebaseAuth.getCurrentUser();

//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        toolbar.setTitle("Add Task");
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

        toolbarName = findViewById(R.id.toolbar_name_another);
        toolbarName.setText("Add Task");


        final EditText addEventNameEditText = findViewById(R.id.add_task_name);
        final EditText addEventDetailEditText = findViewById(R.id.add_task_details);


        intentFromEventsActivity = getIntent();
        eventKey = intentFromEventsActivity.getStringExtra("eventKey");


        mMessagesDatabaseReferenceToUsers = mFirebaseDatabase.getReference().child("users");

        mSelectedUserGridView = findViewById(R.id.grid_view_people_selected);
        final RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getApplicationContext(), 6);
        mSelectedUserGridView.setLayoutManager(mLayoutManager);
        mSelectedUserAdapter = new UserAdapter(selected_user_list);
        mSelectedUserGridView.setAdapter(mSelectedUserAdapter);

        fab = findViewById(R.id.floating_button);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                userAddTaskDialog = new Dialog(AddTaskActivity.this);
                userAddTaskDialog.setContentView(R.layout.users_dialog_box_add_task);
                userAddTaskDialog.show();

                userCancelButton = userAddTaskDialog.findViewById(R.id.btn_add_person_cancel_add_task);

                mUserGridView = userAddTaskDialog.findViewById(R.id.grid_view_people_add_task);
                mUserAdapter = new UserAdapter(add_task_user_list);
                final RecyclerView.LayoutManager mUserLayoutManager = new GridLayoutManager(getApplicationContext(), 6);
                mUserGridView.setLayoutManager(mUserLayoutManager);
                mUserGridView.setAdapter(mUserAdapter);


                userCancelButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        userAddTaskDialog.cancel();
                    }
                });

            }
        });

        final Button btnCancelEventButton = findViewById(R.id.btn_confirm_cancel_task);
        btnCancelEventButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        final Button btnAddEvent = findViewById(R.id.btn_confirm_add_task);

        Button btnAddLabel = findViewById(R.id.btn_add_label);
        btnAddLabel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                labelDialog = new Dialog(AddTaskActivity.this);
                labelDialog.setContentView(R.layout.dialog_box_layout);
                labelDialog.show();


                labelConfirmButton = labelDialog.findViewById(R.id.label_confirm_button);
                newLabelName = labelDialog.findViewById(R.id.new_label_name_edit_text);

                labelCancelButton = labelDialog.findViewById(R.id.label_cancel_button);
                colorBlue = labelDialog.findViewById(R.id.colorBlue);
                colorBush = labelDialog.findViewById(R.id.colorBush);
                colorYellow = labelDialog.findViewById(R.id.colorYellow);
                colorCyan = labelDialog.findViewById(R.id.colorCyan);
                colorPurple = labelDialog.findViewById(R.id.colorPurple);
                colorMagenta = labelDialog.findViewById(R.id.colorMagenta);
                colorGreen = labelDialog.findViewById(R.id.colorGreen);
                colorRed = labelDialog.findViewById(R.id.colorRed);

                labelConfirmButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        labelName = getLabel();


                    }
                });

                labelCancelButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        labelDialog.cancel();

                    }
                });

                colorGreen.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        labelColor = "Green";
                        Toast.makeText(AddTaskActivity.this, "Green", Toast.LENGTH_SHORT).show();
                    }
                });

                colorMagenta.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        labelColor = "Magenta";
                        Toast.makeText(AddTaskActivity.this, "Magenta", Toast.LENGTH_SHORT).show();
                    }
                });
                colorCyan.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        labelColor = "Cyan";
                        Toast.makeText(AddTaskActivity.this, "Cyan", Toast.LENGTH_SHORT).show();
                    }
                });
                colorYellow.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        labelColor = "Yellow";
                        Toast.makeText(AddTaskActivity.this, "Yellow", Toast.LENGTH_SHORT).show();
                    }
                });
                colorBlue.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        labelColor = "Blue";
                        Toast.makeText(AddTaskActivity.this, "Blue", Toast.LENGTH_SHORT).show();
                    }
                });
                colorBush.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        labelColor = "Bush";
                        Toast.makeText(AddTaskActivity.this, "Bush", Toast.LENGTH_SHORT).show();
                    }
                });
                colorPurple.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        labelColor = "Purple";
                        Toast.makeText(AddTaskActivity.this, "Purple", Toast.LENGTH_SHORT).show();
                    }
                });
                colorRed.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        labelColor = "Red";
                        Toast.makeText(AddTaskActivity.this, "Red", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });


        btnAddEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final String taskKey = mMessagesDatabaseReference.child("toDoList").child(eventKey).child("tasksList").push().getKey();
                final String addTaskName = addEventNameEditText.getText().toString();
                final String addTaskDetail = addEventDetailEditText.getText().toString();

                Tasks t = new Tasks();

                if (addTaskName.isEmpty()) {
                    Toast.makeText(AddTaskActivity.this, "Please enter a name for the task", Toast.LENGTH_SHORT).show();
                } else if (addTaskDetail.isEmpty()) {
                    Toast.makeText(AddTaskActivity.this, "Please enter some details for the task", Toast.LENGTH_SHORT).show();
                } else if (labelName.equals("")) {
                    Toast.makeText(AddTaskActivity.this, "Please select a label name", Toast.LENGTH_SHORT).show();
                } else if (labelColor.equals("")) {
                    Toast.makeText(AddTaskActivity.this, "Please select a label name", Toast.LENGTH_SHORT).show();
                } else {
                    t.setTaskName(addTaskName);
                    t.setTaskDate(addTaskDetail);
                    t.setTaskKey(taskKey);
                    t.setTaskLabel(labelName);
                    t.setTaskLabelColor(labelColor);
                    t.setTaskComplete("false");


                    Map<String, Object> childUpdates = new HashMap<>();
                    mMessagesDatabaseReference.child("toDoList").child(eventKey).child("tasksList").push().setValue(childUpdates.put(taskKey, t.toTasksFirebaseObject()));


                    mMessagesDatabaseReference.child("toDoList").child(eventKey).child("tasksList").updateChildren(childUpdates, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                            if (databaseError == null) {
                                finish();
//                                Toast.makeText(com.example.dell.eventmanager.Task.AddTaskActivity.this, "New Task Added", Toast.LENGTH_SHORT).show();
//                                //finish();//TODO
//                                Intent intentToTaskActivity = new Intent(AddTaskActivity.this, TasksActivity.class);
//                                startActivity(intentToTaskActivity);
                            }
                        }
                    });

                    int size = selected_user_list.size();
                    for (int i = 0; i < size; i++) {
//
                        String userKey = selected_user_list.get(i).getUserKey();
                        // mMessagesDatabaseReference.child("toDoList").child(eventKey).child("tasksList").child(taskKey).child("users").push().getKey();
                        Users u = new Users();

                        u.setUserName(selected_user_list.get(i).getUserName());
                        u.setUserEmail(selected_user_list.get(i).getUserEmail());
                        u.setUserKey(selected_user_list.get(i).getUserKey());
                        u.setUserPhotoUrl(selected_user_list.get(i).getUserPhotoUrl());


                        Map<String, Object> userUpdates = new HashMap<>();
                        mMessagesDatabaseReference.child("toDoList").child(eventKey).child("tasksList").child(taskKey).child("users").push().setValue(userUpdates.put(userKey, u.toUsersFirebaseObject()));
                        mMessagesDatabaseReference.child("toDoList").child(eventKey).child("tasksList").child(taskKey).child("users").updateChildren(userUpdates, new DatabaseReference.CompletionListener() {
                            @Override
                            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                if (databaseError == null) {

                                }
                            }
                        });
                    }
                }
            }
        });

        FetchUserListViews();

//
//        mUserGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
//                final Users users = user_list.get(position);
//                final String userName = users.getUserName();
//
//                Toast.makeText(AddTaskActivity.this, userName, Toast.LENGTH_SHORT).show();
//            }
//        });
    }

    private String getLabel() {

        String legendKey = mMessagesDatabaseReference.child("toDoList").child(eventKey).child("legend").push().getKey();
        // Toast.makeText(AddTaskActivity.this, "" + legendKey, Toast.LENGTH_SHORT).show();

        labelName = newLabelName.getText().toString();

        Legend l = new Legend();

        if (labelName.isEmpty()) {
            Toast.makeText(AddTaskActivity.this, "Please enter a name for the label", Toast.LENGTH_SHORT).show();
        } else if (labelColor.isEmpty()) {
            Toast.makeText(AddTaskActivity.this, "Please select a colour  for the label", Toast.LENGTH_SHORT).show();
        }
        else {


            l.setLegendName(labelName);
            l.setLegendKey(legendKey);
            l.setLegendColor(labelColor);

            Map<String, Object> childUpdates = new HashMap<>();
            mMessagesDatabaseReference.child("toDoList").child(eventKey).child("legend").push().setValue(childUpdates.put(legendKey, l.toLegendsFirebaseObject()));

            //childUpdates.put(key, todo.toEventsFirebaseObject());
            mMessagesDatabaseReference.child("toDoList").child(eventKey).child("legend").updateChildren(childUpdates, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                    if (databaseError == null) {
                        Toast.makeText(com.example.dell.eventmanager.Task.AddTaskActivity.this, "New Legend Added", Toast.LENGTH_SHORT).show();
                        labelDialog.cancel();
                    }
                }
            });
        }

        return labelName;
    }


    private void FetchUserListViews() {

        mMessagesDatabaseReferenceToUsers.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                if (mFirebaseUser.getEmail().compareTo("dscvitvellore@gmail.com") != 0) {

                    String user_name = dataSnapshot.child("user_name").getValue().toString();
                    String user_email = dataSnapshot.child("user_email").getValue().toString();
                    String user_photo = dataSnapshot.child("user_photo").getValue().toString();
                    String user_id = dataSnapshot.getKey();

                    if (mFirebaseUser.getDisplayName().equals(user_name)) {
                        add_task_user_list.add(new Users(user_id, user_name, user_email, user_photo));
                    }


                } else {

                    String user_name = dataSnapshot.child("user_name").getValue().toString();
                    String user_email = dataSnapshot.child("user_email").getValue().toString();
                    String user_photo = dataSnapshot.child("user_photo").getValue().toString();
                    String user_id = dataSnapshot.getKey();

                    add_task_user_list.add(new Users(user_id, user_name, user_email, user_photo));

                }
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

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
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
        if (id == android.R.id.home) {
            // onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        return;
    }

    public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserAdapterViewHolder> {

        private List<Users> userList;

        public UserAdapter(List<Users> ul) {
            this.userList = ul;
        }

        @Override
        public UserAdapter.UserAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_list_item, parent, false);
            return new UserAdapter.UserAdapterViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull AddTaskActivity.UserAdapter.UserAdapterViewHolder holder, final int position) {

            Users users = userList.get(position);
            Picasso.with(AddTaskActivity.this).load(users.getUserPhotoUrl()).transform(new CircleTransform()).into(holder.userImageView);

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final Users users = add_task_user_list.get(position);
                    final String userName = users.getUserName();

                    selected_user_list.add(new Users(users.getUserKey(), users.getUserName(), users.getUserEmail(), users.getUserPhotoUrl()));
                    mSelectedUserAdapter.notifyDataSetChanged();

                    Toast.makeText(AddTaskActivity.this, userName, Toast.LENGTH_SHORT).show();

                }
            });

        }

        @Override
        public int getItemCount() {
            return userList.size();
        }

        public class UserAdapterViewHolder extends RecyclerView.ViewHolder {

            ImageView userImageView;

            public UserAdapterViewHolder(View itemView) {
                super(itemView);

                userImageView = itemView.findViewById(R.id.person_image);
            }
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

}
