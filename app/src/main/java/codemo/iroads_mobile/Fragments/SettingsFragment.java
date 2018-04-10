package codemo.iroads_mobile.Fragments;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.Switch;

import codemo.iroads_mobile.NavigationHandler;
import codemo.iroads_mobile.R;
import codemo.iroads_mobile.Reorientation.ReorientationType;
import codemo.iroads_mobile.SensorDataProcessor;

/**
 * A simple {@link Fragment} subclass.
 */
public class SettingsFragment extends Fragment {

    private static final String TAG = "SettingsFragment";

    private Switch saving, filtering, orienting;
    private SeekBar filterBar;
    private RadioButton nericelMechanism, wolverineMechanism;


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
        nericelMechanism = (RadioButton) view.findViewById(R.id.nericel);
        wolverineMechanism = (RadioButton) view.findViewById(R.id.wolverine);

        RadioGroup reorientaationTypeGroup=(RadioGroup) view.findViewById(R.id.reorientationType);

        reorientaationTypeGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener(){

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                if (checkedId==R.id.nericel){
                    SensorDataProcessor.setReorientation(ReorientationType.Nericel);
                }

                else if (checkedId==R.id.wolverine){
                    SensorDataProcessor.setReorientation(ReorientationType.Wolverine);
                }

            }
        });


//        saving.setColorFilter(ContextCompat.getColor(mainActivity.getApplicationContext(), R.color.colorPrimary));
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        NavigationHandler.navigateTo("homeFragment");
    }
}
