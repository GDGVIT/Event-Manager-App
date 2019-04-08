package com.example.dell.eventmanager.Task;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
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

import com.example.dell.eventmanager.R;
import com.example.dell.eventmanager.User.Users;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TaskDetailsActivity extends AppCompatActivity {
    private String mUsername;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mMessagesDatabaseReference;
    private DatabaseReference mMessagesDatabaseReferenceToUsers;
    private DatabaseReference mMessagesDatabaseReferenceToAllUsers;
    private DatabaseReference databaseReference;
    private ChildEventListener mChildEventListener;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private FirebaseStorage storage;
    private FirebaseUser mFirebaseUser;
    private StorageReference photoRef;

    Intent intentFromTasksActivity;
    String eventKey;
    String taskKey;
    TextView taskDateTextView;
    RecyclerView taskUsers;
    RecyclerView fileNames;
    TaskDetailsAdapter mTaskDetailsAdapter;
    FileDetailsAdapter mFileDetailsAdapter;
    private ArrayList<Users> user_list = new ArrayList<Users>();
    private ArrayList<Users> task_user_list = new ArrayList<Users>();

    private ArrayList<String> items = new ArrayList<String>();
    private ArrayList<String> urls = new ArrayList<String>();

    Button btnAddFile;
    Button btnTaskDelete;
    Uri pdfUri;
    ProgressDialog progressDialog;
    EditText textViewFileName;

    FloatingActionButton fab;
    Dialog userDialog;
    Button userCancelButton;
    RecyclerView mUserGridView;
    private UserAdapter mUserAdapter;

    Dialog fileDialog;
    Button fabClose;
    FloatingActionButton fabDownload;
    FloatingActionButton fabDelete;

    ImageView backButton;
    TextView toolbarName;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.task_details_activity);

//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        toolbar.setTitle("Task Details");
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

        toolbarName = findViewById(R.id.toolbar_name);
        toolbarName.setText("Task Details");
        backButton = findViewById(R.id.back_button_dsc);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


        intentFromTasksActivity = getIntent();
        eventKey = intentFromTasksActivity.getStringExtra("eventKey");
        taskKey = intentFromTasksActivity.getStringExtra("taskKey");

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mFirebaseAuth = FirebaseAuth.getInstance();
        mMessagesDatabaseReference = mFirebaseDatabase.getReference().child("toDoList").child(eventKey).child("tasksList").child(taskKey);
        mMessagesDatabaseReferenceToUsers = mFirebaseDatabase.getReference().child("toDoList").child(eventKey).child("tasksList").child(taskKey).child("users");
        mMessagesDatabaseReferenceToAllUsers = mFirebaseDatabase.getReference().child("users");
        storage = FirebaseStorage.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();

        taskDateTextView = findViewById(R.id.show_task_details);

        taskUsers = findViewById(R.id.list_view_task_user);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        taskUsers.setLayoutManager(mLayoutManager);
        mTaskDetailsAdapter = new TaskDetailsAdapter(task_user_list);
        //mTaskDetailsAdapter = new TaskDetailsAdapter(this, R.layout.user_of_task_list_item, task_user_list);
        taskUsers.setAdapter(mTaskDetailsAdapter);


        fileNames = findViewById(R.id.add_files_recycler_view);
        fileNames.setLayoutManager(new LinearLayoutManager(TaskDetailsActivity.this));
        mFileDetailsAdapter = new FileDetailsAdapter(fileNames, TaskDetailsActivity.this, items, urls);
        fileNames.setAdapter(mFileDetailsAdapter);

        databaseReference = FirebaseDatabase.getInstance().getReference();

//        mMessagesDatabaseReference.child("uploads").addListenerForSingleValueEvent(
//                new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                        for(DataSnapshot data: dataSnapshot.getChildren()) {
//                            String fileName = data.getKey();
//                            String url = dataSnapshot.getValue(String.class);
//                            Toast.makeText(TaskDetailsActivity.this, fileName, Toast.LENGTH_SHORT).show();
//                            ((FileDetailsAdapter) fileNames.getAdapter()).update(fileName, url);
//                        }
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                    }
//                }
//        );


        mMessagesDatabaseReference.child("uploads").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                String fileName = dataSnapshot.getKey();
                String url = dataSnapshot.getValue(String.class);
                ((FileDetailsAdapter) fileNames.getAdapter()).update(fileName, url);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
