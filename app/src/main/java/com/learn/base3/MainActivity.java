package com.learn.base3;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.messaging.FirebaseMessaging;
import com.learn.base3.adapter.MovieAdapter;
import com.learn.base3.databinding.ActivityMainBinding;
import com.learn.base3.model.Movie;
import com.learn.base3.model.Showtime;
import com.learn.base3.model.Theater;
import com.learn.base3.model.User;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements MovieAdapter.OnMovieClickListener {

    private ActivityMainBinding binding;
    private MovieAdapter adapter;
    private List<Movie> movieList;
    private FirebaseFirestore db;

    private final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    subscribeToNotifications();
                } else {
                    Toast.makeText(this, "Notification permission denied. You won't receive showtime reminders.", Toast.LENGTH_SHORT).show();
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        db = FirebaseFirestore.getInstance();
        movieList = new ArrayList<>();
        adapter = new MovieAdapter(movieList, this);

        binding.rvMovies.setLayoutManager(new GridLayoutManager(this, 2));
        binding.rvMovies.setAdapter(adapter);

        binding.ivLogout.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
            finish();
        });

        binding.ivViewTickets.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, TicketsActivity.class));
        });

        askNotificationPermission();
        initializeDataIfNeeded();
        fetchMovies();
    }

    private void initializeDataIfNeeded() {
        // Save current user to 'users' collection if not exists
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser != null) {
            User user = new User(firebaseUser.getUid(), firebaseUser.getDisplayName(), firebaseUser.getEmail());
            if (user.getName() == null || user.getName().isEmpty()) user.setName("User " + user.getId().substring(0, 4));
            db.collection("users").document(user.getId()).set(user);
        }

        // Initialize Theaters
        db.collection("theaters").get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult().isEmpty()) {
                addDummyTheaters();
            }
        });

        // Initialize Movies (will trigger showtimes init)
        db.collection("movies").get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult().isEmpty()) {
                addDummyMovies();
            }
        });
    }

    private void addDummyTheaters() {
        String[] names = {"CGV Cinema - District 1", "Lotte Cinema - District 7", "Galaxy Cinema - Tan Binh"};
        String[] addresses = {"123 Le Loi, D1, HCMC", "456 Nguyen Van Linh, D7, HCMC", "789 Cong Hoa, Tan Binh, HCMC"};
        
        for (int i = 0; i < names.length; i++) {
            Theater theater = new Theater(null, names[i], addresses[i]);
            db.collection("theaters").add(theater);
        }
    }

    private void addDummyMovies() {
        String[] titles = {"Spider-Man: Across the Spider-Verse", "Oppenheimer", "Barbie", "Dune: Part Two"};
        String[] genres = {"Animation, Action", "Biography, Drama", "Comedy, Fantasy", "Sci-Fi, Adventure"};
        String[] images = {
                "https://m.media-amazon.com/images/M/MV5BNThiZjA3MjItZGY5Ni00ZmJhLWEwN2EtOTBlYTA4Y2E0M2ZmXkEyXkFqcGc@._V1_.jpg",
                "https://m.media-amazon.com/images/M/MV5BN2JkMDc5MGQtZjg3YS00NmFiLWIyZmQtZTJmNTM5MjVmYTQ4XkEyXkFqcGc@._V1_.jpg",
                "https://m.media-amazon.com/images/M/MV5BNjU3N2QxNzYtMjk1NC00MTc2LTk5OTQtMWMyZDY3ZmM5N2VlXkEyXkFqcGc@._V1_FMjpg_UX1000_.jpg",
                "https://m.media-amazon.com/images/M/MV5BNTc0YmQxMjEtODI5MC00NjFiLTlkMWUtOGQ5NjFmYWUyZGJhXkEyXkFqcGc@._V1_FMjpg_UX1000_.jpg"
        };

        for (int i = 0; i < titles.length; i++) {
            Movie movie = new Movie(null, titles[i], images[i], "An exciting movie experience with " + titles[i], 4.5 + (i * 0.1), genres[i]);
            final int index = i;
            db.collection("movies").add(movie).addOnSuccessListener(documentReference -> {
                // Add showtimes for this movie
                addDummyShowtimes(documentReference.getId());
            });
        }
        fetchMovies();
    }

    private void addDummyShowtimes(String movieId) {
        String[] times = {"10:00 AM", "02:30 PM", "07:00 PM", "09:30 PM"};
        db.collection("theaters").get().addOnSuccessListener(queryDocumentSnapshots -> {
            if (!queryDocumentSnapshots.isEmpty()) {
                String theaterId = queryDocumentSnapshots.getDocuments().get(0).getId();
                for (String time : times) {
                    Showtime showtime = new Showtime(null, movieId, theaterId, time + " - Today", 12.00);
                    db.collection("showtimes").add(showtime);
                }
            }
        });
    }

    private void askNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) ==
                    PackageManager.PERMISSION_GRANTED) {
                subscribeToNotifications();
            } else {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);
            }
        } else {
            subscribeToNotifications();
        }
    }

    private void subscribeToNotifications() {
        FirebaseMessaging.getInstance().subscribeToTopic("movie_reminders");
    }

    private void fetchMovies() {
        binding.progressBar.setVisibility(View.VISIBLE);
        db.collection("movies")
                .get()
                .addOnCompleteListener(task -> {
                    binding.progressBar.setVisibility(View.GONE);
                    if (task.isSuccessful()) {
                        movieList.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Movie movie = document.toObject(Movie.class);
                            movie.setId(document.getId());
                            movieList.add(movie);
                        }
                        adapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(this, "Error getting movies", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public void onMovieClick(Movie movie) {
        Intent intent = new Intent(MainActivity.this, MovieDetailActivity.class);
        intent.putExtra("movie", movie);
        startActivity(intent);
    }
}