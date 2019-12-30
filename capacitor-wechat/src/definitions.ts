declare module "@capacitor/core" {
  interface PluginRegistry {
    Wechat: WechatPlugin;
  }
}

export interface WechatPlugin {
  echo(options: { value: string }): Promise<{value: string}>;
}
