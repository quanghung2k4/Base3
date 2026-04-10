package com.learn.base3.adapter;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.learn.base3.databinding.ItemSeatBinding;
import com.learn.base3.model.Seat;

import java.util.List;

public class SeatAdapter extends RecyclerView.Adapter<SeatAdapter.SeatViewHolder> {

    private final List<Seat> seats;
    private final OnSeatClickListener listener;

    public interface OnSeatClickListener {
        void onSeatClick(Seat seat);
    }

    public SeatAdapter(List<Seat> seats, OnSeatClickListener listener) {
        this.seats = seats;
        this.listener = listener;
    }

    @NonNull
    @Override
    public SeatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemSeatBinding binding = ItemSeatBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new SeatViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull SeatViewHolder holder, int position) {
        Seat seat = seats.get(position);
        holder.bind(seat, listener);
    }

    @Override
    public int getItemCount() {
        return seats.size();
    }

    public static class SeatViewHolder extends RecyclerView.ViewHolder {
        private final ItemSeatBinding binding;

        public SeatViewHolder(ItemSeatBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(Seat seat, OnSeatClickListener listener) {
            binding.tvSeatNumber.setText(seat.getName());

            if (seat.isBooked()) {
                binding.cardSeat.setCardBackgroundColor(Color.LTGRAY);
                binding.tvSeatNumber.setTextColor(Color.WHITE);
                binding.getRoot().setEnabled(false);
            } else if (seat.isSelected()) {
                binding.cardSeat.setCardBackgroundColor(Color.parseColor("#E91E63"));
                binding.tvSeatNumber.setTextColor(Color.WHITE);
            } else {
                binding.cardSeat.setCardBackgroundColor(Color.WHITE);
                binding.tvSeatNumber.setTextColor(Color.parseColor("#333333"));
            }

            binding.getRoot().setOnClickListener(v -> {
                if (!seat.isBooked()) {
                    listener.onSeatClick(seat);
                }
            });
        }
    }
}