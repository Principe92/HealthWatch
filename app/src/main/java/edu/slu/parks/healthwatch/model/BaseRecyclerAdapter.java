package edu.slu.parks.healthwatch.model;

import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by okori on 09-Apr-17.
 */

public abstract class BaseRecyclerAdapter<T, V extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<V> {

    protected List<T> data;

    protected BaseRecyclerAdapter() {
        data = new ArrayList<>();
    }

    protected void add(T item) {
        data.add(item);
        notifyItemInserted(data.size() - 1);
    }

    public void addAll(Collection<T> items) {
        data.addAll(items);
        notifyDataSetChanged();
    }


    @Override
    public int getItemCount() {
        return data.size();
    }
}
