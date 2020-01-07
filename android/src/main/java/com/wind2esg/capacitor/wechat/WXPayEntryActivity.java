package com.wind2esg.capacitor.wechat;

import com.wind2esg.capacitor.wechat.capacitorwechat.R;

import com.tencent.mm.sdk.constants.ConstantsAPI;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

public class WXPayEntryActivity extends Activity implements IWXAPIEventHandler{
	
	private static final String TAG = "MicroMsg.WXPayEntryActivity";
	
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