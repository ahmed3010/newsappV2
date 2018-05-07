package com.shohayeb.newsapp;

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
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.List;


public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int MAIN_ITEM = 0;
    private static final int SIDE_ITEM = 1;
    private Context mContext;
    private List<News> newsList;
    private OnSectionClickListner onSectionClickListner;
    private Animation animation;

    public RecyclerAdapter(Context mContext, List<News> newsList) {
        this.mContext = mContext;
        this.newsList = newsList;
        try {
            onSectionClickListner = (OnSectionClickListner) mContext;
        } catch (ClassCastException e) {
            e.printStackTrace();
        }
        animation = AnimationUtils.loadAnimation(mContext, R.anim.zoom_in);
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return MAIN_ITEM;
        } else {
            return SIDE_ITEM;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType) {
            case MAIN_ITEM:
                return new MainStoryHolder(LayoutInflater.from(mContext).inflate(R.layout.main_story_item, parent, false));
            case SIDE_ITEM:
                return new SideStoryHolder(LayoutInflater.from(mContext).inflate(R.layout.story_item, parent, false));
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
                onSectionClickListner.onClick(story.getSection());
            }
        });
        String line = story.getTitle();
        if (!story.getAuthor().equals("")) {
            line += "\n" + mContext.getResources().getString(R.string.by) + story.getAuthor();
        }
        SpannableString text = new SpannableString(line);
        text.setSpan(new TextAppearanceSpan(mContext, R.style.title), 0, story.getTitle().length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        holder.title.setText(text, TextView.BufferType.SPANNABLE);
        String[] fullDateArray = story.getDate().split("T");
        if (fullDateArray.length > 1) {
            String date = fullDateArray[0] + "\n" + fullDateArray[1].replace("Z", "");
            holder.date.setText(date);
        } else {
            holder.date.setText(story.getDate());
        }
        holder.section.setText(story.getSection());
        if (story.getImageUrl().equals("")) {
            holder.imageView.setImageResource(R.drawable.no_image);
        } else {
            Picasso.get().load(story.getImageUrl()).error(R.drawable.no_image).into(holder.imageView);
        }
    }

    private void setMainItem(MainStoryHolder holder, final News story) {
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
                onSectionClickListner.onClick(story.getSection());
            }
        });
        String line = story.getTitle();
        if (!story.getAuthor().equals("")) {
            line += "\n" + mContext.getResources().getString(R.string.by) + story.getAuthor();
        }
        SpannableString text = new SpannableString(line);
        text.setSpan(new TextAppearanceSpan(mContext, R.style.title_main), 0, story.getTitle().length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        holder.title.setText(text, TextView.BufferType.SPANNABLE);
        String[] fullDateArray = story.getDate().split("T");
        if (fullDateArray.length > 1) {
            String date = fullDateArray[0] + "\n" + fullDateArray[1].replace("Z", "");
            holder.date.setText(date);
        } else {
            holder.date.setText(story.getDate());
        }
        holder.section.setText(story.getSection());
        if (story.getImageUrl().equals("")) {
            holder.imageView.setImageResource(R.drawable.no_image);
        } else {
            Picasso.get().load(story.getImageUrl()).error(R.drawable.no_image).into(holder.imageView);
            animation.setRepeatCount(Animation.INFINITE);
            holder.imageView.setAnimation(animation);
        }
    }

    @Override
    public int getItemCount() {
        return newsList.size();
    }

    interface OnSectionClickListner {
        void onClick(String title);
    }

    class MainStoryHolder extends RecyclerView.ViewHolder {
        private TextView title;
        private TextView section;
        private TextView date;
        private ImageView imageView;
        private View container;

        public MainStoryHolder(View view) {
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

        public SideStoryHolder(View view) {
            super(view);
            this.title = view.findViewById(R.id.title_text_view);
            this.section = view.findViewById(R.id.section_text_view);
            this.date = view.findViewById(R.id.date_text_view);
            this.imageView = view.findViewById(R.id.image_view);
            this.container = view.findViewById(R.id.side_item);
        }
    }
}
