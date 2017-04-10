package edu.slu.parks.healthwatch.health;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import edu.slu.parks.healthwatch.R;
import edu.slu.parks.healthwatch.listener.IHealthListAdapterListener;
import edu.slu.parks.healthwatch.model.BaseRecyclerAdapter;

/**
 * Created by okori on 08-Apr-17.
 */

public class HealthListAdapter extends BaseRecyclerAdapter<Article, HealthListAdapter.ViewHolder> {
    private final IHealthListAdapterListener listener;

    public HealthListAdapter(IHealthListAdapterListener listener) {
        this.listener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new HealthListAdapter.ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.health_item, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final Article article = data.get(position);

        holder.title.setText(article.getTitle());
        holder.summary.setText(article.getSummary());
        holder.link.setText(article.getLink());

        holder.root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.showArticle(article);
            }
        });
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        View root;
        TextView title;
        TextView summary;
        TextView link;

        ViewHolder(View root) {
            super(root);
            this.root = root;
            title = (TextView) root.findViewById(R.id.title);
            summary = (TextView) root.findViewById(R.id.summary);
            link = (TextView) root.findViewById(R.id.link);

        }
    }
}
