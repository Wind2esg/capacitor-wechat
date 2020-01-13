declare module "@capacitor/core" {
  interface PluginRegistry {
    Wechat: WechatPlugin;
  }
}

export interface WechatPlugin {
  echo(options: { value: string }): Promise<{value: string}>;
  registerWx(): void;
  authLogin(): void;
  pay(payParams: {
    appid: string,
    noncestr: string,
    package: string,
    partnerid: string,
    prepayid: string,
    sign: string,
    timestamp: string
  }): Promise<{payResult: string}>;
}
