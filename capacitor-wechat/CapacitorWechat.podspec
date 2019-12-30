
  Pod::Spec.new do |s|
    s.name = 'CapacitorWechat'
    s.version = '0.0.1'
    s.summary = 'capacitor wechat sdk plugin'
    s.license = 'MIT'
    s.homepage = 'github.com/Wind2esg/capacitor-wechat'
    s.author = 'wind2esg'
    s.source = { :git => 'github.com/Wind2esg/capacitor-wechat', :tag => s.version.to_s }
    s.source_files = 'ios/Plugin/**/*.{swift,h,m,c,cc,mm,cpp}'
    s.ios.deployment_target  = '11.0'
    s.dependency 'Capacitor'
  end