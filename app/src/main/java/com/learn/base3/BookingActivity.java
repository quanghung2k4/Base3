package com.learn.base3;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.learn.base3.adapter.SeatAdapter;
import com.learn.base3.databinding.ActivityBookingBinding;
import com.learn.base3.model.Movie;
import com.learn.base3.model.Seat;
import com.learn.base3.model.Ticket;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class BookingActivity extends AppCompatActivity implements SeatAdapter.OnSeatClickListener {

    private ActivityBookingBinding binding;
    private Movie movie;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private List<Seat> seatList;
    private SeatAdapter seatAdapter;
    private Seat selectedSeat = null;
    private final double TICKET_PRICE = 12.00;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityBookingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        movie = (Movie) getIntent().getSerializableExtra("movie");
        if (movie != null) {
            binding.tvBookingMovieTitle.setText(movie.getTitle());
        }

        setupSpinners();
        setupSeatGrid();

        binding.tvTotalPrice.setText("$0.00");

        binding.btnConfirmBooking.setOnClickListener(v -> confirmBooking());
    }

    private void setupSpinners() {
        List<String> theaters = new ArrayList<>();
        theaters.add("CGV Cinema - District 1");
        theaters.add("Lotte Cinema - District 7");
        theaters.add("Galaxy Cinema - Tan Binh");

        ArrayAdapter<String> theaterAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, theaters);
        theaterAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerTheater.setAdapter(theaterAdapter);

        List<String> showtimes = new ArrayList<>();
        showtimes.add("10:00 AM - Today");
        showtimes.add("02:30 PM - Today");
        showtimes.add("07:00 PM - Today");
        showtimes.add("09:30 PM - Today");

        ArrayAdapter<String> showtimeAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, showtimes);
        showtimeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerShowtime.setAdapter(showtimeAdapter);
    }

    private void setupSeatGrid() {
        seatList = new ArrayList<>();
        String[] rows = {"A", "B", "C", "D", "E"};
        for (String row : rows) {
            for (int i = 1; i <= 6; i++) {
                Seat seat = new Seat(row + i);
                if (Math.random() < 0.15) seat.setBooked(true);
                seatList.add(seat);
            }
        }

        seatAdapter = new SeatAdapter(seatList, this);
        binding.rvSeats.setLayoutManager(new GridLayoutManager(this, 6));
        binding.rvSeats.setAdapter(seatAdapter);
    }

    @Override
    public void onSeatClick(Seat seat) {
        if (selectedSeat != null) {
            selectedSeat.setSelected(false);
        }
        
        if (selectedSeat == seat) {
            selectedSeat = null;
            binding.tvSelectedSeat.setText("None");
            binding.tvTotalPrice.setText("$0.00");
        } else {
            selectedSeat = seat;
            selectedSeat.setSelected(true);
            binding.tvSelectedSeat.setText(seat.getName());
            binding.tvTotalPrice.setText(String.format("$%.2f", TICKET_PRICE));
        }
        
        seatAdapter.notifyDataSetChanged();
    }

    private void confirmBooking() {
        if (selectedSeat == null) {
            Toast.makeText(this, "Please select a seat first!", Toast.LENGTH_SHORT).show();
            return;
        }

        String theater = binding.spinnerTheater.getSelectedItem().toString();
        String showtime = binding.spinnerShowtime.getSelectedItem().toString();
        String userId = mAuth.getUid();

        Ticket ticket = new Ticket(
                UUID.randomUUID().toString(),
                userId,
                movie.getId(),
                movie.getTitle(),
                theater,
                showtime,
                TICKET_PRICE,
                selectedSeat.getName()
        );

        binding.btnConfirmBooking.setEnabled(false);
        db.collection("tickets").add(ticket)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(this, "Booking Successful! Reminder set for " + showtime, Toast.LENGTH_LONG).show();
                    // In a real app, you would send this to your backend to schedule an FCM push
                    finishBooking();
                })
                .addOnFailureListener(e -> {
                    binding.btnConfirmBooking.setEnabled(true);
                    Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void finishBooking() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
}