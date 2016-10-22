package org.techconnect.fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import org.centum.techconnect.R;

import java.util.HashSet;
import java.util.Set;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Phani on 2/10/2016.
 */
public class ReportsFragment extends Fragment {

    @Bind(R.id.reports_listView)
    ListView listView;
    @Bind(R.id.noreports_textView)
    TextView noreportsTextView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_reports, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        update();
    }

    private void update() {
        Set<String> reports = getContext().getSharedPreferences("sessions", Context.MODE_PRIVATE).getStringSet("sessions", new HashSet<String>());
        final String reportsArr[] = reports.toArray(new String[reports.size()]);
        String abbreviatedArr[] = new String[reportsArr.length];
        for (int i = 0; i < reportsArr.length; i++) {
            abbreviatedArr[i] = reportsArr[i].substring(0, reportsArr[i].indexOf("\nNotes"));
        }
        listView.setVisibility(reports.size() > 0 ? View.VISIBLE : View.GONE);
        noreportsTextView.setVisibility(reports.size() == 0 ? View.VISIBLE : View.GONE);
        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(),
                android.R.layout.simple_list_item_1, android.R.id.text1, abbreviatedArr
        );
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                new AlertDialog.Builder(getContext())
                        .setTitle("Report")
                        .setMessage(reportsArr[i])
                        .show();
            }
        });
    }
}
