/**
 * 每个core实现类都应该继承此类
 * 提供一些基础设施给子类使用
 */
package com.drumge.template.core;

import android.content.Context;

import com.drumge.template.BasicYYHandlerMgr;
import com.drumge.template.YYHandler;
import com.drumge.template.YYHandlerMgr;

/**
 * 所有的IBaseCore接口的实现类都必须继承此抽象类，因为CoreFactory工厂使用此类来区分接口和具体实现类的
 *
 */
public abstract class AbstractBaseCore implements IBaseCore {

	protected YYHandlerMgr sdkHandlerManager;
	
	/**
	 * 
	 */
	public AbstractBaseCore() {
		// 确保有默认构造函数
		
		sdkHandlerManager = BasicYYHandlerMgr.getInstance().getSdkHandlerManager();
	}
	
	protected Context getContext() {
		if ( ICoreManager.getContext()==null){
			return BasicYYHandlerMgr.getInstance().getAppContext();
		}
		return  ICoreManager.getContext();
	}
	
	protected void notifyClients(Class<? extends ICoreClient> clientClass, String methodName, Object... args) {
		ICoreManager.notifyClients(clientClass, methodName, args);
	}
	
	protected void addYYHandler(YYHandler handler) {
		if (sdkHandlerManager==null){
			if (BasicYYHandlerMgr.getInstance().getSdkHandlerManager()!=null){
				BasicYYHandlerMgr.getInstance().getSdkHandlerManager().add(handler);
			}
		}else {
			sdkHandlerManager.add(handler);
		}
	}
	
	protected void removeYYHandler(YYHandler handler) {
		if (sdkHandlerManager==null){
			if (BasicYYHandlerMgr.getInstance().getSdkHandlerManager()!=null){
				BasicYYHandlerMgr.getInstance().getSdkHandlerManager().remove(handler);
			}
		}else {
			sdkHandlerManager.remove(handler);
		}
	}
	
	protected String sendEntRequest(IEntProtocol entProtocol){
		// TODO 此处进行网络请求，即把协议发送过去
		  return "";
	}

	
//	private enum TaskState {
//		NoTask,
//		HasTask,
//		Succeeded,
//		Failed
//	}
	
