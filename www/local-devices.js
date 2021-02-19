/* global cordova */

module.exports = {
  DEVICE_TYPES: {
    ESCPOS: "printer-escpos",
  },

  /**
   * Scans local network and recognizes device types
   *
   * @param {Object} options - Scan options
   * @param {function} options.onProgress - Progress callback
   * @param {number} options.timeout - Scanning timeout, default: 500 ms
   * @param {String[]} options.deviceTypesToRecognize - Device types to recognize, default: all
   */
  scan: ({ onProgress, timeout, deviceTypesToRecognize }) => new Promise((resolve, reject) => {
    cordova.exec(({ state, data }) => {
      if (state === "progress") {
        return onProgress && onProgress(data);
      }
      return resolve(data);
    }, reject, "LocalDevices", "scan", [timeout, deviceTypesToRecognize]);
  }),
};
