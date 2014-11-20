package Helper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import android.content.Context;
import android.content.SharedPreferences;

public class NotesDataSource {
	
	private static final String PREFKEY = "notes";
	private SharedPreferences notePrefs;
	//pass in context which is the superclass
	public NotesDataSource(Context context) {
		notePrefs = context.getSharedPreferences(PREFKEY, Context.MODE_PRIVATE);
	}

	public List<NoteItem> findAll() {
		
		//retrieve the unsorted data
		Map<String, ?> notesMap = notePrefs.getAll();
		
		//default -> oldest to newest sort
		SortedSet<String> keys = new TreeSet<String>(notesMap.keySet());
		List<NoteItem> noteList = new ArrayList<NoteItem>();
		//loop through the collection of key values and deal with them one at a time
		for (String key : keys) {
			NoteItem note = new NoteItem();
			note.setKey(key);
			note.setText((String) notesMap.get(key));
			noteList.add(note);			
		}
		
		NoteItem note = NoteItem.getNew();
		noteList.add(note);
		return noteList;
	}
	public boolean update(NoteItem note) {
		
		
		SharedPreferences.Editor editor = notePrefs.edit();
		editor.putString(note.getKey(), note.getText());
		editor.commit();		
		return true;
	}
	
	
	//identified by the key of datetime
	public boolean remove(NoteItem note) {
		
		if(notePrefs.contains(note.getKey())) {
		SharedPreferences.Editor editor = notePrefs.edit();
		editor.remove(note.getKey());
		editor.commit();			
	}
		return true;	
	}
}
