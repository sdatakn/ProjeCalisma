package com.sdatakn.projeenqura.adapter;

import android.annotation.SuppressLint;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sdatakn.projeenqura.ClickItem;
import com.sdatakn.projeenqura.R;
import com.sdatakn.projeenqura.model.BankModel;

import java.util.ArrayList;
import java.util.List;

public class AdapterData extends RecyclerView.Adapter<AdapterData.ViewHolder> {
    LayoutInflater layoutInflater;
    List<BankModel> bankModels;
    List<BankModel> copyList;
    Context context;

    public AdapterData(Context context,List<BankModel> bankModelsses){
        this.bankModels = bankModelsses;
        this.layoutInflater = LayoutInflater.from(context);
        this.context = context;
        copyList = new ArrayList<BankModel>();
        copyList.addAll(bankModelsses);
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.itemdata,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {

        holder.sehir.setText(bankModels.get(position).getSehir()+"\n\n"+ bankModels.get(position).getIlce());
       if (!TextUtils.isEmpty(bankModels.get(position).getSube())) {
           holder.sube.setText(bankModels.get(position).getSube());
       }
       else {
           holder.sube.setText(R.string.jsonnull);
       }


        holder.banka_tip.setText(bankModels.get(position).getTipi());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(context,ClickItem.class);
                Bundle bundle = ActivityOptions.makeCustomAnimation(context, R.anim.right, R.anim.back).toBundle();
                intent.putExtra("sehir", bankModels.get(position).getSehir());
                intent.putExtra("ilce", bankModels.get(position).getIlce());
                intent.putExtra("sube", bankModels.get(position).getSube());
                intent.putExtra("tipi", bankModels.get(position).getTipi());
                intent.putExtra("bank_kod", bankModels.get(position).getBank_kod());
                intent.putExtra("adresadi", bankModels.get(position).getAdresadi());
                intent.putExtra("adres", bankModels.get(position).getAdres());
                intent.putExtra("postakod", bankModels.get(position).getPostakod());
                intent.putExtra("of_line", bankModels.get(position).getOf_line());
                intent.putExtra("of_site", bankModels.get(position).getOf_site());
                intent.putExtra("bolge_koordinat", bankModels.get(position).getBolge_koordinat());
                intent.putExtra("en_yakin_atm", bankModels.get(position).getEn_yakin_atm());


                context.startActivity(intent, bundle);

            }
        });

    }
    @SuppressLint("NotifyDataSetChanged")
    public void searchdata(List<BankModel>actorsList)
    {
        bankModels =new ArrayList<>();
        bankModels.addAll(actorsList);
        notifyDataSetChanged();
    }
    @Override
    public int getItemCount() {
        return bankModels.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView sehir,sube,banka_tip;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            sehir = itemView.findViewById(R.id.sehirilce);
            sube = itemView.findViewById(R.id.sube);
            banka_tip = itemView.findViewById(R.id.banka_tip);

        }
    }
}
