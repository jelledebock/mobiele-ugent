package oomt.ugent.be.labo2;


import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.logging.Logger;


/**
 * A simple {@link Fragment} subclass.
 */
public class DetailFragment extends Fragment {
    int heroid;
    String history, name;

    public DetailFragment() {
        //empty ctor
        Log.i("notice","Adding new detailfragment.");
    }

    public static DetailFragment newInstance(int index) {
        DetailFragment f = new DetailFragment();

        f.heroid = index;

        return f;
    }

    public void setHero(int hero_id){
         history = getResources().getStringArray(R.array.superheroes_history)[hero_id];
         name = getResources().getStringArray(R.array.superheroes_names)[hero_id];
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_detail, container, false);
    }


    @Override
    public void onStart() {
        setHero(this.heroid);
        TextView history_txt = (TextView)getView().findViewById(R.id.herohistory);
        history_txt.setText(history);

        TextView name_txt = (TextView)getView().findViewById(R.id.heroname);
        name_txt.setText(name);

        super.onStart();
    }
}
