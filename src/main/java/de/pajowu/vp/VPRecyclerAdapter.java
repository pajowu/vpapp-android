package de.pajowu.vp;
 
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
 
import java.util.List;
 
import android.util.Log;

import de.pajowu.vp.models.MonteVPEntry;
public class VPRecyclerAdapter extends RecyclerView.Adapter<VPRecyclerAdapter.MyViewHolder> {
 
    private List<MonteVPEntry> entryList;
 
    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView class_name, hour, subject, teacher, room, description;
 
        public MyViewHolder(View view) {
            super(view);
            class_name = (TextView) view.findViewById(R.id.class_name);
            hour = (TextView) view.findViewById(R.id.hour);
            subject = (TextView) view.findViewById(R.id.subject);
            teacher = (TextView) view.findViewById(R.id.teacher);
            room = (TextView) view.findViewById(R.id.room);
            description = (TextView) view.findViewById(R.id.description);
        }
    }
 
 
    public VPRecyclerAdapter(List<MonteVPEntry> entryList) {
        this.entryList = entryList;
    }
 
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.lesson_change, parent, false);
 
        return new MyViewHolder(itemView);
    }
 
    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        MonteVPEntry entry = entryList.get(position);
        holder.class_name.setText(entry.changed_class);
        holder.hour.setText(entry.hour);
        holder.subject.setText(entry.subject);
        holder.teacher.setText(entry.teacher);
        holder.room.setText(entry.room);
        holder.description.setText(entry.desc);
        ViewGroup.LayoutParams params = holder.class_name.getLayoutParams();
        if (entry.real != null && !entry.real) {
            
            params.width = ViewGroup.LayoutParams.WRAP_CONTENT;
            
        } else {
            params.width = 0;
        }
        holder.class_name.setLayoutParams(params);
    }
 
    @Override
    public int getItemCount() {
        return entryList.size();
    }
}