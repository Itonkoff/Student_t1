package com.kofu.brighton.student.fagments;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.navigation.Navigation;

import com.google.android.material.textfield.TextInputEditText;
import com.kofu.brighton.student.MainActivityCallBacks;
import com.kofu.brighton.student.R;
import com.kofu.brighton.student.model.PaynowUserData;
import com.kofu.brighton.student.model.Topup;
import com.kofu.brighton.student.network.APIService;
import com.kofu.brighton.student.network.APIServiceBuilder;
import com.kofu.brighton.student.network.PaynowService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import zw.co.paynow.responses.MobileInitResponse;
import zw.co.paynow.responses.StatusResponse;

public class SecondFragment extends Fragment {

    private static final String TAG = "SecondFragment";
    private Button mTopUp;
    private String mEcocashPhone;
    private String mEmailA;
    private String mAmounT;
    private MutableLiveData<MobileInitResponse> mMobileInitResponse = new MutableLiveData<MobileInitResponse>();
    private MutableLiveData<StatusResponse> mStatusResponse = new MutableLiveData<StatusResponse>();
    private MobileInitResponse mMobileInitResponseActual;
    private Button mCheck;
    private APIService mApiService;
    private FragmentActivity mActivity;



    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_second, container, false);
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        MainActivityCallBacks activity = (MainActivityCallBacks) getActivity();
        activity.hideFab();

        mApiService = APIServiceBuilder.buildService(APIService.class);
        mActivity = getActivity();

        TextInputEditText email = view.findViewById(R.id.tiet_email);
        TextInputEditText ecocashNumber = view.findViewById(R.id.tiet_ecocash_number);
        TextInputEditText amount = view.findViewById(R.id.tiet_amount);

        mCheck = view.findViewById(R.id.bn_c);
        mCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new CheckPaymentAsync().execute(mMobileInitResponseActual.pollUrl());
            }
        });

        mTopUp = view.findViewById(R.id.bn_topup);
        mTopUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mEmailA = email.getText().toString();
                mEcocashPhone = ecocashNumber.getText().toString();
                mAmounT = amount.getText().toString();


                new SendPaymentAsync()
                        .execute(new PaynowUserData(mEcocashPhone, Double.parseDouble(mAmounT), mEmailA));
                Toast.makeText(getActivity(), "Payment Sent.", Toast.LENGTH_SHORT).show();
                mTopUp.setEnabled(false);
            }
        });

        mMobileInitResponse.observe(getActivity(), new Observer<MobileInitResponse>() {
            @Override
            public void onChanged(MobileInitResponse mobileInitResponse) {
                mMobileInitResponseActual = mobileInitResponse;
                mCheck.setVisibility(View.VISIBLE);
            }
        });

        mStatusResponse.observe(getActivity(), new Observer<StatusResponse>() {
            @Override
            public void onChanged(StatusResponse statusResponse) {
                if (statusResponse.isPaid()) {
                    Toast.makeText(getActivity(), "PaymentSuccess", Toast.LENGTH_SHORT).show();
                    postUpdate();
                    getActivity().onBackPressed();
                } else
                    Toast.makeText(getActivity(), "Not yet through.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void postUpdate() {
        MainActivityCallBacks activity = (MainActivityCallBacks) getActivity();


        Call<Void> topupCall = mApiService
                .canteenTopup(new Topup(activity.getStudent(), Double.parseDouble(mAmounT)));
        topupCall.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.code() == 201) {
                    Toast.makeText(mActivity, "Updated", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(mActivity, "Update failed", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    class SendPaymentAsync extends AsyncTask<PaynowUserData, Void, MobileInitResponse> {

        @Override
        protected MobileInitResponse doInBackground(PaynowUserData... paynowUserData) {
            MobileInitResponse mobileInitResponse;
            try {
                mobileInitResponse = PaynowService
                        .getInstance()
                        .sendPayment(paynowUserData[0]);
                return mobileInitResponse;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(MobileInitResponse mobileInitResponse) {
            mMobileInitResponse.setValue(mobileInitResponse);
        }
    }

    class CheckPaymentAsync extends AsyncTask<String, Void, StatusResponse> {

        @Override
        protected StatusResponse doInBackground(String... strings) {
            return PaynowService.getInstance().checkStatus(strings[0]);
        }

        @Override
        protected void onPostExecute(StatusResponse statusResponse) {
            mStatusResponse.setValue(statusResponse);
        }
    }
}