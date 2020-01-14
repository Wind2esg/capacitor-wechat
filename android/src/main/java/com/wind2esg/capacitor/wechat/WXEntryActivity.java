package com.wind2esg.capacitor.wechat;

import com.wind2esg.capacitor.wechat.capacitorwechat.R;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.tencent.mm.opensdk.constants.ConstantsAPI;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.modelbiz.SubscribeMessage;
import com.tencent.mm.opensdk.modelbiz.WXLaunchMiniProgram;
import com.tencent.mm.opensdk.modelbiz.WXOpenBusinessView;
import com.tencent.mm.opensdk.modelbiz.WXOpenBusinessWebview;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.modelmsg.ShowMessageFromWX;
import com.tencent.mm.opensdk.modelmsg.WXAppExtendObject;
import com.tencent.mm.opensdk.modelmsg.WXMediaMessage;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.lang.ref.WeakReference;

public class WXEntryActivity extends Activity implements IWXAPIEventHandler{
    private static String TAG = "MicroMsg.WXEntryActivity";

    private IWXAPI api;
    private static MyHandler handler;
    private static Handler handlerAuth;

    private static String refreshToken;
    private static String openId;
    private static String accessToken;
    private static String scope;
    private static String unionId;

    private static String code;

    public static void setHandlerAuth(Handler handler){
        handlerAuth = handler;
    }

    private static class MyHandler extends Handler {
        private final WeakReference<WXEntryActivity> wxEntryActivityWeakReference;

        public MyHandler(WXEntryActivity wxEntryActivity){
            wxEntryActivityWeakReference = new WeakReference<>(wxEntryActivity);
        }

