import { WebPlugin } from '@capacitor/core';
import { WechatPlugin } from './definitions';

export class WechatWeb extends WebPlugin implements WechatPlugin {
  constructor() {
    super({
      name: 'Wechat',
      platforms: ['web']
    });
  }

  async echo(options: { value: string }): Promise<{value: string}> {
    console.log('ECHO', options);
    return options;
  }

  registerWx(){}
  authLogin(){}
  pay(payParams: {
    appid: string,
    noncestr: string,
    package: string,
    partnerid: string,
    prepayid: string,
    sign: string,
    timestamp: string
  }): Promise<{value: string}>{
    console.log(payParams);
    return new Promise(()=>"")
  }
}

const Wechat = new WechatWeb();

export { Wechat };

import { registerWebPlugin } from '@capacitor/core';
registerWebPlugin(Wechat);
