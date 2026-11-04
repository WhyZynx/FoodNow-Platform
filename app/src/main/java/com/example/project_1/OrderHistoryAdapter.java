package com.example.project_1;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Locale;

public class OrderHistoryAdapter extends RecyclerView.Adapter<OrderHistoryAdapter.ViewHolder> {

    private final List<ApiConfig.CustomerOrderModel> orderList;

    public OrderHistoryAdapter(List<ApiConfig.CustomerOrderModel> orderList) {
        this.orderList = orderList;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvOrderId, tvOrderStatus, tvRestaurantName, tvOrderDate, tvOrderItems, tvOrderTotal;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvOrderId = itemView.findViewById(R.id.tvOrderId);
            tvOrderStatus = itemView.findViewById(R.id.tvOrderStatus);
            tvRestaurantName = itemView.findViewById(R.id.tvRestaurantName);
            tvOrderDate = itemView.findViewById(R.id.tvOrderDate);
            tvOrderItems = itemView.findViewById(R.id.tvOrderItems);
            tvOrderTotal = itemView.findViewById(R.id.tvOrderTotal);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_order_history, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ApiConfig.CustomerOrderModel order = orderList.get(position);
        holder.tvOrderId.setText(String.format(Locale.US, "Order #%d", order.id));

        String status = order.status != null ? order.status.toLowerCase(Locale.ROOT) : "pending";
        holder.tvOrderStatus.setText(status.toUpperCase(Locale.ROOT));
        if ("completed".equals(status)) {
            holder.tvOrderStatus.setTextColor(holder.itemView.getContext().getResources().getColor(R.color.colorPrimary));
        } else if ("cancelled".equals(status)) {
            holder.tvOrderStatus.setTextColor(holder.itemView.getContext().getResources().getColor(R.color.colorRed));
        } else {
            holder.tvOrderStatus.setTextColor(holder.itemView.getContext().getResources().getColor(R.color.colorPrimaryDark));
        }

        holder.tvRestaurantName.setText(order.restaurant_name != null ? order.restaurant_name : "Restaurant");
        holder.tvOrderDate.setText(order.created_at != null ? order.created_at : "");
        holder.tvOrderTotal.setText(String.format(Locale.US, "%,.0f VND", order.total_amount));

        if (order.items != null && !order.items.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < order.items.size(); i++) {
                ApiConfig.OrderItemModel item = order.items.get(i);
                sb.append("• ").append(item.quantity).append("x ").append(item.food_name);
                if (i < order.items.size() - 1) {
                    sb.append("\n");
                }
            }
            holder.tvOrderItems.setText(sb.toString());
            holder.tvOrderItems.setVisibility(View.VISIBLE);
        } else {
            holder.tvOrderItems.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }
}
