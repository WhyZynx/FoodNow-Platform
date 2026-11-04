package com.example.project_1;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class OrderHistoryFragment extends Fragment {

    private RecyclerView rvOrderHistory;
    private TextView tvEmptyHistory;
    private OrderHistoryAdapter adapter;
    private List<ApiConfig.CustomerOrderModel> orderList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_order_history, container, false);

        rvOrderHistory = view.findViewById(R.id.rvOrderHistory);
        tvEmptyHistory = view.findViewById(R.id.tvEmptyHistory);
        orderList = new ArrayList<>();

        adapter = new OrderHistoryAdapter(orderList);
        rvOrderHistory.setLayoutManager(new LinearLayoutManager(getContext()));
        rvOrderHistory.setAdapter(adapter);

        loadOrderHistory();

        return view;
    }

    private void loadOrderHistory() {
        UserSession userSession = new UserSession(requireContext());
        if (!userSession.isLoggedIn()) {
            tvEmptyHistory.setVisibility(View.VISIBLE);
            tvEmptyHistory.setText("Please sign in to view order history.");
            rvOrderHistory.setVisibility(View.GONE);
            return;
        }

        int customerId = userSession.getUserDetails().getId();
        if (customerId == 0) customerId = 1;

        ApiConfig.getCustomerOrdersApi(customerId, new ApiConfig.ApiCallback<List<ApiConfig.CustomerOrderModel>>() {
            @Override
            public void onSuccess(List<ApiConfig.CustomerOrderModel> result) {
                orderList.clear();
                if (result != null) {
                    orderList.addAll(result);
                }
                if (orderList.isEmpty()) {
                    tvEmptyHistory.setVisibility(View.VISIBLE);
                    rvOrderHistory.setVisibility(View.GONE);
                } else {
                    tvEmptyHistory.setVisibility(View.GONE);
                    rvOrderHistory.setVisibility(View.VISIBLE);
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onError(String errorMessage) {
                Toast.makeText(getContext(), "Failed to load orders: " + errorMessage, Toast.LENGTH_SHORT).show();
                tvEmptyHistory.setVisibility(View.VISIBLE);
                rvOrderHistory.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        loadOrderHistory();
    }
}
