package ru.abch.alauncher2;

import static android.content.Context.AUDIO_SERVICE;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

import ru.abch.alauncher2.MainActivity2;
import ru.abch.alauncher2.rulerpicker.RulerValuePicker;
import ru.abch.alauncher2.rulerpicker.RulerValuePickerListener;

public class MainFragment extends Fragment {
    public static MainFragment newInstance() {
        return new MainFragment();
    }
    RulerValuePicker rulerValuePicker;
    SeekBar sbRadioVolume;
    TextView tvRSSI, tvRDSText, tvStation, tvVoltage;
    private static final String TAG = "MainFragment";
    AudioManager am;
    Intent radioIntent;
    ImageButton btSearch, btMute, btPlus;
    ButtonsAdapter buttonsAdapter;
    GridView gvButtons;
    RadioStateParcel state = null;
    IntentFilter stateFilter, searchFilter;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_radio2, container, false);
        rulerValuePicker = view.findViewById(R.id.ruler_picker);
        sbRadioVolume = view.findViewById(R.id.radio_volume);
        tvRDSText = view.findViewById(R.id.tv_rds);
        tvRSSI = view.findViewById(R.id.tv_rssi);
        tvStation = view.findViewById(R.id.tv_station);
        btSearch = view.findViewById(R.id.bt_search);
        buttonsAdapter = new ButtonsAdapter(requireActivity(), App.get().getFreqs());
        gvButtons = view.findViewById(R.id.gv_buttons);
        gvButtons.setAdapter(buttonsAdapter);
        btMute = view.findViewById(R.id.bt_mute);
        btPlus = view.findViewById(R.id.bt_plus);
        tvVoltage = view.findViewById(R.id.tv_voltage);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        am = (AudioManager) requireActivity().getSystemService(AUDIO_SERVICE);
        rulerValuePicker.selectValue(App.get().getChannel()/10);
        rulerValuePicker.setValuePickerListener(new RulerValuePickerListener() {
            @Override
            public void onValueChange(int selectedValue) {
                int freq = selectedValue *10;
                App.get().storeChannel(freq);
                radioIntent = new Intent(requireActivity(), RadioService.class);
                radioIntent.putExtra("mute",App.get().getMute());
                radioIntent.putExtra("freq", freq);
                requireActivity().startService(radioIntent);
            }

            @Override
            public void onIntermediateValueChange(int selectedValue) {

            }
        });
        sbRadioVolume.setProgress(App.get().getVolume());
        sbRadioVolume.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                App.get().storeVolume(i);
                radioIntent = new Intent(requireActivity(), RadioService.class);
//                radioIntent.putExtra("mute",App.getMute());
                radioIntent.putExtra("vol", i);
                requireActivity().startService(radioIntent);
                Log.d(TAG, "Set volume " + i);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        btSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                radioIntent = new Intent(requireActivity(), RadioService.class);
                radioIntent.putExtra("search", true);
                requireActivity().startService(radioIntent);
            }
        });
        radioIntent = new Intent(requireActivity(), RadioService.class);
        radioIntent.putExtra("run",true);
        radioIntent.putExtra("mute",App.get().getMute());
