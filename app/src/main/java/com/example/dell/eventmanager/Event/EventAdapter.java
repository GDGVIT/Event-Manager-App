package com.example.dell.eventmanager.Event;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.dell.eventmanager.R;

import java.util.ArrayList;
import java.util.List;


//public abstract class EventAdapter extends RecyclerView.Adapter<EventAdapter.EventViewHolder> {
//
//    private ArrayList<Events> eventList;
//
//    public EventAdapter(ArrayList<Events> el) {
//        this.eventList = el;
//    }
//
//    @Override
//    public EventViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//        View itemView = LayoutInflater.from(parent.getContext())
//                .inflate(R.layout.event_list_item, parent, false);
//
//        return new EventViewHolder(itemView);
//    }
//
//    @Override
//    public void onBindViewHolder(@NonNull EventAdapter.EventViewHolder holder, int position, @NonNull List<Object> payloads) {
//
//        Events events = eventList.get(position);
//        holder.eventNameTextView.setText(events.getEventName());
//        holder.eventDateTextView.setText(events.getEventDate());
//
//    }
//
//    @Override
//    public long getItemId(int position) {
//
//        return eventList.size();
//    }
//
//    public class EventViewHolder extends RecyclerView.ViewHolder {
//
//        TextView eventNameTextView;
//        TextView eventDateTextView;
//
//        public EventViewHolder(View itemView) {
//            super(itemView);
//
//            eventNameTextView = itemView.findViewById(R.id.event_name);
//            eventDateTextView = itemView.findViewById(R.id.event_date);
//        }
//    }
//}
