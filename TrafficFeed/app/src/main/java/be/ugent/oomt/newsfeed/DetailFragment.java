package be.ugent.oomt.newsfeed;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DetailFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
//TODO: implement LoaderManager.LoaderCallbacks<Cursor>
public class DetailFragment extends Fragment {

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

    //TODO: override LoaderManager.LoaderCallbacks<Cursor> methods
}
