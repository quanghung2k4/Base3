package com.learn.base3;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.learn.base3.databinding.ActivityMovieDetailBinding;
import com.learn.base3.model.Movie;

public class MovieDetailActivity extends AppCompatActivity {

    private ActivityMovieDetailBinding binding;
    private Movie movie;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMovieDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("");
        }

        movie = (Movie) getIntent().getSerializableExtra("movie");
        if (movie != null) {
            displayMovieDetails();
        }

        binding.btnBookNow.setOnClickListener(v -> {
            Intent intent = new Intent(MovieDetailActivity.this, BookingActivity.class);
            intent.putExtra("movie", movie);
            startActivity(intent);
        });
    }

    private void displayMovieDetails() {
        binding.tvDetailTitle.setText(movie.getTitle());
        binding.tvDetailGenre.setText(movie.getGenre());
        binding.tvDetailRating.setText("⭐ " + movie.getRating());
        binding.tvDetailDescription.setText(movie.getDescription());

        Glide.with(this)
                .load(movie.getImageUrl())
                .placeholder(R.drawable.ic_launcher_background)
                .into(binding.ivMovieBackdrop);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}