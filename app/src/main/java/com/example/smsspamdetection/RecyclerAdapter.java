package com.example.smsspamdetection;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.MyViewHolder> {

    private ArrayList<Message> smsList;

    public RecyclerAdapter(ArrayList<Message> smsList){
        this.smsList = smsList;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        private TextView user,message,date;

        public MyViewHolder(final View view){
            super(view);
            user = view.findViewById(R.id.sender);
            message = view.findViewById(R.id.messageContent);
            date = view.findViewById(R.id.time_date);
        }
    }


    @NonNull
    @Override
    public RecyclerAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.sms_item,parent,false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerAdapter.MyViewHolder holder, int position) {
        String user = smsList.get(position).getSender();
        holder.user.setText(user);
        String msg = smsList.get(position).getMessage();
        holder.message.setText(msg);
        String date = smsList.get(position).getDate();
        holder.date.setText(date);


    }

    @Override
    public int getItemCount() {
        return smsList.size();
    }
}
