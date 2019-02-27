package com.example.dell.eventmanager.User;

import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.dell.eventmanager.Event.EventsActivity;
import com.example.dell.eventmanager.R;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import java.util.List;

//public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserAdapterViewHolder> {
//
//    private List<Users> userList;
//
//    public UserAdapter(List<Users> ul) {
//        this.userList = ul;
//    }
//
//    @Override
//    public UserAdapter.UserAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_list_item, parent, false);
//        return new UserAdapter.UserAdapterViewHolder(itemView);
//    }
//
//    @Override
//    public void onBindViewHolder(@NonNull EventsActivity.EventAdapter.EventAdapterViewHolder holder, final int position) {
//
//        Users users = userList.get(position);
//        Picasso.with(getContext()).load(users.getUserPhotoUrl()).transform(new CircleTransform()).into(holder.userImageView);
//
//    }
//
//    @Override
//    public int getItemCount() {
//        return userList.size();
//    }
//
//    public class UserAdapterViewHolder extends RecyclerView.ViewHolder {
//
//        ImageView userImageView;
//
//        public UserAdapterViewHolder(View itemView) {
//            super(itemView);
//
//            userImageView = itemView.findViewById(R.id.person_image);
//        }
//    }
//
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
//}

