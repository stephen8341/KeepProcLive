package com.marswin89.marsdaemon;

import android.content.Context;

import com.marswin89.marsdaemon.proc.GuardService;
import com.marswin89.marsdaemon.proc.Receiver1;
import com.marswin89.marsdaemon.proc.Receiver2;
import com.marswin89.marsdaemon.proc.Service2;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

/**
 * 在{@link com.henrik.keeplive.ProcApplication #attachBaseContext(Context base)}启动
 */
public class DaemonClient implements IDaemonClient{
	private DaemonConfigurations mConfigurations;
	public DaemonClient(DaemonConfigurations configurations) {
		this.mConfigurations = configurations;
	}
	@Override
	public void onAttachBaseContext(Context base) {
		initDaemon(base);
	}
	

	
	private BufferedReader mBufferedReader;//release later to save time
	
	
	/**
	 * do some thing about daemon
	 * @param base
	 */
	private void initDaemon(Context base) {
		if(mConfigurations == null){
			return ;
		}
		String processName = getProcessName();
		String packageName = base.getPackageName();
		
		if(processName.startsWith(mConfigurations.PERSISTENT_CONFIG.PROCESS_NAME)){
			IDaemonStrategy.Fetcher.fetchStrategy().onPersistentCreate(base, mConfigurations);
		}else if(processName.startsWith(mConfigurations.DAEMON_ASSISTANT_CONFIG.PROCESS_NAME)){
			IDaemonStrategy.Fetcher.fetchStrategy().onDaemonAssistantCreate(base, mConfigurations);
		}else if(processName.startsWith(packageName)){
			IDaemonStrategy.Fetcher.fetchStrategy().onInitialization(base);
		}
		
		releaseIO();
	}
	
	
	/* spend too much time !! 60+ms
	private String getProcessName(){
		ActivityManager am = (ActivityManager) this.getSystemService(Context.ACTIVITY_SERVICE);
		int pid = android.os.Process.myPid();
		List<RunningAppProcessInfo> infos = am.getRunningAppProcesses();
		for (int i = 0; i < infos.size(); i++) {
			RunningAppProcessInfo info = infos.get(i);
			if(pid == info.pid){
				return info.processName;
			}
		}
		return null;
	}
	*/
	
	private String getProcessName() {
		try {
			File file = new File("/proc/" + android.os.Process.myPid() + "/" + "cmdline");
			mBufferedReader = new BufferedReader(new FileReader(file));
			return mBufferedReader.readLine();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * release reader IO
	 */
	private void releaseIO(){
		if(mBufferedReader != null){
			try {
				mBufferedReader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			mBufferedReader = null;
		}
	}


	private static DaemonConfigurations createDaemonConfigurations(){
		DaemonConfigurations.DaemonConfiguration configuration1 = new DaemonConfigurations.DaemonConfiguration(
				"com.henrik.keeplive:service",
				GuardService.class.getCanonicalName(),
				Receiver1.class.getCanonicalName());
		DaemonConfigurations.DaemonConfiguration configuration2 = new DaemonConfigurations.DaemonConfiguration(
				"com.henrik.keeplive:deamonService",
				Service2.class.getCanonicalName(),
				Receiver2.class.getCanonicalName());
		DaemonConfigurations.DaemonListener listener = new MyDaemonListener();
		//return new DaemonConfigurations(configuration1, configuration2);//listener can be null
		return new DaemonConfigurations(configuration1, configuration2, listener);
	}


	static class MyDaemonListener implements DaemonConfigurations.DaemonListener{
		@Override
		public void onPersistentStart(Context context) {
		}

		@Override
		public void onDaemonAssistantStart(Context context) {
		}

		@Override
		public void onWatchDaemonDaed() {
		}
	}

	/**
	 * 新的进程保活方案：文件锁方案兼容5.0以上机型
	 * 技术参考：http://blog.csdn.net/marswin89/article/details/50917098
	 */
	public static void startDaemon(Context base){
		DaemonClient daemonClient = new DaemonClient(createDaemonConfigurations());
		daemonClient.onAttachBaseContext(base);
	}

}
