package com.shohayeb.newsapp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.TextAppearanceSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int MAIN_ITEM = 0;
    private static final int SIDE_ITEM = 1;
    private static final int LOADING_ITEM = 2;
    private static final int NO_DATA_FOUND = -1;
    private Context mContext;
    private List<News> newsList;
    private OnSectionClickListener sectionClickListener;

    RecyclerAdapter(Context mContext, List<News> newsList) {
        this.mContext = mContext;
        this.newsList = newsList;
        try {
            sectionClickListener = (OnSectionClickListener) mContext;
        } catch (ClassCastException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (!newsList.isEmpty()) {
            if (position == 0) {
                return MAIN_ITEM;
            } else {
                return newsList.get(position) == null ? LOADING_ITEM : SIDE_ITEM;
            }
        } else
            return NO_DATA_FOUND;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType) {
            case MAIN_ITEM:
                return new MainStoryHolder(LayoutInflater.from(mContext).inflate(R.layout.main_story_item, parent, false));
            case SIDE_ITEM:
                return new SideStoryHolder(LayoutInflater.from(mContext).inflate(R.layout.story_item, parent, false));
            case LOADING_ITEM:
                return new LoadingView(LayoutInflater.from(mContext).inflate(R.layout.loading_layout, parent, false));
            case NO_DATA_FOUND:
                return new DummyHolder(new View(mContext));
            default:
                throw new IllegalArgumentException("Unexpected view type " + viewType);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder.getItemViewType() == MAIN_ITEM) {
            setMainItem((MainStoryHolder) holder, newsList.get(position));
        } else if (holder.getItemViewType() == SIDE_ITEM) {
            setSideItem((SideStoryHolder) holder, newsList.get(position));
        }

    }

    private void setSideItem(SideStoryHolder holder, final News story) {
        holder.container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(story.getWebUrl()));
                PackageManager packageManager = mContext.getPackageManager();
                if (i.resolveActivity(packageManager) != null) {
                    mContext.startActivity(i);
                } else {
                    Toast.makeText(mContext, R.string.intent_error, Toast.LENGTH_SHORT).show();
                }
            }
        });
        holder.section.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sectionClickListener.onClick(story.getSection());
            }
        });
        String line = story.getTitle();
        if (!story.getAuthor().equals("")) {
            line += "\n" + mContext.getResources().getString(R.string.by) + " " + story.getAuthor();
        }
        SpannableString text = new SpannableString(line);
        text.setSpan(new TextAppearanceSpan(mContext, R.style.title), 0, story.getTitle().length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        holder.title.setText(text, TextView.BufferType.SPANNABLE);
//        String[] fullDateArray = story.getDate().split("T");
//        if (fullDateArray.length > 1) {
//            String date = fullDateArray[0] + "\n" + fullDateArray[1].replace("Z", "");
//            holder.date.setText(date);
//        } else {
//            holder.date.setText(story.getDate());
//        }
        holder.date.setText(parseDate(story.getDate()));
        holder.section.setText(story.getSection());
        if (story.getImageUrl().equals("")) {
            holder.imageView.setImageResource(R.drawable.no_image);
        } else {
            Picasso.get().load(story.getImageUrl()).error(R.drawable.no_image).into(holder.imageView);

        }
    }

    private void setMainItem(final MainStoryHolder holder, final News story) {
        holder.container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(story.getWebUrl()));
                PackageManager packageManager = mContext.getPackageManager();
                if (i.resolveActivity(packageManager) != null) {
                    mContext.startActivity(i);
                } else {
                    Toast.makeText(mContext, R.string.intent_error, Toast.LENGTH_SHORT).show();
                }
            }
        });
        holder.section.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sectionClickListener.onClick(story.getSection());
            }
        });
        String line = story.getTitle();
        if (!story.getAuthor().equals("")) {
            line += "\n" + mContext.getResources().getString(R.string.by) + " " + story.getAuthor();
        }
        SpannableString text = new SpannableString(line);
        text.setSpan(new TextAppearanceSpan(mContext, R.style.title_main), 0, story.getTitle().length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        holder.title.setText(text, TextView.BufferType.SPANNABLE);

        holder.date.setText(parseDate(story.getDate()));

        holder.section.setText(story.getSection());
        if (story.getImageUrl().equals("")) {
            holder.imageView.setImageResource(R.drawable.no_image);
        } else {
            Picasso.get().load(story.getImageUrl()).into(holder.imageView);
        }
    }


    private String parseDate(String date) {
        if (date.equalsIgnoreCase("")) {
            return "";
        }
        SimpleDateFormat inputDate = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        String timeAtMilliseconds = "";
        try {
            Date newDate = inputDate.parse(date);
            timeAtMilliseconds = String.valueOf(newDate.getTime());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        String result = "now";
//        @SuppressLint("SimpleDateFormat") SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//        String todayDate = formatter.format(new Date());
//        Calendar calendar = Calendar.getInstance();
//
        long dayAgoLong = Long.valueOf(timeAtMilliseconds);
//        calendar.setTimeInMillis(dayAgoLong);
//        String agoFormatter = formatter.format(calendar.getTime());
//
//        Date CurrentDate ;
//        Date CreateDate ;
        int timeZoneDifference = Calendar.getInstance().getTimeZone().getRawOffset();
        try {
//            CurrentDate = formatter.parse(todayDate);
//            CreateDate = formatter.parse(agoFormatter);
            long different = new Date().getTime() - (dayAgoLong + timeZoneDifference);


//            long different = Math.abs(CurrentDate.getTime() - CreateDate.getTime());

            long secondsInMilli = 1000;
            long minutesInMilli = secondsInMilli * 60;
            long hoursInMilli = minutesInMilli * 60;
            long daysInMilli = hoursInMilli * 24;

            long elapsedDays = different / daysInMilli;
            different = different - (elapsedDays * daysInMilli);

            long elapsedHours = different / hoursInMilli;
            different = different - (elapsedHours * hoursInMilli);

            long elapsedMinutes = different / minutesInMilli;
            different = different - (elapsedHours * hoursInMilli);

            long elapsedSeconds = different / secondsInMilli;

            if (elapsedDays == 0) {
                if (elapsedHours == 0) {
                    if (elapsedMinutes == 0) {
                        if (elapsedSeconds < 0) {
                            return "0" + " s";
                        } else {
                            return "now";

                        }
                    } else {
                        return String.valueOf(elapsedMinutes) + "m ago";
                    }
                } else {
                    return String.valueOf(elapsedHours) + "h ago";
                }

            } else {
                if (elapsedDays <= 29) {
                    return String.valueOf(elapsedDays) + "d ago";
                }
                if (elapsedDays <= 58) {
                    return "1Mth ago";
                }
                if (elapsedDays <= 87) {
                    return "2Mth ago";
                }
                if (elapsedDays <= 116) {
                    return "3Mth ago";
                }
                if (elapsedDays <= 145) {
                    return "4Mth ago";
                }
                if (elapsedDays <= 174) {
                    return "5Mth ago";
                }
                if (elapsedDays <= 203) {
                    return "6Mth ago";
                }
                if (elapsedDays <= 232) {
                    return "7Mth ago";
                }
                if (elapsedDays <= 261) {
                    return "8Mth ago";
                }
                if (elapsedDays <= 290) {
                    return "9Mth ago";
                }
                if (elapsedDays <= 319) {
                    return "10Mth ago";
                }
                if (elapsedDays <= 348) {
                    return "11Mth ago";
                }
                if (elapsedDays <= 360) {
                    return "12Mth ago";
                }

                if (elapsedDays <= 720) {
                    return "1 year ago";
                }

                @SuppressLint("SimpleDateFormat")
                SimpleDateFormat formatterYear = new SimpleDateFormat("MM/dd/yyyy");
                Calendar calendarYear = Calendar.getInstance();
                calendarYear.setTimeInMillis(dayAgoLong);
                return formatterYear.format(calendarYear.getTime()) + "";


            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }


    @Override
    public int getItemCount() {
        return newsList.size();
    }

    interface OnSectionClickListener {
        void onClick(String title);
    }

    class MainStoryHolder extends RecyclerView.ViewHolder {
        private TextView title;
        private TextView section;
        private TextView date;
        private ImageView imageView;
        private View container;

        MainStoryHolder(View view) {
            super(view);
            this.title = view.findViewById(R.id.main_title);
            this.section = view.findViewById(R.id.main_section);
            this.date = view.findViewById(R.id.main_date);
            this.imageView = view.findViewById(R.id.main_image);
            this.container = view.findViewById(R.id.main_item);
        }
    }

    class SideStoryHolder extends RecyclerView.ViewHolder {
        private TextView title;
        private TextView section;
        private TextView date;
        private ImageView imageView;
        private View container;

        SideStoryHolder(View view) {
            super(view);
            this.title = view.findViewById(R.id.title_text_view);
            this.section = view.findViewById(R.id.section_text_view);
            this.date = view.findViewById(R.id.date_text_view);
            this.imageView = view.findViewById(R.id.image_view);
            this.container = view.findViewById(R.id.side_item);
        }
    }

    class LoadingView extends RecyclerView.ViewHolder {
        View loading;

        LoadingView(View itemView) {
            super(itemView);
            this.loading = itemView.findViewById(R.id.loading_view);
        }
    }

    class DummyHolder extends RecyclerView.ViewHolder {

        DummyHolder(View itemView) {
            super(itemView);
        }
    }
}
