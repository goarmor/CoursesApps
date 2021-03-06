package ru.gdgkazan.githubmvp.screen.commits;

import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.List;

import ru.gdgkazan.githubmvp.R;
import ru.gdgkazan.githubmvp.content.Commit;
import ru.gdgkazan.githubmvp.widget.BaseAdapter;

/**
 * Created by DIMON on 08.09.2017.
 */

public class CommitsAdapter  extends BaseAdapter<CommitHolder, Commit> {

    public CommitsAdapter(@NonNull List<Commit> items) {
        super(items);
    }

    @Override
    public CommitHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new CommitHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_commit, parent, false));
    }

    @Override
    public void onBindViewHolder(CommitHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        Commit commit = getItem(position);
        holder.bind(commit);
    }
}