	/**
	 * core层接口实现很多时候需要先查询db，取得本地cache数据后，立即通知上层，以便上层能给ui快速显示一些东西
	 * 同时发起sdk网络请求，请求成功后再次通知上层，这两次通知的回调是一样的
	 * 在同时存在两种操作的情况下，网络请求得到的数据总是最新的，因此如果网络已经得到结果，数据库读取还未完成的话，已经没有必要了
	 * 这个类是为了管理类似这样的操作，使得通知上层的操作可以重用，且在合适的情况下才发出
	 * 并且还可以提供一些超时控制之类的功能
	 * 注意，如果有db操作的话，使用CoreManager.addEventListener来监听db回调
	 * 用sdkHandler来监听sdk回调
	 */
//	protected abstract class CoreTask {
//
//		protected int timeout = 60;    // 超时时间，秒
//		protected TaskState dbTaskState = TaskState.NoTask;
//		protected TaskState networkTaskState = TaskState.NoTask;
//		protected Handler timerHandler;
//		protected YYHandler taskSdkHandler;  // 子类如果需要使用sdk网络请求，应该在自己的初始化块里生成此对象
//		protected String name;   // 用来标识此任务，可以用类名+方法名的组合，如UserCoreImpl.requestChannelUserInfo
//		protected ICoreClient dbClient;    // db回调
//		
//		/**
//		 * 
//		 */
//		public CoreTask(String name, boolean hasDbTask, boolean hasNetworkTask) {
//			this.name = name;
//			if (hasDbTask) {
//				dbTaskState = TaskState.HasTask;
//			}
//			if (hasNetworkTask) {
//				networkTaskState = TaskState.HasTask;
//			}
//		}
//		
//		/**
//		 * db操作成功的回调里，子类需要调用此函数
//		 * 它会检查当前是否需要调用onNotifySucceed通知上层
//		 * 子类不应该直接调用onNotifySucceed，而应该调用此函数
//		 * @param shouldNotify - 是否要通知上层，有时db操作成功但是也不一定要通知上层，如没有查询到数据，需要等网络结果。
//		 *                       这时设置为false。注意就算设置为true也不一定会通知上层，还要看当前网络请求是否已成功
//		 * @param args - 要传给上层回调的参数列表
//		 */
//		public void dbTaskSucceed(boolean shouldNotify, Object...args) {
//			dbTaskState = TaskState.Succeeded;
//			CoreManager.removeClient(dbClient);
//			// 如果有network任务且已完成，不需要再通知
//			if (shouldNotify && networkTaskState != TaskState.Succeeded) {
//				onNotifySucceed(args);
//			}
//			
//			if (networkTaskState == TaskState.NoTask) {
//				timerHandler.removeCallbacksAndMessages(null);
//				onCleanUp();
//			}
//		}
//		
//		/**
//		 * db操作失败的回调里，子类需要调用此函数
//		 * 它会检查当前是否需要调用onNotifyFail通知上层
//		 * 子类不应该直接调用onNotifyFail，而应该调用此函数
//		 * @param args - 要传给上层回调的参数列表
//		 */
//		public void dbTaskFail(Object...args) {
//			dbTaskState = TaskState.Failed;
//			CoreManager.removeClient(dbClient);
//			// 如果没有network任务，则需要通知
//			if (networkTaskState == TaskState.NoTask) {
//				onNotifyFail(args);
//				timerHandler.removeCallbacksAndMessages(null);
//				onCleanUp();
//			}
//		}
//		
//		/**
//		 * 网络操作成功的回调里，子类需要调用此函数
//		 * 子类不应该直接调用onNotifySucceed，而应该调用此函数
//		 * @param args - 要传给上层回调的参数列表
//		 */
//		public void networkTaskSucceed(Object...args) {
//			networkTaskState = TaskState.Succeeded;
//			
//			// 如果有db任务且还未完成，无需再等待db回调
//			if (dbTaskState == TaskState.HasTask) {
//				CoreManager.removeClient(dbClient);
//				logger.warn("network task succeeded before db task!");
//			}
//			if (taskSdkHandler != null) {
//				removeYYHandler(taskSdkHandler);
//			}
//			
//			timerHandler.removeCallbacksAndMessages(null);
//			onNotifySucceed(args);
//			onCleanUp();
//		}
//		
//		/**
//		 * 网络操作失败的回调里，子类需要调用此函数
//		 * 子类不应该直接调用onNotifyFail，而应该调用此函数
//		 * @param args - 要传给上层回调的参数列表
//		 */
//		public void networkTaskFail(Object...args) {
//			networkTaskState = TaskState.Failed;
//			
//			if (taskSdkHandler != null) {
//				removeYYHandler(taskSdkHandler);
//			}
//			timerHandler.removeCallbacksAndMessages(null);
//			onNotifyFail(args);
//			onCleanUp();
//		}
//		
//		/**
//		 * 子类重载此函数，调用notifyClients来实现真正的通知上层
//		 * 子类不应该直接调用此函数，而应该调用dbTaskSucceed或networkTaskSucceed
//		 * @param args - 要传给上层回调的参数列表
//		 */
//		public abstract void onNotifySucceed(Object...args);
//		
//		/**
//		 * 子类重载此函数，调用notifyClients来实现真正的通知上层
//		 * 子类不应该直接调用此函数，而应该调用dbTaskFail或networkTaskFail
//		 * @param args - 要传给上层回调的参数列表
//		 */
//		public abstract void onNotifyFail(Object...args);
//		
//		/**
//		 * 子类可以重载此函数进行超时处理
//		 */
//		public void onTimeout() {}
//		
//		/**
//		 * 子类可以重载此函数做一些清理动作
//		 * 注意：remove sdkHandler对象、remove db eventListener和stop timer基类都已经做了
//		 * 不需要子类再做
//		 */
//		public void onCleanUp() {}
//		
//		/**
//		 * 子类应该重载此函数，并加入真正执行数据库操作、网络操作的代码
//		 * 注意：子类的重载需要在合适的地方调用基类的start函数
//		 */
//		public void start() {
//			logger.info("start CoreTask " + name);
//			if (dbTaskState == TaskState.HasTask && dbClient != null) {
//				CoreManager.addClient(dbClient);
//			}
//			timerHandler = new Handler();
//			timerHandler.postDelayed(new Runnable() {
//				
//				@Override
//				public void run() {
//					logger.error(name + " timeout!");
//					if (taskSdkHandler != null) {
//						removeYYHandler(taskSdkHandler);
//					}
//					if (dbTaskState != TaskState.NoTask) {
//						CoreManager.removeClient(dbClient);
//					}
//					timerHandler.removeCallbacksAndMessages(null);
//					onTimeout();
//					onCleanUp();
//				}
//			}, timeout * 1000);
//		}
//	}
}
