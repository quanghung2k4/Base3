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
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.messaging.FirebaseMessaging;
import com.learn.base3.adapter.MovieAdapter;
import com.learn.base3.databinding.ActivityMainBinding;
import com.learn.base3.model.Movie;

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
        fetchMovies();
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
        FirebaseMessaging.getInstance().subscribeToTopic("movie_reminders")
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Subscribed successfully
                    }
                });
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
                        if (movieList.isEmpty()) {
                            addDummyMovies();
                        }
                    } else {
                        Toast.makeText(this, "Error getting movies", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void addDummyMovies() {
        String[] titles = {"Spider-Man: Across the Spider-Verse", "Oppenheimer", "Barbie", "Dune: Part Two"};
        String[] genres = {"Animation, Action", "Biography, Drama", "Comedy, Fantasy", "Sci-Fi, Adventure"};
        String[] images = {
                "https://m.media-amazon.com/images/M/MV5BNThiZjA3MjItZGY5Ni00ZmJhLWEwN2EtOTBlYTA4Y2E0M2ZmXkEyXkFqcGc@._V1_.jpg",
                "https://m.media-amazon.com/images/M/MV5BN2JkMDc5MGQtZjg3YS00NmFiLWIyZmQtZTJmNTM5MjVmYTQ4XkEyXkFqcGc@._V1_.jpg",
                "https://m.media-amazon.com/images/M/MV5BNjU3N2QxNzYtMjk1NC00MTc2LTk5OTQtMWMyZDY3ZmM5N2VlXkEyXkFqcGc@._V1_.jpg",
                "https://m.media-amazon.com/images/M/MV5BNTc0YmQxMjEtODI5MC00NjFiLTlkMWUtOGQ5NjFmYWUyZGJhXkEyXkFqcGc@._V1_FMjpg_UX1000_.jpg"
        };

        for (int i = 0; i < titles.length; i++) {
            Movie movie = new Movie(null, titles[i], images[i], "An exciting movie experience with " + titles[i], 4.5 + (i * 0.1), genres[i]);
            db.collection("movies").add(movie);
        }
        fetchMovies();
    }

    @Override
    public void onMovieClick(Movie movie) {
        Intent intent = new Intent(MainActivity.this, MovieDetailActivity.class);
        intent.putExtra("movie", movie);
        startActivity(intent);
    }
}