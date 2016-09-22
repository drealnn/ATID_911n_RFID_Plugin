# ATID_911n_RFID_Plugin

Cordova plugin for the atid 911n rfid scanner. In development.

Currently supported methods:

```
atid.rfid {
  deinitalize : function(successCallback, errorCallback){
		exec(successCallback, errorCallback, "Rfid", 'deinitalize', []);
	},
	wakeup : function(successCallback, errorCallback){
		exec(successCallback, errorCallback, "Rfid", 'wakeup', []);
	},
	sleep : function(successCallback, errorCallback){
		exec(successCallback, errorCallback, "Rfid", 'sleep', []);
	},
	pause_scanner : function(successCallback, errorCallback){
		exec(successCallback, errorCallback, "Rfid", 'pause_scanner', []);
	},
	resume_scanner : function(successCallback, errorCallback){
		exec(successCallback, errorCallback, "Rfid", 'resume_scanner', []);
	},
	getActionState : function(successCallback, errorCallback){
		exec(successCallback, errorCallback, "Rfid", 'getActionState', []);
	},
	getPowerRange : function(successCallback, errorCallback){
		exec(successCallback, errorCallback, "Rfid", 'getPowerRange', []);
	},
	getPower : function(successCallback, errorCallback){
		exec(successCallback, errorCallback, "Rfid", 'getPower', []);
	},
	getOperationTime : function(successCallback, errorCallback){
		exec(successCallback, errorCallback, "Rfid", 'getOperationTime', []);
	},
	setPower : function(powerInt, successCallback, errorCallback){
		exec(successCallback, errorCallback, "Rfid", 'setPower', [powerInt]);
	},
	setOperationTime : function(operationTime, successCallback, errorCallback){
		exec(successCallback, errorCallback, "Rfid", 'setOperationTime', [operationTime]);
	},
	setInventoryTime : function(inventoryTime, successCallback, errorCallback){
		exec(successCallback, errorCallback, "Rfid", 'setInventoryTime', [inventoryTime]);
	},
	setIdleTime : function(idleTime, successCallback, errorCallback){
		exec(successCallback, errorCallback, "Rfid", 'setIdleTime', [idleTime]);
	},
	onReaderReadTag : function(successCallback, errorCallback){
		exec(successCallback, errorCallback, "Rfid", 'onReaderReadTag', []);
	},
	onReaderResult :  function(successCallback, errorCallback){
		exec(successCallback, errorCallback, "Rfid", 'onReaderResult', []);
	},
	start_readTagSingle :  function(successCallback, errorCallback){
		exec(successCallback, errorCallback, "Rfid", 'start_readSingle', []);
	},
	start_readTagContinuous :  function(successCallback, errorCallback){
		exec(successCallback, errorCallback, "Rfid", 'start_readContinuous', []);
	},
	start_readTagMemory : function(args, successCallback, errorCallback){
		exec(successCallback, errorCallback, "Rfid", 'start_readMemory', [args]);
	},
	start_writeTagMemory : function(args, successCallback, errorCallback){
		exec(successCallback, errorCallback, "Rfid", 'start_writeMemory', [args]);
	},
	stop_scan :  function(successCallback, errorCallback){
		exec(successCallback, errorCallback, "Rfid", 'stop_read', []);
	},
	isStopped :  function(successCallback, errorCallback){
		exec(successCallback, errorCallback, "Rfid", 'isStopped', []);
	},
	onKeyUp : function(successCallback, errorCallback){
		exec(successCallback, errorCallback, "Rfid", 'register_keyUp', []);
	},
	onKeyDown : function(successCallback, errorCallback){
		exec(successCallback, errorCallback, "Rfid", 'register_keyDown', []);
	}


};
```

Methods can be categorized into four sections: 
lifecycle (wakeup, sleep, deinitialize), setters & getters, read/write, and events.

Use readTagSingle/readTagContinous to grap the rfid EPC and RSSI.

The difference between readTagSingle/Continuous vs readTagMemory is the latter allows the selection of specific words to be grabbed from the rfid chip based on the given json arguments; however keep in mind both readTagMemory/writeTagMemory don't natively grab the rssi value from its event listener.

The args json object for readTagMemory/writeTagMemory is as follows (each name/value pair is optional, defaults will be used, offset and length are measured in 16 bit words ie. length =  2 : get the leftmost 32 bits):

```
start_readTagMemory
{
  'bankType' : 'EPC'
  'offset' : 2
  'length' : 2
  'password' :  ''
}

start_writeTagMemory
{
  'bankType' : 'EPC'
  'offset' : 2
  'password' : ''
  'data' : ''
}
```

