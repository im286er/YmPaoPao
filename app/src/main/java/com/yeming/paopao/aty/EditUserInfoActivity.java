package com.yeming.paopao.aty;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bmob.BTPFileResponse;
import com.bmob.BmobProFile;
import com.bmob.btp.callback.UploadListener;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.yeming.paopao.R;
import com.yeming.paopao.app.YmApplication;
import com.yeming.paopao.bean.User;
import com.yeming.paopao.commons.Constant;
import com.yeming.paopao.utils.CustomDialog;
import com.yeming.paopao.utils.FileUtil;
import com.yeming.paopao.utils.LogUtil;
import com.yeming.paopao.utils.SharedPreHelperUtil;
import com.yeming.paopao.views.third.DialogView;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.listener.UpdateListener;

/**
 * author   YeMing(yeming_1001@163.com)
 * Date:    2015-01-28 21:27
 * version: V1.0
 * Description:    当前用户信息 可修改
 */
public class EditUserInfoActivity extends Activity implements View.OnClickListener,CompoundButton.OnCheckedChangeListener {


    private static final String TAG = "EditUserInfoActivity";
    private Context context ;
    private RelativeLayout userIconLayout ,userNickLayout ,userSignLayout,userFansLayout,userFocusLayout;
    private TextView userIconTips,userNickTips,userNickText,userSexTips,userSignTips,userSignText,userFansTips,userFansText,userFocusTips,userFocusText ;
    private Button setText ;
    private ImageView userIconImage ;
    private CheckBox sexSwitch;
    DisplayImageOptions options = new DisplayImageOptions.Builder()
            .showImageForEmptyUri(R.drawable.user_icon_default_main)
            .showImageOnFail(R.drawable.user_icon_default_main)
            .resetViewBeforeLoading(true)
            .cacheOnDisc(true)
            .imageScaleType(ImageScaleType.EXACTLY)
            .bitmapConfig(Bitmap.Config.RGB_565)
            .considerExifParams(true)
            .displayer(new FadeInBitmapDisplayer(300))
            .build();

