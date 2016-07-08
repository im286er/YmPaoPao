package com.yeming.paopao.proxy;

import android.content.Context;
import android.content.Intent;

import com.yeming.paopao.bean.Paopao;
import com.yeming.paopao.bean.User;
import com.yeming.paopao.commons.Constant;
import com.yeming.paopao.utils.LogUtil;
import com.yeming.paopao.utils.SharedPreHelperUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import cn.bmob.v3.AsyncCustomEndpoints;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobPointer;
import cn.bmob.v3.datatype.BmobRelation;
import cn.bmob.v3.listener.CloudCodeListener;
import cn.bmob.v3.listener.CountListener;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.ResetPasswordListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;

/**
 * author   YeMing(yeming_1001@163.com)
 * Date:    2015-01-20 21:14
 * version: V1.0
 * Description:  用户操作代理
 */
public class UserProxy {

    public static final String TAG = "UserProxy";
    private Context mContext;
    private ISignUpListener signUpLister;
    private ILoginListener loginListener;
    private IResetPasswordListener resetPasswordListener;
    private IGetFansListener iGetFansListener ;
    private IGetFocusListener iGetFocusListener ;
    private IGetPaopaoListener iGetPaopaoListener ;
    private IGetFansListListener iGetFansListListener ;
    private IGetFocusListListener iGetFocusListListener ;
    private IGetPaopaoListListener iGetPaopaoListListener ;
    private IGetPaopaoListForFocusUserListener iGetPaopaoListForFocusUserListener ;
    private IUserFocusOtherListener iUserFocusOtherListener ;

    public UserProxy(Context context) {
        this.mContext = context;
    }

    /**
     * @param email
     * @param password 注册
     */
    public void signUp(String email, String password) {
        User user = new User();
        user.setPassword(password);
        user.setEmail(email);
        user.setUsername(email);
     //   String str[] = email.split("@") ;
     //   user.setNickname(str[0]);  //  邮箱前缀 eg：xxx@qq.com    xxx
        user.setNickname(email);
        user.signUp(mContext, new SaveListener() {
            @Override
            public void onSuccess() {
                if (signUpLister != null) {
                    signUpLister.onSignUpSuccess();
                } else {
                    LogUtil.i(TAG, "signup listener is null,you must set one!");
                }
            }

            @Override
            public void onFailure(int i, String msg) {
                if (signUpLister != null) {
                    signUpLister.onSignUpFailure(i,msg);
                } else {
                    LogUtil.i(TAG, "signup listener is null,you must set one!");
                }
            }
        });
    }

    public void setOnSignUpListener(ISignUpListener signUpLister) {
        this.signUpLister = signUpLister;
    }

    /**
     * @param email
     * @param password 登录
     */
    public void login(String email, String password) {
        final User user = new User();
       // user.setEmail(email);
        user.setUsername(email);
        user.setPassword(password);
        user.login(mContext, new SaveListener() {
            @Override
            public void onSuccess() {
                if (loginListener != null) {
                    loginListener.onLoginSuccess();
                } else {
                    LogUtil.i(TAG, "login listener is null,you must set one!");
                }
            }

            @Override
            public void onFailure(int i, String msg) {
                if (loginListener != null) {
                    loginListener.onLoginFailure(i,msg);
                } else {
                    LogUtil.i(TAG, "login listener is null,you must set one!");
                }
            }
        });
    }

    public void setOnLoginListener(ILoginListener loginListener) {
        this.loginListener = loginListener;
    }

    /**
     * @param email 重置密码
     */
    public void resetPassword(String email) {
        BmobUser.resetPassword(mContext, email, new ResetPasswordListener() {
            @Override
            public void onSuccess() {
                if (resetPasswordListener != null) {
                    resetPasswordListener.onResetSuccess();
                } else {
                    LogUtil.i(TAG, "reset listener is null,you must set one!");
                }
            }

            @Override
            public void onFailure(int i, String msg) {
                if (resetPasswordListener != null) {
                    resetPasswordListener.onResetFailure(msg);
                } else {
                    LogUtil.i(TAG, "reset listener is null,you must set one!");
                }
            }
        });
    }

