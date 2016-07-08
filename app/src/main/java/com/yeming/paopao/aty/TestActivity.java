package com.yeming.paopao.aty;

import android.app.Activity;
import android.os.Bundle;

import com.yeming.paopao.R;
import com.yeming.paopao.bean.Paopao;
import com.yeming.paopao.bean.User;
import com.yeming.paopao.utils.LogUtil;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobRelation;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;

/**
 * author   YeMing(yeming_1001@163.com)
 * Date:    2015-02-08 11:38
 * version: V1.0
 * Description:
 */
public class TestActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.test_activity);


    }

String TAG = "TestActivity" ;
    public void testSavePaopao(){
        final User user = BmobUser.getCurrentUser(this, User.class);
        final Paopao paopao = new Paopao();
        paopao.setContent("十年之前，我不认识你 你不属于我，我们还是一样，陪在一个陌生人左右，走过渐渐熟悉的街头;十年之后，我们是朋友， 还可以问候，只是那种温柔，再也找不到拥抱的理由，情人最后难免沦为朋友。");
        paopao.setUser(user);
        //paopao.set
        paopao.save(this,new SaveListener() {
            @Override
            public void onSuccess() {
                LogUtil.d(TAG, "-----onSuccess--");
                BmobRelation relation = new BmobRelation();
                relation.add(paopao);
                user.setPaopao(relation);
                user.update(TestActivity.this,new UpdateListener() {
                    @Override
                    public void onSuccess() {
                        LogUtil.d(TAG, "-----onSuccess1--");
                    }

                    @Override
                    public void onFailure(int i, String s) {
                        LogUtil.d(TAG, "-----onFailure1--");
                    }
                });
            }

            @Override
            public void onFailure(int i, String s) {
                LogUtil.d(TAG, "-----onFailure--");
            }
        });
    }
}
