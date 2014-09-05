package com.klinker.android.twitter_l.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import android.widget.TextView;
import com.klinker.android.twitter_l.R;
import com.klinker.android.twitter_l.ui.drawer_activities.DrawerActivity;
import com.klinker.android.twitter_l.manipulations.widgets.HoloTextView;

import java.util.ArrayList;

public class MainDrawerArrayAdapter extends ArrayAdapter<String> {
    private final Activity context;
    private final ArrayList<String> text;
    public SharedPreferences sharedPrefs;
    public static int current = 0;
    public int textSize;

    static class ViewHolder {
        public TextView name;
        public ImageView icon;
    }

    public static String[] getItems(Context context1) {
        String[] items = new String[] {
                context1.getResources().getString(R.string.timeline),
                context1.getResources().getString(R.string.mentions),
                context1.getResources().getString(R.string.direct_messages),
                context1.getResources().getString(R.string.discover),
                context1.getResources().getString(R.string.lists),
                context1.getResources().getString(R.string.favorite_users),
                context1.getResources().getString(R.string.retweets),
                context1.getResources().getString(R.string.favorite_tweets),
                context1.getResources().getString(R.string.saved_searches) };

        return items;
    }

    public MainDrawerArrayAdapter(Context context, ArrayList<String> text) {
        super(context, 0);
        this.context = (Activity) context;
        this.text = text;
        this.sharedPrefs = context.getSharedPreferences("com.klinker.android.twitter_world_preferences",
                Context.MODE_WORLD_READABLE + Context.MODE_WORLD_WRITEABLE);
    }

    @Override
    public int getCount() {
        return text.size();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View rowView = convertView;

        String settingName = text.get(position);

        if (rowView == null) {
            LayoutInflater inflater = context.getLayoutInflater();
            rowView = inflater.inflate(R.layout.drawer_list_item, null);

            ViewHolder viewHolder = new ViewHolder();

            viewHolder.name = (TextView) rowView.findViewById(R.id.title);
            viewHolder.icon = (ImageView) rowView.findViewById(R.id.icon);

            rowView.setTag(viewHolder);
        }

        ViewHolder holder = (ViewHolder) rowView.getTag();

        holder.name.setText(settingName);

        try {
            if (text.get(position).equals(context.getResources().getString(R.string.timeline))) {
                TypedArray a = context.getTheme().obtainStyledAttributes(new int[]{R.attr.timelineItem});
                int resource = a.getResourceId(0, 0);
                a.recycle();
                holder.icon.setImageResource(resource);
            } else if (text.get(position).equals(context.getResources().getString(R.string.mentions))) {
                TypedArray a = context.getTheme().obtainStyledAttributes(new int[]{R.attr.mentionItem});
                int resource = a.getResourceId(0, 0);
                a.recycle();
                holder.icon.setImageResource(resource);
            } else if (text.get(position).equals(context.getResources().getString(R.string.direct_messages))) {
                TypedArray a = context.getTheme().obtainStyledAttributes(new int[]{R.attr.directMessageItem});
                int resource = a.getResourceId(0, 0);
                a.recycle();
                holder.icon.setImageResource(resource);
            } else if (text.get(position).equals(context.getResources().getString(R.string.retweets))) {
                TypedArray a = context.getTheme().obtainStyledAttributes(new int[]{R.attr.retweetButton});
                int resource = a.getResourceId(0, 0);
                a.recycle();
                holder.icon.setImageResource(resource);
            } else if (text.get(position).equals(context.getResources().getString(R.string.favorite_tweets))) {
                TypedArray a = context.getTheme().obtainStyledAttributes(new int[]{R.attr.favoritedButton});
                int resource = a.getResourceId(0, 0);
                a.recycle();
                holder.icon.setImageResource(resource);
            } else if (text.get(position).equals(context.getResources().getString(R.string.favorite_users))) {
                TypedArray a = context.getTheme().obtainStyledAttributes(new int[]{R.attr.favUser});
                int resource = a.getResourceId(0, 0);
                a.recycle();
                holder.icon.setImageResource(resource);
            } else if (text.get(position).equals(context.getResources().getString(R.string.discover))) {
                TypedArray a = context.getTheme().obtainStyledAttributes(new int[]{R.attr.links});
                int resource = a.getResourceId(0, 0);
                a.recycle();
                holder.icon.setImageResource(resource);
            } else if (text.get(position).equals(context.getResources().getString(R.string.search))) {
                TypedArray a = context.getTheme().obtainStyledAttributes(new int[]{R.attr.searchIcon});
                int resource = a.getResourceId(0, 0);
                a.recycle();
                holder.icon.setImageResource(resource);
            } else if (text.get(position).equals(context.getResources().getString(R.string.lists))) {
                TypedArray a = context.getTheme().obtainStyledAttributes(new int[]{R.attr.listIcon});
                int resource = a.getResourceId(0, 0);
                a.recycle();
                holder.icon.setImageResource(resource);
            } else if (text.get(position).equals(context.getResources().getString(R.string.saved_searches))) {
                TypedArray a = context.getTheme().obtainStyledAttributes(new int[]{R.attr.searchIcon});
                int resource = a.getResourceId(0, 0);
                a.recycle();
                holder.icon.setImageResource(resource);
            }
        } catch (OutOfMemoryError e) {

        }

        if (current == position) {
            holder.icon.setColorFilter(DrawerActivity.settings.themeColors.accentColor);
            holder.name.setTextColor(DrawerActivity.settings.themeColors.accentColor);
        } else {
            TypedArray a = context.getTheme().obtainStyledAttributes(new int[]{R.attr.textColor});
            int resource = a.getResourceId(0, 0);

            holder.icon.setColorFilter(context.getResources().getColor(resource));
            holder.name.setTextColor(context.getResources().getColor(resource));
        }

        return rowView;
    }
}