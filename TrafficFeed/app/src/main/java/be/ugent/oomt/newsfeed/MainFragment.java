package be.ugent.oomt.newsfeed;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import be.ugent.oomt.newsfeed.content.CustomContentProvider;
import be.ugent.oomt.newsfeed.content.database.DatabaseContract;
import be.ugent.oomt.newsfeed.content.database.DbHelper;

/**
 * A simple {@link ListFragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MainFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
//TODO: implement LoaderManager.LoaderCallbacks<Cursor>
public class MainFragment extends ListFragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private OnFragmentInteractionListener mListener;
    private static final int URL_LOADER = 0;

    public MainFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //TODO: change ArrayAdapter to a SimpleCursorAdapter with the appropriate parameters
        getLoaderManager().initLoader(URL_LOADER, null, this);
        CustomContentProvider provider = new CustomContentProvider();

        SimpleCursorAdapter arrayAdapter = new SimpleCursorAdapter(getActivity(), android.R.layout.simple_list_item_activated_2,
                null,null,null,0);
        setListAdapter(arrayAdapter);
    }

    //TODO: override the LoaderManager.LoaderCallbacks<Cursor> methods

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        mListener.onListItemClick(l,v,position,id);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String [] projection = {"*"};
        switch(id){
            case URL_LOADER:
                //return new cursorloader
                return new CursorLoader(
                        getActivity(),
                        CustomContentProvider.ITEMS_CONTENT_URL,
                        projection,
                        DatabaseContract.Item.COLUMN_NAME_ID+"=",
                        args.getStringArray()

                )
        }

    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        void onListItemClick(ListView l, View v, int position, long id);
    }
}
