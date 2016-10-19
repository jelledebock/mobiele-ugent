package be.ugent.oomt.newsfeed;

import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import be.ugent.oomt.newsfeed.content.CustomContentProvider;
import be.ugent.oomt.newsfeed.content.database.DatabaseContract;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DetailFragment#newInstance} factory method to
 * create an instance of this fragment.
 */

public class DetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>  {
    private static final int LOADER_ID = 41;
    // the fragment initialization parameters
    public static final String ARG_ITEM_ID = "item_id";

    private TextView messageView;
    private TextView typeView;
    private TextView titleView;
    private TextView transportView;
    private TextView longitudeView;
    private TextView latitudeView;
    private TextView sourceView;
    private TextView itemIdView;
    private TextView alarmNameView;
    private TextView timestampView;

    public DetailFragment() {
        // Required empty public constructor
    }

    public String getItemId() {
        return getArguments().getString(ARG_ITEM_ID);
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param itemId Parameter 1.
     * @return A new instance of fragment DetailFragment.
     */
    public static DetailFragment newInstance(String itemId) {
        DetailFragment fragment = new DetailFragment();
        Bundle args = new Bundle();
        args.putString(ARG_ITEM_ID, itemId);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLoaderManager().initLoader(LOADER_ID,null,this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_detail, container, false);

        titleView = (TextView) view.findViewById(R.id.detail_title);
        typeView = (TextView) view.findViewById(R.id.type);
        messageView = (TextView) view.findViewById(R.id.message);
        transportView = (TextView) view.findViewById(R.id.transport);
        longitudeView = (TextView) view.findViewById(R.id.longitude);
        latitudeView = (TextView) view.findViewById(R.id.latitude);
        sourceView = (TextView) view.findViewById(R.id.source);
        itemIdView = (TextView) view.findViewById(R.id.itemId);
        alarmNameView = (TextView) view.findViewById(R.id.alarmName);
        timestampView = (TextView) view.findViewById(R.id.timestamp);

        return view;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if(id!=LOADER_ID){
            return null;
        }
        //return new cursorloader


        Log.d("NOTICE","Creating a cursorloader object ID=" + this.getItemId());
        return new CursorLoader(
                getActivity(),
                CustomContentProvider.ITEMS_CONTENT_URL,
                new String[]{"*"},
                DatabaseContract.Item.COLUMN_NAME_ID+"=?",
                new String[]{getItemId()},
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        data.moveToFirst();

        itemIdView.setText(data.getString(0));
        titleView.setText(data.getString(6));
        typeView.setText(data.getString(3));
        messageView.setText(data.getString(6));
        transportView.setText(data.getString(4));;
        longitudeView.setText(data.getString(7));
        latitudeView.setText(data.getString(8));
        sourceView.setText(data.getString(2));
        alarmNameView.setText(data.getString(5));
        timestampView.setText(data.getString(9));

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    //TODO: override LoaderManager.LoaderCallbacks<Cursor> methods
}
