package be.ugent.oomt.newsfeed;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

import be.ugent.oomt.newsfeed.content.CustomContentProvider;
import be.ugent.oomt.newsfeed.content.database.DatabaseContract;

/**
 * A simple {@link ListFragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MainFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
//TODO: implement LoaderManager.LoaderCallbacks<Cursor>
public class MainFragment extends ListFragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private OnFragmentInteractionListener mListener;
    private static final int LOADER_ID = 41;
    private CursorAdapter arrayAdapter;

    public MainFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //TODO: change ArrayAdapter to a SimpleCursorAdapter with the appropriate parameters
        String from[] = {DatabaseContract.Item.COLUMN_NAME_SOURCE , DatabaseContract.Item.COLUMN_NAME_MESSAGE};
        int to[] = {android.R.id.text1, android.R.id.text2};
        arrayAdapter = new SimpleCursorAdapter(getActivity(), android.R.layout.simple_list_item_activated_2,null,from,to,0);
        getLoaderManager().initLoader(LOADER_ID, null,this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if(id!=LOADER_ID){
            return null;
        }
        String [] projection = {"*"};
        //return new cursorloader


        Log.d("NOTICE","Creating a cursorloader object");
        return new CursorLoader(
                getActivity(),
                CustomContentProvider.ITEMS_CONTENT_URL,
                new String[]{"*"},
                DatabaseContract.Item.COLUMN_NAME_ID + " IS NOT NULL",
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if(loader.getId()==LOADER_ID){
            data.moveToFirst();
            Log.d("Notice","Swapping the listview's cursorloader");
            Log.d("Notice","First row "+data.getColumnName(0));
            Log.d("Notice","First row "+data.getColumnName(1));
            Log.d("Notice","Number of matches "+data.getCount());

            arrayAdapter.swapCursor(data);
            setListAdapter(arrayAdapter);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        arrayAdapter.swapCursor(null);
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
