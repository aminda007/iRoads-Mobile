package codemo.iroads_mobile.Fragments;


import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import codemo.iroads_mobile.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class CompassFragment extends Fragment {

    private static final String TAG = "CompassFragment";

    public CompassFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_compass, container, false);

    }

}
