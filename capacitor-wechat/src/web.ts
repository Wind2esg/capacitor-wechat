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
}

const Wechat = new WechatWeb();

export { Wechat };

import { registerWebPlugin } from '@capacitor/core';
registerWebPlugin(Wechat);
