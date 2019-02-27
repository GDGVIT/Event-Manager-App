package com.example.dell.eventmanager.Task;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.dell.eventmanager.R;

import java.util.List;
/*
public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskAdapterViewHolder> {

    private List<Tasks> taskList;

    public TaskAdapter(List<Tasks> objects) {
        // super(context, resource, objects);
        this.taskList = objects;

    }

    @NonNull
    @Override
    public TaskAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View mItemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.task_list_item, parent, false);
        return new TaskAdapterViewHolder(mItemView);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskAdapterViewHolder holder, int position) {
        Tasks tasks = taskList.get(position);
        holder.taskNameTextView.setText(tasks.getTaskName());
        holder.taskDateTextView.setText(tasks.getTaskDate());

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

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(
                gezz,
                LinearLayoutManager.HORIZONTAL,
                false
        );

        mSecondaryRecyclerView.setLayoutManager(linearLayoutManager);
        mSecondaryRecyclerView.setAdapter(getSecondaryAdapter(position));
    }

    @Override
    public int getItemCount() {
        return taskList.size();
    }

    public class TaskAdapterViewHolder extends RecyclerView.ViewHolder {

        TextView taskNameTextView;
        TextView taskDateTextView;
        ImageView imageView;

        private  RecyclerView mSecondaryRecyclerView;

        public TaskAdapterViewHolder(View convertView) {
            super(convertView);

            taskNameTextView = convertView.findViewById(R.id.task_name);
            taskDateTextView = convertView.findViewById(R.id.task_date);
            imageView = convertView.findViewById(R.id.image_bookmark);

            mSecondaryRecyclerView = convertView.findViewById(R.id.secondary_reycler_view);
        }
    }

    */
/*
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = ((Activity) getContext()).getLayoutInflater().inflate(R.layout.task_list_item, parent, false);
        }

        TextView taskNameTextView = convertView.findViewById(R.id.task_name);
        TextView taskDateTextView = convertView.findViewById(R.id.task_date);
        ImageView imageView = convertView.findViewById(R.id.image_bookmark);


        Tasks tasks = getItem(position);
        taskNameTextView.setText(tasks.getTaskName());
        taskDateTextView.setText(tasks.getTaskDate());


        final Drawable mDrawable = ContextCompat.getDrawable(getContext(), R.drawable.ic_bookmark_red_24dp);
        switch (tasks.getTaskLableColor()) {
            case "Bisque":
                mDrawable.setTint(Color.rgb(255, 228, 196));
                imageView.setImageResource(R.drawable.ic_bookmark_bisque_24dp);
                break;
            case "DeepSkyBlue":
                mDrawable.setTint(Color.rgb(0, 191, 255));
                imageView.setImageResource(R.drawable.ic_bookmark_blue_24dp);
                break;
            case "Lime":
                mDrawable.setTint(Color.rgb(188, 246, 12));
                imageView.setImageResource(R.drawable.ic_bookmark_lime_24dp);
                break;
            case "Yellow":
                mDrawable.setTint(Color.rgb(255, 225, 25));
                imageView.setImageResource(R.drawable.ic_bookmark_yellow_24dp);
                break;
            case "Coral":
                mDrawable.setTint(Color.rgb(255, 127, 80));
                imageView.setImageResource(R.drawable.ic_bookmark_coral_24dp);
                break;
            case "Purple":
                mDrawable.setTint(Color.rgb(149, 125, 173));
                imageView.setImageResource(R.drawable.ic_bookmark_purple_24dp);
                break;
            case "Pink":
                mDrawable.setTint(Color.rgb(255, 105, 180));
                imageView.setImageResource(R.drawable.ic_bookmark_pink_24dp);
                break;
            case "Red":
                mDrawable.setTint(Color.rgb(255, 105, 97));
                imageView.setImageResource(R.drawable.ic_bookmark_red_24dp);
                break;
        }

       // imageView.setBackground(mDrawable);
        return convertView;
    }*/



