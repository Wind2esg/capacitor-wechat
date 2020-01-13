package com.wind2esg.capacitor.wechat;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.getcapacitor.JSObject;
import com.getcapacitor.NativePlugin;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;

import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.modelpay.PayReq;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

@NativePlugin(
    requestCodes = {Wechat.REQUEST_UNIONID}
)
public class Wechat extends Plugin {
    public static final String APP_ID = "YOUR_APP_ID";
    public static final String APP_SECRET = "YOUR_APP_SECRET";

    protected static final int REQUEST_UNIONID = 60001;
    public static final int AUTH = 99;
    public static final int PAY = 98;
    public static final String PAY_GOOD = "0";
    public static final String PAY_BAD = "-1";
    public static final String PAY_CANCEL = "-2";

    private IWXAPI api;

    private String TAG = "capacitor-wechat:";
    public Handler handler = null;

    @PluginMethod()
    public void echo(PluginCall call) {
        String value = call.getString("value");
        JSObject ret = new JSObject();
        ret.put("value", "echo from native code");
        call.resolve(ret);
    }

    @PluginMethod()
    public void registerWx(PluginCall call){
        api = WXAPIFactory.createWXAPI(getContext(), Wechat.APP_ID, true);
        api.registerApp(Wechat.APP_ID);
        Log.i(this.TAG, "register to wechat");
    }

    @PluginMethod()
    public void authLogin(PluginCall call) {
        final SendAuth.Req req = new SendAuth.Req();
        req.scope = "snsapi_userinfo";
        req.state = "capacitor";
        saveCall(call);
        WXEntryActivity.setHandlerAuth(getHandler());
        api = WXAPIFactory.createWXAPI(getContext(), Wechat.APP_ID, true);
        api.sendReq(req);
        Log.i(this.TAG, "send auth request to wechat");
    }

    @PluginMethod()
    public void pay(PluginCall call){
        api = WXAPIFactory.createWXAPI(getContext(), Wechat.APP_ID, true);
        api.registerApp(Wechat.APP_ID);
        PayReq request = new PayReq();
        request.appId = call.getString("appid");
        request.partnerId = call.getString("partnerid");
        request.prepayId= call.getString("prepayid");
        request.packageValue = call.getString("package");
        request.nonceStr= call.getString("noncestr");
        request.timeStamp= call.getString("timestamp");
        request.sign= call.getString("sign");
        saveCall(call);
        WXPayEntryActivity.setHandlerPay(getHandler());
        api.sendReq(request);
        Log.i(this.TAG, "send pay request to wechat");
    } 

    @SuppressLint("HandlerLeak")
    protected Handler getHandler(){
        if(handler == null){
            return new Handler() {
                public void handleMessage(Message msg) {
                    int tag = msg.what;
                    switch (tag) {
                        case Wechat.PAY:
                        case Wechat.AUTH: {
                            Bundle data = msg.getData();
                            try{
                                Log.i(TAG, data.getString("result"));
                                JSObject json = new JSObject(data.getString("result"));
                                PluginCall savedCall = getSavedCall();
                                if(savedCall == null){
                                    Log.i(TAG, "no savedCall");
                                    return;
                                }
                                savedCall.resolve(json);
                            }catch(Exception e){
                                Log.e(TAG, e.getMessage());
                            }
    
                        }
                    }
                }
            };
        }else{
            return handler;
        }
        
    }
}

