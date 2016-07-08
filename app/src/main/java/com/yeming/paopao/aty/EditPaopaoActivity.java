package com.yeming.paopao.aty;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bmob.BTPFileResponse;
import com.bmob.BmobProFile;
import com.bmob.btp.callback.UploadListener;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.yeming.paopao.R;
import com.yeming.paopao.app.YmApplication;
import com.yeming.paopao.bean.Paopao;
import com.yeming.paopao.bean.User;
import com.yeming.paopao.commons.Constant;
import com.yeming.paopao.utils.FileUtil;
import com.yeming.paopao.utils.LogUtil;
import com.yeming.paopao.views.ToastView;
import com.yeming.paopao.views.third.DialogView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobRelation;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;

/**
 * author   YeMing(yeming_1001@163.com)
 * Date:    2015-02-08 16:13
 * version: V1.0
 * Description: 编辑发布
 */
public class EditPaopaoActivity extends Activity implements View.OnClickListener{
    private static final String TAG = "EditPaopaoActivity";
    private ImageView imageView ;
    private EditText editContent ;
    private TextView image_pros ;
    AlertDialog chooseDialog;
    private Context context ;
    String dateTime;
    private String imageLocalPath = null;
    private static final int REQUEST_CODE_CAMERA = 1;
    private static final int REQUEST_CODE_IMAGE = 2;
    private static final int REQUEST_CODE_RESULT = 3;
    private ImageLoader imageLoader = ImageLoader.getInstance() ;
    private boolean isFinished = false ;  //  发布信息是否正在进行
    private Dialog loadDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.edit_paopao_layout);

        YmApplication.getInstance().addActivity(this);
        context = this ;
        loadDialog = DialogView.loadDialog(context, R.string.releaseing) ;
        setActionBar() ;
        initView() ;
    }

    public void initView(){
        imageView = (ImageView) findViewById(R.id.image_add);
        editContent = (EditText) findViewById(R.id.add_content);
        image_pros = (TextView) findViewById(R.id.image_progress);
        image_pros.setTypeface(YmApplication.chineseTypeface);
        editContent.setTypeface(YmApplication.chineseTypeface);
        imageView.setOnClickListener(this);
    }

    /**
     * 选择框
     */
    private void showChoosePicDialog(){
        chooseDialog = new AlertDialog.Builder(context).create();
        chooseDialog.setCanceledOnTouchOutside(false);
        View v = LayoutInflater.from(context).inflate(R.layout.dialog_choose_pic_layout, null);
        chooseDialog.show();
        chooseDialog.setContentView(v);
        chooseDialog.getWindow().setGravity(Gravity.CENTER);

        TextView albumPic = (TextView)v.findViewById(R.id.album_pic);
        TextView cameraPic = (TextView)v.findViewById(R.id.camera_pic);

        albumPic.setTypeface(YmApplication.chineseTypeface);
        cameraPic.setTypeface(YmApplication.chineseTypeface);

        albumPic.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                Date date = new Date(System.currentTimeMillis());
                dateTime = date.getTime() + "";
                getPicFromAlbum() ;
                chooseDialog.dismiss();

            }
        });
        cameraPic.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Date date = new Date(System.currentTimeMillis());
                dateTime = date.getTime() + "";
                getPicFromCamere();
                chooseDialog.dismiss();

            }
        });
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
        int titleId = Resources.getSystem().getIdentifier("action_bar_title",
                "id", "android");
        TextView textView = (TextView) findViewById(titleId);
        textView.setTypeface(YmApplication.chineseTypeface);
        textView.setTextColor(0xFFdfdfdf);
        textView.setTextSize(20);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK){
            switch (requestCode) {
                case REQUEST_CODE_RESULT:

                    break;
                case REQUEST_CODE_IMAGE:
                    String fileName = null;
                    if(data!=null){
                        Uri originalUri = data.getData();
                        ContentResolver cr = getContentResolver();
                        Cursor cursor = cr.query(originalUri, null, null, null, null);
                        if(cursor.moveToFirst()){
                            do{
                                fileName= cursor.getString(cursor.getColumnIndex("_data"));
                                LogUtil.d(TAG, "get album:" + fileName);
                            }while (cursor.moveToNext());
                        }
                        Bitmap bitmap = compressImageFromFile(fileName);
                        imageLocalPath = saveToSdCard(bitmap);
                        LogUtil.d(TAG,"------imageLocalPath--------"+imageLocalPath);
                        imageView.setBackgroundDrawable(new BitmapDrawable(bitmap));
                        //imageLoader.displayImage(imageLocalPath,imageView, ImageLoadOptions.getOptionsById(0));
                    }
                    break;
                case REQUEST_CODE_CAMERA:
                    String files =FileUtil.getCacheDirectory(context, true, "pic") + dateTime;
                    File file = new File(files);
                    if(file.exists()){
                        Bitmap bitmap = compressImageFromFile(files);
                        imageLocalPath = saveToSdCard(bitmap);
                        imageView.setBackgroundDrawable(new BitmapDrawable(bitmap));
                    }else{

                    }
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.edit_push_paopao, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        int id = item.getItemId() ;
        switch (id){
            case R.id.action_push:
                Log.d(TAG, "------- action_add click--------");
                actionAdd() ;
                break ;
            case android.R.id.home:
                Log.d(TAG, "------- home click--------");
                if(isFinished){
                  return false ;
                }
                onBackPressed();
                break ;
        }
        return super.onMenuItemSelected(featureId, item);
    }

    /**
     * 发布泡泡
     */
    public void actionAdd(){
        String commitContent = editContent.getText().toString().trim();
        if(TextUtils.isEmpty(commitContent)){
            ToastView.showToast(context,"内容不能为空！", Toast.LENGTH_SHORT);
            return;
        }
    //    ToastView.showToast(context,"正在发布！", Toast.LENGTH_SHORT);
        loadDialog.show();
        isFinished = true ;  //  正在发布
        if(imageLocalPath == null){
            pushWithOutPic(commitContent,null);
        }else{
            pushWithPic(commitContent);
        }
    }

    /**
     * @param commitContent
     * 发表带图片
     */
    private void pushWithPic(final String commitContent){

        BTPFileResponse btpFileResponse = BmobProFile.getInstance(context).upload(imageLocalPath,new UploadListener() {
            @Override
            public void onSuccess(String fileName, String fileUrl) {
                Log.d(TAG, "------- upload Success fileName--------"+fileName);
                Log.d(TAG, "------- upload Success fileUrl--------"+fileUrl);
                String signUrl =BmobProFile.getInstance(context).signURL(fileName,fileUrl,
                        Constant.ACCESS_KEY,0,null);
                Log.d(TAG, "------- upload Success signUrl--------"+signUrl);
                pushWithOutPic(commitContent,signUrl);
                image_pros.setVisibility(View.GONE);
            }

            @Override
            public void onProgress(int i) {
                Log.d(TAG, "------- upload Progress--------"+i);
                image_pros.setVisibility(View.VISIBLE);
                String str = i+"%" ;
                image_pros.setText(str);
            }

            @Override
            public void onError(int i, String s) {
                Log.d(TAG, "------- upload Failure code--------"+i);
                Log.d(TAG, "------- upload Failure--------"+s);
                isFinished = false ;
                if(loadDialog.isShowing()){
                    loadDialog.dismiss();
                }
                image_pros.setVisibility(View.GONE);
                ToastView.showToast(context,"发表失败,图片上传失败！",Toast.LENGTH_SHORT);
            }
        }) ;


        /*final BmobFile bmobFile = new BmobFile(new File(imageLocalPath)) ;
        bmobFile.uploadblock(context,new UploadFileListener() {
            @Override
            public void onSuccess() {
                Log.d(TAG, "------- upload Success--------");
                pushWithOutPic(commitContent,bmobFile);
                handler.sendEmptyMessage(2) ;
            }

            @Override
            public void onProgress(Integer integer) {
                Log.d(TAG, "------- upload Progress--------"+integer);
                super.onProgress(integer);
                //String str = "%1$d%" ;
                //str = String.format(str,integer);
                //String str = String.valueOf(integer)+"%" ;
                //image_pros.setText(str);
                Message message = new Message() ;
                message.arg1 = integer ;
                message.what = 1 ;
                handler.sendMessage(message) ;
            }

            @Override
            public void onStart() {
                Log.d(TAG, "------- upload Start--------");
                  super.onStart();
            //    image_pros.setVisibility(View.VISIBLE);
             //   String str = "0%" ;
               // str = String.format(str,0);
             //   image_pros.setText(str);
                handler.sendEmptyMessage(0) ;
            }

            @Override
            public void onFailure(int i, String s) {
                Log.d(TAG, "------- upload Failure code--------"+i);
                Log.d(TAG, "------- upload Failure--------"+s);
            }
        });*/
    }

    /**
     * @param commitContent
     * @param imageUrl
     * 发表仅内容不带图片
     */
    private void pushWithOutPic(final String commitContent,final String imageUrl/*final BmobFile picFile*/){
        Calendar now = Calendar.getInstance();
        String timeMillis = String.valueOf(now.getTimeInMillis()) ;
        final User user = BmobUser.getCurrentUser(context, User.class);
        final Paopao paopao = new Paopao() ;
        paopao.setUser(user);
        paopao.setCreateTimeMillis(timeMillis);
        paopao.setDevice(Build.MODEL);
        paopao.setContent(commitContent);
        if(imageUrl != null){
           paopao.setImageUrl(imageUrl);
        }
        paopao.save(context,new SaveListener() {
            @Override
            public void onSuccess() {
                Log.d(TAG, "-------paopao onSuccess--------");
                BmobRelation relation = new BmobRelation();
                relation.add(paopao);
                user.setPaopao(relation);
                user.update(context, new UpdateListener() {

                    @Override
                    public void onSuccess() {
                        Log.d(TAG, "-------paopao relation  onSuccess--------");
                        if(loadDialog.isShowing()){
                            loadDialog.dismiss();
                        }
                        ToastView.showToast(context,"发表成功！",Toast.LENGTH_SHORT);
                        isFinished = false ;
                        Intent intent = new Intent();
                        intent.putExtra("add_paopao",paopao) ;
                        setResult(Activity.RESULT_OK, intent);
                        finish();
                    }

                    @Override
                    public void onFailure(int arg0, String arg1) {
                        if(loadDialog.isShowing()){
                            loadDialog.dismiss();
                        }
                        Log.d(TAG, "-------paopao relation  onSuccess--------");
                        isFinished = false ;
                        ToastView.showToast(context,"发表失败！",Toast.LENGTH_SHORT);
                    }
                });


            }

            @Override
            public void onFailure(int i, String s) {
                if(loadDialog.isShowing()){
                    loadDialog.dismiss();
                }
                isFinished = false ;
                ToastView.showToast(context,"发表失败！",Toast.LENGTH_SHORT);
                Log.d(TAG, "-------paopao save onFailure--------"+s);
            }
        });
    }

    /**
     * 图库
     */
    private void getPicFromAlbum(){
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setDataAndType(MediaStore.Images.Media.INTERNAL_CONTENT_URI, "image/*");
        startActivityForResult(intent, REQUEST_CODE_IMAGE);
    }

    /**
     * 照相机
     */
    private void getPicFromCamere(){
        File f = new File(FileUtil.getCacheDirectory(context, true, "pic") + dateTime);
        if (f.exists()) {
            f.delete();
        }
        try {
            f.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Uri uri = Uri.fromFile(f);
        Log.d(TAG, "---------------" + uri + "");

        Intent camera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        camera.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        startActivityForResult(camera, REQUEST_CODE_CAMERA);
    }

    /**
     * @param bitmap
     * @return
     * 保存到sd卡
     */
    public String saveToSdCard(Bitmap bitmap){
        String files =FileUtil.getCacheDirectory(context, true, "pic") + dateTime+"_ym.jpg";
        File file=new File(files);
        try {
            FileOutputStream out=new FileOutputStream(file);
            if(bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out)){
                out.flush();
                out.close();
            }
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        LogUtil.d(TAG, file.getAbsolutePath());
        return file.getAbsolutePath();
    }

    /**
     * @param srcPath
     * @return
     * 压缩图片
     */
    private Bitmap compressImageFromFile(String srcPath) {
        BitmapFactory.Options newOpts = new BitmapFactory.Options();
        newOpts.inJustDecodeBounds = true;//只读边,不读内容
        Bitmap bitmap = BitmapFactory.decodeFile(srcPath, newOpts);

        newOpts.inJustDecodeBounds = false;
        int w = newOpts.outWidth;
        int h = newOpts.outHeight;
        float hh = 800f;//800
        float ww = 480f;//480
        int be = 1;
        if (w > h && w > ww) {
            be = (int) (newOpts.outWidth / ww);
        } else if (w < h && h > hh) {
            be = (int) (newOpts.outHeight / hh);
        }
        if (be <= 0)
            be = 1;
        newOpts.inSampleSize = be;//设置采样率

        newOpts.inPreferredConfig = Bitmap.Config.ARGB_8888;//该模式是默认的,可不设
        newOpts.inPurgeable = true;// 同时设置才会有效
        newOpts.inInputShareable = true;//。当系统内存不够时候图片自动被回收

        bitmap = BitmapFactory.decodeFile(srcPath, newOpts);
        return bitmap;
    }

    @Override
    public void onClick(View view) {
        int id = view.getId() ;
        switch (id){
            case R.id.image_add:   //  点击添加图片
                showChoosePicDialog();
                break ;
            default:
                break ;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
