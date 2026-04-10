package com.learn.base3;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.learn.base3.adapter.TicketAdapter;
import com.learn.base3.databinding.ActivityTicketsBinding;
import com.learn.base3.model.Ticket;

import java.util.ArrayList;
import java.util.List;

public class TicketsActivity extends AppCompatActivity {

    private ActivityTicketsBinding binding;
    private TicketAdapter adapter;
    private List<Ticket> ticketList;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTicketsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        ticketList = new ArrayList<>();
        adapter = new TicketAdapter(ticketList);

        binding.rvTickets.setLayoutManager(new LinearLayoutManager(this));
        binding.rvTickets.setAdapter(adapter);

        fetchTickets();
    }

    private void fetchTickets() {
        String userId = mAuth.getUid();
        if (userId == null) return;

        db.collection("tickets")
                .whereEqualTo("userId", userId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        ticketList.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Ticket ticket = document.toObject(Ticket.class);
                            ticketList.add(ticket);
                        }
                        adapter.notifyDataSetChanged();
                        
                        if (ticketList.isEmpty()) {
                            Toast.makeText(this, "No tickets found", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(this, "Error fetching tickets", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}