/* global cordova */

const promiseExec = (action, args) => new Promise((resolve, reject) => {
  cordova.exec(resolve, reject, "LocalDevices", action, args);
});

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
    promiseExec("scan", [timeout, deviceTypesToRecognize])
      .then(({ state, data }) => {
        if (state === "progress") {
          return onProgress && onProgress(data);
        }
        return resolve(data);
      })
      .catch(reject);
  }),
};
