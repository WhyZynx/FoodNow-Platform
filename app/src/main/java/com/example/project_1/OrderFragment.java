package com.example.project_1;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class OrderFragment extends Fragment implements FoodAdapter.OnFoodItemClickListener {

    View layoutCartView;
    View layoutTrackingView;

    TextView tvEmptyCart;
    RecyclerView rvCartItems;
    View cardCartSummary;
    TextView tvCartTotal;
    Button btnPlaceOrder;

    TextView tvDriverStatus;
    TextView tvDriverSubStatus;
    View progressFindingDriver;
    View layoutDriverInfo;

    TextView tvTrackingRestaurant;
    TextView tvTrackingTotal;
    Button btnCancelBooking;

    FoodAdapter foodAdapter;
    private Handler handler = new Handler(Looper.getMainLooper());
    private Runnable driverMatchRunnable;
    private boolean isDriverFoundThisSession = false;

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(
                R.layout.fragment_order,
                container,
                false
        );

        layoutCartView = view.findViewById(R.id.layoutCartView);
        tvEmptyCart = view.findViewById(R.id.tvEmptyCart);
        rvCartItems = view.findViewById(R.id.rvCartItems);
        cardCartSummary = view.findViewById(R.id.cardCartSummary);
        tvCartTotal = view.findViewById(R.id.tvCartTotal);
        btnPlaceOrder = view.findViewById(R.id.btnPlaceOrder);

        layoutTrackingView = view.findViewById(R.id.layoutTrackingView);
        tvDriverStatus = view.findViewById(R.id.tvDriverStatus);
        tvDriverSubStatus = view.findViewById(R.id.tvDriverSubStatus);
        progressFindingDriver = view.findViewById(R.id.progressFindingDriver);
        layoutDriverInfo = view.findViewById(R.id.layoutDriverInfo);

        tvTrackingRestaurant = view.findViewById(R.id.tvTrackingRestaurant);
        tvTrackingTotal = view.findViewById(R.id.tvTrackingTotal);
        btnCancelBooking = view.findViewById(R.id.btnCancelBooking);

        btnPlaceOrder.setOnClickListener(v -> {
            List<Food> cartItems = CartManager.getInstance().getCartItems();
            if (cartItems.isEmpty()) {
                Toast.makeText(getContext(), "Giỏ hàng của bạn đang trống!", Toast.LENGTH_SHORT).show();
            } else {
                UserSession session = new UserSession(requireContext());
                int customerId = session.isLoggedIn() ? session.getUserDetails().getId() : 1;
                if (customerId == 0) customerId = 1;

                List<ApiConfig.OrderItemRequest> items = new ArrayList<>();
                for (Food f : cartItems) {
                    items.add(new ApiConfig.OrderItemRequest(1, f.quantity, f.price));
                }

                ApiConfig.createOrderApi(customerId, 1, "12 Nguyễn Huệ, Q.1", "COD", CartManager.getInstance().getTotalPrice(), items, new ApiConfig.ApiCallback<Integer>() {
                    @Override
                    public void onSuccess(Integer orderId) {
                        isDriverFoundThisSession = false;
                        CartManager.getInstance().setHasActiveOrder(true);
                        Toast.makeText(getContext(), "Đặt đơn hàng #" + orderId + " thành công! Đang tìm tài xế...", Toast.LENGTH_SHORT).show();
                        updateUI();
                        startDriverSearchSimulation();
                    }

                    @Override
                    public void onError(String errorMessage) {
                        Toast.makeText(getContext(), "Đặt hàng thất bại, vui lòng thử lại!", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        btnCancelBooking.setOnClickListener(v -> {
            cancelDriverSearchSimulation();
            CartManager.getInstance().setHasActiveOrder(false);
            CartManager.getInstance().clear();
            isDriverFoundThisSession = false;
            Toast.makeText(getContext(), "Đã hủy đơn hàng thành công.", Toast.LENGTH_SHORT).show();
            updateUI();
        });

        updateUI();

        return view;
    }

    private void startDriverSearchSimulation() {
        cancelDriverSearchSimulation();
        driverMatchRunnable = () -> {
            isDriverFoundThisSession = true;
            if (isAdded() && tvDriverStatus != null) {
                tvDriverStatus.setText("Đã tìm thấy tài xế!");
                tvDriverSubStatus.setText("Driver found! Your food is on the way.");
                progressFindingDriver.setVisibility(View.GONE);
                layoutDriverInfo.setVisibility(View.VISIBLE);
                Toast.makeText(getContext(), "Đã tìm thấy tài xế đón đơn!", Toast.LENGTH_SHORT).show();
            }
        };
        handler.postDelayed(driverMatchRunnable, 4000);
    }

    private void cancelDriverSearchSimulation() {
        if (driverMatchRunnable != null) {
            handler.removeCallbacks(driverMatchRunnable);
            driverMatchRunnable = null;
        }
    }

    private void updateUI() {
        if (CartManager.getInstance().hasActiveOrder()) {
            layoutCartView.setVisibility(View.GONE);
            layoutTrackingView.setVisibility(View.VISIBLE);

            tvTrackingRestaurant.setText(CartManager.getInstance().getLastRestaurantName());
            tvTrackingTotal.setText(String.format(Locale.US, "%,d VND", CartManager.getInstance().getTotalPrice()));

            if (isDriverFoundThisSession) {
                tvDriverStatus.setText("Đã tìm thấy tài xế!");
                tvDriverSubStatus.setText("Driver found! Your food is on the way.");
                progressFindingDriver.setVisibility(View.GONE);
                layoutDriverInfo.setVisibility(View.VISIBLE);
            } else {
                tvDriverStatus.setText("Đang tìm tài xế...");
                tvDriverSubStatus.setText("Finding driver for your order...");
                progressFindingDriver.setVisibility(View.VISIBLE);
                layoutDriverInfo.setVisibility(View.GONE);
                if (driverMatchRunnable == null) {
                    startDriverSearchSimulation();
                }
            }
        } else {
            layoutCartView.setVisibility(View.VISIBLE);
            layoutTrackingView.setVisibility(View.GONE);
            cancelDriverSearchSimulation();

            List<Food> items = CartManager.getInstance().getCartItems();
            if (items.isEmpty()) {
                tvEmptyCart.setVisibility(View.VISIBLE);
                rvCartItems.setVisibility(View.GONE);
                cardCartSummary.setVisibility(View.GONE);
            } else {
                tvEmptyCart.setVisibility(View.GONE);
                rvCartItems.setVisibility(View.VISIBLE);
                cardCartSummary.setVisibility(View.VISIBLE);

                tvCartTotal.setText(String.format(Locale.US, "%,d VND", CartManager.getInstance().getTotalPrice()));

                foodAdapter = new FoodAdapter(items, this);
                rvCartItems.setLayoutManager(new LinearLayoutManager(getContext()));
                rvCartItems.setAdapter(foodAdapter);
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        updateUI();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        cancelDriverSearchSimulation();
    }

    @Override
    public void onFoodItemClick(Food food) {
    }

    @Override
    public void onPlusClick(Food food, int position) {
        food.quantity++;
        CartManager.getInstance().addFood(food);
        updateUI();
    }

    @Override
    public void onMinusClick(Food food, int position) {
        if (food.quantity > 0) {
            food.quantity--;
            CartManager.getInstance().addFood(food);
            updateUI();
        }
    }
}
