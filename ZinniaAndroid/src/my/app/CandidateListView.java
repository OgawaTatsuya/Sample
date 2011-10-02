package my.app;

import java.util.ArrayList;


import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

class CandidateListView extends LinearLayout implements ShowInterface,ClearInterface{
	private Handler handler = new Handler();
	private org.zinnia.Recognizer recognizer = new org.zinnia.Recognizer();
	private org.zinnia.Character character = new org.zinnia.Character();
	private ShowDelegate show = new ShowDelegate();
	private ClearDelegate clear = new ClearDelegate();
	private TextView inputText; 
	
	public void setInputText(TextView inputText) {
		this.inputText = inputText;
	}
	
	public CandidateListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		recognizer.open("/sdcard/handwriting-ja.model");
	}
	
	private class SearchResultDelegate implements Runnable{
		Draw draw;  
		ArrayList<String> resultList;
		
		private SearchResultDelegate(Draw draw){
			this.draw = draw;
		}
		@Override
		public void run() {
			//Initialize
			resultList = null;
			CandidateListView.this.character.clear();
			if(this.draw != null){
				//for each line
				for(int i = 0;i < draw.size();i++) {  
					 Line line = draw.get(i);
					 //for each point
					 for(int j = 0;j < line.size();j++){
						 CandidateListView.this.character.add(i, line.get(j).x, line.get(j).y); 
					 }
				}
				org.zinnia.Result result = CandidateListView.this.recognizer.classify(CandidateListView.this.character, 10);		
				resultList = new ArrayList<String>();
				for(int k = 0; k < result.size(); k++){
					resultList.add(result.value(k));
				}
				result.dispose();
			}
			handler.post(new Runnable(){
				@Override
				public void run() {
					CandidateListView.this.removeAllViewsInLayout();
					if(resultList != null){
						for( final String str : resultList ){
							Button button = new Button(CandidateListView.this.getContext());
							button.setText((CharSequence)str);
							button.setOnClickListener(new OnClickListener(){
								@Override
								public void onClick(View v) {
									inputText.setText(inputText.getText() + str);
								}});
							CandidateListView.this.addView(button);
						}
					}
				}});
		}
	 } 
	private class ShowDelegate implements ShowInterface{
		Object atomicTask = new Object();
		Runnable nextJob = null;
		TaskManager taskManager;
		
		class TaskManager extends Thread{
			boolean breakFlag = false;
			public boolean wasBreak(){
				return this.breakFlag;
			}
			@Override
			public void run() {
				while(true){
					Runnable currentJob;
					synchronized(atomicTask){
						if(nextJob == null){
							this.breakFlag =true;
							break;
						}else{
							currentJob=nextJob;
							nextJob = null;
						}
					}
					//Delegate run method of latest queueing job.
					currentJob.run();
				}
			}
			
		}
		
		@Override
		public void show(Draw draw){
			synchronized(atomicTask){
				nextJob = new SearchResultDelegate(draw);
			}
			// thread is not executed or break while loop
			if(taskManager == null || !taskManager.isAlive() || taskManager.wasBreak()){
					taskManager = new TaskManager();
					taskManager.start();
			}	
		}
	}
	class ClearDelegate  implements ClearInterface{
		@Override
		public void clear() {
			Thread thread = new Thread(new SearchResultDelegate(null));
			thread.run();
		}
	}
	@Override
	public void clear() {
		// TODO Auto-generated method stub
		this.clear.clear();
		inputText.setText("");
	}
	@Override
	public void show(Draw draw) {
		// TODO Auto-generated method stub
		this.show.show(draw);
	}
}