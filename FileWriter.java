package jcline.filewriter;


import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

public class FileWriter extends AsyncTask<LinkedBlockingQueue<String[]>, Void, Void>{

	/**
	 * params[0] = file name
	 * params[1] = "a" | "n" for append or overwrite
	 * params[2] = "n" | "" for newlines or not
	 * params[3...] = data to write, newline separated or not
	 */
	@Override
	protected Void doInBackground(LinkedBlockingQueue<String[]>... queue) {
		doJob(queue[0]);
		return null;
	}
	
	private void doJob(LinkedBlockingQueue<String[]> queue) {
		boolean append, newline;
		
		while(true) {
			
			append = true;
			newline = true;
			
			String[] params;
			try {
				params = queue.poll(100,TimeUnit.MILLISECONDS);
				if(params == null) {
					if(isCanceled())
						break;
					continue;
				}
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
				continue;
			}

			if(params.length < 3) {
				Log.e("FileWriter", "Incorrect number of arguments! " + params.length);
				continue;
			}

			if(params[1].equals("n"))
				append = false;
			if(params[2].equals(""))
				newline = false;
			
			FileOutputStream f;
			try {
				f = new FileOutputStream(params[0],append);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
				continue;
			}
			try {
				for(int i = 4; i < params.length; ++i) {
					f.write(params[i].getBytes());
					if(newline)
						f.write('\n');
				}
				f.close();
			} catch (IOException e) {
				e.printStackTrace();
				try {
					f.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				continue;
			}

			if(isCancelled() && queue.size() == 0) {
				break;
			}
		}
	}

}

