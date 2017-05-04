package edu.slu.parks.healthwatch.help;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import edu.slu.parks.healthwatch.R;
import edu.slu.parks.healthwatch.model.BaseRecyclerAdapter;

/**
 * Created by okori on 08-Apr-17.
 */

public class HelpListAdapter extends BaseRecyclerAdapter<IHelp, HelpListAdapter.ViewHolder> {
    private final IHelpListAdapterListener listener;

    public HelpListAdapter(IHelpListAdapterListener listener) {
        this.listener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new HelpListAdapter.ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.help_item, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final IHelp help = data.get(position);

        holder.title.setText(help.getTitle());
        holder.summary.setText(help.getSummary());

        holder.root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onClick(help);
            }
        });
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        View root;
        TextView title;
        TextView summary;

        ViewHolder(View root) {
            super(root);
            this.root = root;
            title = (TextView) root.findViewById(R.id.title);
            summary = (TextView) root.findViewById(R.id.summary);
        }
    }
}
