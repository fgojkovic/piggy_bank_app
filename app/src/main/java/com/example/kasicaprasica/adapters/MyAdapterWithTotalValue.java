package com.example.kasicaprasica.adapters;

import android.content.Context;
import android.media.MediaPlayer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;
import com.example.kasicaprasica.R;
import com.example.kasicaprasica.interfaces.MyInterface;
import com.example.kasicaprasica.models.Pictures;
import java.util.ArrayList;

public class MyAdapterWithTotalValue extends RecyclerView.Adapter<MyAdapterWithTotalValue.MyViewHolder> {


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

    public MyAdapterWithTotalValue(Context ct, ArrayList<String> listaStringova, ArrayList<Pictures> listaSlika, ArrayList<Double> listaVrijednosti, MyInterface inter) {
        context = ct;
        this.listaSlika = listaSlika;
        this.listaStringova = listaStringova;
        listaBrojaca = new ArrayList<Integer>();
        myInterface = inter;
        this.listaVrijednosti = listaVrijednosti;
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
        View view = inflater.inflate(R.layout.my_row_with_total_value, parent, false);
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

        holder.image.setImageResource(listaSlika.get(position).getLocation());

        holder.totalValueText.setText(String.valueOf(listaVrijednosti.get(position)));
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
        TextView totalValueText;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.amount_of_money);
            image = itemView.findViewById(R.id.money_picture);
            redLayout = itemView.findViewById(R.id.redLayout);
            totalValueText = itemView.findViewById(R.id.total_value);

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
