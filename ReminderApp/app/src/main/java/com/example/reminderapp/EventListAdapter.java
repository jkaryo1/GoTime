package com.example.reminderapp;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

class EventListAdapter extends RecyclerView.Adapter<EventListAdapter.LessonHolder> {

    private ArrayList<Event> eventList;

    EventListAdapter(ArrayList<Event> list) {
        this.eventList = list;
    }

    @Override
    public LessonHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_event, parent, false);
        return new LessonHolder(v);
    }

    @Override
    public void onBindViewHolder(LessonHolder holder, int position) {
        Event e = eventList.get(position);
        holder.bindData(e);
    }

    @Override
    public int getItemCount() {
        return eventList.size();
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
}
