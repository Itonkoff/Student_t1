package com.kofu.brighton.student.fagments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.kofu.brighton.student.MainActivityCallBacks;
import com.kofu.brighton.student.R;
import com.kofu.brighton.student.model.PersonalInfo;
import com.kofu.brighton.student.network.APIService;
import com.kofu.brighton.student.network.APIServiceBuilder;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link InforFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class InforFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private APIService mApiService;
    private TextView mFullNameField;
    private TextView mNationalIdField;
    private TextView mPhysicalAddressField;
    private TextView mRegField;

    public InforFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment InforFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static InforFragment newInstance(String param1, String param2) {
        InforFragment fragment = new InforFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
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
        return inflater.inflate(R.layout.fragment_infor, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mApiService = APIServiceBuilder.buildService(APIService.class);

        mFullNameField = view.findViewById(R.id.tv_f_name);
        mNationalIdField = view.findViewById(R.id.tv_nation);
        mPhysicalAddressField = view.findViewById(R.id.tv_p_address);
        mRegField = view.findViewById(R.id.tv_reg);

        populateInformation();
    }

    private void populateInformation() {
        MainActivityCallBacks activity = (MainActivityCallBacks) getActivity();
        Call<PersonalInfo> personalInformation =
                mApiService.getPersonalInformation(activity.getStudent());

        personalInformation.enqueue(new Callback<PersonalInfo>() {
            @Override
            public void onResponse(Call<PersonalInfo> call, Response<PersonalInfo> response) {
                if (response.code() == 200) {
                    mFullNameField.setText(response.body().firstName + response.body().lastName);
                    mNationalIdField.setText(response.body().nationalId);
                    mPhysicalAddressField.setText(response.body().physicalAddress);
                    mRegField.setText(response.body().regNumber);
                }
            }

            @Override
            public void onFailure(Call<PersonalInfo> call, Throwable t) {
                Toast.makeText(getActivity(), "Failure: "
                        + t.getMessage() + "", Toast.LENGTH_LONG).show();
            }
        });
    }
}