//                items.clear();
//                urls.clear();
//
//                    String fileName = dataSnapshot.getKey();
//                    String url = dataSnapshot.getValue(String.class);
//                    ((FileDetailsAdapter) fileNames.getAdapter()).update(fileName, url);
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
//                items.clear();
//                urls.clear();
//                for(DataSnapshot data :dataSnapshot.getChildren()){
//                    String fileName = dataSnapshot.getKey();
//                    String url = dataSnapshot.getValue(String.class);
//                    ((FileDetailsAdapter) fileNames.getAdapter()).update(fileName, url);}

                items.remove(dataSnapshot.getKey());
                urls.remove(dataSnapshot.getKey());

//                int index = items.indexOf(dataSnapshot.getKey());
//                messageList.remove(index);
//                keyList.remove(index);
//                mAdapter.notifyDataSetChanged();

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
//                items.clear();
//                urls.clear();
//                for (DataSnapshot data : dataSnapshot.getChildren()) {
//                    String fileName = dataSnapshot.getKey();
//                    String url = dataSnapshot.getValue(String.class);
//                    ((FileDetailsAdapter) fileNames.getAdapter()).update(fileName, url);
                //  }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        mMessagesDatabaseReference.addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        String date = dataSnapshot.child("date").getValue().toString();
                        taskDateTextView.setText(date);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                }
        );

        mMessagesDatabaseReferenceToUsers.addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        for (DataSnapshot data : dataSnapshot.getChildren()) {
                            String userName = data.child("user_name").getValue().toString();
                            String userPhoto = data.child("user_photo").getValue().toString();
                            String userEmail = data.child("user_email").getValue().toString();
                            String userId = data.child("user_id").getValue().toString();

                            task_user_list.add(new Users(userId, userName, userEmail, userPhoto));
                            mTaskDetailsAdapter.notifyDataSetChanged();
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                }
        );

        textViewFileName = findViewById(R.id.edit_text_file_name);

        btnAddFile = findViewById(R.id.button_add_file);
        btnAddFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ContextCompat.checkSelfPermission(TaskDetailsActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {

                    if (textViewFileName.getText().toString().equals("")) {
                        Toast.makeText(TaskDetailsActivity.this, "Please enter a name for the file...", Toast.LENGTH_SHORT).show();
                    } else {
                        selectPdf();
                    }
                } else {
                    ActivityCompat.requestPermissions(TaskDetailsActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 9);
                }
            }
        });


        fab = findViewById(R.id.floating_button_details);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                userDialog = new Dialog(TaskDetailsActivity.this);
                userDialog.setContentView(R.layout.users_dialog_box);
                //userDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                userDialog.show();

                userCancelButton = userDialog.findViewById(R.id.btn_add_person_cancel);

                mUserGridView = userDialog.findViewById(R.id.grid_view_people);
                mUserAdapter = new UserAdapter(user_list);
                final RecyclerView.LayoutManager mUserLayoutManager = new GridLayoutManager(getApplicationContext(), 6);
                mUserGridView.setLayoutManager(mUserLayoutManager);
                mUserGridView.setAdapter(mUserAdapter);


                userCancelButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        mMessagesDatabaseReference.child("users").removeValue();
                        int size = task_user_list.size();
                        //Toast.makeText(TaskDetailsActivity.this, "Size is " + task_user_list.size(), Toast.LENGTH_SHORT).show();
                        for (int i = 0; i < size; i++) {

                            //String userKey = mMessagesDatabaseReference.child("users").push().getKey();
                            Users u = new Users();

                            u.setUserName(task_user_list.get(i).getUserName());
                            u.setUserEmail(task_user_list.get(i).getUserEmail());
                            u.setUserKey(task_user_list.get(i).getUserKey());
                            u.setUserPhotoUrl(task_user_list.get(i).getUserPhotoUrl());


                            Map<String, Object> userUpdates = new HashMap<>();

                            mMessagesDatabaseReference.child("users").push().setValue(userUpdates.put(task_user_list.get(i).getUserKey(), u.toUsersFirebaseObject()));
                            mMessagesDatabaseReference.child("users").updateChildren(userUpdates, new DatabaseReference.CompletionListener() {
                                @Override
                                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                    if (databaseError == null) {

                                    }
                                }
                            });
                        }

                        userDialog.cancel();
                    }
                });

            }
        });
        FetchUserListViews();

    }

    private void FetchUserListViews() {

        mMessagesDatabaseReferenceToAllUsers.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {


                if (mFirebaseUser.getEmail().compareTo("dscvitvellore@gmail.com") != 0) {

                    String user_name = dataSnapshot.child("user_name").getValue().toString();
                    String user_email = dataSnapshot.child("user_email").getValue().toString();
                    String user_photo = dataSnapshot.child("user_photo").getValue().toString();
                    String user_id = dataSnapshot.getKey();

                    if (mFirebaseUser.getDisplayName().equals(user_name)) {
                        user_list.add(new Users(user_id, user_name, user_email, user_photo));
                    }


                } else {

                    String user_name = dataSnapshot.child("user_name").getValue().toString();
                    String user_email = dataSnapshot.child("user_email").getValue().toString();
                    String user_photo = dataSnapshot.child("user_photo").getValue().toString();
                    String user_id = dataSnapshot.getKey();

                    user_list.add(new Users(user_id, user_name, user_email, user_photo));

                }

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
//                user_list.clear();
//                if (mFirebaseUser.getEmail().compareTo("dscvitvellore@gmail.com") != 0) {
//
//                    String user_name = dataSnapshot.child("user_name").getValue().toString();
//                    String user_email = dataSnapshot.child("user_email").getValue().toString();
//                    String user_photo = dataSnapshot.child("user_photo").getValue().toString();
//                    String user_id = dataSnapshot.getKey();
//
//                    if (mFirebaseUser.getDisplayName().equals(user_name)) {
//                        user_list.add(new Users(user_id, user_name, user_email, user_photo));
//                    }
//
//
//                } else {
//
//                    String user_name = dataSnapshot.child("user_name").getValue().toString();
//                    String user_email = dataSnapshot.child("user_email").getValue().toString();
//                    String user_photo = dataSnapshot.child("user_photo").getValue().toString();
//                    String user_id = dataSnapshot.getKey();
//
//                    user_list.add(new Users(user_id, user_name, user_email, user_photo));
//
//                }
//
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
//                user_list.clear();
//                if (mFirebaseUser.getEmail().compareTo("dscvitvellore@gmail.com") != 0) {
//
//                    String user_name = dataSnapshot.child("user_name").getValue().toString();
//                    String user_email = dataSnapshot.child("user_email").getValue().toString();
//                    String user_photo = dataSnapshot.child("user_photo").getValue().toString();
//                    String user_id = dataSnapshot.getKey();
//
//                    if (mFirebaseUser.getDisplayName().equals(user_name)) {
//                        user_list.add(new Users(user_id, user_name, user_email, user_photo));
//                    }
//
//
//                } else {
//
//                    String user_name = dataSnapshot.child("user_name").getValue().toString();
//                    String user_email = dataSnapshot.child("user_email").getValue().toString();
//                    String user_photo = dataSnapshot.child("user_photo").getValue().toString();
//                    String user_id = dataSnapshot.getKey();
//
//                    user_list.add(new Users(user_id, user_name, user_email, user_photo));
//
//                }

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
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 9 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            selectPdf();
        } else {
            Toast.makeText(TaskDetailsActivity.this, "Please provide permission ...", Toast.LENGTH_SHORT).show();
        }
    }

    private void selectPdf() {
        Intent intent = new Intent();
        intent.setType("application/pdf");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, 86);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 86 && resultCode == RESULT_OK && data != null) {
            pdfUri = data.getData();
            //File file = new File(pdfUri.getPath());
            // final String fileName = file.getName();

            final String fileName;

            progressDialog = new ProgressDialog(this);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            progressDialog.setTitle("Uploading file...");
            progressDialog.setProgress(0);
            progressDialog.show();

            fileName = textViewFileName.getText().toString();


            final StorageReference storageReference = storage.getReference();

            final String url;
            storageReference.child(fileName).putFile(pdfUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                              @Override
                                              public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                                  storageReference.child(fileName).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                      @Override
                                                      public void onSuccess(Uri uri) {
                                                          mMessagesDatabaseReference.child("uploads").child(fileName).setValue(uri.toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                              @Override
                                                              public void onComplete(@NonNull Task<Void> task) {
                                                                  if (task.isSuccessful()) {
                                                                      Toast.makeText(TaskDetailsActivity.this, "File successfully uploaded", Toast.LENGTH_SHORT).show();
                                                                  } else {
                                                                      Toast.makeText(TaskDetailsActivity.this, "File not uploaded", Toast.LENGTH_SHORT).show();
                                                                      progressDialog.dismiss();
                                                                  }
                                                              }
                                                          });
                                                      }
                                                  });


                                              }
                                          }
                    ).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(TaskDetailsActivity.this, "File not uploaded", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    int currentProgress = (int) (100 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                    progressDialog.setProgress(currentProgress);
                    if (currentProgress == 100) {
                        progressDialog.dismiss();
                        textViewFileName.setText("");
                    }
                }
            });
        }

        // uploadFile(pdfUri);
        else {
            Toast.makeText(TaskDetailsActivity.this, "Please select a file", Toast.LENGTH_SHORT).show();
        }
    }

    private void uploadFile(final Uri pdfUri) {

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

            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    public class TaskDetailsAdapter extends RecyclerView.Adapter<TaskDetailsAdapter.TaskDetailsViewHolder> {
        // private ArrayList<Users> userList;

        public TaskDetailsAdapter(ArrayList<Users> ul) {
            // this.userList = ul;
            task_user_list = ul;
        }

        @NonNull
        @Override
        public TaskDetailsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_of_task_list_item, parent, false);
            return new TaskDetailsViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull TaskDetailsViewHolder holder, final int position) {
            final Users users = task_user_list.get(position);
            holder.userTaskName.setText(users.getUserName());
            Picasso.with(TaskDetailsActivity.this).load(users.getUserPhotoUrl()).transform(new com.example.dell.eventmanager.Task.TaskDetailsActivity.TaskDetailsAdapter.CircleTransform()).into(holder.userTaskPhoto);

            holder.userTaskName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    task_user_list.remove(users);
                    String userKey = users.getUserKey();
                    notifyDataSetChanged();
                    mFirebaseDatabase.getReference().child("toDoList").child(eventKey).child("tasksList").child(taskKey).child("users").child(userKey).removeValue();
                    Toast.makeText(getApplicationContext(), users.getUserName()+"   "+eventKey+"    "+taskKey, Toast.LENGTH_SHORT).show();
                }
            });

        }

        @Override
        public int getItemCount() {
            return task_user_list.size();
        }


        class TaskDetailsViewHolder extends RecyclerView.ViewHolder {

            TextView userTaskName;
            ImageView userTaskPhoto;

            public TaskDetailsViewHolder(View itemView) {
                super(itemView);

                userTaskName = itemView.findViewById(R.id.task_user_name);
                userTaskPhoto = itemView.findViewById(R.id.task_user_photo);
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

    public class FileDetailsAdapter extends RecyclerView.Adapter<FileDetailsAdapter.FileDetailsViewHolder> {
        //        private ArrayList<String> items = new ArrayList<>();
//        private ArrayList<String> urls = new ArrayList<>();
        RecyclerView recyclerView;
        Context context;

        public void update(String name, String url) {
            items.add(name);
            urls.add(url);
            notifyDataSetChanged();
        }

        public FileDetailsAdapter(RecyclerView recyclerView, Context context, ArrayList<String> items_list, ArrayList<String> urls_list) {
            this.recyclerView = recyclerView;
            this.context = context;
            items = items_list;
            urls = urls_list;
        }

        @NonNull
        @Override
        public FileDetailsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.file_list_item, parent, false);
            return new FileDetailsViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull FileDetailsViewHolder holder, final int position) {
            holder.fileName.setText(items.get(position));
//            holder.deleteIcon.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    Toast.makeText(getApplicationContext(), items.get(position), Toast.LENGTH_SHORT).show();
//                    //mFirebaseDatabase.getReference().child("toDoList").child(eventKey).child("tasksList").child(taskKey).child("uploads").child(items.get(position)).removeValue();
//                   // photoRef = storage.getReferenceFromUrl(mImagel);
////TODO
////                    recyclerView.
////                    task_user_list.remove(users);
////                    String userKey = users.getUserKey();
////                    notifyDataSetChanged();
////                    mFirebaseDatabase.getReference().child("toDoList").child(eventKey).child("tasksList").child(taskKey).child("users").child(userKey).removeValue();
//               }
//            });
        }

        @Override
        public int getItemCount() {
            return items.size();
        }


        class FileDetailsViewHolder extends RecyclerView.ViewHolder {

            TextView fileName;

            public FileDetailsViewHolder(final View itemView) {
                super(itemView);

                fileName = itemView.findViewById(R.id.file_name);

                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(final View view) {

                        fileDialog = new Dialog(TaskDetailsActivity.this);
                        fileDialog.setContentView(R.layout.file_dialog);
                        fileDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        fileDialog.show();

                        fabClose = fileDialog.findViewById(R.id.fab_close);
                        fabDelete = fileDialog.findViewById(R.id.fab_delete);
                        fabDownload = fileDialog.findViewById(R.id.fab_download);


                        fabClose.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                fileDialog.cancel();
                            }
                        });

                        fabDelete.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View file_view) {
                                int position = recyclerView.getChildLayoutPosition(view);
                                mFirebaseDatabase.getReference().child("toDoList").child(eventKey).child("tasksList").child(taskKey).child("uploads").child(items.get(position)).removeValue();

                                urls.remove(position);
                                items.remove(position);
                                notifyDataSetChanged();
                                fileDialog.cancel();

                            }
                        });
                        fabDownload.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View file_view) {
                                int position = recyclerView.getChildLayoutPosition(view);
                                Intent intent = new Intent(Intent.ACTION_VIEW).setData(Uri.parse(urls.get(position)));
                                startActivity(intent);
                                fileDialog.cancel();
                            }
                        });

                        //   Toast.makeText(TaskDetailsActivity.this, "Hello", Toast.LENGTH_SHORT).show();
