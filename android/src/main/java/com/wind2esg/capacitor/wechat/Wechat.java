package com.wind2esg.capacitor.wechat;

import com.getcapacitor.JSObject;
import com.getcapacitor.NativePlugin;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;

import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

@NativePlugin()
public class Wechat extends Plugin {
    private String APP_ID = ""; //you appid here
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
        api = WXAPIFactory.createWXAPI(getContext(), this.APP_ID, false);
        api.registerApp(this.APP_ID);
    }

    @PluginMethod()
    public void sendToWx(PluginCall call) {
        final SendAuth.Req req = new SendAuth.Req();
        req.scope = "snsapi_userinfo";
        req.state = "wechat_sdk_mzy";
        api.sendReq(req);
    }

}
