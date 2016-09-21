var cordova = require('cordova'),
    exec = require('cordova/exec');

module.exports =  {
	echoObject : function(str, callback) {
	    exec(callback, function(err) {
		callback('Nothing to echo.');
	    }, "Barcode", "echo", [str]);
	},
	startDecode : function(successCallback, errorCallback){
		exec(successCallback, errorCallback, "Barcode", 'scanner_startDecode', []);
	},
	stopDecode : function(successCallback, errorCallback){
		exec(successCallback, errorCallback, "Barcode", 'scanner_stopDecode', []);
	},
	isDecoding : function(successCallback, errorCallback){
		exec(successCallback, errorCallback, "Barcode", 'scanner_isDecoding', []);
	},
	wakeup : function(successCallback, errorCallback){
		exec(successCallback, errorCallback, "Barcode", 'wakeup_scanner', []);
	},
	sleep : function(successCallback, errorCallback){
		exec(successCallback, errorCallback, "Barcode", 'sleep_scanner', []);
	},
	deinitialize : function(successCallback, errorCallback){
		exec(successCallback, errorCallback, "Barcode", 'deinitialize_scanner', []);
	},
	onDecode : function(successCallback, errorCallback){
		exec(successCallback, errorCallback, "Barcode", 'register_decode', []);
	},
	onKeyUp : function(successCallback, errorCallback){
		exec(successCallback, errorCallback, "Barcode", 'register_keyUp', []);
	},
	onKeyDown : function(successCallback, errorCallback){
		exec(successCallback, errorCallback, "Barcode", 'register_keyDown', []);
	}

};



