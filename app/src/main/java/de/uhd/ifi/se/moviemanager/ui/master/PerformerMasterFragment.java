package de.uhd.ifi.se.moviemanager.ui.master;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import de.uhd.ifi.se.moviemanager.R;
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