    public void setOnResetPasswordListener(IResetPasswordListener resetPasswordListener) {
        this.resetPasswordListener = resetPasswordListener;
    }

    /**
     * @return 获取本地用户信息
     */
    public User getCurrentUser() {
        User user = BmobUser.getCurrentUser(mContext, User.class);
        if (user != null) {
            LogUtil.i(TAG, "本地用户信息" + user.getObjectId() + "-"
                    + user.getUsername() + "-"
                    + user.getSessionToken() + "-"
                    + user.getCreatedAt() + "-"
                    + user.getNickname() + "-"
                    + user.getSex());
            return user;
        } else {
            LogUtil.i(TAG, "本地用户为null,请登录。");
        }
        return null;
    }

    /**
     * 退出登录
     */
    public void logout() {
       // BmobUser.logOut(mContext);
        User.logOut(mContext);
        LogUtil.i(TAG, "logout result:" + (null == getCurrentUser()));
    }

    /**
     * 获取用户粉丝数
     * @param user        查询user粉丝数量
     */
    public void getUserFansCountNum(User user){
       // User user = BmobUser.getCurrentUser(mContext, User.class);
        BmobQuery<User> query = new BmobQuery<User>();
        query.addWhereRelatedTo("fans", new BmobPointer(user)); // 条件：查询当前用户粉丝
        query.count(mContext, User.class, new CountListener() {

            @Override
            public void onSuccess(int count) {
                LogUtil.d(TAG,"-----getUserFansCountNum-success-----"+count);
                /*// 保存粉丝数
                SharedPreHelperUtil.getInstance(mContext).setUserFansNum(count);
                // 发送更新粉丝数的广播
                Intent intent = new Intent() ;
                intent.setAction(Constant.USER_FANSNUM_CHANGE) ;
                mContext.sendBroadcast(intent);*/
                if (iGetFansListener != null) {
                    iGetFansListener.onGetFansSuccess(count);
                } else {
                    LogUtil.i(TAG, "reset listener is null,you must set one!");
                }
            }

            @Override
            public void onFailure(int arg0, String arg1) {
                LogUtil.d(TAG, "------getUserFansCountNum-onFailure-----code==" + arg0 + "---" + arg1);
                if (iGetFansListener != null) {
                    iGetFansListener.onGetFansFailure(arg0,arg1);
                } else {
                    LogUtil.i(TAG, "reset listener is null,you must set one!");
                }
            }
        });
    }

    public void setOnGetFansListener(IGetFansListener iGetFansListener) {
        this.iGetFansListener = iGetFansListener;
    }

    /**
     * 获取用户发布的泡泡数
     */
    public void getUserPaoPaoNum(String userObjectId){
        BmobQuery<Paopao> query = new BmobQuery<Paopao>();
        query.addWhereEqualTo("user", userObjectId);
        query.count(mContext, Paopao.class, new CountListener() {

            @Override
            public void onSuccess(int arg0) {
                LogUtil.d(TAG, "-------getUserPaoPaoNum onSuccess--------" + arg0);
                /*SharedPreHelperUtil.getInstance(mContext).setUserPaopaoNum(arg0);
                // 发送更新泡泡数的广播
                Intent intent = new Intent() ;
                intent.setAction(Constant.USER_PAOPAONUM_CHANGE) ;
                mContext.sendBroadcast(intent);*/
                if (iGetPaopaoListener != null) {
                    iGetPaopaoListener.onGetPaopaoSuccess(arg0);
                } else {
                    LogUtil.i(TAG, "reset listener is null,you must set one!");
                }
            }

            @Override
            public void onFailure(int arg0, String arg1) {
                LogUtil.d(TAG, "---getUserPaoPaoNum onFailure----code==" + arg0 + "---"+ arg1);
                if (iGetPaopaoListener != null) {
                    iGetPaopaoListener.onGetPaopaoFailure(arg0,arg1);
                } else {
                    LogUtil.i(TAG, "reset listener is null,you must set one!");
                }
            }
        });
    }

