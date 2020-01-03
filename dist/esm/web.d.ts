import { WebPlugin } from '@capacitor/core';
import { WechatPlugin } from './definitions';
export declare class WechatWeb extends WebPlugin implements WechatPlugin {
    constructor();
    echo(options: {
        value: string;
    }): Promise<{
        value: string;
    }>;
}
declare const Wechat: WechatWeb;
export { Wechat };
