package com.bo.android.runtracker;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

/**
 * A placeholder fragment containing a simple view.
 */
public class RunListFragment extends ListFragment {

    private static final int REQUEST_NEW_RUN = 0;

    private RunDatabaseHelper.RunCursor mCursor;

    public RunListFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        RunManager runManager = RunManager.get(getActivity());
        mCursor = runManager.queryRuns();
        RunCursorAdapter adapter = new RunCursorAdapter(getActivity(), runManager, mCursor);
        setListAdapter(adapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        ((RunCursorAdapter) getListAdapter()).notifyDataSetChanged();
    }

    @Override
    public void onDestroy() {
        mCursor.close();
        super.onDestroy();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_run_list, menu);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        Intent i = new Intent(getActivity(), RunActivity.class);
        i.putExtra(RunActivity.EXTRA_RUN_ID, id);
        startActivity(i);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_new_run:
                Intent i = new Intent(getActivity(), RunActivity.class);
                startActivityForResult(i, REQUEST_NEW_RUN);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (REQUEST_NEW_RUN == requestCode) {
            mCursor.requery();
            ((RunCursorAdapter) getListAdapter()).notifyDataSetChanged();
        }
    }

    private static class RunCursorAdapter extends CursorAdapter {

        private RunManager runManager;
        private RunDatabaseHelper.RunCursor mRunCursor;
        private Drawable activeBackground;
        private Drawable defaultBackground;

        public RunCursorAdapter(Context context, RunManager runManager, RunDatabaseHelper.RunCursor cursor) {
            super(context, cursor, 0);
            activeBackground = context.getResources().getDrawable(android.R.color.darker_gray);
            this.runManager = runManager;
            mRunCursor = cursor;
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            TextView view = (TextView) inflater.inflate(android.R.layout.simple_list_item_1, parent, false);
            defaultBackground = view.getBackground();
            return view;
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            Run run = mRunCursor.getRun();
            TextView startDateTextView = (TextView) view;
            String cellText = context.getString(R.string.cell_text, run.getStartDate());
            startDateTextView.setText(cellText);
            startDateTextView.setBackgroundDrawable(runManager.isTrackingRun(run) ? activeBackground : defaultBackground);
        }

    }
}
