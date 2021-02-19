/* global cordova */

module.exports = {
  scan(successCallback, errorCallback) {
    cordova.exec(successCallback, errorCallback, "LocalDevices", "scan", []);
  },
};
