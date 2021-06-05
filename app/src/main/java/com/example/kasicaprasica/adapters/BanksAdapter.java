package com.example.kasicaprasica.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.kasicaprasica.R;
import com.example.kasicaprasica.database.DatabaseHelper;
import com.example.kasicaprasica.fragments.AccountFragment;
import com.example.kasicaprasica.interfaces.BanksRecyclerViewInterface;
import com.example.kasicaprasica.models.Bank;

import java.util.ArrayList;
import java.util.Locale;

public class BanksAdapter extends RecyclerView.Adapter<BanksAdapter.BanksViewHolder> {

    private ArrayList<Bank> banks;
    private ArrayList<Bitmap> banksBitmapImage;
    private int[] listaSlikica = {R.mipmap.hrvatska_kuna_symbol, R.mipmap.pound_sterling_symbol};
    private final Context context;
    private DatabaseHelper helper;
    private final BanksRecyclerViewInterface banksRecyclerViewInterface;

    public BanksAdapter(Context ct, BanksRecyclerViewInterface inter) {
        context = ct;
        banksRecyclerViewInterface = inter;
        banksBitmapImage = new ArrayList<Bitmap>();
    }

    public BanksAdapter(Context ct, ArrayList<Bank> banks, BanksRecyclerViewInterface inter) {
        context = ct;
        this.banks = banks;
        banksRecyclerViewInterface = inter;
        banksBitmapImage = new ArrayList<Bitmap>();
    }

    public void setItems(ArrayList<Bank> banks) {
        this.banks = banks;
    }

    @Override
    public void onViewRecycled(@NonNull BanksViewHolder holder) {
        super.onViewRecycled(holder);
    }

    @NonNull
    @Override
    public BanksViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.my_row_banks, parent, false);
        return new BanksAdapter.BanksViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BanksViewHolder holder, final int position) {
        holder.ordinal_number.setText(String.format(Locale.getDefault(), "%d", banks.get(position).getId()));
        holder.bank_title.setText(banks.get(position).getBankName());
        holder.bank_creation.setText(banks.get(position).getDateOfCreation());
        // ??
        holder.bank_value_in_default_currency.setText(banks.get(position).getValue());
        holder.imageView.setImageBitmap(generatePhoto(banks.get(position).getImagePath()));
        //holder.imageView.setImageResource(listaSlikica[position]);

        holder.removeGumb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int id = banks.get(position).getId();
                /*Log.w("Vju:", view.toString());
                Log.w("Redni broj:", String.valueOf(banks.get(position).getId()));
                Log.w("Naziv Banke:", banks.get(position).getBankName());
                Log.w("Pozicija:", String.valueOf(position));*/
                obrisiKasicu(id);
            }
        });

        holder.editGumb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int id = banks.get(position).getId();
                urediPodatke(id);
            }
        });


        /*holder.removeGumb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.w("Vju:", view.toString());
                Log.w("Naslov:", data.toString());
                Log.w("Pozicija:", String.valueOf(position) );

                listaBrojaca.set(position, listaBrojaca.get(position) - 1);
                if(listaBrojaca.get(position) == 0) {
                    holder.removeGumb.setVisibility(View.INVISIBLE);
                }
                holder.title.setText(" x " + listaBrojaca.get(position));
                posaljiVrijednost(position, listaBrojaca);
            }
        });*/
    }



    @Override
    public int getItemCount() {
        return banks.size();
    }

    //custom funkcije za interface
    private void obrisiKasicu(int position) {
        banksRecyclerViewInterface.onSmeceClick(position);
    }
    private void urediPodatke(int id) {
        banksRecyclerViewInterface.onPenClick(id);
    }


    //VIEW HOLDER CLASS
    public class BanksViewHolder extends RecyclerView.ViewHolder {

        TextView ordinal_number;
        TextView bank_title;
        TextView bank_creation;
        TextView bank_value_in_default_currency;
        ImageView imageView;
        ConstraintLayout redLayoutBankList;
        ImageButton editGumb;
        ImageButton removeGumb;



        public BanksViewHolder(@NonNull View itemView) {
            super(itemView);

            ordinal_number = itemView.findViewById(R.id.redni_broj);
            bank_title = itemView.findViewById(R.id.naziv_banke);
            redLayoutBankList = itemView.findViewById(R.id.redLayoutBanks);
            bank_creation = itemView.findViewById(R.id.datum_otvaranja_banke_tekst_polje);
            bank_value_in_default_currency = itemView.findViewById(R.id.ukupan_iznos_default_curr_tekst_polje);
            imageView = itemView.findViewById(R.id.bank_row_image);
            editGumb = itemView.findViewById(R.id.edit_name_gumb_bank_list);
            removeGumb = itemView.findViewById(R.id.ponisti_gumb_bank_list);

        }
    }

    private Bitmap generatePhoto(String path) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inScaled = true;
        options.inSampleSize = 4;
        /*options.inJustDecodeBounds = true;
        int srcWidth  = options.outWidth;
        int srcHeight = options.outHeight;
        options.inDensity = srcWidth;
        options.inTargetDensity = srcWidth * options.inSampleSize;*/

        return BitmapFactory.decodeFile(path, options);

    }



}
