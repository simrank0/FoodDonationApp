package com.example.android.fooddonation;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class OfferAdapter extends RecyclerView.Adapter<OfferAdapter.ViewHolder> {
    public static final String OFFER_ID = "Offer Id";
    public static final String OFFER_OWNER = "Offer Owner";
    private final Context context;
    private final List<Item> items;
    private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("offers");

    public OfferAdapter(Context context, List<Item> items) {
        this.context = context;
        this.items = items;
    }

    @NonNull
    @Override
    public OfferAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final OfferAdapter.ViewHolder holder, final int position) {
        databaseReference.child(items.get(position).getUserId()).child("location").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                holder.location.setText((String) dataSnapshot.getValue());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        holder.foodName.setText(items.get(position).getFood());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, OfferActivity.class);
                intent.putExtra(OFFER_ID, items.get(position).getId());
                intent.putExtra(OFFER_OWNER, items.get(position).getUserId());
                context.startActivity(intent);

            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView foodName;
        TextView location;

        public ViewHolder(View itemView) {
            super(itemView);
            location = itemView.findViewById(R.id.text_item_location);
            foodName = itemView.findViewById(R.id.text_item_food);
        }
    }
}
