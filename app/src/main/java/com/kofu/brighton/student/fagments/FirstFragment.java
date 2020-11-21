package com.kofu.brighton.student.fagments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;

import com.kofu.brighton.student.MainActivityCallBacks;
import com.kofu.brighton.student.R;
import com.kofu.brighton.student.model.DashboardData;
import com.kofu.brighton.student.network.APIService;
import com.kofu.brighton.student.network.APIServiceBuilder;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FirstFragment extends Fragment {

    private APIService mApiService;
    private TextView mCanteenBalance;
    private TextView mBooksBorrowed;
    private TextView mAccumulatedPenalty;
    private View mBtnTopup;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_first, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        this.setHasOptionsMenu(true);
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        MainActivityCallBacks activity = (MainActivityCallBacks) getActivity();
        activity.hideFab();
        mCanteenBalance = view.findViewById(R.id.tv_canteen_balance);
        mBooksBorrowed = view.findViewById(R.id.tv_books_borrowed);
        mAccumulatedPenalty = view.findViewById(R.id.tv_accumulated_penalty);

        populateView(activity);

        mBtnTopup = view.findViewById(R.id.bn_top_up);
        mBtnTopup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation
                        .findNavController(getActivity().findViewById(R.id.nav_host_fragment))
                        .navigate(FirstFragmentDirections
                                .actionFirstFragmentToSecondFragment(2));
            }
        });

        view.findViewById(R.id.bn_pay_pen).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation
                        .findNavController(getActivity().findViewById(R.id.nav_host_fragment))
                        .navigate(FirstFragmentDirections
                                .actionFirstFragmentToSecondFragment(1));
            }
        });
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.student_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.action_propfile) {
            Navigation
                    .findNavController(getActivity().findViewById(R.id.nav_host_fragment))
                    .navigate(FirstFragmentDirections
                            .actionFirstFragmentToInforFragment());
        } else {
            logout();
        }
        return super.onOptionsItemSelected(item);
    }

    private void logout() {
        MainActivityCallBacks activity = (MainActivityCallBacks) getActivity();
        activity.setToken(null, 0);
        Navigation
                .findNavController(getActivity().findViewById(R.id.nav_host_fragment))
                .navigate(FirstFragmentDirections
                        .actionFirstFragmentToLoginFragment());
    }

    private void populateView(MainActivityCallBacks activity) {
        mApiService = APIServiceBuilder.buildService(APIService.class);
        int student = activity.getStudent();
        if (student > 0) {
            Call<DashboardData> dashBoardDataForStudent =
                    mApiService.getDashBoardDataForStudent(student);
            dashBoardDataForStudent.enqueue(new Callback<DashboardData>() {
                @Override
                public void onResponse(Call<DashboardData> call, Response<DashboardData> response) {
                    if (response.code() == 200) {
                        DashboardData body = response.body();
                        mCanteenBalance.setText("$ " + String.valueOf(body.canteenBalance));
                        mBooksBorrowed.setText(String.valueOf(String.valueOf(body.numberOfBooksBorrowed)));
                        mAccumulatedPenalty.setText("$ " + String.valueOf(body.penalty));
                        mBtnTopup.setEnabled(true);
                    } else {
                        Toast.makeText(getActivity(), "", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<DashboardData> call, Throwable t) {
                    Toast.makeText(getActivity(), "Failure: "
                            + t.getMessage() + "", Toast.LENGTH_LONG).show();
                }
            });
        }else {
            logout();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        MainActivityCallBacks activity = (MainActivityCallBacks) getActivity();
        populateView(activity);
    }
}