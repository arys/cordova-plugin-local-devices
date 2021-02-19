/* global cordova */

module.exports = {
  greet(name, successCallback, errorCallback) {
    cordova.exec(successCallback, errorCallback, "LocalDevices", "greet", [name]);
  },
  scan(args, successCallback, errorCallback) {
    cordova.exec(successCallback, errorCallback, "LocalDevices", "scan", [args]);
  },
};
