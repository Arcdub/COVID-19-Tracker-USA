package arcsky.steph.covidtracker;

/*
 * Created by @Arcdub Github - 4/14/2020 - https://github.com/Arcdub
 */

/*
 * Dedicated to the Glory of God.
 * https://www.gotquestions.org/Jesus-died-for-our-sins.html
 */

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.getkeepsafe.taptargetview.TapTarget;
import com.getkeepsafe.taptargetview.TapTargetSequence;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<List<JSONData>> {

    private static final String LOG_TAG = MainActivity.class.getName();

    static final String STATE_USER = "user";
    private String mUser;

    private ConnectivityManager mConnectivityManager;
    private JSONCasesAdapter mAdapter;
    private ListView mCasesDataListView;
    //    private TextView mDataFoundTextView;
    private TextView mNoDataFoundTextView;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private ProgressBar mProgressBar;
    private ProgressBar mHintDummyView;
    private TextView mCredits;
    private Drawable swipeScreenIcon;
    private Drawable tapScreenIcon;
    Resources res;
    TapTargetSequence tapTargetSequenceDetails = new TapTargetSequence(this);

    private static final String REQUEST_URL =
            "https://covidtracking.com/api/us";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            mUser = savedInstanceState.getString(STATE_USER);
        } else {
            mUser = "NewUser";
        }
        setContentView(R.layout.activity_main);

        res = this.getResources();
        mProgressBar = findViewById(R.id.loading_spinner);
        mHintDummyView = findViewById(R.id.hint_dummy_view);
        mCasesDataListView = findViewById(R.id.list);
        mNoDataFoundTextView = findViewById(R.id.noDataFoundText);
        mCredits = findViewById(R.id.credits);
        swipeScreenIcon = res.getDrawable(R.drawable.ic_gesture_swipe_down_white_36dp);
        tapScreenIcon = res.getDrawable(R.drawable.ic_gesture_tap_white_36dp);

        // Open the COVID Tracking Project website on click
        mCredits.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = "https://covidtracking.com/";
                Intent privacyPolicyIntent = new Intent(Intent.ACTION_VIEW);
                privacyPolicyIntent.setData(Uri.parse(url));
                if (privacyPolicyIntent.resolveActivity(getPackageManager()) != null) {
                    startActivity(privacyPolicyIntent);
                }
            }
        });

        // Instantiate and set the length of an alpha animation (from invisible to visible).
        final Animation in = new AlphaAnimation(0.0f, 1.0f);
        in.setDuration(500);
        // Instantiate and set the length of an alpha animation (from visible to invisible).
        final Animation out = new AlphaAnimation(1.0f, 0.0f);
        out.setDuration(500);

        // Open the COVID Tracking Project US daily data page on click
        mCasesDataListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String url = "https://covidtracking.com/data/us-daily";
                Intent privacyPolicyIntent = new Intent(Intent.ACTION_VIEW);
                privacyPolicyIntent.setData(Uri.parse(url));
                if (privacyPolicyIntent.resolveActivity(getPackageManager()) != null) {
                    startActivity(privacyPolicyIntent);
                }
            }
        });

        mCasesDataListView.startAnimation(out);
        // Handler to delay textView's fade-in effect.
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mCasesDataListView.startAnimation(in);
            }
        }, 200);

        // Find a reference to the {@link SwipeRefreshLayout} in the layout
        mSwipeRefreshLayout = findViewById(R.id.swipeRefresh);

        // Set the TextView instantiated above onto the ListView as an EmptyView to be used if no
        // earthquakes are found.
        mCasesDataListView.setEmptyView(mNoDataFoundTextView);

        // Listen for scroll events from the user and take according action in the code below.
        mCasesDataListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount) {

                // If the ListView item/child at the given index of 0 is not null (active/visible),
                // then proceed to enable the swipe-to-refresh function and icon.
                // Otherwise don't activate.
                if (mCasesDataListView.getChildAt(0) != null) {

                    mSwipeRefreshLayout.setEnabled(mCasesDataListView.getFirstVisiblePosition()
                            == 0 && mCasesDataListView.getChildAt(0).getTop() == 0);
                }
            }
        });

        findCasesData();

        mAdapter = new JSONCasesAdapter(this, new ArrayList<JSONData>());

        mSwipeRefreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        Log.i(LOG_TAG, "TEST: onRefresh called from SwipeRefreshLayout");

                        mCasesDataListView.startAnimation(out);
                        mCasesDataListView.startAnimation(in);

                        findCasesData();
                        mAdapter.notifyDataSetChanged();
                    }
                }
        );

        mCasesDataListView.setAdapter(mAdapter);

        // Run code contained within only on a fresh install with no previous data/cache.
        final String TUTORIAL_PREFS_1 = "PhotoTutorialPref1";
        SharedPreferences tutorialPrefs = getSharedPreferences(TUTORIAL_PREFS_1, MODE_PRIVATE);
        SharedPreferences.Editor tutorialPrefsEditor1 =
                getSharedPreferences(TUTORIAL_PREFS_1, MODE_PRIVATE).edit();

        // One-time, first app-launch code is run here.
        if (!tutorialPrefs.getBoolean("firstRun", false)) {
            tutorialPrefsEditor1.putBoolean("firstRun", true);
            tutorialPrefsEditor1.apply();

            tapTargetSeqDetails();
            tapTargetSequenceDetails.start();
        }
    }

    public void tapTargetSeqDetails() {

        tapTargetSequenceDetails.targets(TapTarget.forView(mHintDummyView,
                getString(R.string.hint_refresh_title),
                getString(R.string.hint_refresh_description))
                        .icon(swipeScreenIcon)
                        .outerCircleColor(R.color.grey_850)
                        .outerCircleAlpha(0.96f)
                        .targetCircleColor(R.color.pure_white)
                        .titleTextSize(20)
                        .titleTextColor(R.color.pure_white)
                        .descriptionTextSize(16)
                        .descriptionTextColor(R.color.less_white)
                        .dimColor(android.R.color.black)
                        .drawShadow(true)
                        .cancelable(true)
                        .targetRadius(32)
                        .id(1),
                TapTarget.forView(mProgressBar,
                        getString(R.string.hint_see_more_data_title),
                        getString(R.string.hint_see_more_data_description))
                        .icon(tapScreenIcon)
                        .outerCircleColor(R.color.grey_850)
                        .outerCircleAlpha(0.96f)
                        .targetCircleColor(R.color.pure_white)
                        .titleTextSize(20)
                        .titleTextColor(R.color.pure_white)
                        .descriptionTextSize(16)
                        .descriptionTextColor(R.color.less_white)
                        .dimColor(android.R.color.black)
                        .drawShadow(true)
                        .cancelable(true)
                        .targetRadius(32)
                        .id(2));
        tapTargetSequenceDetails.considerOuterCircleCanceled(true);
        tapTargetSequenceDetails.continueOnCancel(true);
    }

    @Override
    public Loader<List<JSONData>> onCreateLoader(int i, Bundle bundle) {
        Log.i(LOG_TAG, "TEST: onCreateLoader() called ...");
        // Create a loader for the URL
        return new CasesDataLoader(this, REQUEST_URL);
    }

    @Override
    public void onLoadFinished(Loader<List<JSONData>> loader, List<JSONData> JSONData) {
        Log.i(LOG_TAG, "TEST: onLoadFinished() called ...");

        mAdapter.clear();

        // Hide the ProgressBar once loading has finished.
        mProgressBar.setVisibility(View.INVISIBLE);

        if (JSONData != null && !JSONData.isEmpty()) {
            mAdapter.addAll(JSONData);
        } else {
            mNoDataFoundTextView.setVisibility(View.VISIBLE);
            mNoDataFoundTextView.setText(R.string.no_data_found);
        }
    }

    @Override
    public void onLoaderReset(Loader<List<JSONData>> loader) {
        Log.i(LOG_TAG, "TEST: onLoaderReset() called ...");
        // Loader reset, so we can clear out our existing data.
        mAdapter.clear();
    }

    public void findCasesData() {

        // Empty State text may not be shown, so make the TextView gone.
        mNoDataFoundTextView.setVisibility(View.GONE);

        // Hide the ListView as the ProgressBar needs to be seen(VISIBLE).
        mCasesDataListView.setVisibility(View.GONE);

        mConnectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        // Get details on the currently active default data network.
        NetworkInfo activeNetwork = mConnectivityManager.getActiveNetworkInfo();

        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();

        if (isConnected) {

            LoaderManager loaderManager = getLoaderManager();

            Log.i(LOG_TAG, "TEST: calling initLoader() ...");

            loaderManager.initLoader(1, null, this);
        } else {

            // Hide the ProgressBar since there is no internet connection and therefore no
            // loading of data to be done.
            mProgressBar.setVisibility(View.INVISIBLE);
            // Empty State text is going to be shown, so make the TextView visible.
            mNoDataFoundTextView.setVisibility(View.VISIBLE);
            // Update empty state text to display "No internet connection." error message.
            mNoDataFoundTextView.setText(R.string.no_internet_connection);
        }

        // Wait 2 seconds/2000ms and then hide the refresh icon
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mSwipeRefreshLayout.setColorSchemeResources(android.R.color.holo_red_dark);
                mSwipeRefreshLayout.setRefreshing(false); // Disables the refresh icon
            }
        }, 2000);
    }

    // Menu icons are inflated just as they were with the Actionbar.
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        // Handle presses on the action bar items
        int itemId = item.getItemId();
        if (itemId == R.id.hint_reshow_tutorial) {
            tapTargetSequenceDetails = null;
            tapTargetSequenceDetails = new TapTargetSequence(MainActivity.this);
            tapTargetSeqDetails();
            tapTargetSequenceDetails.start();
        } else {
            return super.onOptionsItemSelected(item);
        }
        return true;
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putString(STATE_USER, mUser);
        super.onSaveInstanceState(savedInstanceState);
    }
}
