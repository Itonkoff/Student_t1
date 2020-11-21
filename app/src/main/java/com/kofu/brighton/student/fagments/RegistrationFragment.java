package com.kofu.brighton.student.fagments;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.budiyev.android.codescanner.CodeScanner;
import com.budiyev.android.codescanner.CodeScannerView;
import com.budiyev.android.codescanner.DecodeCallback;
import com.google.android.material.textfield.TextInputEditText;
import com.google.zxing.Result;
import com.kofu.brighton.student.MainActivityCallBacks;
import com.kofu.brighton.student.R;
import com.kofu.brighton.student.model.Register;
import com.kofu.brighton.student.network.APIService;
import com.kofu.brighton.student.network.APIServiceBuilder;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RegistrationFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RegistrationFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final int REQUEST_CODE = 1;

    private static final String TAG = "RegistrationFragment";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private TextInputEditText mFirst_name_field;
    private TextInputEditText mSurname_field;
    private TextInputEditText mEmail_field;
    private TextInputEditText mP_number_field;
    private TextInputEditText mPswrd_field;
    private TextInputEditText mPswrd_confirm_field;
    private APIService mApiService;
    private View mView;
    private CodeScanner mCodeScanner;
    private String mStudentRef;
    private APIService mApiService1;
    private CodeScannerView mCodeScannerView;

    public RegistrationFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment RegistrationFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static RegistrationFragment newInstance(String param1, String param2) {
        RegistrationFragment fragment = new RegistrationFragment();
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
        return inflater.inflate(R.layout.fragment_registration, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        mCodeScannerView = view.findViewById(R.id.scanner_v);

        verifyPermissions();

        mApiService = APIServiceBuilder.buildService(APIService.class);

        mEmail_field = view.findViewById(R.id.tiet_email_r);
        mP_number_field = view.findViewById(R.id.tiet_p_number);
        mPswrd_field = view.findViewById(R.id.tiet_password_r);
        mPswrd_confirm_field = view.findViewById(R.id.tiet_re_password);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        verifyPermissions();
    }

    private void verifyPermissions() {
        int permissionCheck =
                ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA);

        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
            setUpCodeScanner();
        } else {
            String[] permissions = {Manifest.permission.CAMERA};
            requestPermissions(permissions, REQUEST_CODE);
        }
    }

    private void setUpCodeScanner() {
        mCodeScanner = new CodeScanner(getActivity(), mCodeScannerView);
        mCodeScanner.setDecodeCallback(new DecodeCallback() {
            @Override
            public void onDecoded(@NonNull Result result) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mStudentRef = result.getText();
                        Button lendButton = mView.findViewById(R.id.bn_reg_done);
                        lendButton.setEnabled(true);
                        lendButton.setVisibility(View.VISIBLE);
                        lendButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Register details = collectDetails();
                                Call registerCall = mApiService.register(details);

                                registerCall.enqueue(new Callback() {
                                    @Override
                                    public void onResponse(Call call, Response response) {
                                        Log.d(TAG, "onResponse: Success code: " + response.code() + "");
                                        if (response.code() == 201) {
                                            Toast.makeText(getActivity(), "Registered successfully", Toast.LENGTH_LONG).show();
                                            Navigation
                                                    .findNavController(getActivity().findViewById(R.id.nav_host_fragment))
                                                    .navigate(RegistrationFragmentDirections.actionRegistrationFragmentToLoginFragment());
                                        } else {
                                            Toast.makeText(getActivity(), "Failed to create account", Toast.LENGTH_LONG).show();
                                        }
                                    }

                                    @Override
                                    public void onFailure(Call call, Throwable t) {
                                        Log.d(TAG, "onResponse: Failed: " + t.getMessage() + "");
                                        Toast.makeText(getActivity(), "Failure: " + t.getMessage() + "", Toast.LENGTH_LONG).show();
                                    }
                                });
                            }
                        });
//                        Toast.makeText(getActivity(), result.getText(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        mCodeScannerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCodeScanner.startPreview();
            }
        });
    }

    private Register collectDetails() {
        String email = mEmail_field.getText().toString();
        String phoneNumber = mP_number_field.getText().toString();
        String password = mPswrd_field.getText().toString();
        String passwordConfirm = mPswrd_confirm_field.getText().toString();

        return new Register(mStudentRef, email, phoneNumber, password, passwordConfirm);
    }

}