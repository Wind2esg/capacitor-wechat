import { WebPlugin } from '@capacitor/core';
import { WechatPlugin } from './definitions';
export declare class WechatWeb extends WebPlugin implements WechatPlugin {
    constructor();
    echo(options: {
        value: string;
    }): Promise<{
        value: string;
    }>;
    registerWx(): void;
    authLogin(): void;
    pay(payParams: {
        appid: string;
        noncestr: string;
        package: string;
        partnerid: string;
        prepayid: string;
        sign: string;
        timestamp: string;
    }): Promise<{
        value: string;
    }>;
}
declare const Wechat: WechatWeb;
export { Wechat };
