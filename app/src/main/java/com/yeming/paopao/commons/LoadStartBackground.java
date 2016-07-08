package com.yeming.paopao.commons;

import android.content.Context;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.FileAsyncHttpResponseHandler;
import com.yeming.paopao.app.YmApplication;
import com.yeming.paopao.bean.StartBackground;
import com.yeming.paopao.utils.LogUtil;
import com.yeming.paopao.utils.NetworkUtil;
import com.yeming.paopao.utils.SharedPreHelperUtil;

import org.apache.http.Header;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;

/**
 * author   YeMing(yeming_1001@163.com)
 * Date:    2015-01-24 10:40
 * version: V1.0
 * Description: 加载启动页图片
 */
public class LoadStartBackground {

    private static final String TAG = "LoadStartBackground";

    private Context context;
    private boolean needUpdate = true;  // 是否需要update
    private SharedPreHelperUtil sharedPreHelperUtil;

    public LoadStartBackground(Context context) {
        this.context = context;
        sharedPreHelperUtil = SharedPreHelperUtil.getInstance(context);
    }


    /**
     * 更新启动页面背景图数据
     */
    public void updateStartBackground() {
        //  Wifi环境下更新图片数据
        if (!NetworkUtil.isWifiConnected(context)) {
            LogUtil.d(TAG, "--------updateStartBackground not wifi---------");
            return;
        }

        if (isFileExist(context)) {
            // 距离上次更新不到24小时则不更新
            if (!SharedPreHelperUtil.checkStartBackgroundTime(context)) {
                LogUtil.d(TAG, "--------updateStartBackground checkStartBackgroundTime---------");
                return;
            }
        }


        if (needUpdate) {
            BmobQuery<StartBackground> bmobQuery = new BmobQuery<StartBackground>();
            bmobQuery.setLimit(3);  //  设置下载三张背景图
            bmobQuery.findObjects(context, new FindListener<StartBackground>() {
                @Override
                public void onSuccess(List<StartBackground> startBackgrounds) {
                    LogUtil.d(TAG, "--------onSuccess---------" + startBackgrounds.size());
                    ArrayList<StartBackground> lists = (ArrayList<StartBackground>) startBackgrounds;
                    DataInfoCache.saveStartBackgrounds(context, lists);  // 保存
                    downloadBackgroundImage();  //  下载
                    sharedPreHelperUtil.setStartBackgroundUpdateTime(); // 设置更新时间

                }

                @Override
                public void onError(int i, String s) {
                    LogUtil.d(TAG, "--------onError---------" + s);
                }
            });
        }
    }

    /**
     * @return 获取显示的图片对象
     */
    public StartBackground getStartBackground() {
        ArrayList<StartBackground> list = DataInfoCache.loadStartBackgrounds(context);
        LogUtil.d(TAG, "--------getStartBackground---------" + list.size());
        ArrayList<StartBackground> cached = new ArrayList();
        for (StartBackground item : list) {
            if (item.isImageCached(context)) {
                cached.add(item);
            }
        }
        int max = cached.size();
        if (max == 0) {
            return new StartBackground();
        }
        int index = new Random().nextInt(max);
        return cached.get(index);
    }

    /**
     * @return
     */
    public int getStartBackgroundCount() {
        return DataInfoCache.loadStartBackgrounds(context).size();
    }

    /**
     * 下载图片
     */
    private void downloadBackgroundImage() {
        //  Wifi环境下更新图片数据
        if (!NetworkUtil.isWifiConnected(context)) {
            return;
        }
        //  下载前删除以前缓存的文件图片
        clearImageDirBeforUpdate(context);

        ArrayList<StartBackground> lists = DataInfoCache.loadStartBackgrounds(context);
        for (StartBackground startBackground : lists) {
            File file = startBackground.getCacheFile(context);
            if (!file.exists()) {
                AsyncHttpClient client = new AsyncHttpClient();
                String url = startBackground.getStartImage().getFileUrl(context); // 图片Url
                client.get(context, url, new FileAsyncHttpResponseHandler(file) {
                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, File file) {
                        LogUtil.d(TAG, "--------onFailure----------" + statusCode);
                    }

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, File file) {
                        LogUtil.d(TAG, "--------onSuccess------statusCode=" + statusCode);
                        LogUtil.d(TAG, "--------onSuccess------file=" + file.getAbsolutePath());

                    }
                });
                // 图片较大，可能有几兆，超时设长一点
                client.setTimeout(10 * 60 * 1000);
            }
        }
    }

    /**
     * @param needUpdate 设置是否更新参数
     */
    public void setNeedUpdate(boolean needUpdate) {
        this.needUpdate = needUpdate;
    }

    /**
     * @param context
     * @return 数据文件是否存在
     */
    public boolean isFileExist(Context context) {
        boolean flag = true;
        File fileDir = new File(context.getFilesDir(), Constant.DATA_CACHE_FILDER);
        File file;
        if (fileDir.exists() || fileDir.isDirectory()) {
            file = new File(fileDir, Constant.START_BACKGROUNDS);
            if (file.exists()) {
                flag = true;
            } else {
                file = new File(context.getFilesDir(), Constant.START_BACKGROUNDS);
                if (file.exists()) {
                    flag = true;
                } else {
                    flag = false;
                }
            }
        } else {
            flag = false;
        }
        return flag;
    }

    /**
     * 更新前删除之前的启动背景图缓存文件夹
     * @param ctx
     * @return
     */
    private void clearImageDirBeforUpdate(Context ctx) {
        final String dirName = "START_BACKGROUND";
        File root = ctx.getExternalFilesDir(null);
        File dir = new File(root, dirName);
        if (!dir.exists() || !dir.isDirectory()) {
            YmApplication.getInstance().clearCache(dir);
            dir.mkdir() ;
        }
    }
}
