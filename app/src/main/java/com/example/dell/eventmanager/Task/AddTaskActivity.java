package com.example.dell.eventmanager.Task;

import android.app.Activity;
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
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.dell.eventmanager.Event.EventsActivity;
import com.example.dell.eventmanager.Legend.Legend;
import com.example.dell.eventmanager.Legend.LegendAdapter;
import com.example.dell.eventmanager.R;
import com.example.dell.eventmanager.Task.AddTaskActivity.UserAdapter;
import com.example.dell.eventmanager.User.Users;
import com.google.firebase.auth.FirebaseAuth;
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
    private ChildEventListener mChildEventListener;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;

    RecyclerView mUserGridView;
    RecyclerView mSelectedUserGridView;
    private UserAdapter mUserAdapter;
    private UserAdapter mSelectedUserAdapter;
    private ArrayList<Users> user_list = new ArrayList<Users>();
    private ArrayList<Users> selected_user_list = new ArrayList<Users>();

    private FloatingActionButton fab;

    Intent intentFromEventsActivity;
    String eventKey;
    String lableName;
    String lableColor;
    Dialog lableDialog;
    Dialog userDialog;
    Button lableConfirmButton;
    Button lableCancelButton;
    Button userConfirmButton;
    Button userCancelButton;
    EditText newLableName;
    Button colorBisque;
    Button colorDeepSkyBlue;
    Button colorLime;
    Button colorYellow;
    Button colorCoral;
    Button colorPurple;
    Button colorPink;
    Button colorRed;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.add_task_activity);


        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mFirebaseAuth = FirebaseAuth.getInstance();
        mMessagesDatabaseReference = mFirebaseDatabase.getReference();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Add Task");
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

        final EditText addEventNameEditText = findViewById(R.id.add_task_name);
        final EditText addEventDetailEditText = findViewById(R.id.add_task_details);


        intentFromEventsActivity = getIntent();
        eventKey = intentFromEventsActivity.getStringExtra("eventKey");


        mMessagesDatabaseReferenceToUsers = mFirebaseDatabase.getReference().child("users");


//       mUserGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
//                final Users users = user_list.get(position);
//                final String userName = users.getUserName();
//
//                Toast.makeText(AddTaskActivity.this, userName, Toast.LENGTH_SHORT).show();
//            }
//        });

        mSelectedUserGridView = findViewById(R.id.grid_view_people_selected);
        final RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getApplicationContext(),6);
        mSelectedUserGridView.setLayoutManager(mLayoutManager);
        mSelectedUserAdapter = new UserAdapter(selected_user_list);
        mSelectedUserGridView.setAdapter(mSelectedUserAdapter);

        fab = findViewById(R.id.floating_button);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                userDialog = new Dialog(AddTaskActivity.this);
                userDialog.setContentView(R.layout.users_dialog_box);
                userDialog.show();

                //userConfirmButton = userDialog.findViewById(R.id.btn_add_person_confirm);
                userCancelButton = userDialog.findViewById(R.id.btn_add_person_cancel);

                mUserGridView = userDialog.findViewById(R.id.grid_view_people);
                mUserAdapter = new UserAdapter(user_list);
                final RecyclerView.LayoutManager mUserLayoutManager = new GridLayoutManager(getApplicationContext() , 6);
                mUserGridView.setLayoutManager(mUserLayoutManager);
                mUserGridView.setAdapter(mUserAdapter);


                /*mUserGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                        final Users users = user_list.get(position);
                        final String userName = users.getUserName();

                        selected_user_list.add(new Users(users.getUserKey(), users.getUserName(), users.getUserEmail(), users.getUserPhotoUrl()));
                        mSelectedUserAdapter.notifyDataSetChanged();

                        Toast.makeText(AddTaskActivity.this, userName, Toast.LENGTH_SHORT).show();
                    }
                });*/

//                userConfirmButton.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {


