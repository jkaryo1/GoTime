package com.example.reminderapp;

import android.content.Context;
import android.graphics.PorterDuff;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

class EventListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<Object> eventList;
    private final int EVENT = 0, DIVIDER = 1;
    private Context context;
    private int colors[];

    EventListAdapter(ArrayList<Object> list, Context c) {
        this.eventList = list;
        this.colors = new int[this.eventList.size()];
        this.context = c;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

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

        LessonHolder(View itemView) {
            super(itemView);
            cardView = itemView;
            eventTitle = (TextView) itemView.findViewById(R.id.event_title);
            eventLocation = (TextView) itemView.findViewById(R.id.event_location);
            eventTime = (TextView) itemView.findViewById(R.id.event_time);

//            itemView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Intent intent = new Intent(this, ViewEventActivity.class);
//                    intent.putExtra("TITLE", event.getTitle());
//                    intent.putExtra("DATE", event.getDate());
//                    intent.putExtra("TIME", event.getTime());
//                    intent.putExtra("PREP_TIME", event.getPrepTime());
//                    intent.putExtra("TRANSPORT", event.getTransport());
//                    intent.putExtra("LOCATION", event.getLocation());
//                    context.startActivity(intent);
//                    ((Activity) context).overridePendingTransition(R.transition.enter, R.transition.stack);
//                }
//            });
        }

        void bindData(int position) {
            event = (Event) eventList.get(position);
            eventTitle.setText(event.getTitle());
//            Time time = event.getTime();
//            SimpleDateFormat format = new SimpleDateFormat("HH:mm a", Locale.getDefault());
//            String timeString = format.format(time);
            eventTime.setText(event.getTime());
            eventLocation.setText(event.getLocation());
            if (colors[position] == 0) {
                if (position == 1) {
                    colors[position] = 1;
                } else if (colors[position - 1] == 0) {
                    colors[position] = (colors[position - 2] % 2) + 1;
                } else {
                    colors[position] = colors[position - 1];
                }
            }
            if (colors[position] == 1) {
                cardView.getBackground().setColorFilter(ContextCompat.getColor(context, R.color.darkerCard), PorterDuff.Mode.SRC_IN);
            } else {
                cardView.getBackground().setColorFilter(ContextCompat.getColor(context, R.color.lighterCard), PorterDuff.Mode.SRC_IN);
            }
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
            Log.d("TAG", day);
//            Time time = event.getTime();
//            SimpleDateFormat format = new SimpleDateFormat("HH:mm a", Locale.getDefault());
//            String timeString = format.format(time);
        }
    }

}
