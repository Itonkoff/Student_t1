package com.kofu.brighton.student.fagments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.kofu.brighton.student.MainActivityCallBacks;
import com.kofu.brighton.student.R;
import com.kofu.brighton.student.model.Login;
import com.kofu.brighton.student.model.Token;
import com.kofu.brighton.student.network.APIService;
import com.kofu.brighton.student.network.APIServiceBuilder;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link LoginFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LoginFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private static final String TAG = "LoginFragment";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private APIService mApiService;

    public LoginFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment LoginFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static LoginFragment newInstance(String param1, String param2) {
        LoginFragment fragment = new LoginFragment();
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
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        mApiService = APIServiceBuilder.buildService(APIService.class);

        MainActivityCallBacks activity = (MainActivityCallBacks) getActivity();
        activity.hideFab();

        if(!TextUtils.isEmpty(activity.getToken()))
            proceedToHome();

        view.findViewById(R.id.bn_sign_up).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation
                        .findNavController(getActivity().findViewById(R.id.nav_host_fragment))
                        .navigate(LoginFragmentDirections.actionLoginFragmentToRegistrationFragment());
            }
        });

        Log.d(TAG, "onClick: Initialising text fields");
        TextInputEditText emailField = view.findViewById(R.id.tiet_email);
        TextInputEditText passwordField = view.findViewById(R.id.tiet_password);

        view.findViewById(R.id.bn_sign_in).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: Getting login fields");
                String email = emailField.getText().toString();
                String password = passwordField.getText().toString();

                Log.d(TAG, "onClick: Constructing call");
                Call<Token> loginCall = mApiService
                        .login(new Login(email, password));

                Log.d(TAG, "onClick: Enqueuing LOGIN Call");
                loginCall.enqueue(new Callback<Token>() {
                    @Override
                    public void onResponse(Call<Token> call, Response<Token> response) {
                        Log.d(TAG, "onResponse: SUCCESS response");
                        if (response.code() == 200) {
                            MainActivityCallBacks activity = (MainActivityCallBacks) getActivity();
                            activity.setToken(response.body().token,response.body().student);
                            proceedToHome();
                        } else {
                            Toast.makeText(getActivity(),"either your email or password is incorrect",Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<Token> call, Throwable t) {
                        Log.d(TAG, "onFailure: FAILURE Response");
                        Toast.makeText(getActivity(), "Failed", Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
    }

    private void proceedToHome() {
        Navigation
                .findNavController(getActivity().findViewById(R.id.nav_host_fragment))
                .navigate(LoginFragmentDirections.actionLoginFragmentToFirstFragment());
    }
}