        @Override
        public void handleMessage(Message msg) {
            int tag = msg.what;
            Bundle data = msg.getData();
            JSONObject json = null;
            switch (tag) {
                case NetworkUtil.GET_TOKEN: {
                    try {
                        json = new JSONObject(data.getString("result"));
                        openId = json.getString("openid");
                        accessToken = json.getString("access_token");
                        refreshToken = json.getString("refresh_token");
                        scope = json.getString("scope");

                        if (accessToken != null && openId != null){
                            NetworkUtil.sendWxAPI(handler, String.format("https://api.weixin.qq.com/sns/auth?" +
                                    "access_token=%s&openid=%s", accessToken, openId), NetworkUtil.CHECK_TOKEN);
                        } else {
                            Toast.makeText(wxEntryActivityWeakReference.get(), "请先获取code", Toast.LENGTH_LONG).show();
                        }
                        try {
                            unionId = json.getString("unionid");
                        }catch (JSONException e) {
                            Log.e(TAG, e.getMessage());
                        }
                    } catch (JSONException e) {
                        Log.e(TAG, e.getMessage());
                    }
                }
                case NetworkUtil.CHECK_TOKEN: {
                    try {
                        json = new JSONObject(data.getString("result"));
                        int errcode = json.getInt("errcode");
                        if (errcode == 0) {
                            NetworkUtil.sendWxAPI(handler, String.format("https://api.weixin.qq.com/sns/userinfo?" +
                                    "access_token=%s&openid=%s", accessToken, openId), NetworkUtil.GET_INFO);
                        } else {
                            NetworkUtil.sendWxAPI(handler, String.format("https://api.weixin.qq.com/sns/oauth2/refresh_token?" +
                                            "appid=%s&grant_type=refresh_token&refresh_token=%s", Wechat.APP_ID, refreshToken),
                                    NetworkUtil.REFRESH_TOKEN);
                        }
                    } catch (JSONException e) {
                        Log.e(TAG, e.getMessage());
                    }
                    break;
                }
                case NetworkUtil.REFRESH_TOKEN: {
                    try {
                        json = new JSONObject(data.getString("result"));
                        openId = json.getString("openid");
                        accessToken = json.getString("access_token");
                        refreshToken = json.getString("refresh_token");
                        scope = json.getString("scope");
                        NetworkUtil.sendWxAPI(handler, String.format("https://api.weixin.qq.com/sns/userinfo?" +
                                "access_token=%s&openid=%s", accessToken, openId), NetworkUtil.GET_INFO);
                    } catch (JSONException e) {
                        Log.e(TAG, e.getMessage());
                    }
                    break;
                }
                case NetworkUtil.GET_INFO: {
                    try {
                        json = new JSONObject(data.getString("result"));
                        JSONObject msgData = new JSONObject();
                        String encode = getCode(json.getString("nickname"));
                        String nickname = new String(json.getString("nickname").getBytes(encode), "utf-8");

                        msgData.put("openid", json.getString("openid"));
                        msgData.put("unionid", json.getString("unionid"));
                        msgData.put("nickname", nickname);
                        msgData.put("code", code);
                        msgData.put("sex", json.getString("sex"));
                        msgData.put("language", json.getString("language"));
                        msgData.put("city", json.getString("city"));
                        msgData.put("province", json.getString("province"));
                        msgData.put("country", json.getString("country"));
                        msgData.put("headimgurl", json.getString("headimgurl"));
                        data.putString("result", msgData.toString());

                        Message msgPlugin = Message.obtain();
                        msgPlugin.what = Wechat.AUTH;
                        msgPlugin.setData(data);

                        handlerAuth.sendMessage(msgPlugin);

                    } catch (JSONException e) {
                        Log.e(TAG, e.getMessage());
                    } catch (UnsupportedEncodingException e) {
                        Log.e(TAG, e.getMessage());
                    }
                    break;
                }
                case NetworkUtil.GET_IMG: {
                    byte[] imgdata = data.getByteArray("imgdata");
                    final Bitmap bitmap;
                    if (imgdata != null) {
                        bitmap = BitmapFactory.decodeByteArray(imgdata, 0, imgdata.length);
                    } else {
                        bitmap = null;
                        showToast(wxEntryActivityWeakReference.get(), "头像图片获取失败");
                    }
                    break;
                }
            }
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        api = WXAPIFactory.createWXAPI(this, Wechat.APP_ID, false);
        handler = new MyHandler(this);

        try {
            Intent intent = getIntent();
            api.handleIntent(intent, this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        setIntent(intent);
        api.handleIntent(intent, this);
    }

    @Override
    public void onReq(BaseReq req) {
        switch (req.getType()) {
            case ConstantsAPI.COMMAND_GETMESSAGE_FROM_WX:
                goToGetMsg();
                break;
            case ConstantsAPI.COMMAND_SHOWMESSAGE_FROM_WX:
                goToShowMsg((ShowMessageFromWX.Req) req);
                break;
            default:
                break;
        }
        finish();
    }

    @Override
    public void onResp(BaseResp resp) {
        int result = 0;

        switch (resp.errCode) {
            case BaseResp.ErrCode.ERR_OK:
                result = R.string.errcode_success;
                break;
            case BaseResp.ErrCode.ERR_USER_CANCEL:
                result = R.string.errcode_cancel;
                break;
            case BaseResp.ErrCode.ERR_AUTH_DENIED:
                result = R.string.errcode_deny;
                break;
            case BaseResp.ErrCode.ERR_UNSUPPORT:
                result = R.string.errcode_unsupported;
                break;
            default:
                result = R.string.errcode_unknown;
                break;
        }

//        Toast.makeText(this, getString(result) + ", type=" + resp.getType(), Toast.LENGTH_SHORT).show();

        // if (resp.getType() == ConstantsAPI.COMMAND_SUBSCRIBE_MESSAGE) {
        //     SubscribeMessage.Resp subscribeMsgResp = (SubscribeMessage.Resp) resp;
        //     String text = String.format("openid=%s\ntemplate_id=%s\nscene=%d\naction=%s\nreserved=%s",
        //             subscribeMsgResp.openId, subscribeMsgResp.templateID, subscribeMsgResp.scene, subscribeMsgResp.action, subscribeMsgResp.reserved);

        //     Toast.makeText(this, text, Toast.LENGTH_LONG).show();
        // }

        // if (resp.getType() == ConstantsAPI.COMMAND_LAUNCH_WX_MINIPROGRAM) {
        //     WXLaunchMiniProgram.Resp launchMiniProgramResp = (WXLaunchMiniProgram.Resp) resp;
        //     String text = String.format("openid=%s\nextMsg=%s\nerrStr=%s",
        //             launchMiniProgramResp.openId, launchMiniProgramResp.extMsg,launchMiniProgramResp.errStr);

        //     Toast.makeText(this, text, Toast.LENGTH_LONG).show();
        // }

        // if (resp.getType() == ConstantsAPI.COMMAND_OPEN_BUSINESS_VIEW) {
        //     WXOpenBusinessView.Resp launchMiniProgramResp = (WXOpenBusinessView.Resp) resp;
        //     String text = String.format("openid=%s\nextMsg=%s\nerrStr=%s\nbusinessType=%s",
        //             launchMiniProgramResp.openId, launchMiniProgramResp.extMsg,launchMiniProgramResp.errStr,launchMiniProgramResp.businessType);

        //     Toast.makeText(this, text, Toast.LENGTH_LONG).show();
        // }

        // if (resp.getType() == ConstantsAPI.COMMAND_OPEN_BUSINESS_WEBVIEW) {
        //     WXOpenBusinessWebview.Resp response = (WXOpenBusinessWebview.Resp) resp;
        //     String text = String.format("businessType=%d\nresultInfo=%s\nret=%d",response.businessType,response.resultInfo,response.errCode);

        //     Toast.makeText(this, text, Toast.LENGTH_LONG).show();
        // }

        if (resp.getType() == ConstantsAPI.COMMAND_SENDAUTH) {
            SendAuth.Resp authResp = (SendAuth.Resp)resp;

            code = authResp.code;//here we need the code

//            Toast.makeText(this, "code=" + authResp.code, Toast.LENGTH_SHORT).show();
            if(Wechat.AUTH_URL == ""){
                //authLogin
                NetworkUtil.sendWxAPI(
                    handler, 
                    String.format("https://api.weixin.qq.com/sns/oauth2/access_token?" + "appid=%s&secret=%s&code=%s&grant_type=authorization_code", Wechat.APP_ID, Wechat.APP_SECRET, authResp.code), 
                    NetworkUtil.GET_TOKEN
                );
            }else{
                //authLoginRemote
                NetworkUtil.sendWxAPI(
                    handler, 
                    Wechat.AUTH_URL,
                    NetworkUtil.GET_INFO
                );
            }
        }
        finish();
    }

    private void goToGetMsg() {
        Intent intent = new Intent(this, Wechat.class);
        intent.putExtras(getIntent());
        startActivity(intent);
        finish();
    }

    private void goToShowMsg(ShowMessageFromWX.Req showReq) {
        WXMediaMessage wxMsg = showReq.message;
        WXAppExtendObject obj = (WXAppExtendObject) wxMsg.mediaObject;

        StringBuffer msg = new StringBuffer();
        msg.append("description: ");
        msg.append(wxMsg.description);
        msg.append("\n");
        msg.append("extInfo: ");
        msg.append(obj.extInfo);
        msg.append("\n");
        msg.append("filePath: ");
        msg.append(obj.filePath);

        Intent intent = new Intent(this, Wechat.class);
        intent.putExtra("showmsg_title", wxMsg.title);
        intent.putExtra("showmsg_message", msg.toString());
        intent.putExtra("showmsg_thumb_data", wxMsg.thumbData);
        startActivity(intent);
        finish();
    }

    private static void showToast(Context context, String text) {
        Toast.makeText(context, text, Toast.LENGTH_LONG).show();
    }

    private static String getCode (String str) {
        String[] encodelist ={"GB2312","ISO-8859-1","UTF-8","GBK","Big5","UTF-16LE","Shift_JIS","EUC-JP"};
        for(int i =0;i<encodelist.length;i++){
            try {
                if (str.equals(new String(str.getBytes(encodelist[i]),encodelist[i]))) {
                    return encodelist[i];
                }
            } catch (Exception e) {

            } finally{

            }
        } return "";
    }
}