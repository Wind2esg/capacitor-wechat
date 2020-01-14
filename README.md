# capacitor-wechat
[![Build Status](https://travis-ci.org/Wind2esg/capacitor-wechat.svg?branch=master)](https://travis-ci.org/Wind2esg/capacitor-wechat)
[![Build Status](https://img.shields.io/npm/l/capacitor-wechat)](https://www.npmjs.com/package/capacitor-wechat)
[![Build Status](https://img.shields.io/npm/v/capacitor-wechat)](https://www.npmjs.com/package/capacitor-wechat)
[![Build Status](https://img.shields.io/npm/dm/capacitor-wechat)](https://www.npmjs.com/package/capacitor-wechat)

## Usage
Install  

`npm i -S capacitor-wechat`    
`npx cap update`

### Android
Edit native code.  

+ `npx cap open android`  

+ Find module `app`, open `<your package>/MainActivity.java`, add `add(Wechat.class);`.

+ Find module `capacitor-wechat`, open `com.wind2esg.capacitor.wechat/Wechat.java`.
Then fill your appid in `APP_ID`.  
As official recommended, you better store your appsecret on server and offer api, just ignore `APP_SECRET` and use method `authLoginRemote`. Your api must return data as `https://api.weixin.qq.com/sns/userinfo?access_token=ACCESS_TOKEN&openid=OPENID`  
Or you can use `authLogin`, then fill your appsecret in `APP_SECRET`.

编辑原生代码。
+ `npx cap open android` 打开 IDE。  

+ 找到 `app` 模块，找到你包名下的 `ainActivity.java`，添加 `add(Wechat.class);`。  
+ 找到 `capacitor-wechat` 模块下 `com.wind2esg.capacitor.wechat/Wechat.java` 文件。
将你的 appid 填入 `APP_ID`。  
官方推荐将 appsecret 存储到服务端，并提供 api。如果使用此类方式，忽略 `APP_SECRET` 并且调用 `authLoginRemote` 方法。api 的返回应该与 `https://api.weixin.qq.com/sns/userinfo?access_token=ACCESS_TOKEN&openid=OPENID` 一致。  
或者调用 `authLogin`，那么需要将 appsecret 填入 `APP_SECRET`。  

For more infomation, plz refer to [wechat official wiki](https://developers.weixin.qq.com/doc/oplatform/Mobile_App/WeChat_Login/Development_Guide.html)  

更多详情 [官方文档](https://developers.weixin.qq.com/doc/oplatform/Mobile_App/WeChat_Login/Development_Guide.html)  

### IOS  
TBD  

## API
+ `registerWx`
  + param:
  + return: 
+ `pay`
  + param: `{ appid: string, partnerid: string, prepayid: string, package: string, noncestr: string, timestamp: string, sign: string }`
  + return: `{ payResult: string }`
+ `authLoginRemote`
  + param: `{ authUrl: string }`
  + return: `{openid: string, nickname: string, sex: string, province: string, city: string, country: string, headimgurl: string, privilege: string, unionid: string, code: string}`
+ `authLogin`
  + param:
  + return: same as `authLoginRemote`

> `authLoginRemote` and `authLogin`, here offer extra data `code`.  

> For more infomation, plz refer to [wechat official wiki](https://developers.weixin.qq.com/doc/oplatform/Mobile_App/WeChat_Login/Development_Guide.html)  

## Related link
[capacitor 支付宝插件](https://github.com/Wind2esg/capacitor-alipay)  
[capacitor-alipay](https://github.com/Wind2esg/capacitor-alipay)  

