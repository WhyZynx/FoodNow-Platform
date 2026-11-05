package com.example.project_1;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
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

    TextView tvTrackingOrderId;
    TextView tvTrackingRestaurant;
    TextView tvTrackingAddress;
    TextView tvTrackingPayment;
    TextView tvTrackingTotal;
    TextView tvDriverName;
    TextView tvDriverLicense;
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

        tvTrackingOrderId = view.findViewById(R.id.tvTrackingOrderId);
        tvTrackingRestaurant = view.findViewById(R.id.tvTrackingRestaurant);
        tvTrackingAddress = view.findViewById(R.id.tvTrackingAddress);
        tvTrackingPayment = view.findViewById(R.id.tvTrackingPayment);
        tvTrackingTotal = view.findViewById(R.id.tvTrackingTotal);
        tvDriverName = view.findViewById(R.id.tvDriverName);
        tvDriverLicense = view.findViewById(R.id.tvDriverLicense);
        btnCancelBooking = view.findViewById(R.id.btnCancelBooking);

        btnPlaceOrder.setOnClickListener(v -> handlePlaceOrder());
        btnCancelBooking.setOnClickListener(v -> handleCancelOrder());

        updateUI();

        return view;
    }

    private void handlePlaceOrder() {
        List<Food> cartItems = CartManager.getInstance().getCartItems();
        if (cartItems.isEmpty()) {
            Toast.makeText(getContext(), "Giỏ hàng của bạn đang trống!", Toast.LENGTH_SHORT).show();
            return;
        }

        UserSession session = new UserSession(requireContext());
        User user = session.getUserDetails();
        int customerId = user != null ? user.getId() : 1;
        if (customerId == 0) customerId = 1;

        int restaurantId = CartManager.getInstance().getRestaurantId();
        if (restaurantId == 0) restaurantId = 1;

        String restaurantName = CartManager.getInstance().getLastRestaurantName();

        int subtotal = CartManager.getInstance().getTotalPrice();
        int totalAmount = subtotal + 15000;

        List<ApiConfig.OrderItemRequest> items = new ArrayList<>();
        for (Food f : cartItems) {
            items.add(new ApiConfig.OrderItemRequest(f.id > 0 ? f.id : 1, f.quantity, f.price));
        }

        String address = session.isLoggedIn() && user != null && !TextUtils.isEmpty(user.getAddress())
                ? user.getAddress() : "123 Main Street, District 1, City";

        ApiConfig.createOrderApi(customerId, restaurantId, address, "Cash on Delivery", totalAmount, items, new ApiConfig.ApiCallback<Integer>() {
            @Override
            public void onSuccess(Integer orderId) {
                isDriverFoundThisSession = false;
                CartManager.getInstance().setLastOrderId(orderId);
                CartManager.getInstance().setLastRestaurantName(restaurantName);
                CartManager.getInstance().setLastOrderAddress(address);
                CartManager.getInstance().setLastOrderTotal(totalAmount);
                CartManager.getInstance().setLastPaymentMethod("Cash on Delivery");
                CartManager.getInstance().setLastOrderStatus("pending");
                CartManager.getInstance().setHasActiveOrder(true);
                CartManager.getInstance().clear();

                Toast.makeText(getContext(), "Order #" + orderId + " placed successfully!", Toast.LENGTH_SHORT).show();
                if (getActivity() instanceof MainActivity) {
                    ((MainActivity) getActivity()).updateCartBadge();
                    ((MainActivity) getActivity()).selectTab(2);
                }
                updateUI();
            }

            @Override
            public void onError(String errorMessage) {
                Toast.makeText(getContext(), "Order placement failed: " + errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void handleCancelOrder() {
        int orderId = CartManager.getInstance().getLastOrderId();
        String currentStatus = CartManager.getInstance().getLastOrderStatus();

        if (orderId <= 0) {
            cancelDriverSearchSimulation();
            CartManager.getInstance().setHasActiveOrder(false);
            CartManager.getInstance().clear();
            Toast.makeText(getContext(), "Đã hủy đơn hàng thành công.", Toast.LENGTH_SHORT).show();
            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).updateCartBadge();
            }
            updateUI();
            return;
        }

        if ("confirmed".equalsIgnoreCase(currentStatus)
                || "preparing".equalsIgnoreCase(currentStatus)
                || "delivering".equalsIgnoreCase(currentStatus)
                || "completed".equalsIgnoreCase(currentStatus)) {
            Toast.makeText(getContext(), "Đơn hàng đã được xác nhận/đang giao, không thể hủy!", Toast.LENGTH_LONG).show();
            btnCancelBooking.setVisibility(View.GONE);
            return;
        }

        ApiConfig.cancelOrderApi(orderId, new ApiConfig.ApiCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean result) {
                cancelDriverSearchSimulation();
                CartManager.getInstance().setHasActiveOrder(false);
                CartManager.getInstance().clear();
                isDriverFoundThisSession = false;
                Toast.makeText(getContext(), "Đã hủy đơn hàng #" + orderId + " thành công.", Toast.LENGTH_SHORT).show();
                if (getActivity() instanceof MainActivity) {
                    ((MainActivity) getActivity()).updateCartBadge();
                }
                updateUI();
            }

            @Override
            public void onError(String errorMessage) {
                Toast.makeText(getContext(), errorMessage, Toast.LENGTH_LONG).show();
                fetchLatestOrderAndSync();
            }
        });
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

    private void fetchLatestOrderAndSync() {
        UserSession session = new UserSession(requireContext());
        User user = session.getUserDetails();
        if (user == null) return;

        ApiConfig.getCustomerOrdersApi(user.getId(), new ApiConfig.ApiCallback<List<ApiConfig.CustomerOrderModel>>() {
            @Override
            public void onSuccess(List<ApiConfig.CustomerOrderModel> orders) {
                if (!isAdded() || orders == null || orders.isEmpty()) return;

                ApiConfig.CustomerOrderModel latestOrder = orders.get(0);

                if ("cancelled".equalsIgnoreCase(latestOrder.status) || "completed".equalsIgnoreCase(latestOrder.status)) {
                    CartManager.getInstance().setHasActiveOrder(false);
                    updateUI();
                    return;
                }

                CartManager.getInstance().setLastOrderId(latestOrder.id);
                CartManager.getInstance().setLastRestaurantName(latestOrder.restaurant_name);
                CartManager.getInstance().setLastOrderAddress(latestOrder.delivery_address);
                CartManager.getInstance().setLastPaymentMethod(latestOrder.payment_method);
                CartManager.getInstance().setLastOrderTotal((int) latestOrder.total_amount);
                CartManager.getInstance().setLastOrderStatus(latestOrder.status);
                if (!TextUtils.isEmpty(latestOrder.driver_name)) {
                    CartManager.getInstance().setLastDriverName(latestOrder.driver_name);
                }
                if (!TextUtils.isEmpty(latestOrder.driver_phone)) {
                    CartManager.getInstance().setLastDriverPhone(latestOrder.driver_phone);
                }

                displayActiveOrderData();
            }

            @Override
            public void onError(String errorMessage) {
            }
        });
    }

    private void displayActiveOrderData() {
        if (!isAdded()) return;

        int orderId = CartManager.getInstance().getLastOrderId();
        String restaurantName = CartManager.getInstance().getLastRestaurantName();
        String address = CartManager.getInstance().getLastOrderAddress();
        String payment = CartManager.getInstance().getLastPaymentMethod();
        int total = CartManager.getInstance().getLastOrderTotal();
        String status = CartManager.getInstance().getLastOrderStatus();
        String driverName = CartManager.getInstance().getLastDriverName();
        String driverPhone = CartManager.getInstance().getLastDriverPhone();

        if (tvTrackingOrderId != null) {
            tvTrackingOrderId.setText(orderId > 0 ? String.format(Locale.US, "Order ID: #%d", orderId) : "Order ID: Pending");
        }
        if (tvTrackingRestaurant != null) {
            tvTrackingRestaurant.setText(restaurantName != null ? restaurantName : "Restaurant");
        }
        if (tvTrackingAddress != null) {
            tvTrackingAddress.setText(address != null ? address : "Delivery Address");
        }
        if (tvTrackingPayment != null) {
            tvTrackingPayment.setText(payment != null ? payment : "Cash on Delivery");
        }
        if (tvTrackingTotal != null) {
            tvTrackingTotal.setText(String.format(Locale.US, "%,d VND", total));
        }

        if (tvDriverName != null && !TextUtils.isEmpty(driverName)) {
            tvDriverName.setText(driverName);
        }
        if (tvDriverLicense != null && !TextUtils.isEmpty(driverPhone)) {
            tvDriverLicense.setText(driverPhone);
        }

        boolean isConfirmedOrDelivering = "confirmed".equalsIgnoreCase(status)
                || "preparing".equalsIgnoreCase(status)
                || "delivering".equalsIgnoreCase(status)
                || "completed".equalsIgnoreCase(status)
                || isDriverFoundThisSession;

        if (isConfirmedOrDelivering) {
            tvDriverStatus.setText("Đã tìm thấy tài xế!");
            tvDriverSubStatus.setText("Driver found! Your food is on the way.");
            progressFindingDriver.setVisibility(View.GONE);
            layoutDriverInfo.setVisibility(View.VISIBLE);
            btnCancelBooking.setVisibility(View.GONE);
        } else {
            tvDriverStatus.setText("Đang tìm tài xế...");
            tvDriverSubStatus.setText("Finding driver for your order...");
            progressFindingDriver.setVisibility(View.VISIBLE);
            layoutDriverInfo.setVisibility(View.GONE);
            btnCancelBooking.setVisibility(View.VISIBLE);
            btnCancelBooking.setEnabled(true);
            if (driverMatchRunnable == null) {
                startDriverSearchSimulation();
            }
        }
    }

    private void updateUI() {
        if (CartManager.getInstance().hasActiveOrder()) {
            layoutCartView.setVisibility(View.GONE);
            layoutTrackingView.setVisibility(View.VISIBLE);

            displayActiveOrderData();
            fetchLatestOrderAndSync();
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
        if (getActivity() instanceof MainActivity) {
            ((MainActivity) getActivity()).updateCartBadge();
        }
        updateUI();
    }

    @Override
    public void onMinusClick(Food food, int position) {
        if (food.quantity > 0) {
            food.quantity--;
            CartManager.getInstance().addFood(food);
            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).updateCartBadge();
            }
            updateUI();
        }
    }
}
