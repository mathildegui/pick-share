package com.mathilde.customcam.custom_pick;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mathilde.customcam.R;
import com.mathilde.customcam.adapter.FilterAdapter;
import com.mathilde.customcam.camera.SaveFile;
import com.mathilde.customcam.image.Utils;
import com.mathilde.customcam.widget.StartPointSeekBar;
import com.meetme.android.horizontallistview.HorizontalListView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
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
public class CustomPickFragment extends Fragment implements View.OnClickListener{
    private static String TAG = "CustomPickFragment";
    private static int CROP = 100;

    private static int BRIGHTNESS = 100;
    private static int CONTRAST = 100;


    private HorizontalListView mHorizontalListView;
    private FilterAdapter mFilterAdapter;
    private HashMap<Integer, String> mList;
    private Bitmap srcBitmap;
    private ImageView mPreviewImageView;
    private ImageView mDoneImageView;
    private ImageView mUndoImageView;
    private SaveFile mSaveFile;

    private int mState;

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
        final Bundle b = getActivity().getIntent().getExtras();
        View v = inflater.inflate(R.layout.fragment_custom_pick, container, false);
        mPreviewImageView = (ImageView)v.findViewById(R.id.image_preview_custom);
        mDoneImageView = (ImageView)v.findViewById(R.id.done);
        mUndoImageView = (ImageView)v.findViewById(R.id.undo);
        mDoneImageView.setOnClickListener(this);
        mUndoImageView.setOnClickListener(this);
        mList = new HashMap<Integer, String>();
        mHorizontalListView = (HorizontalListView) v.findViewById(R.id.horizontalListView_filters);
        mFilterAdapter = new FilterAdapter(getActivity(),mList);
        mSaveFile = new SaveFile(getActivity());

        /**
         * TODO: REMOVE THIS SHIT
         */
        mList.put(new Integer(0),"Brightness");
        mList.put(new Integer(1),"Crop");
        mList.put(new Integer(2),"Contrast");
        mList.put(new Integer(3),"Black&White");
        mList.put(new Integer(4),"Sepia");
        mList.put(new Integer(5),"Negative");
        mList.put(new Integer(6),"x-pro II");
        mList.put(new Integer(7),"Sierra");
        mList.put(new Integer(8),"Willow");

        srcBitmap = BitmapFactory.decodeFile(b.get("path").toString());
        mPreviewImageView.setImageURI((Uri) b.get("uri"));
        StartPointSeekBar<Integer> seekBar = new StartPointSeekBar<Integer>(-100, +100, getActivity());
        seekBar.setNormalizedValue(0.5);
        seekBar.setOnSeekBarChangeListener(new StartPointSeekBar.OnSeekBarChangeListener<Integer>()
        {
            @Override
            public void onOnSeekBarValueChange(StartPointSeekBar<?> bar, Integer value){
                if(mState == BRIGHTNESS)mPreviewImageView.setImageBitmap(Utils.doBrightness(srcBitmap, value));
                else if(mState == CONTRAST)mPreviewImageView.setImageBitmap(Utils.doContrast(srcBitmap, value));
            }
        });

        // add RangeSeekBar to pre-defined layout
        ViewGroup layout = (ViewGroup) v.findViewById(R.id.seekbarwrapper);

        layout.addView(seekBar);
        mFilterAdapter.notifyDataSetChanged();
        // Assign adapter to the HorizontalListView
        mHorizontalListView.setAdapter(mFilterAdapter);
        final RelativeLayout seekBarRelativeLayout = (RelativeLayout)v.findViewById(R.id.layout_seekbarwrapper);
        mHorizontalListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    switch(position){
                        case 0:
                            if(seekBarRelativeLayout.getVisibility() == View.GONE) seekBarRelativeLayout.setVisibility(View.VISIBLE);
                            mState = BRIGHTNESS;
                            break;
                        case 1:
                            Utils.performCrop((Uri) b.get("uri"), getActivity(), CROP);
                            break;
                        case 2:
                            mState = CONTRAST;
                            if(seekBarRelativeLayout.getVisibility() == View.GONE) seekBarRelativeLayout.setVisibility(View.VISIBLE);
                            break;
                        case 3:
                            mPreviewImageView.setImageBitmap(Utils.doMonochrome(srcBitmap));
                            break;
                        case 4:
                            mPreviewImageView.setImageBitmap(Utils.doSepia(srcBitmap));
                            break;
                        case 5:
                            mPreviewImageView.setImageBitmap(Utils.doNegative(srcBitmap));
                            break;
                        case 6:
                            Fragment newFragment = new CustomMatrixFragment();
                            // consider using Java coding conventions (upper first char class names!!!)
                            FragmentTransaction transaction = getFragmentManager().beginTransaction();

                            // Replace whatever is in the fragment_container view with this fragment,
                            // and add the transaction to the back stack
                            Bundle bundle = new Bundle();
                            bundle.putString("uri",b.get("uri").toString());
                            bundle.putString("path",b.get("path").toString());
                            newFragment.setArguments(bundle);
                            transaction.replace(R.id.container, newFragment);
                            transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                            transaction.addToBackStack(TAG);
                            // Commit the transaction
                            transaction.commit();
//                            startActivity(new Intent(getActivity(), CustomMatrixFragment.class)
//                                    .putExtra("uri",(Uri) b.get("uri"))
//                                    .putExtra("path",(Uri) b.get("path")));
                            break;
                    }
            }
        });
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

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.done:
                File pictureFile = mSaveFile.getOutputMediaFile(SaveFile.MEDIA_TYPE_IMAGE);
                byte[] pictureBytes;
                mPreviewImageView.setDrawingCacheEnabled(false);
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                //copy the bitmap cause you cannot compress a recycle bitmap
                Bitmap mb = ((BitmapDrawable)mPreviewImageView.getDrawable()).getBitmap().copy(Bitmap.Config.ARGB_8888, false);
                mb.compress(Bitmap.CompressFormat.JPEG, 100, bos);
                pictureBytes = bos.toByteArray();

                //recycle bitmap to avoid outofmemeory error
                mb.recycle();
                FileOutputStream fs;
                try {
                    fs = new FileOutputStream(pictureFile);
                    fs.write(pictureBytes);
                    fs.close();
                    Log.d(TAG, "Bitmap save");
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.undo:
                getActivity().onBackPressed();
                break;
        }
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
