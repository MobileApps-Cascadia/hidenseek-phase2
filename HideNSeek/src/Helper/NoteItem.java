package Helper;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class NoteItem {
	
	private String Key;
	private String text;
	public String getKey() {
		return Key;
	}
	public void setKey(String key) {
		Key = key;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	
	public static NoteItem getNew() {
		//settign key val using device current time
		//enUS 
		Locale locale = new Locale("en_US");
		Locale.setDefault(locale);
		
		String pattern = "yyyy-MM-dd HH:mm:ss Z";
		SimpleDateFormat formatter = new SimpleDateFormat(pattern);
		//create key value
		String key = formatter.format(new Date());
		
		NoteItem note = new NoteItem();
		note.setKey(key);
		note.setText("");
		return note;
	}

}