    public void setOnGetPaopaoListener(IGetPaopaoListener iGetPaopaoListener) {
        this.iGetPaopaoListener = iGetPaopaoListener;
    }


    /**
     * 获取用户关注数
     */
    public void getUserFocusCountNum(User user){
       // User user = BmobUser.getCurrentUser(mContext, User.class);
        BmobQuery<User> query = new BmobQuery<User>();
        query.addWhereRelatedTo("focus", new BmobPointer(user)); // 条件：查询当前用户关注的人
        query.count(mContext, User.class, new CountListener() {

            @Override
            public void onSuccess(int count) {
                LogUtil.d(TAG,"------getUserFocusCountNum success-----"+count);
                // 保存关注数
                /*SharedPreHelperUtil.getInstance(mContext).setUserFocusNum(count);
                // 发送更新关注数的广播
                Intent intent = new Intent() ;
                intent.setAction(Constant.USER_FOCUSNUM_CHANGE) ;
                mContext.sendBroadcast(intent);*/
                if (iGetFocusListener != null) {
                    iGetFocusListener.onGetFocusSuccess(count);
                } else {
                    LogUtil.i(TAG, "reset listener is null,you must set one!");
                }
            }

            @Override
            public void onFailure(int arg0, String arg1) {
                LogUtil.d(TAG, "-------getUserFocusCountNum onFailure---code==" + arg0 + "---" + arg1);
                if (iGetFocusListener != null) {
                    iGetFocusListener.onGetFocusFailure(arg0,arg1);
                } else {
                    LogUtil.i(TAG, "reset listener is null,you must set one!");
                }
            }
        });
    }

    public void setOnGetFocusListener(IGetFocusListener iGetFocusListener) {
        this.iGetFocusListener = iGetFocusListener;
    }

    /**
     * 获取用户的粉丝列表
     * @param user
     */
    public void getUserFansList(User user){
        BmobQuery<User> query = new BmobQuery<User>() ;
        query.addWhereRelatedTo("fans", new BmobPointer(user));
        query.findObjects(mContext, new FindListener<User>() {

            @Override
            public void onSuccess(List<User> users) {
                if (iGetFansListListener != null) {
                    iGetFansListListener.onGetFansListSuccess(users);
                } else {
                    LogUtil.i(TAG, "reset listener is null,you must set one!");
                }
            }

            @Override
            public void onError(int i, String s) {
                if (iGetFansListListener != null) {
                    iGetFansListListener.onGetFansListFailure(i,s);
                } else {
                    LogUtil.i(TAG, "reset listener is null,you must set one!");
                }
            }
        }) ;
    }

    public void setOnGetFansListListener(IGetFansListListener iGetFansListListener) {
        this.iGetFansListListener = iGetFansListListener;
    }

    /**
     * 获取用户关注列表
     * @param user
     */
    public void getUserFocusList(User user){
        BmobQuery<User> query = new BmobQuery<User>() ;
        query.addWhereRelatedTo("focus", new BmobPointer(user));
        query.findObjects(mContext, new FindListener<User>() {

            @Override
            public void onSuccess(List<User> users) {
                if (iGetFocusListListener != null) {
                    iGetFocusListListener.onGetFocusListSuccess(users);
                } else {
                    LogUtil.i(TAG, "reset listener is null,you must set one!");
                }
            }

            @Override
            public void onError(int i, String s) {
                if (iGetFocusListListener != null) {
                    iGetFocusListListener.onGetFocusListFailure(i,s);
                } else {
                    LogUtil.i(TAG, "reset listener is null,you must set one!");
                }
            }
        }) ;
    }

    public void setOnGetFocusListListener(IGetFocusListListener iGetFocusListListener) {
        this.iGetFocusListListener = iGetFocusListListener;
    }

