package com.wind2esg.capacitor.wechat;

import com.tencent.mm.opensdk.constants.ConstantsAPI;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

public class WXPayEntryActivity extends Activity implements IWXAPIEventHandler{
	
	private static final String TAG = "MicroMsg.WXPay";
	
    private IWXAPI api;
	
	private static Handler handlerPay;

	public static void setHandlerPay(Handler handler){
        handlerPay = handler;
	}
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    	api = WXAPIFactory.createWXAPI(this, Wechat.APP_ID);
        api.handleIntent(getIntent(), this);
    }

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		setIntent(intent);
        api.handleIntent(intent, this);
	}

	@Override
	public void onReq(BaseReq req) {
	}

	@Override
	public void onResp(BaseResp resp) {
		if (resp.getType() == ConstantsAPI.COMMAND_PAY_BY_WX) {
			Log.d(TAG, "onPayFinish, errCode = " + resp.errCode);
			JSONObject msgData = new JSONObject();
			try{
				switch (resp.errCode){
					case 0:
						msgData.put("payResult", Wechat.PAY_GOOD);
						break;
					case -1:
						msgData.put("payResult",  Wechat.PAY_BAD);
						break;
					case -2:
						msgData.put("payResult",  Wechat.PAY_CANCEL);
						break;
				}
			}catch (JSONException e){
				Log.e(TAG, e.getMessage());
			}


			Message msgPlugin = Message.obtain();
			msgPlugin.what = Wechat.PAY;
			Bundle data = new Bundle();
			data.putString("result", msgData.toString());
			msgPlugin.setData(data);
			handlerPay.sendMessage(msgPlugin);

			finish();
		}
	}
}