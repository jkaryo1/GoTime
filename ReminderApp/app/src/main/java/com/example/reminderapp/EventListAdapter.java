package com.example.driversed;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Locale;

class LessonListAdapter extends RecyclerView.Adapter<LessonListAdapter.LessonHolder> {

    private FragmentManager fragmentManager;
    private Context context;
    private ArrayList<Lesson> lessonList;

    LessonListAdapter (ArrayList<Lesson> list, FragmentManager fm, Context c) {
        this.lessonList = list;
        this.fragmentManager = fm;
        this.context = c;
    }

    @Override
    public LessonHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_lesson, parent, false);
        return new LessonHolder(v, fragmentManager, context);
    }

    @Override
    public void onBindViewHolder(LessonHolder holder, int position) {
        Lesson l = lessonList.get(position);
        holder.bindData(l);
    }

    @Override
    public int getItemCount() {
        return lessonList.size();
    }

    class LessonHolder extends RecyclerView.ViewHolder {
        private Lesson lesson;
        ImageView typeImageView;
        ImageView weatherImageView;
        TextView dateTextView;
        TextView hoursTextView;
        private FragmentManager fragmentManager;
        private Context context;

        LessonHolder(View itemView, FragmentManager fm, Context c) {
            super(itemView);
            typeImageView = (ImageView) itemView.findViewById(R.id.lesson_type_image);
            weatherImageView = (ImageView) itemView.findViewById(R.id.lesson_weather_image);
            dateTextView = (TextView) itemView.findViewById(R.id.lesson_date);
            hoursTextView = (TextView) itemView.findViewById(R.id.lesson_hours);
            fragmentManager = fm;
            context = c;

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, ViewLessonActivity.class);
                    intent.putExtra("DATE", lesson.getDate());
                    intent.putExtra("HOURS", lesson.getHours());
                    intent.putExtra("LESSON_TYPE", lesson.getLessonType());
                    intent.putExtra("WEATHER", lesson.getWeather());
                    intent.putExtra("DAY", lesson.getDay());
                    context.startActivity(intent);
                    ((Activity) context).overridePendingTransition(R.transition.enter, R.transition.stack);
                }
            });
        }

        void bindData(Lesson l) {
            lesson = l;
            Drawable typeImage;
            Drawable weatherImage;
            String lessonType = lesson.getLessonType();
            switch (lessonType) {
                case "Highway":
                    typeImage = ResourcesCompat.getDrawable(context.getResources(), R.drawable.ic_highway, null);
                    break;
                case "Commercial":
                    typeImage = ResourcesCompat.getDrawable(context.getResources(), R.drawable.ic_commercial, null);
                    break;
                case "Residential": default:
                    typeImage = ResourcesCompat.getDrawable(context.getResources(), R.drawable.ic_residential, null);
                    break;
            }
            typeImageView.setImageDrawable(typeImage);
            String weather = lesson.getWeather();
            switch (weather) {
                case "Rainy":
                    weatherImage = ResourcesCompat.getDrawable(context.getResources(), R.drawable.ic_rainy, null);
                    break;
                case "Snowy":
                    weatherImage = ResourcesCompat.getDrawable(context.getResources(), R.drawable.ic_snowy, null);
                    break;
                case "Clear": default:
                    weatherImage = ResourcesCompat.getDrawable(context.getResources(), R.drawable.ic_clear, null);
                    break;
            }
            weatherImageView.setImageDrawable(weatherImage);
            dateTextView.setText(lesson.getDate());
            String time = String.format(Locale.getDefault(), "%1$.2f", lesson.getHours()) + " hours";
            hoursTextView.setText(time);
        }
    }
}