//        requireActivity().startService(radioIntent);
//        radioIntent = new Intent(requireActivity(), RadioService.class);
        radioIntent.putExtra("vol", App.get().getVolume());
        radioIntent.putExtra("freq", App.get().getChannel());
        requireActivity().startService(radioIntent);
        btMute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean state = App.get().getMute();
                App.get().setMute(!state);
                Log.d(TAG, "Mute button " + App.get().getMute());
                radioIntent.putExtra("mute",!state);
                requireActivity().startService(radioIntent);
                if(App.get().getMute()) {
                    btMute.setImageDrawable(getResources().getDrawable(R.drawable.speaker_96px));
                } else {
                    btMute.setImageDrawable(getResources().getDrawable(R.drawable.mute_96px));
                }
            }
        });
        if(App.get().getMute()) {
            btMute.setImageDrawable(getResources().getDrawable(R.drawable.speaker_96px));
        } else {
            btMute.setImageDrawable(getResources().getDrawable(R.drawable.mute_96px));
        }
        btPlus.setOnClickListener(view -> {
            boolean notInList = true;
            int currentFreq = rulerValuePicker.getCurrentValue() * 10;
            for (int f : App.get().getFreqs()) {
                if(f == currentFreq) {
                    notInList = false;
                    break;
                }
            }
            if(notInList) {
                boolean add = true;
                Log.d(TAG, "Add freq " + currentFreq + " lo list");
                for (int i = 0; i < App.get().getFreqs().size(); i++) {
                    if(currentFreq < App.get().getFreqs().get(i)) {
                        App.get().getFreqs().add(i, currentFreq);
                        add = false;
                        break;
                    }
                }
                if (add) App.get().getFreqs().add(App.get().getFreqs().size(), currentFreq);
                App.get().saveFreqsList(App.get().getFreqs());
                buttonsAdapter = new ButtonsAdapter(requireActivity(), App.get().getFreqs());
                gvButtons.setAdapter(buttonsAdapter);
            }
        });
    }
    BroadcastReceiver freqsReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int[] freqs = intent.getIntArrayExtra("freqs");
            ArrayList<Integer> freqsList = new ArrayList<>();
            if (freqs != null) {
                for (int f : freqs) freqsList.add(f);
                App.get().saveFreqsList(freqsList);
                buttonsAdapter = new ButtonsAdapter(requireActivity(), App.get().getFreqs());
                gvButtons.setAdapter(buttonsAdapter);
            }
        }
    };
    BroadcastReceiver stateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int prevF = (state != null && state.freq != 0)? state.freq : 0;
            state = intent.getParcelableExtra("state");
            if(state != null) {
                int f = state.freq;
                int v = state.volume;
                int r = state.rssi;
                boolean m = state.mute;
                String n = state.name;
                String t = state.info;
                float voltage = state.voltage;
                Log.d(TAG, intent.getAction() + " " + f + " " + v + " " + r + " " + m + " " + n + " " + t + " " + voltage);
                String sF = null;
                if(prevF != f) {
                    sF = f/100. + " MHz";
                    rulerValuePicker.selectValue(f/10);
                }
                if(n != null && !n.trim().isEmpty()) sF = n.trim();
                if(sF != null) tvStation.setText(sF);
                if(t != null) tvRDSText.setText(t.trim());
                String rssi = "RSSI " + r;
                tvRSSI.setText(rssi);
                DecimalFormat df = new DecimalFormat();
                df.setMaximumFractionDigits(2);
                String volt = "U " + df.format(voltage);
                tvVoltage.setText(volt);
                if(voltage < 12) {
                    tvVoltage.setTextColor(getResources().getColor(R.color.red));
                } else if (voltage < 12.6) {
                    tvVoltage.setTextColor(getResources().getColor(R.color.yellow));
                } else if (voltage < 14.8) {
                    tvVoltage.setTextColor(getResources().getColor(R.color.green));
                }else if (voltage >= 14.8) {
                    tvVoltage.setTextColor(getResources().getColor(R.color.red));
                }
            }
        }
    };
    public void onResume() {
        super.onResume();
        Log.d(TAG, "Main Fragment onResume");
        stateFilter = new IntentFilter("ru.abch.alauncher2.state");
        stateFilter.addCategory("android.intent.action.DEFAULT");
        requireActivity().registerReceiver(stateReceiver,stateFilter);
        searchFilter = new IntentFilter("ru.abch.alauncher2.freqs");
        searchFilter.addCategory("android.intent.action.DEFAULT");
        requireActivity().registerReceiver(freqsReceiver,searchFilter);
    }

    public void onPause() {
        super.onPause();
        Log.d(TAG, "Main Fragment onPause");
        requireActivity().unregisterReceiver(stateReceiver);
        requireActivity().unregisterReceiver(freqsReceiver);
    }
}