package de.uhd.ifi.se.moviemanager.ui.master;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.ArrayList;
import java.util.Set;

import de.uhd.ifi.se.moviemanager.R;
import de.uhd.ifi.se.moviemanager.model.Movie;
import de.uhd.ifi.se.moviemanager.model.Performer;
import de.uhd.ifi.se.moviemanager.ui.adapter.DataRVAdapter;
import de.uhd.ifi.se.moviemanager.ui.detail.PerformerDetailActivity;
import de.uhd.ifi.se.moviemanager.ui.detail.PerformerDetailEditActivity;
import de.uhd.ifi.se.moviemanager.ui.master.comparator.AgeComparator;
import de.uhd.ifi.se.moviemanager.ui.master.comparator.NameComparator;
import de.uhd.ifi.se.moviemanager.ui.master.comparator.RatingComparator;
import de.uhd.ifi.se.moviemanager.ui.view.SortingMenuItem;

/**
 * Performer MasterView.
 */
public class PerformerMasterFragment extends DataMasterFragment<Performer> {

    public static PerformerMasterFragment getInstance(@StringRes int nameId, Set<Performer> movies) {
        PerformerMasterFragment fragment = new PerformerMasterFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(ARGUMENT_NAME_ID, nameId);
        bundle.putParcelableArrayList(ARGUMENT_ORIGINAL_DATA, new ArrayList<>(movies));
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected void addSortingMenuItems() {
        String name = getString(R.string.performer_criterion_name);
        String rating = getString(R.string.performer_criterion_rating);
        String age = getString(R.string.performer_criterion_age);

        sortingMenuItems.add(new SortingMenuItem<>(name,
                new NameComparator<>(Performer::getRatingInStarsWithNumber),
                true));
        sortingMenuItems
                .add(new SortingMenuItem<>(rating, new RatingComparator<>()));
        sortingMenuItems.add(new SortingMenuItem<>(age, new AgeComparator()));
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null)
            originalData = getArguments().getParcelableArrayList(ARGUMENT_ORIGINAL_DATA);
    }

    @Override
    protected DataRVAdapter<Performer> createAdapter() {
        final String constraint = "";
        adapter = new DataRVAdapter<>(this, sortingMenuItems.get(0),
                originalData, constraint);
        adapter.setDetailActivity(PerformerDetailActivity.class);
        adapter.setDetailEditActivity(PerformerDetailEditActivity.class);
        adapter.setRemoveFromStorageMethod(this::warnAndRemoveFromStorage);
        return adapter;
    }

    @Override
    protected void afterCreation() {
        originalData.clear();
        originalData.addAll(model.getPerformers());
        adapter.sortAndFilter();
    }

    @Override
    protected void warnAndRemoveFromStorage(Performer performer) {
        new MaterialAlertDialogBuilder(getContext())
                .setTitle("Warning")
                .setMessage(getString(R.string.deletion_warning_message, performer.getName()))
                .setPositiveButton(R.string.yes, (dialog, which) -> {
                    storage.deletePerformerFile(performer);
                    adapter.removeModelObject(performer);
                    dialog.dismiss();
                }).setNegativeButton(R.string.no, (dialog, which) -> dialog.dismiss())
                .show();
    }
}
