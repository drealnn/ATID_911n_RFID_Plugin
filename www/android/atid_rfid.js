var cordova = require('cordova'),
    exec = require('cordova/exec');

module.exports =  {
	onKeyUp : function(successCallback, errorCallback){
		exec(successCallback, errorCallback, "Barcode", 'register_keyUp', []);
	},
	onKeyDown : function(successCallback, errorCallback){
		exec(successCallback, errorCallback, "Barcode", 'register_keyDown', []);
	}

};



