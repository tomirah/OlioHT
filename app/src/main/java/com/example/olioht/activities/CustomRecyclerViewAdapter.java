package com.example.olioht.activities;

import android.content.Context;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.olioht.R;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;

/*
Adapter for recyclerview, which associates the data (results from API) with the viewholder views.
 */

public class CustomRecyclerViewAdapter extends RecyclerView.Adapter<CustomRecyclerViewAdapter.ViewHolder> {

    Context context;
    ArrayList<String> stringArrayList;

    public CustomRecyclerViewAdapter(Context context, ArrayList<String> stringArrayList) {
        this.context = context;
        this.stringArrayList = stringArrayList;
    }

    //Constructing new ViewHolder by inflating XML layout file list_view_row
    @Override
    public ViewHolder onCreateViewHolder(@NonNull  ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.list_view_row, parent, false);
        return new ViewHolder(view);
    }

    /*
    Using onBindViewHolder() to associate Viewholder with data. In other words,
     method updated/fetches data (he results of CO2-calculation from API)
     and uses it to fill the ViewHolder layout
     */
    @Override
    public void onBindViewHolder(@NonNull  CustomRecyclerViewAdapter.ViewHolder holder, int position) {
        JSONObject json = null;
        try {
            json = new JSONObject(stringArrayList.get(position));
            Log.d("TAG", json.toString());

            Log.d("TAG", "Meat : " + json.getString("Meat"));
            Log.d("TAG", "Meat : " + json.getString("Dairy"));
            StringBuilder stringBuilder = new StringBuilder();

            stringBuilder.append("<b>Dairy :</b> ").append(json.getInt("Dairy")).append(" kg CO2 eq./year<br>");
            stringBuilder.append("<b>Meat : </b>").append(json.getInt("Meat")).append(" kg CO2 eq./year<br>");
            stringBuilder.append("<b>Plant : </b>").append(json.getInt("Plant")).append(" kg CO2 eq./year<br>");
            stringBuilder.append("<b>Restaurant : </b>").append(json.getInt("Restaurant")).append(" kg CO2 eq./year<br>");
            stringBuilder.append("<b>Total : </b>").append(json.getInt("Total")).append(" kg CO2 eq./year<br>");

            holder.rowResultText.setText(Html.fromHtml(stringBuilder.toString()));

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return stringArrayList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {


        TextView rowResultText;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            rowResultText = itemView.findViewById(R.id.result_tv_row);
        }
    }
}