//                        int position = recyclerView.getChildLayoutPosition(view);
//                        Intent intent = new Intent(Intent.ACTION_VIEW).setData(Uri.parse(urls.get(position)));
//                        startActivity(intent);
                    }
                });

            }

        }
    }

    public class UserAdapter extends RecyclerView.Adapter<TaskDetailsActivity.UserAdapter.UserAdapterViewHolder> {

        private List<Users> userList;

        public UserAdapter(List<Users> ul) {
            this.userList = ul;
        }

        @Override
        public TaskDetailsActivity.UserAdapter.UserAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_list_item, parent, false);
            return new TaskDetailsActivity.UserAdapter.UserAdapterViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull TaskDetailsActivity.UserAdapter.UserAdapterViewHolder holder, final int position) {

            Users users = userList.get(position);
            Picasso.with(TaskDetailsActivity.this).load(users.getUserPhotoUrl()).transform(new TaskDetailsActivity.UserAdapter.CircleTransform()).into(holder.userImageView);
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final Users users = user_list.get(position);
                    final String userName = users.getUserName();

                    task_user_list.add(new Users(users.getUserKey(), users.getUserName(), users.getUserEmail(), users.getUserPhotoUrl()));
                    mTaskDetailsAdapter.notifyDataSetChanged();

                    Toast.makeText(TaskDetailsActivity.this, userName, Toast.LENGTH_SHORT).show();

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