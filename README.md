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
Edit  
`npx cap open android`  
Find module `capacitor-wechat`, open `com.wind2esg.capacitor.wechat/Wechat.java`.
Then fill your appid in `APP_ID`.  
As official recommended, you better store your appsecret on server and offer api, just ignore`APP_SECRET` and use method `authLoginRemote`. Your api must return data as `https://api.weixin.qq.com/sns/userinfo?access_token=ACCESS_TOKEN&openid=OPENID`  
Or you can use `authLogin`, then fill your appsecret in `APP_SECRET`.

For more infomation, plz refer to [wechat official wiki](https://developers.weixin.qq.com/doc/oplatform/Mobile_App/WeChat_Login/Development_Guide.html)  

## API
`registerWx()` register to wechat  
`authLoginRemote({authUrl: <your api>})` auth login with your remote api
`authLogin()` auth login

## Related link
[capacitor 支付宝插件](https://github.com/Wind2esg/capacitor-alipay)  
[capacitor-alipay](https://github.com/Wind2esg/capacitor-alipay)  