    private Dialog editDialog;
    private User user = null ;
    private Uri fileUri;
    private Uri fileCropUri;
    private Dialog loadDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.edit_userinfo_layout);

        YmApplication.getInstance().addActivity(this);
        context = this ;
        user = BmobUser.getCurrentUser(context,User.class) ;
        setActionBar() ;


        loadDialog = DialogView.loadDialog(context, R.string.saveing) ;
        initView();
        initListener();
    }

    /**
     * 初始化控件
     */
    private void initView(){
        userIconLayout = (RelativeLayout) findViewById(R.id.user_icon);
        userNickLayout = (RelativeLayout) findViewById(R.id.user_nick);
        userSignLayout = (RelativeLayout) findViewById(R.id.user_sign);
        userFansLayout = (RelativeLayout) findViewById(R.id.user_fans);
        userFocusLayout = (RelativeLayout) findViewById(R.id.user_focus);

        userIconTips = (TextView) findViewById(R.id.user_icon_tips);
        userNickTips = (TextView) findViewById(R.id.user_nick_tips);
        userNickText = (TextView) findViewById(R.id.user_nick_text);
        userSexTips = (TextView) findViewById(R.id.sex_choice_tips);
        userSignTips = (TextView) findViewById(R.id.user_sign_tips);
        userSignText = (TextView) findViewById(R.id.user_sign_text);

        userFansTips = (TextView) findViewById(R.id.user_fans_tips);
        userFocusTips = (TextView) findViewById(R.id.user_focus_tips);
        userFansText = (TextView) findViewById(R.id.user_fans_text);
        userFocusText = (TextView) findViewById(R.id.user_focus_text);

        setText = (Button) findViewById(R.id.user_set);

        userIconImage = (ImageView) findViewById(R.id.user_icon_image);

        sexSwitch = (CheckBox) findViewById(R.id.sex_choice_switch);

        userIconTips.setTypeface(YmApplication.chineseTypeface);
        userNickTips.setTypeface(YmApplication.chineseTypeface);
        userNickText.setTypeface(YmApplication.chineseTypeface);
        userSexTips.setTypeface(YmApplication.chineseTypeface);
        userSignTips.setTypeface(YmApplication.chineseTypeface);
        userSignText.setTypeface(YmApplication.chineseTypeface);

        userFansTips.setTypeface(YmApplication.chineseTypeface);
        userFocusText.setTypeface(YmApplication.chineseTypeface);
        userFansText.setTypeface(YmApplication.chineseTypeface);
        userFocusTips.setTypeface(YmApplication.chineseTypeface);

        setText.setTypeface(YmApplication.chineseTypeface);

       // User user = BmobUser.getCurrentUser(this, User.class);
        String sign = user.getSign() ;
        String avatarUrl = null;
        if(user != null){
            /*if(user.getAvatar()!=null){
                avatarUrl = user.getAvatar().getFileUrl(this) ;
            }*/
            avatarUrl = user.getAvatarUrl() ;
            userNickText.setText(user.getNickname());
            if(sign != null && !"".equals(sign)){
                userSignText.setText(sign);
            }
            if("m".equals(user.getSex())){
                sexSwitch.setChecked(true);
            }else if("w".equals(user.getSex())){
                sexSwitch.setChecked(false);
            }
        }
        ImageLoader.getInstance().displayImage(avatarUrl,userIconImage,options);

        /*ImageSize targetSize = new ImageSize(60, 60);
        ImageLoader.getInstance().loadImage(avatarUrl, targetSize, options, new SimpleImageLoadingListener() {
            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                userIconImage.setImageBitmap(loadedImage);
            }
        });*/

        userFansText.setText(SharedPreHelperUtil.getInstance(context).getUserFansNum()+"");
        userFocusText.setText(SharedPreHelperUtil.getInstance(context).getUserFocusNum()+"");
    }

    /**
     * 注册监听器
     */
    private void initListener(){
        userIconLayout.setOnClickListener(this);
        userNickLayout.setOnClickListener(this);
        userSignLayout.setOnClickListener(this);
        userFansLayout.setOnClickListener(this);
        userFocusLayout.setOnClickListener(this);
        userIconImage.setOnClickListener(this);
        setText.setOnClickListener(this);
        sexSwitch.setOnCheckedChangeListener(this);
    }

    /**
     * actionbar style
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    public void setActionBar(){
        //this.getActionBar().setTitle("PaoPao");
        getActionBar().setBackgroundDrawable(
                this.getBaseContext().getResources()
                        .getDrawable(R.drawable.actionbar_bg));
        //getActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setDisplayShowHomeEnabled(false);
        //getActionBar().setTitle(user.getNickname());
        int titleId = Resources.getSystem().getIdentifier("action_bar_title",
                "id", "android");
        TextView textView = (TextView) findViewById(titleId);
        textView.setTypeface(YmApplication.chineseTypeface);
        textView.setTextColor(0xFFdfdfdf);
        textView.setTextSize(20);
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        int id = item.getItemId() ;
        switch (id){
            case android.R.id.home:
                onBackPressed();
                break ;
        }
        return super.onMenuItemSelected(featureId, item);
    }

    @Override
    public void onClick(View view) {
        Intent intent = new Intent() ;
        switch (view.getId()){
            case R.id.user_icon:
                showPicChoiceFormDialog();
                break ;
            case R.id.user_icon_image:
                intent.setClass(context, ZoomImageActivity.class);
                intent.putExtra("url",user.getAvatarUrl()) ;
                startActivity(intent);
                break ;
            case R.id.user_nick:
                showEditDialog();
                break ;
            case R.id.user_fans:
                intent.setClass(context, FansListActivity.class);
                intent.putExtra("user",YmApplication.getCurrentUser()) ;
                startActivity(intent);
                break ;
            case R.id.user_focus:
                intent.setClass(context, FocusListActivity.class);
                intent.putExtra("user",YmApplication.getCurrentUser()) ;
                startActivity(intent);
                break ;
            case R.id.user_sign:
                intent.setClass(context, EditUserSignActivity.class);
                startActivityForResult(intent, Constant.EDIT_SIGN_REQUEST_CODE);
                break ;
            case R.id.user_set:
                onBackPressed();
                break ;
            default:
                break ;
        }

    }

    /**
     * 相机 或 图库 选择框
     */
    @SuppressLint("ResourceAsColor")
    private void showPicChoiceFormDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("更换头像")
                .setItems(R.array.camera_gallery, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == 0) {
                            camera();
                        } else {
                            photo();
                        }
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
        CustomDialog.dialogTitleLineColor(context, dialog);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == Activity.RESULT_OK){
            switch (requestCode){
                case Constant.EDIT_SIGN_REQUEST_CODE:   //  从编辑签名页 返回
                    String signText = data.getStringExtra("sign") ;
                    if(signText == null){
                        userSignText.setText(user.getSign());
                    }else{
                        // 发送修改昵称的广播
                        Intent intent = new Intent() ;
                        intent.setAction(Constant.USER_SIGN_CHANGE) ;
                        sendBroadcast(intent);
                        userSignText.setText(signText);
                    }

                    break ;
                case RESULT_REQUEST_PHOTO:
                    if (data != null) {
                        fileUri = data.getData();
                    }

                    fileCropUri = FileUtil.getOutputMediaFileUri();
                    cropImageUri(fileUri, fileCropUri, 640, 640, RESULT_REQUEST_PHOTO_CROP);
                    break ;
                case RESULT_REQUEST_PHOTO_CROP:
                    loadDialog.show();
                    String filePath = FileUtil.getFilePathByUri(fileCropUri) ;
                    ImageLoader.getInstance().displayImage(String.valueOf(fileCropUri),userIconImage,options);
                    /*ImageSize targetSize = new ImageSize(60, 60);
                    ImageLoader.getInstance().loadImage(String.valueOf(fileCropUri), targetSize, options, new SimpleImageLoadingListener() {
                        @Override
                        public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                            userIconImage.setImageBitmap(loadedImage);
                        }
                    });*/
                    //  上传头像图片
                    pushPic(filePath);
                    break ;
                default:
                    break ;
            }
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
        switch (compoundButton.getId()) {
            case R.id.sex_choice_switch:     //  性别
                if(isChecked){   //  m
                    updateSex("m");
                }else{           //  w
                    updateSex("w");
                }
                break ;
            default:
                break;
        }
    }

    /**
     * 更新性别
     * @param sex
     */
    private void updateSex(String sex){
       // User user = BmobUser.getCurrentUser(context, User.class);
        User newUser = new User() ;
        newUser.setSex(sex);
        newUser.update(context,user.getObjectId(),new UpdateListener() {
            @Override
            public void onSuccess() {
               LogUtil.i(TAG, "更新性别信息成功。");
                // 发送修改性别的广播
                Intent intent = new Intent() ;
                intent.setAction(Constant.USER_SEX_CHANGE) ;
                sendBroadcast(intent);
            }

            @Override
            public void onFailure(int i, String s) {
                LogUtil.i(TAG,"性别更新失败-->code="+i+"_"+s);
            }
        });
    }

    /**
     * 更新nick
     * @param nick
     */
    private void updateNick(final String nick){
       // User user = BmobUser.getCurrentUser(context, User.class);
        if(user.getNickname().equals(nick)){
        //    softInput();
            editDialog.dismiss();
            return ;
        }
        User newUser = new User() ;
        newUser.setNickname(nick);
        newUser.update(context,user.getObjectId(),new UpdateListener() {
            @Override
            public void onSuccess() {
                LogUtil.i(TAG, "更新昵称信息成功.");
                // 发送修改昵称的广播
                Intent intent = new Intent() ;
                intent.setAction(Constant.USER_NICK_CHANGE) ;
                sendBroadcast(intent);
                userNickText.setText(nick);

            }

            @Override
            public void onFailure(int i, String s) {
                LogUtil.i(TAG,"昵称更新失败-->code="+i+"_"+s);
            }

        });
    //    softInput();
        editDialog.dismiss();
    }

    /**
     * 上传头像
     * @param picPath
     */
    private void pushPic(String picPath){
        BTPFileResponse btpFileResponse = BmobProFile.getInstance(context).upload(picPath,new UploadListener() {
            @Override
            public void onSuccess(String fileName, String fileUrl) {
                Log.d(TAG, "------- upload Success fileName--------" + fileName);
                Log.d(TAG, "------- upload Success fileUrl--------"+fileUrl);
                String signUrl =BmobProFile.getInstance(context).signURL(fileName,fileUrl,
                        Constant.ACCESS_KEY,0,null);
                Log.d(TAG, "------- upload Success signUrl--------" + signUrl);
                updatePic(signUrl);
            }

            @Override
            public void onProgress(int i) {
                Log.d(TAG, "------- upload Progress--------"+i);
            }

            @Override
            public void onError(int i, String s) {
                Log.d(TAG, "------- upload Failure code--------"+i);
                Log.d(TAG, "------- upload Failure--------"+s);
            }
        }) ;
    }

    /**
     * 更新头像
     * @param url
     */
    private void updatePic(String url){
        User newUser = new User() ;
        newUser.setAvatarUrl(url);
        newUser.update(context,user.getObjectId(),new UpdateListener() {
            @Override
            public void onSuccess() {
                LogUtil.i(TAG, "更新头像信息成功.");
                // 发送修改头像的广播
                Intent intent = new Intent() ;
                intent.setAction(Constant.USER_AVATER_CHANGE) ;
                sendBroadcast(intent);
            //    ImageLoader.getInstance().displayImage(FileUtil.getFilePathByUri(fileCropUri),userIconImage,options);
                user = BmobUser.getCurrentUser(context,User.class) ;
            }

            @Override
            public void onFailure(int i, String s) {
                LogUtil.i(TAG,"头像更新失败-->code="+i+"_"+s);
            }

        });
        loadDialog.dismiss();
    }

    /**
     * 编辑框
     */
    private void showEditDialog(){
       // editDialog = new AlertDialog.Builder(context).create();
        editDialog = new Dialog(context,R.style.mydialog) ;
        editDialog.setCanceledOnTouchOutside(false);
        View v = LayoutInflater.from(context).inflate(R.layout.edit_dialog_layout, null);
       // editDialog.getWindow().setContentView(R.layout.edit_dialog_layout);
        editDialog.getWindow().setContentView(v);

        final EditText editNick = (EditText) v.findViewById(R.id.edit_nick);
        editNick.setText(userNickText.getText());
        TextView titleTip = (TextView) v.findViewById(R.id.titleTip);
        Button setCancle = (Button) v.findViewById(R.id.set_cancle);
        Button setEnsure = (Button) v.findViewById(R.id.set_ensure);

        editNick.setTypeface(YmApplication.chineseTypeface);
        titleTip.setTypeface(YmApplication.chineseTypeface);
        setCancle.setTypeface(YmApplication.chineseTypeface);
        setEnsure.setTypeface(YmApplication.chineseTypeface);

        setCancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
        //        softInput();
                editDialog.dismiss();

            }
        });
        setEnsure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String nickText = editNick.getText().toString() ;
                // 更新昵称
                updateNick(nickText);
            }
        });

        editDialog.getWindow().setGravity(Gravity.CENTER);
        editDialog.show();
       //设置自定义高度，在布局edit_dialog_layout中设置layout_width宽度无效。需使用下面代码设置dialog宽度
        WindowManager windowManager = getWindowManager();
        Display display = windowManager.getDefaultDisplay();
        WindowManager.LayoutParams lp = editDialog.getWindow().getAttributes();
        lp.width = (int)(display.getWidth() - 50); //设置宽度
        editDialog.getWindow().setAttributes(lp);
    }

    /**
     * 如果输入法在窗口上已经显示，则隐藏.
     */
    private void softInput(){
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        boolean isOpen=imm.isActive();//isOpen若返回true，则表示输入法打开
        if(isOpen){
            imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    private final int RESULT_REQUEST_PHOTO = 1005;
    private final int RESULT_REQUEST_PHOTO_CROP = 1006;
    /**
     * 相机
     */
    private void camera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        fileUri = FileUtil.getOutputMediaFileUri();
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
        startActivityForResult(intent, RESULT_REQUEST_PHOTO);
    }

    /**
     * 图库
     */
    private void photo() {
        Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(i, RESULT_REQUEST_PHOTO);
    }

    /**
     * 处理图片
     * @param uri
     * @param outputUri
     * @param outputX
     * @param outputY
     * @param requestCode
     */
    private void cropImageUri(Uri uri, Uri outputUri, int outputX, int outputY, int requestCode) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", outputX);
        intent.putExtra("outputY", outputY);
        intent.putExtra("scale", true);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, outputUri);
        intent.putExtra("return-data", false);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        intent.putExtra("noFaceDetection", true); // no face detection
        startActivityForResult(intent, requestCode);
    }
}
