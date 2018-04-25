package codemo.iroads_mobile.Fragments;


import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

import codemo.iroads_mobile.MainActivity;
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
    private static MainActivity mainActivity;
    private TextView filterText;

    public SettingsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        saving = (Switch) view.findViewById(R.id.savingSwitch);
        filterText = (TextView) view.findViewById(R.id.filterText);
        filtering = (Switch) view.findViewById(R.id.filteringSwitch);
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

        saving.setOnCheckedChangeListener(new Switch.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
//                Log.d(TAG, "settings auto save changed");
                if(b){
                    Log.d(TAG, "settings auto save enabled");
                    HomeFragment.setAutoSaveON(true);
                    saving.getThumbDrawable().setColorFilter(ContextCompat.getColor(getMainActivity().getApplicationContext(), R.color.colorPrimary), PorterDuff.Mode.MULTIPLY);
                }else{
                    Log.d(TAG, "settings auto save disabled");
                    HomeFragment.setAutoSaveON(false);
                    saving.getThumbDrawable().setColorFilter(ContextCompat.getColor(getMainActivity().getApplicationContext(), R.color.colorDisabledThumb), PorterDuff.Mode.MULTIPLY);
                }
            }
        });

        filterBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        disableFiltering();

        filtering.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    Log.d(TAG, "filtering enabled");
                    enableFiltering();
                }else{
                    Log.d(TAG, "filtering disabled");
                    disableFiltering();
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

    public void enableFiltering(){
        filterBar.setEnabled(true);
        filterText.setEnabled(true);
        filtering.getThumbDrawable().setColorFilter(ContextCompat.getColor(getMainActivity().getApplicationContext(), R.color.colorPrimary), PorterDuff.Mode.MULTIPLY);
    }

    public void disableFiltering(){
        filterBar.setEnabled(false);
        filterText.setEnabled(false);
        filtering.getThumbDrawable().setColorFilter(ContextCompat.getColor(getMainActivity().getApplicationContext(), R.color.colorDisabledThumb), PorterDuff.Mode.MULTIPLY);

    }

    public static MainActivity getMainActivity() {
        return mainActivity;
    }

    public static void setMainActivity(MainActivity mainActivity) {
        SettingsFragment.mainActivity = mainActivity;
    }
}