//                        String legendKey = mMessagesDatabaseReference.child("toDoList").child(eventKey).child("tasksList").push().getKey();
//                        Toast.makeText(AddTaskActivity.this, "" + legendKey, Toast.LENGTH_SHORT).show();
//
//                        lableName = newLableName.getText().toString();
//
//                        Legend l = new Legend();
//
//                        if (lableName.isEmpty()) {
//                            Toast.makeText(AddTaskActivity.this, "Please enter a name for the label", Toast.LENGTH_SHORT).show();
//                        } else {
//
//
//                            l.setLegendName(lableName);
//                            l.setLegendKey(legendKey);
//                            l.setLegendColor(lableColor);
//
//                            Map<String, Object> childUpdates = new HashMap<>();
//                            mMessagesDatabaseReference.child("toDoList").child(eventKey).child("legend").push().setValue(childUpdates.put(legendKey, l.toLegendsFirebaseObject()));
//
//                            //childUpdates.put(key, todo.toEventsFirebaseObject());
//                            mMessagesDatabaseReference.child("toDoList").child(eventKey).child("legend").updateChildren(childUpdates, new DatabaseReference.CompletionListener() {
//                                @Override
//                                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
//                                    if (databaseError == null) {
//                                        Toast.makeText(com.example.dell.eventmanager.Task.AddTaskActivity.this, "New Legend Added", Toast.LENGTH_SHORT).show();
//                                        lableDialog.cancel();
//                                    }
//                                }
//                            });
//                        }


