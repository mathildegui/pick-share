package com.mathilde.customcam.custom_pick;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.mathilde.customcam.R;
import com.mathilde.customcam.adapter.FilterAdapter;
import com.mathilde.customcam.widget.StartPointSeekBar;
import com.meetme.android.horizontallistview.HorizontalListView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link CustomPickFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link CustomPickFragment#newInstance} factory method to
 * create an instance of this fragment.
 *
 */
public class CustomPickFragment extends Fragment {
    private static String TAG = "CustomPickFragment";


    private HorizontalListView mHorizontalListView;
    private FilterAdapter mFilterAdapter;
    private List<String> mList;


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CustomPickFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CustomPickFragment newInstance(String param1, String param2) {
        CustomPickFragment fragment = new CustomPickFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }
    public CustomPickFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        Bundle b = getActivity().getIntent().getExtras();
        View v = inflater.inflate(R.layout.fragment_custom_pick, container, false);
        ImageView i = (ImageView)v.findViewById(R.id.image_preview_custom);
        i.setImageURI((Uri) b.get("path"));
        mList = new ArrayList<String>();
        mHorizontalListView = (HorizontalListView) v.findViewById(R.id.horizontalListView_filters);
        mFilterAdapter = new FilterAdapter(getActivity(),mList);
        mList.add("Normal");
        mList.add("Amaro");
        mList.add("Mayfair");
        mList.add("Rise");
        mList.add("Hudson");
        mList.add("Valencia");
        mList.add("x-pro II");
        mList.add("Sierra");
        mList.add("Willow");


        StartPointSeekBar<Integer> seekBar = new StartPointSeekBar<Integer>(-100, +100, getActivity());
        seekBar.setNormalizedValue(0.5);
        seekBar.setOnSeekBarChangeListener(new StartPointSeekBar.OnSeekBarChangeListener<Integer>()
        {
            @Override
            public void onOnSeekBarValueChange(StartPointSeekBar<?> bar, Integer value)
            {
                Log.d(TAG, "seekbar value:" + value);
            }
        });

        // add RangeSeekBar to pre-defined layout
        ViewGroup layout = (ViewGroup) v.findViewById(R.id.seekbarwrapper);
        layout.addView(seekBar);
        mFilterAdapter.notifyDataSetChanged();
        // Assign adapter to the HorizontalListView
        mHorizontalListView.setAdapter(mFilterAdapter);
        return v;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
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
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }

}