    /**
     * 获取用户泡泡列表
     * @param user
     */
    public void getUserPaopaoList(User user){
        BmobQuery<Paopao> query = new BmobQuery<Paopao>() ;
        query.addWhereRelatedTo("paopao", new BmobPointer(user));
        query.findObjects(mContext,new FindListener<Paopao>() {
            @Override
            public void onSuccess(List<Paopao> paopaos) {
                if (iGetPaopaoListListener != null) {
                    iGetPaopaoListListener.onGetPaopaoListSuccess(paopaos);
                } else {
                    LogUtil.i(TAG, "reset listener is null,you must set one!");
                }
            }

            @Override
            public void onError(int i, String s) {
                if (iGetPaopaoListListener != null) {
                    iGetPaopaoListListener.onGetPaopaoListFailure(i,s);
                } else {
                    LogUtil.i(TAG, "reset listener is null,you must set one!");
                }
            }
        });
    }

    public void setOnGetPaopaoListListener(IGetPaopaoListListener iGetPaopaoListListener) {
        this.iGetPaopaoListListener = iGetPaopaoListListener;
    }


    /**
     * 获取user关注用户发布的泡泡列表（朋友圈泡泡）
     * @param user
     */
    public void getPaopaoListForFocusUser(User user){
        final BmobQuery<Paopao> query = new BmobQuery<Paopao>();
        BmobQuery<User> innerQuery = new BmobQuery<User>();
        innerQuery.addWhereRelatedTo("focus", new BmobPointer(user)); // 条件：查询当前用户关注的人
        innerQuery.findObjects(mContext,new FindListener<User>() {
            @Override
            public void onSuccess(List<User> users) {
                // 获取所关注的人的id封装成数组
                String[] id = new String[users.size()];
                for (int i = 0; i < users.size(); i++) {
                    id[i] = users.get(i).getObjectId();
                }
                query.addWhereContainedIn("user", Arrays.asList(id)); // 条件：查询用户id与上述数组中id匹配的paopao
                query.include("user"); // 条件：获取paopao，同时也包括它们关联的用户
                query.findObjects(mContext,new FindListener<Paopao>() {
                    @Override
                    public void onSuccess(List<Paopao> paopaos) {
                        if (iGetPaopaoListForFocusUserListener != null) {
                            iGetPaopaoListForFocusUserListener.onGetPaopaoListForFocusUserSuccess(paopaos);
                        } else {
                            LogUtil.i(TAG, "reset listener is null,you must set one!");
                        }
                    }

                    @Override
                    public void onError(int i, String s) {
                        if (iGetPaopaoListForFocusUserListener != null) {
                            iGetPaopaoListForFocusUserListener.onGetPaopaoListForFocusUserFailure(i,s);
                        } else {
                            LogUtil.i(TAG, "reset listener is null,you must set one!");
                        }
                    }
                });
            }

            @Override
            public void onError(int i, String s) {
                if (iGetPaopaoListForFocusUserListener != null) {
                    iGetPaopaoListForFocusUserListener.onGetPaopaoListForFocusUserFailure(i,s);
                } else {
                    LogUtil.i(TAG, "reset listener is null,you must set one!");
                }
            }
        });
    }

    public void setOnPaopaoListForFocusUserListener(IGetPaopaoListForFocusUserListener iGetPaopaoListForFocusUserListener) {
        this.iGetPaopaoListForFocusUserListener = iGetPaopaoListForFocusUserListener;
    }


