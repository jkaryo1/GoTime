package com.example.reminderapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import java.util.ArrayList;

class EventListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<Object> eventList;
    private final int EVENT = 0, DIVIDER = 1;
    private Context context;
    private int colors[];

    private static final String ID = "id";
    private static final String TITLE = "title";
    private static final String DATE = "date";
    private static final String PREP_TIME = "prep_time";
    private static final String TRANSPORT = "transport";
    private static final String LOCATION = "location";
    private static final String PLACE_ID = "place_id";
    private static final String GCAL_ID = "gcal_id";

    // Set array and context, and initialize colors array
    EventListAdapter(ArrayList<Object> list, Context c) {
        this.eventList = list;
        this.colors = new int[this.eventList.size()];
        this.context = c;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        //ViewHolder depends on if card or new date
        switch (viewType) {
            case EVENT:
                View v1 = inflater.inflate(R.layout.single_event, parent, false);
                viewHolder = new LessonHolder(v1);
                break;
            case DIVIDER: default:
                View v2 = inflater.inflate(R.layout.day_divider, parent, false);
                viewHolder = new DividerHolder(v2);
                break;
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        //Bind data specific to correct ViewHolder
        switch (holder.getItemViewType()) {
            case EVENT:
                LessonHolder vh1 = (LessonHolder) holder;
                vh1.bindData(position);
                break;
            case DIVIDER: default:
                DividerHolder vh2 = (DividerHolder) holder;
                vh2.bindData((String) eventList.get(position));
                break;
        }
    }

    @Override
    public int getItemCount() {
        return eventList.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (eventList.get(position) instanceof Event) {
            return EVENT;
        } else if (eventList.get(position) instanceof String) {
            return DIVIDER;
        }
        return -1;
    }

    private class LessonHolder extends RecyclerView.ViewHolder {
        private Event event;
        TextView eventTitle;
        TextView eventLocation;
        TextView eventTime;
        View cardView;

        // Initialize card views
        LessonHolder(View itemView) {
            super(itemView);
            cardView = itemView;
            eventTitle = (TextView) itemView.findViewById(R.id.event_title);
            eventLocation = (TextView) itemView.findViewById(R.id.event_location);
            eventTime = (TextView) itemView.findViewById(R.id.event_time);
            cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, EventActivity.class);
                    intent.putExtra("EXISTING_EVENT", true);
                    intent.putExtra(ID, event.id);
                    intent.putExtra(TITLE, event.title);
                    intent.putExtra(DATE, event.date.getTimeInMillis());
                    intent.putExtra(PREP_TIME, event.prepTime);
                    intent.putExtra(TRANSPORT, event.transport);
                    intent.putExtra(LOCATION, event.location);
                    intent.putExtra(PLACE_ID, event.placeID);
                    intent.putExtra(GCAL_ID, event.gcalID);
                    context.startActivity(intent);
                    ((Activity) context).overridePendingTransition(R.transition.enter, R.transition.stack);
                }
            });
        }

        //Set values of views, also create event associated with each card
        //and populate colors array to ensure alternating colors by day
        void bindData(int position) {
            event = (Event) eventList.get(position);
            eventTitle.setText(event.title);
            eventTime.setText(event.getTime());
            eventLocation.setText(event.location);

            cardView.getBackground().setColorFilter(ContextCompat.getColor(context, R.color.colorEventCard), PorterDuff.Mode.SRC_IN);

        }
    }
    private class DividerHolder extends RecyclerView.ViewHolder {

        TextView newDay;

        DividerHolder(View itemView) {
            super(itemView);
            newDay = (TextView) itemView.findViewById(R.id.day_division);
        }

        void bindData(String day) {
            newDay.setText(day);
        }
    }

}
