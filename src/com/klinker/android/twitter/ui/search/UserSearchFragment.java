package com.klinker.android.twitter.ui.search;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.klinker.android.twitter.R;
import com.klinker.android.twitter.adapters.ArrayListLoader;
import com.klinker.android.twitter.adapters.PeopleArrayAdapter;
import com.klinker.android.twitter.adapters.TimelineArrayAdapter;
import com.klinker.android.twitter.data.App;
import com.klinker.android.twitter.manipulations.widgets.swipe_refresh_layout.FullScreenSwipeRefreshLayout;
import com.klinker.android.twitter.manipulations.widgets.swipe_refresh_layout.SwipeProgressBar;
import com.klinker.android.twitter.settings.AppSettings;
import com.klinker.android.twitter.utils.Utils;

import org.lucasr.smoothie.AsyncListView;
import org.lucasr.smoothie.ItemManager;

import java.util.ArrayList;

import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.ResponseList;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.User;
import uk.co.senab.bitmapcache.BitmapLruCache;

/**
 * Created by luke on 4/28/14.
 */
public class UserSearchFragment extends Fragment {

    private AsyncListView listView;
    private LinearLayout spinner;

    private Context context;
    private AppSettings settings;

    private boolean translucent;

    public String searchQuery;

    private FullScreenSwipeRefreshLayout mPullToRefreshLayout;

    public UserSearchFragment(String query, boolean translucent) {
        this.translucent = translucent;
        this.searchQuery = query.replaceAll("@", "");
    }

    public UserSearchFragment() {
        this.translucent = false;
        this.searchQuery = "";
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        context = activity;
    }

    public View layout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        settings = AppSettings.getInstance(context);

        inflater = LayoutInflater.from(context);
        layout = inflater.inflate(R.layout.ptr_list_layout, null);

