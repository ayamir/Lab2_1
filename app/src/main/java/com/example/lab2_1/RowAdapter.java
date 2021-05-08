package com.example.lab2_1;

import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.example.lab2_1.Row.ADD;
import static com.example.lab2_1.Row.DEL;
import static com.example.lab2_1.Row.UPD;

public class RowAdapter extends RecyclerView.Adapter<RowAdapter.RowViewHolder> {
    public static List<Row> rows;
    public static HashMap<Integer, Integer> actionMap = new HashMap<>();

    @Override
    public boolean onFailedToRecycleView(@NonNull RowViewHolder holder) {
        return super.onFailedToRecycleView(holder);
    }

    public RowAdapter(List<Row> rows) {
        RowAdapter.rows = rows;
        int i = 0;
        for (Row ignored : rows) {
            actionMap.put(i, 0);
            i++;
        }
    }

    public static class RowViewHolder extends RecyclerView.ViewHolder {
        EditText etId;
        EditText etName;
        EditText etGender;
        EditText etDepartment;
        EditText etSalary;
        Spinner actionSpinner;

        public RowViewHolder(@NonNull View itemView) {
            super(itemView);
            etId = itemView.findViewById(R.id.et_id);
            etName = itemView.findViewById(R.id.et_name);
            etGender = itemView.findViewById(R.id.et_gender);
            etDepartment = itemView.findViewById(R.id.et_department);
            etSalary = itemView.findViewById(R.id.et_salary);
            actionSpinner = itemView.findViewById(R.id.action_spinner);
        }
    }

    @NonNull
    @Override
    public RowViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.table_row_layout, parent, false);
        return new RowViewHolder(view);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onBindViewHolder(@NonNull RowViewHolder holder, int position) {
        Row row = rows.get(position);
        holder.etId.setText(row.getId());
        holder.etName.setText(row.getName());
        holder.etGender.setText(row.getGender());
        holder.etDepartment.setText(row.getDepartment());
        holder.etSalary.setText(row.getSalary());

        holder.actionSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selected = parent.getItemAtPosition(position).toString().trim();
                switch (selected) {
                    case "添加":
                        actionMap.replace(position, ADD);
                        break;
                    case "更新":
                        actionMap.replace(position, UPD);
                        break;
                    case "删除":
                        actionMap.replace(position, DEL);
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return rows.size();
    }

}
