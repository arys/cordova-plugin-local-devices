/* global cordova */

module.exports = {
  greet(name, successCallback, errorCallback) {
    cordova.exec(successCallback, errorCallback, "LocalDevices", "greet", [name]);
  },
  scan(successCallback, errorCallback) {
    console.log("SCANNN");
    cordova.exec(successCallback, errorCallback, "LocalDevices", "scan", []);
  },
};
