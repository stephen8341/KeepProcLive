# KeepProcLive
Android进程保活主流方案集锦

这里面集成的方案包括：

1.  Service指定为START_STICKY 被系统回收的进程会被系统重新拉起

2.  Service设置为前台进程 将后台进程设置为前台进程，提高进程优先级

   

3.  1像素Activity方案 关屏后加载1个像素的Activity到Window，提高锁屏 后的进程优先级

4.  静态广播自启 利用监听开机启动广播、网络变化广播、应用安装删 除等广播，接收到广播后实现自启

5.  JobSchedule (5.0以上)和AlarmManager 利用Android的API某些机制去实现自启

6.   账号同步拉活 利用Android自身的账号同步机制周期拉活

7.   守护进程 :  这块为了解决5.0以上机器拉活的问题，采用了文件锁监听进程死亡的机制，具体参考：http://blog.csdn.net/marswin89/article/details/50916631