        mPullToRefreshLayout = (FullScreenSwipeRefreshLayout) layout.findViewById(R.id.swipe_refresh_layout);
        mPullToRefreshLayout.setFullScreen(true);
        mPullToRefreshLayout.setOnRefreshListener(new FullScreenSwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                onRefreshStarted();
            }
        });
        if (settings.addonTheme) {
            mPullToRefreshLayout.setColorScheme(settings.accentInt,
                    SwipeProgressBar.COLOR2,
                    settings.accentInt,
                    SwipeProgressBar.COLOR3);
        } else {
            if (settings.theme != AppSettings.THEME_LIGHT) {
                mPullToRefreshLayout.setColorScheme(context.getResources().getColor(R.color.app_color),
                        SwipeProgressBar.COLOR2,
                        context.getResources().getColor(R.color.app_color),
                        SwipeProgressBar.COLOR3);
            } else {
                mPullToRefreshLayout.setColorScheme(context.getResources().getColor(R.color.app_color),
                        getResources().getColor(R.color.light_ptr_1),
                        context.getResources().getColor(R.color.app_color),
                        getResources().getColor(R.color.light_ptr_2));
            }
        }

        listView = (AsyncListView) layout.findViewById(R.id.listView);

        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {

            }

            @Override
            public void onScroll(AbsListView absListView, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                final int lastItem = firstVisibleItem + visibleItemCount;

                if(lastItem == totalItemCount && canRefresh) {
                    getMoreUsers(searchQuery.replace("@", ""));
                }
            }
        });

        /*View viewHeader = inflater.inflate(R.layout.ab_header, null);
        listView.addHeaderView(viewHeader, null, false);

        View footer = new View(context);
        footer.setOnClickListener(null);
        footer.setOnLongClickListener(null);
        ListView.LayoutParams params = new ListView.LayoutParams(ListView.LayoutParams.MATCH_PARENT, toDP(5));
        footer.setLayoutParams(params);
        listView.addFooterView(footer);
        listView.setFooterDividersEnabled(false);*/

        if (translucent) {
            if (Utils.hasNavBar(context)) {
                View footer = new View(context);
                footer.setOnClickListener(null);
                footer.setOnLongClickListener(null);
                ListView.LayoutParams params = new ListView.LayoutParams(ListView.LayoutParams.MATCH_PARENT, Utils.getNavBarHeight(context));
                footer.setLayoutParams(params);
                listView.addFooterView(footer);
                listView.setFooterDividersEnabled(false);
            }

            /*View view = new View(context);
            view.setOnClickListener(null);
            view.setOnLongClickListener(null);
            ListView.LayoutParams params2 = new ListView.LayoutParams(ListView.LayoutParams.MATCH_PARENT, Utils.getStatusBarHeight(context));
            view.setLayoutParams(params2);
            listView.addHeaderView(view);
            listView.setHeaderDividersEnabled(false);*/
        }

        spinner = (LinearLayout) layout.findViewById(R.id.list_progress);
        spinner.setVisibility(View.GONE);

        if (searchQuery != null && !searchQuery.equals("") && !searchQuery.contains("@")) {
            BitmapLruCache cache = App.getInstance(context).getBitmapCache();
            ArrayListLoader loader = new ArrayListLoader(cache, context);

            ItemManager.Builder builder = new ItemManager.Builder(loader);
            builder.setPreloadItemsEnabled(true).setPreloadItemsCount(50);
            builder.setThreadPoolSize(4);

            listView.setItemManager(builder.build());
        }

        doUserSearch(searchQuery);

        return layout;
    }

    public void onRefreshStarted() {
        /*new Thread(new Runnable() {
            @Override
            public void run() {
                final long topId;
                if (tweets.size() > 0) {
                    topId = tweets.get(0).getId();
                } else {
                    topId = 0;
                }

                try {
                    Twitter twitter = Utils.getTwitter(context, settings);
                    query = new Query(searchQuery);
                    QueryResult result = twitter.search(query);

                    tweets.clear();

                    for (twitter4j.Status status : result.getTweets()) {
                        tweets.add(status);
                    }

                    if (result.hasNext()) {
                        query = result.nextQuery();
                        hasMore = true;
                    } else {
                        hasMore = false;
                    }

                    ((Activity)context).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            int top = 0;
                            for (int i = 0; i < tweets.size(); i++) {
                                if (tweets.get(i).getId() == topId) {
                                    top = i;
                                    break;
                                }
                            }

                            adapter = new TimelineArrayAdapter(context, tweets);
                            listView.setAdapter(adapter);
                            listView.setVisibility(View.VISIBLE);
                            listView.setSelection(top);

                            spinner.setVisibility(View.GONE);

                            mPullToRefreshLayout.setRefreshing(false);
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                    ((Activity)context).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            spinner.setVisibility(View.GONE);
                            mPullToRefreshLayout.setRefreshing(false);
                        }
                    });
                }
            }
        }).start();*/
    }

    public ArrayList<User> users;
    public int userPage = 1;
    public PeopleArrayAdapter peopleAdapter;

    public void doUserSearch(final String mQuery) {
        listView.setVisibility(View.GONE);
        spinner.setVisibility(View.VISIBLE);
        hasMore = true;

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Twitter twitter = Utils.getTwitter(context, settings);
                    ResponseList<User> result = twitter.searchUsers(mQuery, userPage);

                    userPage++;

                    if (result.size() < 18) {
                        hasMore = false;
                    }

                    users = new ArrayList<User>();

                    for (User u : result) {
                        users.add(u);
                    }

                    ((Activity)context).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            peopleAdapter = new PeopleArrayAdapter(context, users);
                            listView.setAdapter(peopleAdapter);
                            listView.setVisibility(View.VISIBLE);

                            spinner.setVisibility(View.GONE);
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();

                    ((Activity)context).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            spinner.setVisibility(View.GONE);
                        }
                    });
                    hasMore = false;
                }
            }
        }).start();
    }

    public int toDP(int px) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, px, getResources().getDisplayMetrics());
    }

    public boolean canRefresh = true;
    public boolean hasMore;

    public void getMoreUsers(final String mQuery) {
        if (hasMore) {
            canRefresh = false;
            mPullToRefreshLayout.setRefreshing(true);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Twitter twitter = Utils.getTwitter(context, settings);
                        ResponseList<User> result = twitter.searchUsers(mQuery, userPage);

                        userPage++;

                        for (User u : result) {
                            users.add(u);
                        }

                        ((Activity)context).runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (peopleAdapter != null) {
                                    peopleAdapter.notifyDataSetChanged();
                                }
                                mPullToRefreshLayout.setRefreshing(false);
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();

                        ((Activity)context).runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mPullToRefreshLayout.setRefreshing(false);
                            }
                        });
                        hasMore = false;
                    }
                }
            }).start();
        }
    }
}