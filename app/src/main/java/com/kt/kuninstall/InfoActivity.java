package com.kt.kuninstall;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.PermissionInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.kingsoft.kuninstall.R;
import com.kt.kuninstall.ShellCommand.CommandResult;
import com.umeng.analytics.MobclickAgent;

public class InfoActivity extends Activity implements Callback {
	private Intent mIntent;
	private PackageInfo pi;
	private ImageView icon;
	private PackageManager pm;
	private Handler mhHandler;
	private String dir;
	private Context context;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.info);

		mhHandler = new Handler(this);
		pm = getPackageManager();
		mIntent = getIntent();
		pi = (PackageInfo) mIntent.getParcelableExtra("pi");

		icon = (ImageView) findViewById(R.id.icon);
		icon.setImageDrawable(pi.applicationInfo.loadIcon(pm));
		TextView tv_name = (TextView) findViewById(R.id.appName);
		tv_name.setText(pi.applicationInfo.loadLabel(pm));
		TextView tv_pkname = (TextView) findViewById(R.id.packageName);
		tv_pkname.setText(pi.applicationInfo.packageName);
		TextView tv_per = (TextView) findViewById(R.id.tv_permission);

		try {
			PackageInfo pkgInfo = pm.getPackageInfo(
					pi.applicationInfo.packageName,
					PackageManager.GET_PERMISSIONS);
			dir = pkgInfo.applicationInfo.sourceDir;

			String[] p = pkgInfo.requestedPermissions;
			String pname = "";
			if (p != null) {

				for (int i = 0; i < p.length; i++) {

					PermissionInfo tmpPermInfo = pm.getPermissionInfo(p[i], 0);
					if (tmpPermInfo != null) {
						CharSequence cs = tmpPermInfo.loadDescription(pm);
						if (cs != null)
							pname += cs.toString() + "\n";
					}
				}
				tv_per.setText(pname);
			}
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}// 通过包名，返回包信息
		Button btn_u = (Button) findViewById(R.id.u_nor);
		Button btn_p = (Button) findViewById(R.id.u_pre);
		Button btn_s = (Button) findViewById(R.id.u_sys);
		btn_s.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (new ShellCommand().canSU()) {
					new Thread() {
						public void run() {
							if (unistallrootsys(dir)) {
								mhHandler.sendEmptyMessage(0);
							} else {
								mhHandler.sendEmptyMessage(1);
							}

						};

					}.start();

				} else {
					Toast.makeText(getBaseContext(), "获取root权限失败", 2000).show();
				}
			}
		});
		btn_u.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Uri packageURI = Uri.parse("package:"
						+ pi.applicationInfo.packageName);
				Intent uninstallIntent = new Intent(Intent.ACTION_DELETE,
						packageURI);
				startActivity(uninstallIntent);
			}
		});
		btn_p.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				if (new ShellCommand().canSU()) {
					Toast.makeText(getBaseContext(), "正在卸载，请稍候。。。", 3000)
							.show();
					new Thread() {
						public void run() {
							if (unistallroot(pi.applicationInfo.packageName)) {
								mhHandler.sendEmptyMessage(0);
							} else {
								mhHandler.sendEmptyMessage(1);
							}

						};

					}.start();

				} else {
					Toast.makeText(getBaseContext(), "获取root权限失败", 2000).show();
				}
			}
		});
	}

	public boolean RootCommand(String command) {
		Process process = null;
		DataOutputStream os = null;
		try {
			// String path = "/data/data/com.kenny.Screen/files/";
			process = Runtime.getRuntime().exec("su ");
			os = new DataOutputStream(process.getOutputStream());
			os.writeBytes(command + "\n");
			// os.writeBytes("chmod 777 /dev/graphics/fb0\n");

			os.writeBytes("exit\n");

			os.flush();
			process.waitFor();
		} catch (Exception e) {
			Log.d("*** DEBUG ***", "ROOT REE" + e.getMessage());
			return false;
		} finally {
			try {
				if (os != null) {
					os.close();
				}
				process.destroy();
			} catch (Exception e) {
			}
		}
		Log.d("*** DEBUG ***", "Root SUC ");
		return true;
	}

	private boolean unistallrootsys(String dir) {
		// Process process = null;
		// DataOutputStream out = null;
		// InputStream in = null;
		try {
			StringBuffer buffer = new StringBuffer();
			buffer.append("chmod 777 " + getPackageCodePath() + "\n");

			buffer.append("mount -o remount /dev/block/mtdblock0 /system  \n");
			String apkPath = dir;
			String odexPath = apkPath.substring(0, apkPath.length() - 3)
					+ "odex";
			buffer.append("pm uninstall  " + pi.packageName + "\n");

			buffer.append("rm -rf " + apkPath + "\n");
			buffer.append("rm -rf " + odexPath + "\n");
			buffer.append("rm -rf data\\data\\" + pi.packageName + "\n");
			buffer.append("exit\n");
			ShellCommand sm = new ShellCommand();
			CommandResult result = sm.suOrSH().runWaitFor(buffer.toString());
			Log.v("wmh", "cmd=" + buffer.toString());
			Log.v("wmh", "result.stderr=" + result.stderr);

			if (result.success()) {
				mhHandler.sendEmptyMessage(0);
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		mhHandler.sendEmptyMessage(1);
		return false;
	}

	// private boolean unistallrootsys(String dir){
	// Process process = null;
	// OutputStream out = null;
	// InputStream in = null;
	// try {
	// // 请求root
	// process = Runtime.getRuntime().exec("su");
	// out = process.getOutputStream();
	// // 调用安装
	//
	//
	// out.write(("mount -o remount /dev/block/mtdblock0 /system " +
	// " \n").getBytes());
	// out.write(("rm -rf " + dir + "\n").getBytes());
	// in = process.getInputStream();
	// int len = 0;
	// byte[] bs = new byte[1024];
	// Toast.makeText(context, "卸载成功", 1000).show();
	// // while (-1 != (len = in.read(bs))) {
	// // String state = new String(bs, 0, len);
	// // if (state.equals("Success\n")) {
	// // // 安装成功后的操作
	// // return true;
	// // }
	// //// else{
	// //// return false;
	// //// }
	// // }
	// } catch (Exception e) {
	// e.printStackTrace();
	// Toast.makeText(context, "卸载失败", 1000).show();
	// return false;
	// } finally {
	// try {
	// if (out != null) {
	// out.flush();
	// out.close();
	// }
	// if (in != null) {
	// in.close();
	// }
	// return true;
	// } catch (IOException e) {
	// e.printStackTrace();
	// return false;
	// }
	// }
	// }
	@SuppressWarnings("finally")
	private boolean unistallroot(String pagname) {
		Process process = null;
		OutputStream out = null;
		InputStream in = null;
		try {
			// 请求root
			process = Runtime.getRuntime().exec("su");
			out = process.getOutputStream();
			// 调用安装
			out.write(("pm uninstall " + pagname + "\n").getBytes());
			in = process.getInputStream();
			int len = 0;
			byte[] bs = new byte[1024];
			while (-1 != (len = in.read(bs))) {
				String state = new String(bs, 0, len);
				if (state.equals("Success\n")) {
					// 安装成功后的操作
					return true;
				}
				// else{
				// return false;
				// }
			}
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			try {
				if (out != null) {
					out.flush();
					out.close();
				}
				if (in != null) {
					in.close();
				}
				return true;
			} catch (IOException e) {
				e.printStackTrace();
				return false;
			}
		}
	}

	@Override
	public boolean handleMessage(Message msg) {
		// TODO Auto-generated method stub
		switch (msg.what) {
		case 0:
			Toast.makeText(getBaseContext(), "卸载成功", 1000).show();
			break;
		case 1:
			Toast.makeText(getBaseContext(), "卸载失败", 1000).show();
			break;
		default:
			break;
		}
		return false;
	}

	protected void onPause() {
		// TODO Auto-generated method stub
		MobclickAgent.onPause(this);
		super.onPause();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		MobclickAgent.onResume(this);
		super.onResume();
	}
}
