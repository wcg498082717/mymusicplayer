package com.chengguangdown;

import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.chengguangyingyin.R;

public class ActivityDown extends FragmentActivity {

	private static final String TAG = ActivityDown.class.getSimpleName();

	private ProgressBar mProgressBar;
	private Button start;
	private Button pause;
	private Button delete;
	private Button reset;
	private TextView total;

	private int max;

	private DownloadUtil mDownloadUtil;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_down);
		mProgressBar = (ProgressBar) findViewById(R.id.progressBar1);
		start = (Button) findViewById(R.id.button_start);
		pause = (Button) findViewById(R.id.button_pause);
		delete = (Button) findViewById(R.id.button_delete);
		reset = (Button) findViewById(R.id.button_reset);
		total = (TextView) findViewById(R.id.textView_total);
		String urlString = "http://bbra.cn/Uploadfiles/imgs/20110303/fengjin/013.jpg";
		Log.i("wcg", urlString);
		String localPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/chengguangyingyin";
		mDownloadUtil = new DownloadUtil(2, localPath, "abc.jpg", urlString, this);
		mDownloadUtil.setOnDownloadListener(new DownloadUtil.OnDownloadListener() {

			@Override
			public void downloadStart(int fileSize) {
				// TODO Auto-generated method stub
				max = fileSize;
				mProgressBar.setMax(fileSize);
			}

			@Override
			public void downloadProgress(int downloadedSize) {
				// TODO Auto-generated method stub
				Log.w(TAG, "Compelete::" + downloadedSize);
				mProgressBar.setProgress(downloadedSize);
				total.setText((int) downloadedSize * 100 / max + "%");
			}

			@Override
			public void downloadEnd() {
				// TODO Auto-generated method stub
			}
		});

		start.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				mDownloadUtil.start();
			}
		});
		pause.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				mDownloadUtil.pause();
			}
		});
		delete.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				mDownloadUtil.delete();
			}
		});
		reset.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				mDownloadUtil.reset();
			}
		});
	}

}
