package com.example.gingaminga;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class TransactionAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<Transaction> transactionList = new ArrayList<>();

    public void setTransactionList(ArrayList<Transaction> transactionList) {
        this.transactionList = transactionList;
    }

    public TransactionAdapter(Context context) {
        this.context = context;
    }

    @Override
    public int getCount() {
        return transactionList.size();
    }

    @Override
    public Object getItem(int position) {
        return transactionList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View itemView = convertView;

        if (itemView == null) {
            itemView = LayoutInflater.from(context).inflate(R.layout.item_transaction, parent, false);
        }

        ViewHolder viewHolder = new ViewHolder(itemView);

        Transaction transaction = (Transaction) getItem(position);
        viewHolder.bind(transaction);
        return itemView;
    }

    private class ViewHolder {
        private TextView title, category, nominal;
        ViewHolder(View view) {
            title = view.findViewById(R.id.tv_title);
            category = view.findViewById(R.id.tv_category);
            nominal = view.findViewById(R.id.tv_nominal);
        }

        void bind(Transaction transaction) {

            title.setText(transaction.getTitle());
            category.setText(transaction.getCategory());
            nominal.setText(String.format("Rp%,d", Integer.parseInt(transaction.getNominal())).replaceAll(",", ".")+",00");
//            nominal.setText(String.format("Rp%,d,-", 123456));
//            nominal.setText("hahhaha");
        }
    }
}
