package com.example.kasicaprasica.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.example.kasicaprasica.R;
import com.example.kasicaprasica.interfaces.BankStatusInterface;
import com.example.kasicaprasica.models.Bank;
import com.example.kasicaprasica.models.Pictures;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class MyHorizontalAdapter extends RecyclerView.Adapter<MyHorizontalAdapter.BankStatusViewHolder> {

    private ArrayList<Bank> banks;

    //Slike
    ArrayList<Pictures> listaSlika;

    //Vrijednosti pojedine valute
    ArrayList<Double> values;

    private Context context;
    private BankStatusInterface bankStatusInterface;


    public MyHorizontalAdapter(Context context, ArrayList<Pictures> listaSlika, BankStatusInterface bankStatusInterface, ArrayList<Double> values) {
        this.context = context;
        this.listaSlika = listaSlika;
        this.bankStatusInterface = bankStatusInterface;
        this.values = values;
    }

    @NonNull
    @Override
    public BankStatusViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.my_horizontal_row, parent, false);
        return new BankStatusViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BankStatusViewHolder holder, final int position) {
        holder.image.setImageResource(listaSlika.get(position).getLocation());
        holder.image2.setImageResource(listaSlika.get(position).getLocation());

        //DecimalFormat formater = new DecimalFormat("#.##");
        DecimalFormat formater = new DecimalFormat("0.00");
        String string = formater.format(values.get(position));
        holder.value.setText(string);

        /*holder.detailsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                *//*Toast.makeText(context, "PRITISNUT SAM!", Toast.LENGTH_SHORT).show();*//*
                Pictures p = listaSlika.get(position);
                onDetailsClick(p.getId());
            }
        });*/
    }

    @Override
    public int getItemCount() {
        return this.listaSlika.size();
    }

    public class BankStatusViewHolder extends RecyclerView.ViewHolder {

        ImageView image;
        ImageView image2;
        TextView value;
        //ImageButton detailsButton;

        public BankStatusViewHolder(@NonNull View itemView) {
            super(itemView);

            image = itemView.findViewById(R.id.status_bank_curr_pic);
            image2 = itemView.findViewById(R.id.status_bank_curr_pic_2);
            value = itemView.findViewById(R.id.status_bank_curr_value);
            //detailsButton = itemView.findViewById(R.id.status_bank_details);

        }
    }

    //custom funkcije za interface
    private void onDetailsClick(int id) {
        bankStatusInterface.onDetailsClick(id);
    }
}
