package cn.moqi.android.xposed.zhihu;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;

public class AvatarShow implements IXposedHookLoadPackage {
	
	private final String imageViewActivity="com.zhihu.android.ui.activity.ImageViewActivity";
	private final int tagKey=1024536421;
	
	private View.OnClickListener show=new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			String bigURL=(String) v.getTag(tagKey);
			if(bigURL!=null){
				Context context=v.getContext();
				Intent intent=new Intent();
				intent.setClassName(context, imageViewActivity);
				
				ArrayList<String> prm = new ArrayList<String>(1);
				prm.add(bigURL);
				intent.putStringArrayListExtra("extra_image_urls", prm);
				
				context.startActivity(intent);
			}
		}
	};
	
	@Override
	public void handleLoadPackage(LoadPackageParam lpparam) throws Throwable {
		
		if (!lpparam.packageName.equals("com.zhihu.android"))
	        return;
		
		XposedHelpers.findAndHookMethod("com.zhihu.android.widget.AvatarView", lpparam.classLoader, "a",
				lpparam.classLoader.loadClass("com.zhihu.android.api.model.User"),
				lpparam.classLoader.loadClass("com.zhihu.android.api.util.ImageUtils$ImageSize"),
				new XC_MethodHook() {
					@Override
					protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
						Object user = param.args[0];
						String avatarUrl = (String) XposedHelpers.callMethod(user, "getAvatarUrl");
						if(avatarUrl!=null){
							int _index=avatarUrl.lastIndexOf('_');
							String bigURL=_index!=-1?avatarUrl.substring(0, _index)+avatarUrl.substring(_index+2):avatarUrl;
							XposedHelpers.callMethod(param.thisObject, "setTag", tagKey,bigURL);
							XposedHelpers.callMethod(param.thisObject, "setOnClickListener",show);
						}
					}
				}
		);
	}

}
