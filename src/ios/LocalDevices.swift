import Foundation

@objc(LocalDevices)
public class LocalDevices : CDVPlugin {
  @objc
  func scan(_ command: CDVInvokedUrlCommand) {
    var lanScanner : MMLANScanner!
    self.lanScanner = MMLANScanner(delegate:self)
    self.lanScanner.start()
    let pluginResult:CDVPluginResult
    pluginResult = CDVPluginResult.init(status: CDVCommandStatus_ERROR)
    self.commandDelegate.send(pluginResult, callbackId: command.callbackId)
  }
}
