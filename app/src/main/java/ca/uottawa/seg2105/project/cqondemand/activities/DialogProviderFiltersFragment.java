package ca.uottawa.seg2105.project.cqondemand.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;

import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import java.sql.Timestamp;
import java.util.Date;

import androidx.fragment.app.DialogFragment;
import ca.uottawa.seg2105.project.cqondemand.R;

public class DialogProviderFiltersFragment extends DialogFragment implements View.OnClickListener, TimePickerDialog.OnTimeSetListener, DatePickerDialog.OnDateSetListener {

    protected OnFragmentInteractionListener mListener;
    protected int minRating;
    protected Date startTime;
    protected Date endTime;
    RatingBar rating_stars;

    public DialogProviderFiltersFragment() {
        // Required empty public constructor
    }

    public static DialogProviderFiltersFragment newInstance(int minRating, long startTime, long endTime) {
        DialogProviderFiltersFragment fragment = new DialogProviderFiltersFragment();
        Bundle args = new Bundle();
        args.putInt("minRating", minRating);
        args.putLong("startTime", startTime);
        args.putLong("endTime", endTime);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            minRating = getArguments().getInt("minRating") % 6;
            long start = getArguments().getLong("startTime");
            if (start > -1) { startTime = new Date(start); }
            long end = getArguments().getLong("endTime");
            if (end > -1) { endTime = new Date(end); }
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dialog_provider_filters, container);
        rating_stars = view.findViewById(R.id.stars_filter_rating);
        rating_stars.setRating(minRating);
        view.findViewById(R.id.btn_apply).setOnClickListener(this);
        view.findViewById(R.id.btn_cancel).setOnClickListener(this);
        view.findViewById(R.id.txt_clear_rating).setOnClickListener(this);
        view.findViewById(R.id.txt_clear_availability).setOnClickListener(this);
        view.findViewById(R.id.btn_set_availability).setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_apply:
                mListener.onFiltersApply((int) rating_stars.getRating(), startTime, endTime);
                this.dismissAllowingStateLoss();
                break;
            case R.id.btn_cancel:
                this.dismissAllowingStateLoss();
                break;
            case R.id.txt_clear_rating:

                break;
            case R.id.txt_clear_availability:

                break;
            case R.id.btn_set_availability:

                break;
        }
    }


    public void onApplyPressed(View view) {

    }

    public void onCancelPressed(View view) {

    }

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
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {

    }

    @Override
    public void onTimeSet(TimePickerDialog view, int hourOfDay, int minute, int second) {

    }

    @Override
    public void onDismiss(final DialogInterface dialog) {
        super.onDismiss(dialog);
        mListener.onFiltersDialogDismiss();
    }

    public interface OnFragmentInteractionListener {
        void onFiltersApply(int minRating, Date startTime, Date endTime);
        void onFiltersDialogDismiss();
    }

}
