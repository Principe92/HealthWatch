package edu.slu.parks.healthwatch.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import edu.slu.parks.healthwatch.R;
import edu.slu.parks.healthwatch.async.AddressDownloader;
import edu.slu.parks.healthwatch.database.Record;
import edu.slu.parks.healthwatch.model.IDate;
import edu.slu.parks.healthwatch.model.pressure.PressureType;
import edu.slu.parks.healthwatch.utils.Constants;

/**
 * Created by okori on 12-Jan-17.
 */

public class TableListAdapter extends RecyclerView.Adapter<TableListAdapter.ViewHolder> {
    private final IDate joda;
    private List<Record> records;
    private AddressDownloader addressDownloader;

    public TableListAdapter(IDate joda, Context context) {
        this.joda = joda;
        records = new ArrayList<>();
        addressDownloader = new AddressDownloader(context);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.table_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final Record record = records.get(position);

        holder.systolicView.setText(String.format(Locale.getDefault(), "Systolic: %d mmHg", record.systolic));
        holder.diastolicView.setText(String.format(Locale.getDefault(), "Diastolic: %d mmHg", record.diastolic));

        String cmt = record.comment != null ? record.comment : "";
        holder.commentView.setText(cmt);
        holder.dateView.setText(joda.toString(Constants.DATE_FORMAT, record.date));

        addressDownloader.download(record, holder.locationView);

        holder.card.setBackgroundResource(PressureType.GetType(record.systolic).getColor());
    }

    @Override
    public int getItemCount() {
        return records.size();
    }

    public void setRecords(List<Record> records) {
        this.records = records == null ? new ArrayList<Record>() : records;
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView systolicView;
        TextView diastolicView;
        TextView locationView;
        TextView commentView;
        TextView dateView;
        View card;

        ViewHolder(View itemView) {
            super(itemView);

            card = itemView.findViewById(R.id.card_view);
            systolicView = (TextView) itemView.findViewById(R.id.txt_systolic);
            diastolicView = (TextView) itemView.findViewById(R.id.txt_diastolic);
            locationView = (TextView) itemView.findViewById(R.id.txt_location);
            commentView = (TextView) itemView.findViewById(R.id.txt_comment);
            dateView = (TextView) itemView.findViewById(R.id.txt_date);
        }
    }

}
