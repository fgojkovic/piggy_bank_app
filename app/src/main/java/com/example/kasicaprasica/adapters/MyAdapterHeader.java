package com.example.kasicaprasica.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.kasicaprasica.R;

public class MyAdapterHeader extends RecyclerView.Adapter<MyAdapterHeader.MyViewHolder>{

    private Context context;

    public MyAdapterHeader(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.my_row_total_values_header, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 1;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView moneyPictureText;
        TextView amountOfMoneyText;
        TextView totalValueText;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            moneyPictureText = itemView.findViewById(R.id.picture_header_title);
            amountOfMoneyText = itemView.findViewById(R.id.amount_of_money_header_title);
            totalValueText = itemView.findViewById(R.id.total_value_header_title);
        }
    }
}
