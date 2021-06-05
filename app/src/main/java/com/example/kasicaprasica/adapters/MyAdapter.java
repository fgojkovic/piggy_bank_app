package com.example.kasicaprasica.adapters;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.Parcelable;
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
import com.example.kasicaprasica.fragments.EntryFragment;
import com.example.kasicaprasica.interfaces.MyInterface;
import com.example.kasicaprasica.models.Pictures;

import java.util.ArrayList;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {


    //količina coinova x 0
    String data = "x 0";
    ArrayList<String> listaStringova;

    //slike
    ArrayList<Pictures> listaSlika;

    Context context;
    ArrayList<Integer> listaBrojaca;
    ArrayList<Double> listaVrijednosti;

    private MyInterface myInterface;

    //varijabla da očisti gumbe kanta za smeće po pojdinom itemu
    private boolean ciscenje = false;

    private MediaPlayer player;

    /*private MyInterface listener;

    public MyAdapter(MyInterface listener){
        this.listener = listener;
    }*/

    public MyAdapter(Context ct, ArrayList<String> listaStringova, ArrayList<Pictures> listaSlika, MyInterface inter) {
        context = ct;
        this.listaSlika = listaSlika;
        this.listaStringova = listaStringova;
        listaBrojaca = new ArrayList<Integer>();
        myInterface = inter;
        listaVrijednosti = new ArrayList<Double>();
        popuniListuBrojacaIVrijednosti();

    }

    public void popuniListuBrojacaIVrijednosti() {
        listaBrojaca.clear();
        listaVrijednosti.clear();
        /*listaStringova.clear();*/
        int br = getItemCount();
        if(br == 0) {
            br = 3;
        }
        for(int i = 0; i < br; i++) {
            listaBrojaca.add(i, 0);
        }

        for(Pictures p : listaSlika) {
            listaVrijednosti.add(p.getValue());
        }
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.my_row, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {
        int velicina = listaSlika.size();
        try {
            holder.title.setText(listaStringova.get(position));
        } catch (IndexOutOfBoundsException e) {
            holder.title.setText(data);
        }
        /*if(listaStringova.get(position) == null) {
            holder.title.setText(data);
        }else {
            holder.title.setText(listaStringova.get(position));
        }
*/
        holder.image.setImageResource(listaSlika.get(position).getLocation());
        //setImageDrawable(getResources().getDrawable(R.mipmap.pet_kuna, getContext().getTheme()));

        if(ciscenje) {
            holder.removeGumb.setVisibility(View.INVISIBLE);
        }

        holder.redLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listaBrojaca.set(position, listaBrojaca.get(position) + 1);
                holder.title.setText(" x " + listaBrojaca.get(position));
                holder.removeGumb.setVisibility(View.VISIBLE);
                posaljiVrijednost(position, listaBrojaca, listaVrijednosti);
            }
        });

        holder.removeGumb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*Log.w("Vju:", view.toString());
                Log.w("Naslov:", data.toString());
                Log.w("Pozicija:", String.valueOf(position) );*/

                listaBrojaca.set(position, listaBrojaca.get(position) - 1);
                if(listaBrojaca.get(position) == 0) {
                    holder.removeGumb.setVisibility(View.INVISIBLE);
                }
                holder.title.setText(" x " + listaBrojaca.get(position));
                posaljiVrijednost(position, listaBrojaca, listaVrijednosti);
            }
        });

        if(velicina - 1 == position && ciscenje) {
            ciscenje = false;
        }
    }

    private void posaljiVrijednost(int position, ArrayList<Integer> lista, ArrayList<Double> values) {
        myInterface.interfaceMethod(position, lista, values);
    }

    @Override
    public int getItemCount() {

        return this.listaSlika.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView title;
        ImageView image;
        ConstraintLayout redLayout;
        ImageButton removeGumb;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.kolicinaNovca);
            image = itemView.findViewById(R.id.slikaNovca);
            redLayout = itemView.findViewById(R.id.redLayout);
            removeGumb = itemView.findViewById(R.id.ponistiGumb);

        }
    }

    public void clearListCounters(){
        listaBrojaca.clear();
        listaVrijednosti.clear();
        ciscenje = true;
    }

    public ArrayList<Integer> getListaBrojaca() {
        return  listaBrojaca;
    }

}
