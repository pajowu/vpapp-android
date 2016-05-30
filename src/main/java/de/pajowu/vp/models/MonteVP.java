package de.pajowu.vp.models;

import java.util.ArrayList;
import android.content.Context;
import de.pajowu.vp.R; 
public class MonteVP {
	public String date, absent_teachers, absent_classes, changed_teachers, changed_classes, more_information;
	public ArrayList<MonteVPEntry> changes = new ArrayList<MonteVPEntry>();

	public ArrayList<MonteVPEntry> getItemList(Context c, String class_teacher_filter) {
		ArrayList<MonteVPEntry> items = new ArrayList<MonteVPEntry>();
		items.add(0, new MonteVPEntry(c.getString(R.string.vpdate), date));
		items.add(1, new MonteVPEntry(c.getString(R.string.absent_teachers), absent_teachers));
		items.add(2, new MonteVPEntry(c.getString(R.string.absent_classes), absent_classes));
		items.add(3, new MonteVPEntry(c.getString(R.string.changed_teachers), changed_teachers));
		items.add(4, new MonteVPEntry(c.getString(R.string.changed_classes), changed_classes));
		items.add(5, new MonteVPEntry(c.getString(R.string.more), more_information));
		
		for (MonteVPEntry entry : changes) {
			Boolean add = false;
			for (String filter: class_teacher_filter.split(",")){
				String f = filter.trim();
				if(entry.changed_class.contains(f) || entry.teacher.contains(f)){add=true;}
			}
			if (add) {
				items.add(entry);
			}
		}
		return items;
	}
}