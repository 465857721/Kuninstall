package com.kt.kuninstall;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.kingsoft.kuninstall.R;
import com.umeng.analytics.MobclickAgent;
import com.umeng.update.UmengUpdateAgent;

public class MainActivity extends Activity implements Callback {
	private PackageManager pm;
	private ListView lv;
	private MyAdapter adapter;
	ArrayList<HashMap<String, Object>> items = new ArrayList<HashMap<String, Object>>();
	private Context context;
	private List<PackageInfo> packs;
	private List<PackageInfo> packss = new ArrayList<PackageInfo>();
	private ProgressDialog loaddialog;
	private Handler mHandler;
	private int apptype = 0;// 0 系统应用 1 用用户
	private Button btn_sys;
	private Button btn_user;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		MobclickAgent.onError(this);
		UmengUpdateAgent.update(this);

		mHandler = new Handler(this);
		pm = getPackageManager();
		lv = (ListView) findViewById(R.id.listview);
		context = this;
		PackageManager pm = getPackageManager();
		// initData();
		btn_sys = (Button) findViewById(R.id.btn_sys);
		btn_sys.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				apptype = 0;
				intView();
				initData();
			}
		});

		btn_user = (Button) findViewById(R.id.btn_user);
		btn_user.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				apptype = 1;
				intView();
				initData();
			}
		});

		lv.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				Intent i = new Intent(MainActivity.this, InfoActivity.class);
				i.putExtra("pi", packss.get(position));
				startActivity(i);
			}
		});
	}

	private void intView() {
		if (apptype == 0) {
			btn_sys.setBackgroundResource(R.drawable.tab2_left_select);
			btn_sys.setTextColor(getResources().getColor(R.color.white));

			btn_user.setBackgroundResource(R.drawable.tab2_right_unselect);
			btn_user.setTextColor(getResources().getColor(R.color.black));
		} else {
			btn_sys.setBackgroundResource(R.drawable.tab2_left_unselect);
			btn_sys.setTextColor(getResources().getColor(R.color.black));

			btn_user.setBackgroundResource(R.drawable.tab2_right_select);
			btn_user.setTextColor(getResources().getColor(R.color.white));
		}
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		initData();
		super.onResume();
	}

	private void initData() {
		mHandler.sendEmptyMessage(0);
		new Thread() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				packs = pm.getInstalledPackages(0);
				packss.clear();
				items.clear();

				for (PackageInfo pi : packs) {

					if (apptype == 0) {
						if ((pi.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0) {

							HashMap<String, Object> map = new HashMap<String, Object>();
							map.put("icon", pi.applicationInfo.loadIcon(pm));
							map.put("appName", pi.applicationInfo.loadLabel(pm));
							map.put("packageName",
									pi.applicationInfo.packageName);

							items.add(map);
							packss.add(pi);
						}
					} else if (apptype == 1) {
						Log.d("zkzk", "f" + pi.applicationInfo.flags);
						if ((pi.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
							HashMap<String, Object> map = new HashMap<String, Object>();
							map.put("icon", pi.applicationInfo.loadIcon(pm));// 图标
							map.put("appName", pi.applicationInfo.loadLabel(pm));// 应用程序名称
							map.put("packageName",
									pi.applicationInfo.packageName);// 应用程序包名

							items.add(map);
							packss.add(pi);
						}
					}

				}

				adapter = new MyAdapter(context, items, R.layout.list_item,
						new String[] { "icon", "appName", "packageName" },
						new int[] { R.id.icon, R.id.appName, R.id.packageName });
				mHandler.sendEmptyMessage(1);
				super.run();
			}
		}.start();

	}

	class MyAdapter extends SimpleAdapter {
		private int[] appTo;
		private String[] appFrom;
		private ViewBinder appViewBinder;
		private List<? extends Map<String, ?>> appData;
		private int appResource;
		private LayoutInflater appInflater;

		public MyAdapter(Context context, List<? extends Map<String, ?>> data,
				int resource, String[] from, int[] to) {
			super(context, data, resource, from, to);
			appData = data;
			appResource = resource;
			appFrom = from;
			appTo = to;
			appInflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			return createViewFromResource(position, convertView, parent,
					appResource);
		}

		private View createViewFromResource(int position, View convertView,
				ViewGroup parent, int resource) {
			View v;

			if (convertView == null) {
				v = appInflater.inflate(resource, parent, false);
				final int[] to = appTo;
				final int count = to.length;
				final View[] holder = new View[count];

				for (int i = 0; i < count; i++) {
					holder[i] = v.findViewById(to[i]);
				}

				v.setTag(holder);
			} else {
				v = convertView;
			}

			bindView(position, v);
			return v;
		}

		private void bindView(int position, View view) {
			final Map dataSet = appData.get(position);

			if (dataSet == null) {
				return;
			}

			final ViewBinder binder = appViewBinder;
			final View[] holder = (View[]) view.getTag();
			final String[] from = appFrom;
			final int[] to = appTo;
			final int count = to.length;

			for (int i = 0; i < count; i++) {
				final View v = holder[i];

				if (v != null) {
					final Object data = dataSet.get(from[i]);
					String text = data == null ? "" : data.toString();

					if (text == null) {
						text = "";
					}

					boolean bound = false;

					if (binder != null) {
						bound = binder.setViewValue(v, data, text);
					}

					if (!bound) {
						if (v instanceof TextView) {
							setViewText((TextView) v, text);
						} else if (v instanceof ImageView) {
							setViewImage((ImageView) v, (Drawable) data);
						} else {
							throw new IllegalStateException(
									v.getClass().getName()
											+ " is not a "
											+ "view that can be bounds by this SimpleAdapter");
						}
					}
				}
			}
		}

		public void setViewImage(ImageView v, Drawable value) {
			v.setImageDrawable(value);
		}
	}

	@Override
	public boolean handleMessage(Message msg) {
		// TODO Auto-generated method stub
		switch (msg.what) {
		case 0:
			if (loaddialog != null) {
				if (loaddialog.isShowing()) {
					loaddialog.dismiss();
					loaddialog = ProgressDialog.show(context, "深度卸载",
							"正在扫描应用程序,请稍候。。。", true);
				} else {
					loaddialog = ProgressDialog.show(context, "深度卸载",
							"正在扫描应用程序,请稍候。。。", true);
				}
			} else {
				loaddialog = ProgressDialog.show(context, "深度卸载",
						"正在扫描应用程序,请稍候。。。", true);
			}

			break;
		case 1:
			lv.setAdapter(adapter);
			adapter.notifyDataSetChanged();
			if (loaddialog != null) {
				loaddialog.cancel();
			}
			break;
		default:
			break;
		}
		return true;
	}
}
