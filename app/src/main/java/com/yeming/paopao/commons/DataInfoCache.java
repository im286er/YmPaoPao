package com.yeming.paopao.commons;

import android.content.Context;

import com.yeming.paopao.bean.Paopao;
import com.yeming.paopao.bean.PicViewImage;
import com.yeming.paopao.bean.Pictrue;
import com.yeming.paopao.bean.StartBackground;
import com.yeming.paopao.bean.User;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

/**
 * author   YeMing(yeming_1001@163.com)
 * Date:    2015-01-24 10:42
 * version: V1.0
 * Description:   数据对象信息保存
 */
public class DataInfoCache {

    /**
     * @param ctx
     * @param data 保存StartBackground集合对象
     */
    public static void saveStartBackgrounds(Context ctx, ArrayList<StartBackground> data) {
        new DataCache<StartBackground>().saveGlobal(ctx, data, Constant.START_BACKGROUNDS);
    }

    /**
     * @param ctx
     * @return 加载保存的StartBackground集合对象
     */
    public static ArrayList<StartBackground> loadStartBackgrounds(Context ctx) {
        return new DataCache<StartBackground>().loadGlobal(ctx, Constant.START_BACKGROUNDS);
    }

    /**
     * @param ctx
     * @param data 保存Pictruefragment  Pictrue集合对象
     */
    public static void saveGridPictrues(Context ctx, ArrayList<Pictrue> data) {
        new DataCache<Pictrue>().saveGlobal(ctx, data, Constant.GRID_PICTRUES);
    }

    /**
     * @param ctx
     * @return 加载保存的Pictruefragment  Pictrue集合对象
     */
    public static ArrayList<Pictrue> loadGridPictrues(Context ctx) {
        return new DataCache<Pictrue>().loadGlobal(ctx, Constant.GRID_PICTRUES);
    }

    /**
     * @param ctx
     * @param data
     * 保存Paopaofragment  Paopao集合对象
     */
    public static void saveListPaopaos(Context ctx, ArrayList<Paopao> data) {
        new DataCache<Paopao>().saveGlobal(ctx, data, Constant.LIST_PAOPAOS);
    }

    /**
     * @param ctx
     * @return
     * 加载保存的Paopaofragment  Paopao集合对象
     */
    public static ArrayList<Paopao> loadListPaopaos(Context ctx) {
        return new DataCache<Paopao>().loadGlobal(ctx, Constant.LIST_PAOPAOS);
    }

    /**
     * @param ctx
     * @param data 保存Pictruefragment  ViewPage集合对象
     */
    public static void saveViewPagePictrues(Context ctx, ArrayList<PicViewImage> data) {
        new DataCache<PicViewImage>().saveGlobal(ctx, data, Constant.VIEWPAGE_PICTRUES);
    }

    /**
     * @param ctx
     * @return 加载保存的Pictruefragment  ViewPage集合对象
     */
    public static ArrayList<PicViewImage> loadViewPagePictrues(Context ctx) {
        return new DataCache<PicViewImage>().loadGlobal(ctx, Constant.VIEWPAGE_PICTRUES);
    }

    /**
     * @param ctx
     * @param data 保存用户粉丝列表集合对象
     */
    public static void saveUserFansList(Context ctx, ArrayList<User> data) {
        new DataCache<User>().saveGlobal(ctx, data, Constant.USER_FANS_LIST);
    }

    /**
     * @param ctx
     * @return 加载保存的用户粉丝列表集合对象
     */
    public static ArrayList<User> loadUserFansList(Context ctx) {
        return new DataCache<User>().loadGlobal(ctx, Constant.USER_FANS_LIST);
    }

    /**
     * @param ctx
     * @param data 保存用户关注列表集合对象
     */
    public static void saveUserFocusList(Context ctx, ArrayList<User> data) {
        new DataCache<User>().saveGlobal(ctx, data, Constant.USER_FOCUS_LIST);
    }

    /**
     * @param ctx
     * @return 加载保存的用户关注列表集合对象
     */
    public static ArrayList<User> loadUserFocusList(Context ctx) {
        return new DataCache<User>().loadGlobal(ctx, Constant.USER_FOCUS_LIST);
    }

    /**
     * @param ctx
     * @param data
     * 保存Friendfragment  Paopao集合对象
     */
    public static void saveFriendListPaopaos(Context ctx, ArrayList<Paopao> data) {
        new DataCache<Paopao>().saveGlobal(ctx, data, Constant.FRIEND_LIST_PAOPAOS);
    }

    /**
     * @param ctx
     * @return
     * 加载保存的Friendfragment  Paopao集合对象
     */
    public static ArrayList<Paopao> loadFriendListPaopaos(Context ctx) {
        return new DataCache<Paopao>().loadGlobal(ctx, Constant.FRIEND_LIST_PAOPAOS);
    }



    /**
     * @param <T> 数据缓存 save or load
     */
    static class DataCache<T> {
        public void save(Context ctx, ArrayList<T> data, String name) {
            save(ctx, data, name, "");
        }

        public void saveGlobal(Context ctx, ArrayList<T> data, String name) {
            save(ctx, data, name, Constant.DATA_CACHE_FILDER);
        }

        private void save(Context ctx, ArrayList<T> data, String name,
                          String folder) {
            if (ctx == null) {
                return;
            }
            File file;
            if (!folder.isEmpty()) {
                File fileDir = new File(ctx.getFilesDir(), folder);
                if (!fileDir.exists() || !fileDir.isDirectory()) {
                    fileDir.mkdir();
                }
                file = new File(fileDir, name);
            } else {
                file = new File(ctx.getFilesDir(), name);
            }
            if (file.exists()) {
                file.delete();
            }
            try {
                ObjectOutputStream oos = new ObjectOutputStream(
                        new FileOutputStream(file));
                oos.writeObject(data);
                oos.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        public ArrayList<T> load(Context ctx, String name) {
            return load(ctx, name, "");
        }

        public ArrayList<T> loadGlobal(Context ctx, String name) {
            return load(ctx, name, Constant.DATA_CACHE_FILDER);
        }

        private ArrayList<T> load(Context ctx, String name, String folder) {
            ArrayList<T> data = null;

            File file;
            if (!folder.isEmpty()) {
                File fileDir = new File(ctx.getFilesDir(), folder);
                if (!fileDir.exists() || !fileDir.isDirectory()) {
                    fileDir.mkdir();
                }
                file = new File(fileDir, name);
            } else {
                file = new File(ctx.getFilesDir(), name);
            }
            if (file.exists()) {
                try {
                    ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file));
                    data = (ArrayList<T>) ois.readObject();
                    ois.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (data == null) {
                data = new ArrayList<T>();
            }
            return data;
        }
    }
}