//
//
//
//
//
//
//
//
//
//
//
//
//                    }
//                });

                userCancelButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        userDialog.cancel();
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
        // btnAddEvent.setEnabled(false);
        Button btnAddLable = findViewById(R.id.btn_add_lable);
        btnAddLable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                lableDialog = new Dialog(AddTaskActivity.this);
                lableDialog.setContentView(R.layout.dialog_box_layout);
                lableDialog.show();


                lableConfirmButton = lableDialog.findViewById(R.id.lable_confirm_button);
                newLableName = lableDialog.findViewById(R.id.new_lable_name_edit_text);

                lableCancelButton = lableDialog.findViewById(R.id.lable_cancel_button);
                colorBisque = lableDialog.findViewById(R.id.colorBisque);
                colorLime = lableDialog.findViewById(R.id.colorLime);
                colorYellow = lableDialog.findViewById(R.id.colorYellow);
                colorCoral = lableDialog.findViewById(R.id.colorCoral);
                colorPurple = lableDialog.findViewById(R.id.colorPurple);
                colorDeepSkyBlue = lableDialog.findViewById(R.id.colorDeepSkyBlue);
                colorPink = lableDialog.findViewById(R.id.colorPink);
                colorRed = lableDialog.findViewById(R.id.colorRed);

                lableConfirmButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        lableName = getLable();
                        // btnAddEvent.setEnabled(true);

                    }
                });

                lableCancelButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        lableDialog.cancel();
                        // btnAddEvent.setEnabled(true);
                    }
                });

                colorBisque.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        lableColor = "Bisque";
                        Toast.makeText(AddTaskActivity.this, "Bisque", Toast.LENGTH_SHORT).show();
                    }
                });

                colorDeepSkyBlue.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        lableColor = "DeepSkyBlue";
                        Toast.makeText(AddTaskActivity.this, "Deep Sky Blue", Toast.LENGTH_SHORT).show();
                    }
                });
                colorLime.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        lableColor = "Lime";
                        Toast.makeText(AddTaskActivity.this, "Lime", Toast.LENGTH_SHORT).show();
                    }
                });
                colorYellow.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        lableColor = "Yellow";
                        Toast.makeText(AddTaskActivity.this, "Yellow", Toast.LENGTH_SHORT).show();
                    }
                });
                colorCoral.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        lableColor = "Coral";
                        Toast.makeText(AddTaskActivity.this, "Coral", Toast.LENGTH_SHORT).show();
                    }
                });
                colorPink.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        lableColor = "Pink";
                        Toast.makeText(AddTaskActivity.this, "Pink", Toast.LENGTH_SHORT).show();
                    }
                });
                colorPurple.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        lableColor = "Purple";
                        Toast.makeText(AddTaskActivity.this, "Purple", Toast.LENGTH_SHORT).show();
                    }
                });
                colorRed.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        lableColor = "Red";
                        Toast.makeText(AddTaskActivity.this, "Red", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });


        btnAddEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final String taskKey = mMessagesDatabaseReference.child("toDoList").child(eventKey).child("tasksList").push().getKey();
                Toast.makeText(AddTaskActivity.this, "" + taskKey, Toast.LENGTH_SHORT).show();
                final String addTaskName = addEventNameEditText.getText().toString();
                final String addTaskDetail = addEventDetailEditText.getText().toString();

                Tasks t = new Tasks();

                if (addTaskName.isEmpty()) {
                    Toast.makeText(AddTaskActivity.this, "Please enter a name for the task", Toast.LENGTH_SHORT).show();
                } else if (addTaskDetail.isEmpty()) {
                    Toast.makeText(AddTaskActivity.this, "Please enter some deatils for the task", Toast.LENGTH_SHORT).show();
                } else if (lableName.isEmpty()) {
                    Toast.makeText(AddTaskActivity.this, "Please select a lable name", Toast.LENGTH_SHORT).show();
                } else {
                    t.setTaskName(addTaskName);
                    t.setTaskDate(addTaskDetail);
                    t.setTaskKey(taskKey);
                    t.setTaskLable(lableName);
                    t.setTaskLableColor(lableColor);
                    t.setTaskComplete("false");


                    Map<String, Object> childUpdates = new HashMap<>();
                    mMessagesDatabaseReference.child("toDoList").child(eventKey).child("tasksList").push().setValue(childUpdates.put(taskKey, t.toTasksFirebaseObject()));


                    mMessagesDatabaseReference.child("toDoList").child(eventKey).child("tasksList").updateChildren(childUpdates, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                            if (databaseError == null) {
                                Toast.makeText(com.example.dell.eventmanager.Task.AddTaskActivity.this, "New Task Added", Toast.LENGTH_SHORT).show();
                                //finish();//TODO
                                Intent intentToTaskActivity = new Intent(AddTaskActivity.this, TasksActivity.class);
                                startActivity(intentToTaskActivity);
                            }
                        }
                    });

                    int size = selected_user_list.size();
                    for (int i = 0; i < size; i++) {
//
                        String userKey = mMessagesDatabaseReference.child("toDoList").child(eventKey).child("tasksList").child(taskKey).child("users").push().getKey();
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

    private String getLable() {

        String legendKey = mMessagesDatabaseReference.child("toDoList").child(eventKey).child("legend").push().getKey();
        Toast.makeText(AddTaskActivity.this, "" + legendKey, Toast.LENGTH_SHORT).show();

        lableName = newLableName.getText().toString();

        Legend l = new Legend();

        if (lableName.isEmpty()) {
            Toast.makeText(AddTaskActivity.this, "Please enter a name for the label", Toast.LENGTH_SHORT).show();
        } else {


            l.setLegendName(lableName);
            l.setLegendKey(legendKey);
            l.setLegendColor(lableColor);

            Map<String, Object> childUpdates = new HashMap<>();
            mMessagesDatabaseReference.child("toDoList").child(eventKey).child("legend").push().setValue(childUpdates.put(legendKey, l.toLegendsFirebaseObject()));

            //childUpdates.put(key, todo.toEventsFirebaseObject());
            mMessagesDatabaseReference.child("toDoList").child(eventKey).child("legend").updateChildren(childUpdates, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                    if (databaseError == null) {
                        Toast.makeText(com.example.dell.eventmanager.Task.AddTaskActivity.this, "New Legend Added", Toast.LENGTH_SHORT).show();
                        lableDialog.cancel();
                    }
                }
            });
        }

        return lableName;
    }


    private void FetchUserListViews() {

        mMessagesDatabaseReferenceToUsers.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                //for (DataSnapshot data : dataSnapshot.getChildren()) {

                String user_name = dataSnapshot.child("user_name").getValue().toString();
                String user_email = dataSnapshot.child("user_email").getValue().toString();
                String user_photo = dataSnapshot.child("user_photo").getValue().toString();
                String user_id = dataSnapshot.getKey();

                // String key = data.getKey();
                //Toast.makeText(TasksActivity.this, data.toString(), Toast.LENGTH_SHORT).show();
                user_list.add(new Users(user_id, user_name, user_email, user_photo));

                // Toast.makeText(TasksActivity.this ,dataSnapshot.getValue().toString()
                //child(eventKey).child("tasksList").toString()
                //       , Toast.LENGTH_SHORT).show();
                //  }
                //mUserAdapter.notifyDataSetChanged();
                //TODO
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
//                legend_list.clear();
//                for (DataSnapshot data : dataSnapshot.getChildren()) {
//
//                    //Events value = data.getValue(Events.class);
//                    //event_list.add(value);
//
//                    String name = dataSnapshot.child("name").getValue().toString();
//                    String key = data.getKey();
//                    String color = dataSnapshot.child("color").getValue().toString();
//                    // Events events = dataSnapshot.child("todos").getValue(Events.class);
//                    legend_list.add(new Legend(name,color));
//                    //    event_list.add(new Events(ds.child("name").getValue().toString(),ds.child("date").getValue().toString()));
//                }
//                mLegendAdapter.notifyDataSetChanged();

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
                            final Users users = user_list.get(position);
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
