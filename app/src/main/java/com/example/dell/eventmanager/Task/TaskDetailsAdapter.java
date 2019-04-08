//package com.example.dell.eventmanager.Task;
//
//import android.content.Context;
//import android.content.Intent;
//import android.graphics.Bitmap;
//import android.graphics.BitmapShader;
//import android.graphics.Canvas;
//import android.graphics.Paint;
//import android.support.annotation.NonNull;
//import android.support.v7.widget.RecyclerView;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.ImageView;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import com.example.dell.eventmanager.Event.Events;
//import com.example.dell.eventmanager.Event.EventsActivity;
//import com.example.dell.eventmanager.R;
//import com.example.dell.eventmanager.User.Users;
//import com.squareup.picasso.Picasso;
//import com.squareup.picasso.Transformation;
//
//import java.util.ArrayList;
//
//public class TaskDetailsAdapter extends RecyclerView.Adapter<TaskDetailsAdapter.TaskDetailsViewHolder> {
//
//    private ArrayList<Users> task_user_list;
//    private Context context;
//
//    public TaskDetailsAdapter(ArrayList<Users> userList) {
//        this.task_user_list = userList;
//        //this.context = context;
//    }
//
//    @NonNull
//    @Override
//    public TaskDetailsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//
//        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_of_task_list_item, parent, false);
//        return new TaskDetailsViewHolder(view);
//    }
//
//    @Override
//    public void onBindViewHolder(@NonNull TaskDetailsViewHolder holder, final int position) {
//        final Users users = task_user_list.get(position);
//        holder.userTaskName.setText(users.getUserName());
//      //  Picasso.with(context).load(users.getUserPhotoUrl()).transform(new com.example.dell.eventmanager.Task.TaskDetailsAdapter.CircleTransform()).into(holder.userTaskPhoto);
//
//    }
//
//    @Override
//    public int getItemCount() {
//        return task_user_list.size();
//    }
//
//
//    class TaskDetailsViewHolder extends RecyclerView.ViewHolder {
//
//        TextView userTaskName;
//        ImageView userTaskPhoto;
//
//        public TaskDetailsViewHolder(View itemView) {
//            super(itemView);
//
//            userTaskName = itemView.findViewById(R.id.task_user_name);
//            userTaskPhoto = itemView.findViewById(R.id.task_user_photo);
//        }
//
//    }
//
//    public class CircleTransform implements Transformation {
//        @Override
//        public Bitmap transform(Bitmap source) {
//            int size = Math.min(source.getWidth(), source.getHeight());
//
//            int x = (source.getWidth() - size) / 2;
//            int y = (source.getHeight() - size) / 2;
//
//            Bitmap squaredBitmap = Bitmap.createBitmap(source, x, y, size, size);
//            if (squaredBitmap != source) {
//                source.recycle();
//            }
//
//            Bitmap bitmap = Bitmap.createBitmap(size, size, source.getConfig());
//
//            Canvas canvas = new Canvas(bitmap);
//            Paint paint = new Paint();
//            BitmapShader shader = new BitmapShader(squaredBitmap,
//                    BitmapShader.TileMode.CLAMP, BitmapShader.TileMode.CLAMP);
//            paint.setShader(shader);
//            paint.setAntiAlias(true);
//
//            float r = size / 2f;
//            canvas.drawCircle(r, r, r, paint);
//
//            squaredBitmap.recycle();
//            return bitmap;
//        }
//
//        @Override
//        public String key() {
//            return "circle";
//        }
//    }
//
//}
//
