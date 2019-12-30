package com.wind2esg.capacitor.wechat;

import com.getcapacitor.JSObject;
import com.getcapacitor.NativePlugin;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;

import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import

@NativePlugin()
public class Wechat extends Plugin {
    private IWXAPI api;
    @PluginMethod()
    public void echo(PluginCall call) {
        String value = call.getString("value");
        JSObject ret = new JSObject();
        ret.put("value", value);
        call.success(ret);
    }

    @PluginMethod()
    public void registerWx(PluginCall call){
        if(!call.getData().has("appId")){
            call.reject("appId is necessary");
            return;
        }else{
            String appId = call.getString("appId");
            api = WXAPIFactory.createWXAPI(getContext(),appId, true);
            api.registerApp(appId);
        }
    }

    @PluginMethod()
    public void sendToWx(PluginCall call) {
        Final SendAuth.Req req = new SendAuth.Req();
        req.scope = "snsapi_userinfo";
        req.state = "wechat_sdk_demo_test";
        api.sendReq(req);
    }

}