    /**
     * 用户关注其他用户，并在其他用添加当前用户为粉丝
     * @param user   当前用户 关注用户user
     */
    public void userFocusOther(final User user){
        final User currentUser = BmobUser.getCurrentUser(mContext,User.class) ;
        BmobRelation relation = new BmobRelation() ;
        relation.add(user);
        currentUser.setFocus(relation);
        //  在当前用户的关注中添加关联关系 user
        currentUser.update(mContext,new UpdateListener() {
            @Override
            public void onSuccess() {
                LogUtil.i(TAG, "------userFocusOther update onSuccess--------");
                //  在当前用户添加focus关联成功后，把当前用户添加到user用户的fans关联关系中。
                //  传入两个用户的id
                //  调用云端代码 ，更新用户user的fans关联关系列
                AsyncCustomEndpoints endpoints = new AsyncCustomEndpoints() ;
                JSONObject jsonObject = new JSONObject() ;
                try {
                    jsonObject.put("objectId", user.getObjectId()) ;
                    jsonObject.put("objectId1", currentUser.getObjectId()) ;

                    endpoints.callEndpoint(mContext, "addFans", jsonObject, new CloudCodeListener() {

                        @Override
                        public void onSuccess(Object arg0) {
                            LogUtil.i(TAG, "------userFocusOther callEndpoint onSuccess--------");
                            LogUtil.i(TAG, "------userFocusOther callEndpoint onSuccess----"+arg0.toString());
                            if (iUserFocusOtherListener != null) {
                                iUserFocusOtherListener.onUserFocusOtherSuccess(arg0.toString());
                            } else {
                                LogUtil.i(TAG, "reset listener is null,you must set one!");
                            }

                            //String id = "" ;
                            /*try {
                                LogUtil.i(TAG, "------userFocusOther callEndpoint onSuccess----"+arg0.toString());
                                id = new JSONObject(arg0.toString()).getJSONObject("results").getString("objectId") ;
                                LogUtil.i(TAG, "------userFocusOther callEndpoint onSuccess----id="+id);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }*/
                        }

                        @Override
                        public void onFailure(int arg0, String arg1) {
                            LogUtil.i(TAG, "------userFocusOther callEndpoint onFailure---"+arg0+"__"+arg1);
                            if (iGetPaopaoListForFocusUserListener != null) {
                                iGetPaopaoListForFocusUserListener.onGetPaopaoListForFocusUserFailure(arg0,arg1);
                            } else {
                                LogUtil.i(TAG, "reset listener is null,you must set one!");
                            }
                        }
                    }) ;

                }catch (JSONException e){
                    LogUtil.i(TAG, "------userFocusOther update JSONException---"+e);
                }
            }

            @Override
            public void onFailure(int i, String s) {
                LogUtil.i(TAG, "------userFocusOther update onFailure---"+i+"__"+s);
                if (iGetPaopaoListForFocusUserListener != null) {
                    iGetPaopaoListForFocusUserListener.onGetPaopaoListForFocusUserFailure(i,s);
                } else {
                    LogUtil.i(TAG, "reset listener is null,you must set one!");
                }
            }
        });

    }

    public void setOnUserFocusOtherListener(IUserFocusOtherListener iUserFocusOtherListener) {
        this.iUserFocusOtherListener = iUserFocusOtherListener;
    }

    public interface IResetPasswordListener {
        void onResetSuccess();

        void onResetFailure(String msg);
    }

    public interface ISignUpListener {
        void onSignUpSuccess();

        void onSignUpFailure(int code,String msg);
    }

    public interface ILoginListener {
        void onLoginSuccess();

        void onLoginFailure(int code,String msg);
    }

    public interface IGetFansListener {
        void onGetFansSuccess(int count);

        void onGetFansFailure(int code,String msg);
    }
    public interface IGetFocusListener {
        void onGetFocusSuccess(int count);

        void onGetFocusFailure(int code,String msg);
    }
    public interface IGetPaopaoListener {
        void onGetPaopaoSuccess(int count);

        void onGetPaopaoFailure(int code,String msg);
    }
    public interface IGetFansListListener {
        void onGetFansListSuccess(List<User> list);

        void onGetFansListFailure(int code,String msg);
    }
    public interface IGetFocusListListener {
        void onGetFocusListSuccess(List<User> list);

        void onGetFocusListFailure(int code,String msg);
    }
    public interface IGetPaopaoListListener {
        void onGetPaopaoListSuccess(List<Paopao> list);

        void onGetPaopaoListFailure(int code,String msg);
    }
    public interface IGetPaopaoListForFocusUserListener {
        void onGetPaopaoListForFocusUserSuccess(List<Paopao> list);

        void onGetPaopaoListForFocusUserFailure(int code,String msg);
    }
    public interface IUserFocusOtherListener {
        void onUserFocusOtherSuccess(String s);

        void onUserFocusOtherFailure(int code,String msg);
    }
}
