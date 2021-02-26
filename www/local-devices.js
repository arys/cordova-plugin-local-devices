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
  scan: (options) => {
    const { onProgress, timeout, deviceTypesToRecognize } = options || {};
    return new Promise((resolve, reject) => {
      cordova.exec(({ state, data }) => {
        if (state === "progress") {
          return onProgress && onProgress(data);
        }
        return resolve(data);
      }, reject, "LocalDevices", "scan", [timeout, deviceTypesToRecognize]);
    })
  },

  printEscPosHtml: (options) => {
    const { html, printer } = options || {};
    return new Promise((resolve, reject) => {
      cordova.exec(resolve, reject, "LocalDevices", "printEscPosHtml", [html, printer])
    });
  }
};
