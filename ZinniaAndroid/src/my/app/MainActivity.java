package my.app;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends Activity{
	@Override
    public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
		final DrawView draw = (DrawView)findViewById(R.id.viewDraw);
		final CandidateListView result = (CandidateListView)findViewById(R.id.linearLayoutSearchResult);
		draw.setResultView(result);
		
		result.setInputText((TextView)findViewById(R.id.textViewResult));
		
		
		Button clearButton = (Button)findViewById(R.id.buttonClear);
		clearButton.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				draw.clear();
				result.clear();
			}});
	}
}
