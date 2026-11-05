package com.example.project_1;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class OrderHistoryActivity extends AppCompatActivity {

    private RecyclerView rvOrderHistory;
    private TextView tvEmptyHistory;
    private OrderHistoryAdapter adapter;
    private List<ApiConfig.CustomerOrderModel> orderList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_history);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Order History");
        }
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        rvOrderHistory = findViewById(R.id.rvOrderHistory);
        tvEmptyHistory = findViewById(R.id.tvEmptyHistory);
        orderList = new ArrayList<>();

        adapter = new OrderHistoryAdapter(orderList);
        rvOrderHistory.setLayoutManager(new LinearLayoutManager(this));
        rvOrderHistory.setAdapter(adapter);

        loadOrderHistory();
    }

    private void loadOrderHistory() {
        UserSession userSession = new UserSession(this);
        if (!userSession.isLoggedIn()) {
            tvEmptyHistory.setVisibility(View.VISIBLE);
            tvEmptyHistory.setText("Please sign in to view order history.");
            rvOrderHistory.setVisibility(View.GONE);
            return;
        }

        User user = userSession.getUserDetails();
        int customerId = user != null ? user.getId() : 1;
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
                Toast.makeText(OrderHistoryActivity.this, "Failed to load orders: " + errorMessage, Toast.LENGTH_SHORT).show();
                tvEmptyHistory.setVisibility(View.VISIBLE);
                rvOrderHistory.setVisibility(View.GONE);
            }
        });
    }
}
