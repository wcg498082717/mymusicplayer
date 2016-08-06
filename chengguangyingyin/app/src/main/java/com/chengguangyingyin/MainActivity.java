package com.chengguangyingyin;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore.Audio.Media;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.SimpleAdapter;

import com.Cp.cp_mainpage_fragment_first;
import com.Cp.cp_mainpage_fragment_second;
import com.Cp.cp_mainpage_fragment_third;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements OnClickListener,RadioGroup.OnCheckedChangeListener {
	private RadioGroup radioGroup;
	List<Object> musiclists = new ArrayList<Object>();
	List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
	Button play_pause, stop, onplay, downplay, close, exit;
	ActivityReceiver activityReceiver;
	public static final String CTL_ACTION = "wcg_ACTION1";
	public static final String UPDATE_ACTION = "wcg_ACTION2";
	Intent intentservice;

	int status = 0x11;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_mainplay);
		UIinit();
		logic();
		musicList();
		initFragment();

		activityReceiver = new ActivityReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction(UPDATE_ACTION);
		registerReceiver(activityReceiver, filter);
		intentservice = new Intent(this, MusicService.class);
		startService(intentservice);

	}

	private void initFragment() {
		radioGroup = (RadioGroup) findViewById(R.id.mainpageTop_radioGroup);
		radioGroup.setOnCheckedChangeListener(this);
		FragmentManager manager = getSupportFragmentManager();
		FragmentTransaction transaction = manager.beginTransaction();
		cp_mainpage_fragment_first fragment_first = new cp_mainpage_fragment_first();
		cp_mainpage_fragment_second fragment_second = new cp_mainpage_fragment_second();
		cp_mainpage_fragment_third fragment_third = new cp_mainpage_fragment_third();
		transaction.add(R.id.mainpage_fragment,fragment_first);
		transaction.commit();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	public void UIinit() {
		onplay = (Button) this.findViewById(R.id.upson);
		play_pause = (Button) this.findViewById(R.id.play_pause);
		stop = (Button) this.findViewById(R.id.stop);
		onplay = (Button) this.findViewById(R.id.nextson);
		close = (Button) this.findViewById(R.id.close);
		exit = (Button) this.findViewById(R.id.exit);
		downplay = (Button) this.findViewById(R.id.nextson);
	}

	public void logic() {
		play_pause.setOnClickListener(this);
		stop.setOnClickListener(this);
		onplay.setOnClickListener(this);
		downplay.setOnClickListener(this);
		close.setOnClickListener(this);
		exit.setOnClickListener(this);
	}

	@Override
	public void onClick(View source) {
		Intent intent = new Intent(CTL_ACTION);
		switch (source.getId()) {
		case R.id.play_pause: {
			intent.putExtra("control", 1);
			break;
		}
		
		case R.id.stop: {
			intent.putExtra("control", 2);
			break;
		}
		case R.id.upson: {
			intent.putExtra("control", 3);
			break;
		}
		case R.id.nextson: {
			intent.putExtra("control", 4);
			break;
		}
		case R.id.close: {
			this.finish();
			break;
		}
		case R.id.exit: {
			stopService(intentservice);
			this.finish();
			break;
		}
		}
		sendBroadcast(intent);

	}

	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId) {
		FragmentManager manager = getSupportFragmentManager();
		FragmentTransaction transaction = manager.beginTransaction();
		if(R.id.radioBtnOne==checkedId){
			transaction.replace(R.id.mainpage_fragment,new cp_mainpage_fragment_first());
		}
		if(R.id.radioBtnTwo==checkedId){
			transaction.replace(R.id.mainpage_fragment,new cp_mainpage_fragment_second());
		}
		if(R.id.radioBtnThree==checkedId){
			transaction.replace(R.id.mainpage_fragment,new cp_mainpage_fragment_third());
		}
		transaction.commit();
	}

	public class ActivityReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			// 获取Intent中的update消息，update代表播放状态
			int update = intent.getIntExtra("update", -1);
			switch (update) {
			case 0x11: {
				play_pause.setText("播放");
				status = 0x11;
				break;
			}

			// 控制系统进入播放状态
			case 0x12: {
				// 播放状态下设置使用按钮
				play_pause.setText("暂停");
				// 设置当前状态
				status = 0x12;
				break;
			}
			// 控制系统进入暂停状态
			case 0x13: {
				play_pause.setText("播放");
				status = 0x13;
				break;
			}
			}
		}

	}

	/* 播放列表 */
	public void musicList() {
		// 取得指定位置的文件设置显示到播放列表
		String[] music = new String[] { Media._ID, Media.DISPLAY_NAME, Media.TITLE, Media.DURATION, Media.ARTIST,
				Media.DATA };
		Cursor cursor = getContentResolver().query(Media.EXTERNAL_CONTENT_URI, music, null, null, null);
		while (cursor.moveToNext()) {
			Music temp = new Music();
			temp.setFilename(cursor.getString(1));
			temp.setTitle(cursor.getString(2));
			temp.setDuration(cursor.getInt(3));
			temp.setArtist(cursor.getString(4));
			temp.setData(cursor.getString(5));
			musiclists.add(temp);

			Map<String, Object> map = new HashMap<String, Object>();
			map.put("name", cursor.getString(1));
			map.put("artist", cursor.getString(4));
			list.add(map);
		}

		ListView listview = (ListView) findViewById(R.id.musics);
		SimpleAdapter adapter = new SimpleAdapter(this, list, R.layout.musicsshow, new String[] { "name", "artist" },
				new int[] { R.id.name, R.id.artist });
		listview.setAdapter(adapter);
		listview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int current, long id) {

				Intent intent = new Intent(CTL_ACTION);
				intent.putExtra("control", 5);
				intent.putExtra("current", current);
				sendBroadcast(intent);
			}
		});
	}
}