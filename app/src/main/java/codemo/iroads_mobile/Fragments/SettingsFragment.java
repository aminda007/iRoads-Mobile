package codemo.iroads_mobile.Fragments;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.SeekBar;
import android.widget.Switch;

import codemo.iroads_mobile.NavigationHandler;
import codemo.iroads_mobile.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class SettingsFragment extends Fragment {

    private Switch saving, filtering, orienting;
    private SeekBar filterBar;
    private RadioButton mechanism1, mechanism2;


    public SettingsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        saving = (Switch) view.findViewById(R.id.savingSwitch);
        filtering = (Switch) view.findViewById(R.id.filteringSwitch);
        orienting = (Switch) view.findViewById(R.id.orientSwitch);
        filterBar = (SeekBar) view.findViewById(R.id.filterSeekbar);
        mechanism1 = (RadioButton) view.findViewById(R.id.Mech1Radio);
        mechanism2 = (RadioButton) view.findViewById(R.id.Mech2Radio);

//        saving.setColorFilter(ContextCompat.getColor(mainActivity.getApplicationContext(), R.color.colorPrimary));
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        NavigationHandler.navigateTo("homeFragment");
    }
}
