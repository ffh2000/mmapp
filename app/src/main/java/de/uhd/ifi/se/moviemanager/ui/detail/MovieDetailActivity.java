package de.uhd.ifi.se.moviemanager.ui.detail;


import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static java.lang.String.format;
import static de.uhd.ifi.se.moviemanager.util.RecyclerViewUtils.setLinearLayoutTo;

import android.content.Intent;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

import de.uhd.ifi.se.moviemanager.R;
import de.uhd.ifi.se.moviemanager.model.ImagePyramid;
import de.uhd.ifi.se.moviemanager.model.Movie;
import de.uhd.ifi.se.moviemanager.model.Performer;
import de.uhd.ifi.se.moviemanager.ui.adapter.PosterAdapter;
import de.uhd.ifi.se.moviemanager.util.DateUtils;

/**
 * Responsible for the Movie DetailView.
 */
public class MovieDetailActivity extends DetailActivity<Movie> {

    private TextView showTitle;
    private TextView showSubtitle;
    private TextView showDescriptionHeader;
    private TextView showDescription;
    private TextView linkedPerformersHeader;
    private RecyclerView linkedPerformersList;
    private PosterAdapter<Performer> linkedPerformersAdapter;
    private TextView showLanguagesHeader;
    private TextView showLanguages;
    private TextView showReleasesHeader;
    private TextView showReleases;
    private TextView showProductionLocationsHeader;
    private TextView showProductionLocations;

    public MovieDetailActivity() {
        super(R.layout.activity_movie_detail, MovieDetailEditActivity.class);
    }

    @Override
    protected void setupLists() {
        setupLinkedPerformersList();
    }

    @Override
    protected void bindViews() {
        showTitle = findViewById(R.id.content_title);
        showSubtitle = findViewById(R.id.content_subtitle);
        showDescriptionHeader = findViewById(R.id.description_header);
        showDescription = findViewById(R.id.description);
        linkedPerformersHeader = findViewById(R.id.linked_performers_header);
        linkedPerformersList = findViewById(R.id.linked_performers);
        showLanguagesHeader = findViewById(R.id.languages_header);
        showLanguages = findViewById(R.id.languages);
        showReleasesHeader = findViewById(R.id.releases_header);
        showReleases = findViewById(R.id.releases);
        showProductionLocationsHeader = findViewById(R.id.production_locations_header);
        showProductionLocations = findViewById(R.id.production_locations);
    }

    private void setupLinkedPerformersList() {
        setLinearLayoutTo(this, linkedPerformersList, LinearLayoutManager.HORIZONTAL);

        linkedPerformersAdapter = new PosterAdapter<>(this,
                currentObject.getPerformers(),
                R.layout.medium_poster);
        linkedPerformersAdapter.setOnItemClickListener(perf -> {
            Intent intent = new Intent(this, PerformerDetailActivity.class);
            intent.putExtra(CURRENT_OBJECT, perf);
            updateAfterLinkedDetailsLauncher.launch(intent);
        });
        linkedPerformersList.addItemDecoration(
                new PosterAdapter.PosterItemDecoration((int) getResources()
                        .getDimension(R.dimen.default_margin)));
        linkedPerformersList.setAdapter(linkedPerformersAdapter);
    }

    @Override
    protected void updateAfterLinkedDetails(Intent result) {
        Optional<Movie> currentModelOpt = model
                .getMovieById(currentObject.getId());
        if (currentModelOpt.isPresent()) {
            currentObject = currentModelOpt.get();
            updateUIWithModelData();
        } else {
            finish();
        }
    }

    @Override
    protected boolean updateAfterEdit(Intent intent) {
        Movie movie = intent.getParcelableExtra(CURRENT_OBJECT);
        if (movie == null) {
            return false;
        }
        currentObject = movie;
        setUpdated(true);
        updateUIWithModelData();
        return true;
    }

    @Override
    protected void updateUIWithModelData() {
        initImageView(ImagePyramid.ImageSize.LARGE);
        hideUnusedViews();

        showTitle.setText(currentObject.getName());
        showSubtitle.setText(format(
                Locale.getDefault(), "%s | %s %nRating: %s | %s",
                currentObject.getWatchDate() != null ?
                        DateUtils.dateToText(currentObject.getWatchDate()) : "Not watched",
                currentObject.getRuntime() > 0 ?
                        currentObject.getRuntime() + " min" : "Unknown runtime",
                currentObject.getRating() >= 0 ?
                        currentObject.getRating() : "Not rated",
                currentObject.getOverallRating() >= 0 ?
                        "Overall rating: " + String.format(Locale.US, "%.1f", currentObject.getOverallRating()) :
                        "No overall rating"
        ));

        showDescription.setText(currentObject.getDescription());
        linkedPerformersAdapter.update(currentObject.getPerformers());
        showLanguages.setText(Optional.ofNullable(currentObject.getLanguages()).map(strings ->
                String.join("\n", strings)).orElse(""));
        showReleases.setText(
                currentObject.getReleases().stream().map(movieRelease -> format(
                                Locale.getDefault(), "%s in %s",
                                DateUtils.dateToText(movieRelease.getDate()),
                                movieRelease.getLocation()))
                        .collect(Collectors.joining("\n")));

        showProductionLocations.setText(
                Optional.ofNullable(currentObject.getProductionLocations()).map(strings ->
                        String.join("\n", strings)).orElse(""));
    }

    private void hideUnusedViews() {
        if (currentObject.getDescription() == null || currentObject.getDescription().isEmpty()) {
            showDescriptionHeader.setVisibility(GONE);
            showDescription.setVisibility(GONE);
        } else {
            showDescriptionHeader.setVisibility(VISIBLE);
            showDescription.setVisibility(VISIBLE);
        }

        if (currentObject.getPerformers().isEmpty()) {
            linkedPerformersHeader.setVisibility(GONE);
            linkedPerformersList.setVisibility(GONE);
        } else {
            linkedPerformersHeader.setVisibility(VISIBLE);
            linkedPerformersList.setVisibility(VISIBLE);
        }

        if (currentObject.getLanguages() == null || currentObject.getLanguages().isEmpty()) {
            showLanguagesHeader.setVisibility(GONE);
            showLanguages.setVisibility(GONE);
        } else {
            showLanguagesHeader.setVisibility(VISIBLE);
            showLanguages.setVisibility(VISIBLE);
        }

        if (currentObject.getReleases() == null || currentObject.getReleases().isEmpty()) {
            showReleasesHeader.setVisibility(GONE);
            showReleases.setVisibility(GONE);
        } else {
            showReleasesHeader.setVisibility(VISIBLE);
            showReleases.setVisibility(VISIBLE);
        }

        if (currentObject.getProductionLocations() == null || currentObject.getProductionLocations().isEmpty()) {
            showProductionLocationsHeader.setVisibility(GONE);
            showProductionLocations.setVisibility(GONE);
        } else {
            showProductionLocationsHeader.setVisibility(VISIBLE);
            showProductionLocations.setVisibility(VISIBLE);
        }
    }

    @Override
    protected void registerSpecificListeners() {
        //not needed
    }

}