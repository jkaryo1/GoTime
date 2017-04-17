package com.example.reminderapp;

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

    EventListAdapter(ArrayList<Object> list) {
        this.eventList = list;
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
                vh1.bindData((Event) eventList.get(position));
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

    class LessonHolder extends RecyclerView.ViewHolder {
        private Event event;
        TextView eventTitle;
        TextView eventLocation;
        TextView eventTime;

        LessonHolder(View itemView) {
            super(itemView);
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

        void bindData(Event e) {
            event = e;
            eventTitle.setText(e.getTitle());
//            Time time = event.getTime();
//            SimpleDateFormat format = new SimpleDateFormat("HH:mm a", Locale.getDefault());
//            String timeString = format.format(time);
            eventTime.setText(e.getTime());
            eventLocation.setText(e.getLocation());
        }
    }
    class DividerHolder extends RecyclerView.ViewHolder {

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
