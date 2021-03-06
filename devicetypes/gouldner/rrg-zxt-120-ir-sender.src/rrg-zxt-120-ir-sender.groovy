/**
 *  ZXT-120 IR Sender Unit from Remotec
 *  tested on V1.6H version of the device
 *
 *  Author: Ronald Gouldner (based on b.dahlem@gmail.com version)
 *  Date: 2015-01-20
 *  Code: https://github.com/gouldner/ST-Devices/src/ZXT-120
 *
 * Copyright (C) 2013 Ronald Gouldner
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this
 * software and associated documentation files (the "Software"), to deal in the Software
 * without restriction, including without limitation the rights to use, copy, modify,
 * merge, publish, distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to the following
 * conditions: The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A
 * PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
 * HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF
 * CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE
 * OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

//***** Metadata */
//
// A description of the ZXT-120 IR Extender for HVAC and its options and commands for the SmartThings hub.

// Preferences pane
//
// options that the user can directly edit in the SmartThings app
preferences {
	input("remoteCode", "number",
		title: "Remote Code (000 for learned, don't forget to hit configure after changing)",
		description: "The number of the remote to emulate")
	input("tempOffset", "enum", title: "Temp correction offset?", options: ["-5","-4","-3","-2","-1","0","1","2","3","4","5"])
    input("shortName", "string", title: "Short Name for Home Page Temp Icon", description: "Short Name:")
}

metadata {
	definition (name: "RRG ZXT-120 IR Sender", namespace: "gouldner", author: "Ronald Gouldner") {
		// Device capabilities of the ZXT-120
		capability "Actuator"
		capability "Temperature Measurement"
		capability "Thermostat"
		capability "Configuration"
		capability "Polling"
		capability "Sensor"
		capability "Battery"
        capability "Switch"

		// Commands that this device-type exposes for controlling the ZXT-120 directly
		//command "switchMode"
		command "switchModeOff"
		command "switchModeHeat"
		command "switchModeCool"
		command "switchModeDry"
		command "switchModeAuto"
		command "switchFanMode"
		command "switchFanOscillate"
		command "setRemoteCode"
		
		//commands for thermostat interface
		command "cool"
		command "heat"
        command "dry"
		command "off"
		// how do these work....do they take arguments ?
		//command "setCoolingSetpoint"
		//command "setHeatingSetpoint"
		//command "setThermostatMode"  
		
		//command "adjustTemperature", ["NUMBER"]
		
		attribute "swingMode", "STRING"
		attribute "lastPoll", "STRING"
		attribute "currentConfigCode", "STRING"
		attribute "currentTempOffset", "STRING"
		attribute "temperatureName", "STRING"
		
		// Z-Wave description of the ZXT-120 device
		fingerprint deviceId: "0x0806"
		fingerprint inClusters: "0x20,0x27,0x31,0x40,0x43,0x44,0x70,0x72,0x80,0x86"
	}

	// simulator metadata - for testing in the simulator
	simulator {
		// Not sure if these are correct
		status "off"			: "command: 4003, payload: 00"
		status "heat"			: "command: 4003, payload: 01"
		status "cool"			: "command: 4003, payload: 02"
		status "auto"			: "command: 4003, payload: 03"
		status "emergencyHeat"	: "command: 4003, payload: 04"

		status "fanAuto"		: "command: 4403, payload: 00"
		status "fanOn"			: "command: 4403, payload: 01"
		status "fanCirculate"	: "command: 4403, payload: 06"

		status "heat 60"        : "command: 4303, payload: 01 01 3C"
		status "heat 68"        : "command: 4303, payload: 01 01 44"
		status "heat 72"        : "command: 4303, payload: 01 01 48"

		status "cool 72"        : "command: 4303, payload: 02 01 48"
		status "cool 76"        : "command: 4303, payload: 02 01 4C"
		status "cool 80"        : "command: 4303, payload: 02 01 50"

		status "temp 58"        : "command: 3105, payload: 01 22 02 44"
		status "temp 62"        : "command: 3105, payload: 01 22 02 6C"
		status "temp 70"        : "command: 3105, payload: 01 22 02 BC"
		status "temp 74"        : "command: 3105, payload: 01 22 02 E4"
		status "temp 78"        : "command: 3105, payload: 01 22 03 0C"
		status "temp 82"        : "command: 3105, payload: 01 22 03 34"

		// reply messages
		reply "2502": "command: 2503, payload: FF"
	}

	// SmartThings app user interface
	tiles {
		// The currently detected temperature.  Show this as a large tile, changing colors as an indiciation
		// of the temperature
		valueTile("temperature", "device.temperature") {
			state("temperature", label:'${currentValue}°',
				backgroundColors:[
					[value: 31, color: "#153591"],
					[value: 44, color: "#1e9cbb"],
					[value: 59, color: "#90d2a7"],
					[value: 74, color: "#44b621"],
					[value: 84, color: "#f1d801"],
					[value: 95, color: "#d04e00"],
					[value: 96, color: "#bc2323"]
				]
			)
		}
		// Battery Status tile
		valueTile("battery", "device.battery", inactiveLabel: false, decoration: "flat") {
			state "battery", label:'${currentValue}% battery', unit:""
		}
		// Power Off Mode tile
		standardTile("off", "device.thermostatMode", inactiveLabel: false) {
			state "off", action:"switchModeOff", backgroundColor:"#ff0000", icon: "st.thermostat.heating-cooling-off"
		}
		// Heat Mode tile
		standardTile("heat", "device.thermostatMode", inactiveLabel: false) {
			state "heat", action:"switchModeHeat", backgroundColor:"#ff0000", icon: "st.thermostat.heat"
		}
		// Cool Mode tile
		standardTile("cool", "device.thermostatMode", inactiveLabel: false) {
			state "cool", action:"switchModeCool", backgroundColor:"#0000ff", icon: "st.thermostat.cool"
		}
		// Dry Mode tile
		standardTile("dry", "device.thermostatMode", inactiveLabel: false) {
			state "dry", action:"switchModeDry", backgroundColor:"#ffffff", label: "Dry", icon: "st.Weather.weather12"
		}
		// Auto Mode tile
		standardTile("auto", "device.thermostatMode", inactiveLabel: false) {
			state "auto", action:"switchModeAuto", backgroundColor:"#b266b2", icon: "st.thermostat.auto"
		}
		// AutoChangeover Mode tile
		standardTile("autoChangeover", "device.thermostatMode", inactiveLabel: false) {
			state "autoChangeover", action:"switchModeAuto", backgroundColor:"#b266b2", icon: "st.thermostat.auto"
		}
		// EmergencyHeat Mode tile
		standardTile("emergencyHeat", "device.thermostatMode", inactiveLabel: false) {
			state "emergencyHeat", action:"switchModeAuto", backgroundColor:"#ff0000", icon: "st.thermostat.emergency-heat"
		}
		// Mode switch.  Indicate and allow the user to change between heating/cooling modes
		standardTile("thermostatMode", "device.thermostatMode", inactiveLabel: false, decoration: "flat", canChangeIcon: true, canChangeBackground: true) {
			state "off", action:"switchMode", icon:"st.thermostat.heating-cooling-off", label: ' '
			state "heat", action:"switchMode", icon:"st.thermostat.heat", label: ' '
			state "emergencyHeat", action:"switchMode", icon:"st.thermostat.emergency-heat", label: ' '
			state "cool", action:"switchMode", icon:"st.thermostat.cool", label: ' '
			state "auto", action:"switchMode", icon:"st.thermostat.auto", label: ' '
			state "dry", action:"switchMode", icon:"st.Weather.weather12", label: 'Dry'
			state "autoChangeover", icon:"st.thermostat.auto", label: ' '
		}
		// Fan mode switch.  Indicate and allow the user to change between fan speed settings
		standardTile("fanMode", "device.thermostatFanMode", inactiveLabel: false, decoration: "flat", canChangeIcon: true, canChangeBackground: true) {
			state "fanAuto", action:"switchFanMode", icon:"st.Appliances.appliances11", label: 'AUTO'
			state "fanLow", action:"switchFanMode", icon:"st.Appliances.appliances11", label: 'LOW'
			state "fanMedium", action:"switchFanMode", icon:"st.Appliances.appliances11", label: 'MED'
			state "fanHigh", action:"switchFanMode", icon:"st.Appliances.appliances11", label: 'HIGH'
		}
		// Swing mode switch.  Indicate and allow the user to change between fan oscillation settings
		standardTile("swingMode", "device.swingMode", inactiveLabel: false, decoration: "flat", canChangeIcon: true, canChangeBackground: true) {
			state "on", action:"switchFanOscillate", icon:"st.secondary.refresh-icon", label: 'Swing On'
			state "off", action:"switchFanOscillate", icon:"st.secondary.refresh-icon", label: 'Swing Off'
		}
		controlTile("heatSliderControl", "device.heatingSetpoint", "slider", height: 1, width: 2, inactiveLabel: false, range:"(67..84)") {
			state "setHeatingSetpoint", action:"thermostat.setHeatingSetpoint", backgroundColor: "#d04e00"
		}
		valueTile("heatingSetpoint", "device.heatingSetpoint", inactiveLabel: false, decoration: "flat") {
			state "heatingSetpoint", label:'${currentValue}° heat', unit:"F", backgroundColor:"#ffffff"
		}
		controlTile("coolSliderControl", "device.coolingSetpoint", "slider", height: 1, width: 2, inactiveLabel: false, range:"(67..84)") {
			state "setCoolingSetpoint", action:"thermostat.setCoolingSetpoint", backgroundColor: "#1e9cbb"
		}        
		valueTile("coolingSetpoint", "device.coolingSetpoint", inactiveLabel: false, decoration: "flat") {
			state "coolingSetpoint", label:'${currentValue}° cool', unit:"F", backgroundColor:"#ffffff"
		}
		// Last Poll Tile
		valueTile("lastPoll", "device.lastPoll", inactiveLabel: false, decoration: "flat") {
			state "lastPoll", label:'${currentValue}', unit:""
		}
		// Current Config Code
		valueTile("currentConfigCode", "device.currentConfigCode", inactiveLabel: false, decoration: "flat") {
			state "currentConfigCode", label:'Config# ${currentValue}', unit:""
		}      
		// Current Temp Offset
		valueTile("currentTempOffset", "device.currentTempOffset", inactiveLabel: false, decoration: "flat") {
			state "currentTempOffset", label:'Offset ${currentValue}', unit:""
		}
		// Extra Temperature Tile with Name for Home Screen
		valueTile("temperatureName", "device.temperatureName", inactiveLabel: false, decoration: "flat") {
			state "temperatureName", label:'${currentValue}', unit:""
			//,  icon: "st.quirky.spotter.quirky-spotter-temp"
			//, icon: "https://raw.githubusercontent.com/gouldner/ST-Projects/master/ST-Devices/src/ZXT-120/zxt-120-tile.png"
		}
		
		// Temperature control.  Allow the user to control the target temperature with up and down arrows
		// to allow for precise temperature setting.  Indicate the set temperature between the arrows
		/*
		standardTile("temperatureLower", "device.thermostatSetpoint", inactiveLabel: false, decoration: "flat", canChangeIcon: true, canChangeBackground: true) {
			state "lowerTemp", action:"lowerTemperature", backgroundColor:"#ffffff", icon: "st.thermostat.thermostat-down"
		}
		valueTile("temperatureSetpoint", "device.thermostatSetpoint", inactiveLabel: false, decoration: "flat", canChangeIcon: true, canChangeBackground: true) {
			state("thermostatSetpoint", label:'${currentValue}°'
			)
		}
		standardTile("temperatureRaise", "device.thermostatSetpoint", inactiveLabel: false, decoration: "flat", canChangeIcon: true, canChangeBackground: true) {
			state "raiseTemp", action:"raiseTemperature", backgroundColor: "#ffffff", icon: "st.thermostat.thermostat-up"
		}
		*/
		
		// Refresh command button.  Allow the user to request the device be polled and the UI be updated
		 // with the current settings/sensor data
		standardTile("refresh", "device.thermostatMode", inactiveLabel: false, decoration: "flat") {
			state "default", action:"polling.poll", icon:"st.secondary.refresh"
		}
		// Configure button.  Syncronize the device capabilities that the UI provides
		standardTile("configure", "device.configure", inactiveLabel: false, decoration: "flat") {
			state "configure", label:'', action:"configuration.configure", icon:"st.secondary.configure"
		}
		
		// Layout the controls on the SmartThings device UI.  The page is a 3x3 layout, tiles are layed out
		// starting in the upper left working right then down.
		//main "temperature"
		main (["temperature","temperatureName"]) 
		//details(["temperature", "battery", "temperatureRaise", "temperatureSetpoint", "mode", "fanMode", "temperatureLower", "swingMode", "refresh", "configure", "setRemoteCode"])
		//details(["temperature", "battery", "off", "cool", "dry", "heat", "autoChangeover", "auto", "emergencyHeat", "heatingSetpoint", "heatSliderControl", "coolingSetpoint", "coolSliderControl","mode", "fanMode", "swingMode", "refresh", "configure", "setRemoteCode"])
		details(["temperature", "battery", "off", "cool", "dry", "heat", "heatingSetpoint", "heatSliderControl", "coolingSetpoint", "coolSliderControl","thermostatMode", "fanMode", "swingMode", "temperatureName", "refresh", "configure", "lastPoll", "currentConfigCode", "currentTempOffset", "setRemoteCode"])
	}
}

//***** Enumerations */

// modes - Possible heating/cooling modes for the device
def modes() {
	["off", "auto", "heat", "emergencyHeat", "cool", "dry", "autoChangeover"]
}

// setpointModeMap - Link the possible modes the device can be in to the possible temperature setpoints.
def getSetpointModeMap() { [
	"heat": "heatingSetpoint",
	"cool": "coolingSetpoint",
	"dry": "dryingSetpoint",
	"autoChangeover": "autoChangeoverSetpoint"
]}



// setpointMap - Link the setpoint descriptions with ZWave id numbers
def getSetpointMap() { [
	"heatingSetpoint": physicalgraph.zwave.commands.thermostatsetpointv1.ThermostatSetpointSet.SETPOINT_TYPE_HEATING_1,
	"coolingSetpoint": physicalgraph.zwave.commands.thermostatsetpointv1.ThermostatSetpointSet.SETPOINT_TYPE_COOLING_1,
	"dryingSetpoint": physicalgraph.zwave.commands.thermostatsetpointv1.ThermostatSetpointSet.SETPOINT_TYPE_DRY_AIR,
	"autoChangeoverSetpoint": physicalgraph.zwave.commands.thermostatsetpointv1.ThermostatSetpointSet.SETPOINT_TYPE_AUTO_CHANGEOVER
]}



// modeMap - Link the heating/cooling modes with their ZWave id numbers
def getModeMap() { [
	"off": physicalgraph.zwave.commands.thermostatmodev1.ThermostatModeSet.MODE_OFF,
	"heat": physicalgraph.zwave.commands.thermostatmodev1.ThermostatModeSet.MODE_HEAT,
	"cool": physicalgraph.zwave.commands.thermostatmodev1.ThermostatModeSet.MODE_COOL,
	"auto": physicalgraph.zwave.commands.thermostatmodev1.ThermostatModeSet.MODE_AUTO,
	"emergencyHeat": physicalgraph.zwave.commands.thermostatmodev1.ThermostatModeSet.MODE_AUXILIARY_HEAT,
	"dry": physicalgraph.zwave.commands.thermostatmodev1.ThermostatModeSet.MODE_DRY_AIR,
	"autoChangeover": physicalgraph.zwave.commands.thermostatmodev1.ThermostatModeSet.MODE_AUTO_CHANGEOVER
]}

def fanModes() {
	["fanAuto", "fanLow", "fanMedium", "fanHigh"]
}

// fanModeMap - Link the possible fan speeds with their ZWave id numbers
def getFanModeMap() { [
	"fanAuto": physicalgraph.zwave.commands.thermostatfanmodev2.ThermostatFanModeReport.FAN_MODE_AUTO_LOW,
	"fanLow": physicalgraph.zwave.commands.thermostatfanmodev2.ThermostatFanModeReport.FAN_MODE_LOW,
	"fanMedium": physicalgraph.zwave.commands.thermostatfanmodev2.ThermostatFanModeReport.FAN_MODE_MEDIUM,
	"fanHigh": physicalgraph.zwave.commands.thermostatfanmodev2.ThermostatFanModeReport.FAN_MODE_HIGH
]}

// Command parameters
def getCommandParameters() { [
	"remoteCode": 27,
	"tempOffsetParam": 37,
	"oscillateSetting": 33
]}


//***** Commands */

// parse
/*
def parse(String description)
{
	def result = []
	def cmd = zwave.parse(description, [0x70:1, 0x42:1, 0x43:2, 0x31: 3])
	log.debug "Parsing cmd=$cmd"
	if (cmd) {
		if( cmd.CMD == "4303" ) { 
			def map = createEvent(zwaveEvent(cmd))
			
			
		    result << new physicalgraph.device.HubAction(zwave.wakeUpV1.wakeUpNoMoreInformation().format()) 
		}
		result << createEvent(zwaveEvent(cmd))
	}
	log.debug "Parse returned ${result}"
	return result
}
*/
// parse - Handle events coming from the user and the device

def parse(String description)
{
	// If the device sent an update, interpret it
	log.info "Parsing Description=$description"
	def map = createEvent(zwaveEvent(zwave.parse(description, [0x70:1, 0x42:1, 0x43:2, 0x31: 3])))
	// if the update wasn't from the device, quit
	if (!map) {
		log.warn "parse called generating null map....why is this possible ? description=$description"
		return null
	}
		
	log.debug "Parse map=$map"
	
	def result = [map]
	
/*	
	// If the update was a change in the device's settings (its operating mode or thermostat setting)
	def stateChanges = setpointMap.keySet() + "thermostatMode"
	if (map.isStateChange && map.name in stateChanges) {
		log.warn "Entered Code I plan to refactor....."
		// Interpret the device's setting report
		def map2 = [
			name: "thermostatSetpoint",
			unit: getTemperatureScale()
		]
		
		// if the device reported a mode change
		if (map.name == "thermostatMode") {
			log.warn "Entered thermostatMode section of code to be refactored"
			// store the new mode
			updateState("lastTriedMode", map.value)
			
			// Determine what kind of erature setpoint this mode uses, defaulting to cooling, cuz A/C
			def setpointModeName = setpointModeMap[map.value] ?: "coolingSetpoint"
  
			  // Load last setpoint from when the thermostat was in this mode
			map2.value = device.latestValue(setpointModeName)
			log.info "THERMOSTAT, latest " + setpointModeName + " = ${map2.value}"
		}
		// If this was a temperature setting change (IE new value form up/down i think)
		else {
			log.warn "Entered temperature change part of code to be refactored"
			// Determine the current cooling/heating mode and save the new temperature
			def mode = device.latestValue("thermostatMode")
			if (map.name == setpointModeMap[mode]) {
				map2.value = map.value
				map2.unit = map.unit
			}
			log.info "THERMOSTAT, latest mode = ${mode}, new temp: ${map.value}"
		}
		
		// If there was a new temperature, report it to the UI
		if (map2.value != null) {
			log.warn "More bad code to be refactored"
			log.debug "THERMOSTAT, adding setpoint event: $map"
			result << createEvent(map2)
		}
*/
	// If the update was a change in the device's fan speed
	if (map.name == "thermostatFanMode" && map.isStateChange) {
		// store the new fan speed
		updateState("lastTriedFanMode", map.value)
	}

	log.debug "Parse returned $result"
	result
}

//***** Event Handlers */
//Handle events coming from the device

// Battery Level event
def zwaveEvent(physicalgraph.zwave.commands.batteryv1.BatteryReport cmd) {
	def map = [:]
	map.name = "battery"
	map.value = cmd.batteryLevel > 0 ? cmd.batteryLevel.toString() : 1
	map.unit = "%"
	map.displayed = false
	map
}

// - Thermostat Setpoint Report
// The device is telling us what temperatures it is set to for a particular mode
def zwaveEvent(physicalgraph.zwave.commands.thermostatsetpointv2.ThermostatSetpointReport cmd)
{
	/*  Ignore this because we don't currently report on screen what the unit things the setting is
	 * since we only set the value when the mode is selected.  Otherwise we just hold it in the device
	 * attributes.  This is because we don't want to send new values as the settings are made or it will
	 * turn the AC/HEAT unit on
	 */
	
	/*
    log.debug "*************Entered thermostat set point cmd=$cmd"
	// Determine the temperature and mode the device is reporting
	def cmdScale = cmd.scale == 1 ? "F" : "C"
	def map = [:]
	map.value = convertTemperatureIfNeeded(cmd.scaledValue, cmdScale, cmd.precision)
	map.unit = getTemperatureScale()
	map.displayed = false
	
	// Determine what mode the setpoint is for, if the mode is not valid, bail out
	def name = setpointMap.find {it.value == cmd.setpointType}?.key
	if (name == null) {
		return [:]
	}
	map.name = name

	// So we can respond with same format
	state.size = cmd.size
	state.scale = cmd.scale
	state.precision = cmd.precision
	
	// Return the interpretation of the report
	map
	*/
}

// - Sensor Multilevel Report
// The device is reporting temperature readings
def zwaveEvent(physicalgraph.zwave.commands.sensormultilevelv3.SensorMultilevelReport cmd)
{
	log.debug "Temp reporting...cmd=$cmd"
	// Determine the temperature the device is reporting
	def map = [:]
	switch (cmd.sensorType) {
		case 1:
			// temperature
			def cmdScale = cmd.scale == 1 ? "F" : "C"
			// converTemp returns string with two decimal places
			// convert to double then to int to drop the decimal
			Integer temp = (int) convertTemperatureIfNeeded(cmd.scaledSensorValue, cmdScale).toDouble()
			log.debug "temp=${temp}"
			map.value = temp
			map.unit = getTemperatureScale()
			map.name = "temperature"
			// Send event to set ShortName + Temp tile
			def shortNameVal = shortName == null ? "ZXT-120" : shortName
		    def tempName = shortNameVal + " " + map.value + "°"
			log.warn "Setting temperatureName $tempName" 
			sendEvent("name":"temperatureName", "value":tempName)
			break;
		default:
			log.warn "Unknown sensorType reading from device"
			break;
	}
	map
}

// - Thermostat Mode Report
// The device is reporting its heating/cooling Mode
def zwaveEvent(physicalgraph.zwave.commands.thermostatmodev2.ThermostatModeReport cmd) {
	def map = [:]
	
	// Determine the mode the device is reporting, based on its ZWave id
	map.value = modeMap.find {it.value == cmd.mode}?.key
	map.name = "thermostatMode"
	log.debug "Thermostat Mode reported : $map.value"
	// Return the interpreted report
	map
}

// - Thermostat Fan Mode Report
// The device is reporting its current fan speed
def zwaveEvent(physicalgraph.zwave.commands.thermostatfanmodev3.ThermostatFanModeReport cmd) {
	def map = [:]
	
	// Determine the fan speed the device is reporting, based on its ZWave id
	map.value = fanModeMap.find {it.value == cmd.fanMode}?.key
	map.name = "thermostatFanMode"
	map.displayed = false
	
	// Return the interpreted report
	map
}

def zwaveEvent(physicalgraph.zwave.commands.configurationv1.ConfigurationReport cmd) {
	log.debug "Config Report cmd=$cmd"
	def map = [:]
	
	switch (cmd.parameterNumber) {
		// If the device is reporting its remote code
		case commandParameters["remoteCode"]:
			map.name = "remoteCode"
			map.displayed = false
			
			def short remoteCodeLow = cmd.configurationValue[1]
			def short remoteCodeHigh = cmd.configurationValue[0]
			map.value = (remoteCodeHigh << 8) + remoteCodeLow
			
		    // Display configed code in tile
			log.debug "currentConfigCode=$map.value"
			sendEvent("name":"currentConfigCode", "value":map.value)
			
			break

		// If the device is reporting its remote code
		case commandParameters["tempOffsetParam"]:
			map.name = "tempOffset"
			map.displayed = false
			
			def short offset = cmd.configurationValue[0]
			if (offset >= 0xFB) {
				// Hex FB-FF represent negative offsets FF=-1 - FB=-5
				offset = offset - 256
			}
			map.value = offset
			log.debug "offset=$map.value"
			// Display temp offset in tile
			sendEvent("name":"currentTempOffset", "value":map.value)
			
			break
		// If the device is reporting its oscillate mode
		case commandParameters["oscillateSetting"]:
			// determine if the device is oscillating
			def oscillateMode = (cmd.configurationValue[0] == 0) ? "off" : "on"

			//log.debug "Updated: Oscillate " + oscillateMode
			map.name = "swingMode"
			map.value = oscillateMode
			map.displayed = false

			map.isStateChange = oscillateMode != getDataByName("swingMode")

			// Store and report the oscillate mode
			updateState("swingMode", oscillateMode)

			break
	}
	
	map
}

// - Thermostat Supported Modes Report
// The device is reporting heating/cooling modes it supports
def zwaveEvent(physicalgraph.zwave.commands.thermostatmodev2.ThermostatModeSupportedReport cmd) {
	// Create a string with mode names for each available mode
	def supportedModes = ""
	if(cmd.off) { supportedModes += "off " }
	if(cmd.heat) { supportedModes += "heat " }
	if(cmd.auxiliaryemergencyHeat) { supportedModes += "emergencyHeat " }
	if(cmd.cool) { supportedModes += "cool " }
	if(cmd.auto) { supportedModes += "auto " }
	if(cmd.dryAir) { supportedModes += "dry " }
	if(cmd.autoChangeover) { supportedModes += "autoChangeover " }
	
	// Report and save available modes
	log.debug "Supported Modes: ${supportedModes}"
	updateState("supportedModes", supportedModes)
}

// - Thermostat Fan Supported Modes Report
// The device is reporting fan speeds it supports
def zwaveEvent(physicalgraph.zwave.commands.thermostatfanmodev3.ThermostatFanModeSupportedReport cmd) {
	// Create a string with mode names for each available mode
	def supportedFanModes = ""
	if(cmd.auto) { supportedFanModes += "fanAuto " }
	if(cmd.low) { supportedFanModes += "fanLow " }
	if(cmd.medium) { supportedFanModes += "fanMedium " }
	if(cmd.high) { supportedFanModes += "fanHigh " }
	
	// Report and save available speeds
	log.debug "Supported Fan Modes: ${supportedFanModes}"
	updateState("supportedFanModes", supportedFanModes)
}

// - Basic Report
// The device is sending standard ZWave updates
def zwaveEvent(physicalgraph.zwave.commands.basicv1.BasicReport cmd) {
	log.debug "Zwave event received: $cmd"
}

// - Command Report
// The device is reporting parameter settings
def zwaveEvent(physicalgraph.zwave.Command cmd) {
	// simply report it
	log.warn "Unexpected zwave command $cmd"
}

// Update State
// Store mode and settings
def updateState(String name, String value) {
	state[name] = value
	device.updateDataValue(name, value)
}

// Command Implementations
// Ask the device for its current state
def poll() {
	def now=new Date()
	def tz = location.timeZone
	def nowString = now.format("MMM/dd HH:mm",tz)
	
	sendEvent("name":"lastPoll", "value":nowString)
	
	log.debug "Polling now $nowString"
	// create a list of requests to send
	def commands = []
	
	commands <<	zwave.sensorMultilevelV3.sensorMultilevelGet().format()		// current temperature
	commands <<	zwave.batteryV1.batteryGet().format()                       // current battery level
	commands <<	zwave.thermostatModeV2.thermostatModeGet().format()     	// thermostat mode
	commands <<	zwave.thermostatFanModeV3.thermostatFanModeGet().format()	// fan speed
	commands <<	zwave.configurationV1.configurationGet(parameterNumber: commandParameters["remoteCode"]).format()		// remote code
	commands <<	zwave.configurationV1.configurationGet(parameterNumber: commandParameters["tempOffsetParam"]).format()  // temp offset
	commands <<	zwave.configurationV1.configurationGet(parameterNumber: commandParameters["oscillateSetting"]).format()	// oscillate setting
	
	// add requests for each thermostat setpoint available on the device
	//for (setpoint in setpointModeMap) {
	//	def supportedModes = getDataByName("supportedModes")
	//	if (supportedModes.tokenize()?.contains(setpoint.key)) {
	//		commands << [zwave.thermostatSetpointV1.thermostatSetpointGet(setpointType: setpointMap[setpoint.value]).format()]
	//	}
	//}
	
	log.debug "Polling sending commands"
	// send the requests
	delayBetween(commands, 2300)
}

def setHeatingSetpoint(degrees) {
	def degreesInteger = degrees as Integer
	def temperatureScale = getTemperatureScale()
	
	if (temperatureScale == "C") {
		// ZXT-120 lowest settings is 19 C
		if (degreesInteger < 19) {
			degreesInteger = 19;
		}
		// ZXT-120 highest setting is 28 C
		if (degreesInteger > 28) {
			degreesInteger = 28;
		}
	} else {
		// ZXT-120 lowest settings is 67 F
		if (degreesInteger < 67) {
			degreesInteger = 67;
		}
		// ZXT-120 highest setting is 84
		if (degreesInteger > 84) {
			degreesInteger = 84;
		}
	}
	log.debug "setHeatingSetpoint({$degreesInteger} ${temperatureScale})"
	sendEvent("name":"heatingSetpoint", "value":degreesInteger)
	//def celsius = (temperatureScale == "C") ? degreesInteger : (fahrenheitToCelsius(degreesInteger) as Double).round(2)
	//"st wattr 0x${device.deviceNetworkId} 1 0x201 0x12 0x29 {" + hex(celsius*100) + "}"
	//def setpointMode = physicalgraph.zwave.commands.thermostatsetpointv1.ThermostatSetpointSet.SETPOINT_TYPE_HEATING_1
	//setThermostatSetpointForMode(degreesInteger.toDouble(), setpointMode)
}

def setCoolingSetpoint(degrees) {
	def degreesInteger = degrees as Integer
	def temperatureScale = getTemperatureScale()
	
	if (temperatureScale == "C") {
		// ZXT-120 lowest settings is 19 C
		if (degreesInteger < 19) {
			degreesInteger = 19;
		}
		// ZXT-120 highest setting is 28 C
		if (degreesInteger > 28) {
			degreesInteger = 28;
		}
	} else {
		// ZXT-120 lowest settings is 67 F
		if (degreesInteger < 67) {
			degreesInteger = 67;
		}
		// ZXT-120 highest setting is 28
		if (degreesInteger > 84) {
			degreesInteger = 84;
		}
	}
	log.debug "setCoolingSetpoint({$degreesInteger} ${temperatureScale})"
	sendEvent("name":"coolingSetpoint", "value":degreesInteger)
	//def celsius = (temperatureScale == "C") ? degreesInteger : (fahrenheitToCelsius(degreesInteger) as Double).round(2)
	//"st wattr 0x${device.deviceNetworkId} 1 0x201 0x11 0x29 {" + hex(celsius*100) + "}"
	//def setpointMode = physicalgraph.zwave.commands.thermostatsetpointv1.ThermostatSetpointSet.SETPOINT_TYPE_COOLING_1
	//setThermostatSetpointForMode(degreesInteger.toDouble(), setpointMode)
}

/*
def setThermostatSetpointForMode(Double degrees, setpointMode) {
	// Convert the temperature from the UserInterface's temperature scale to the device's scale
	def deviceScale = state.scale ?: 1
	def deviceScaleString = deviceScale == 2 ? "C" : "F"
	def locationScale = getTemperatureScale()
	def p = (state.precision == null) ? 1 : state.precision
	
	def convertedDegrees
	if (locationScale == "C" && deviceScaleString == "F") {
		convertedDegrees = celsiusToFahrenheit(degrees)
	} else if (locationScale == "F" && deviceScaleString == "C") {
		convertedDegrees = fahrenheitToCelsius(degrees)
	} else {
		convertedDegrees = degrees
	}
	
	// Report the new temperature being set
	log.debug "new temp ${degrees}"
	
	// Send the new temperature from the thermostat and request confirmation
	//delayBetween([
	//	zwave.thermostatSetpointV1.thermostatSetpointSet(setpointType: setpointMode, scale: deviceScale, precision: p, scaledValue: convertedDegrees).format(),
	//	zwave.thermostatSetpointV1.thermostatSetpointGet(setpointType: setpointMode).format()
	//])
}
*/

def sendThermostatSetpointForMode(Double degrees, setpointMode) {
	// Convert the temperature from the UserInterface's temperature scale to the device's scale
	def deviceScale = state.scale ?: 1
	def deviceScaleString = deviceScale == 2 ? "C" : "F"
	def locationScale = getTemperatureScale()
	def p = (state.precision == null) ? 1 : state.precision
	
	def convertedDegrees
	if (locationScale == "C" && deviceScaleString == "F") {
		convertedDegrees = celsiusToFahrenheit(degrees)
	} else if (locationScale == "F" && deviceScaleString == "C") {
		convertedDegrees = fahrenheitToCelsius(degrees)
	} else {
		convertedDegrees = degrees
	}
	
	// Report the new temperature being set
	log.debug "new temp ${degrees}"
	
	// Send the new temperature from the thermostat and request confirmation
	delayBetween([
		zwave.thermostatSetpointV1.thermostatSetpointSet(setpointType: setpointMode, scale: deviceScale, precision: p, scaledValue: convertedDegrees).format(),
		zwave.thermostatSetpointV1.thermostatSetpointGet(setpointType: setpointMode).format()
	])
}

//***** Set the thermostat */
def setThermostatSetpoint(degrees) {
	log.debug "setThermostatSetpoint called.....want to get rid of that"
	// convert the temperature to a number and execute
	setThermostatSetpoint(degrees.toDouble())
}

/*
def setThermostatSetpoint(Double degrees, setpointMode = null) {
	log.debug "setThermostatSetpoint 2arg called.....want to get rid of that"

	// Convert the temperature from the UserInterface's temperature scale to the device's scale
	def deviceScale = state.scale ?: 1
	def deviceScaleString = deviceScale == 2 ? "C" : "F"
	def locationScale = getTemperatureScale()
	def p = (state.precision == null) ? 1 : state.precision
	
	def convertedDegrees
	if (locationScale == "C" && deviceScaleString == "F") {
		convertedDegrees = celsiusToFahrenheit(degrees)
	} else if (locationScale == "F" && deviceScaleString == "C") {
		convertedDegrees = fahrenheitToCelsius(degrees)
	} else {
		convertedDegrees = degrees
	}
	
	// If a cooling/heating mode setpoint wasn't specified, use the current mode
	if (setpointMode == null) {
		def mode = device.currentState("thermostatMode")?.value ?: null
		setpointMode = setpointMap[setpointModeMap[mode]] ?: 0
	}
	
	// Report the new temperature being set
	log.debug "new temp ${degrees}"
	
	// Send the new temperature from the thermostat and request confirmation
	delayBetween([
		zwave.thermostatSetpointV1.thermostatSetpointSet(setpointType: setpointMode, scale: deviceScale, precision: p, scaledValue: convertedDegrees).format(),
		zwave.thermostatSetpointV1.thermostatSetpointGet(setpointType: setpointMode).format()
	])
}
*/

// Set temperature for the heating mode
//def setHeatingSetpoint(degrees) {
//	setThermostatSetpoint(degrees.toDouble(), physicalgraph.zwave.commands.thermostatsetpointv1.ThermostatSetpointSet.SETPOINT_TYPE_HEATING_1)
//}

// Set temperature for the cooling mode
//def setCoolingSetpoint(degrees) {
//	setThermostatSetpoint(degrees.toDouble(), physicalgraph.zwave.commands.thermostatsetpointv1.ThermostatSetpointSet.SETPOINT_TYPE_COOLING_1)
//}

// Configure
// Syncronize the device capabilities with those that the UI provides
def configure() {
	delayBetween([
		// update the device's remote code to ensure it provides proper mode info
		setRemoteCode(),
		setTempOffset(),
		// Request the device's current heating/cooling mode
		zwave.thermostatModeV2.thermostatModeSupportedGet().format(),
		// Request the device's current fan speed
		zwave.thermostatFanModeV3.thermostatFanModeSupportedGet().format(),
		// Assign the device to ZWave group 1
		zwave.associationV1.associationSet(groupingIdentifier:1, nodeId:[zwaveHubNodeId]).format()
	], 2300)
}

//***** Change mode */
// Change the thermostat's heating/cooling mode

// Switch Mode
// Change to the next available mode
/*
def switchMode() {

	// Determine the thermostat's current mode of operation
	def currentMode = device.currentState("thermostatMode")?.value
	def lastTriedMode = getDataByName("lastTriedMode") ?: currentMode ?: "off"
	
	// Determine what modes the device supports
	def supportedModes = getDataByName("supportedModes")
	def modeOrder = modes()
	
	// Determine the next mode to use, based on the current mode
	def next = { modeOrder[modeOrder.indexOf(it) + 1] ?: modeOrder[0] }
	def nextMode = next(lastTriedMode)
	
	if (supportedModes?.tokenize()?.contains(currentMode)) {
		while (!supportedModes.tokenize()?.contains(nextMode) && nextMode != "off") {
			nextMode = next(nextMode)
		}
	}
	
	// Make it so
	switchToMode(nextMode)
}
*/

// Switch To Mode
// Given the name of a mode, change to that mode if possible
/*
def switchToMode(nextMode) {
	// Determine the available modes
	def supportedModes = getDataByName("supportedModes")
	
	// If the thermostat can't be set to this mode, cry about it
	if(supportedModes && !supportedModes.tokenize()?.contains(nextMode)) {
		log.warn "thermostat mode '$nextMode' is not supported"
	}
	
	// If the mode is even possible
	if (nextMode in modes()) {
		// Try to switch to the mode
		updateState("lastTriedMode", nextMode)
		return "$nextMode"()  // Call the function perform the mode switch
	} else {
		// Otherwise, bail
		log.debug("no mode method '$nextMode'")
	}
}
*/

// Switch Fan Mode
// Switch to the next available fan speed
def switchFanMode() {
	// Determine the current fan speed setting
	def currentMode = device.currentState("thermostatFanMode")?.value
	def lastTriedMode = getDataByName("lastTriedFanMode") ?: currentMode.value ?: "off"
	
	// Determine what fan speeds are available
	def supportedModes = getDataByName("supportedFanModes") ?: "fanAuto fanLow"
	def modeOrder = fanModes()
	//log.info modeOrder
	
	// Determine what the next fan speed should be
	def next = { modeOrder[modeOrder.indexOf(it) + 1] ?: modeOrder[0] }
	def nextMode = next(lastTriedMode)
	while (!supportedModes?.contains(nextMode) && nextMode != "fanAuto") {
		nextMode = next(nextMode)
	}
	
	// Make it so
	switchToFanMode(nextMode)
}

// Switch to Fan Mode
// Given the name of a fan mode, make it happen
def switchToFanMode(nextMode) {
	def supportedFanModes = getDataByName("supportedFanModes")
	if(supportedFanModes && !supportedFanModes.tokenize()?.contains(nextMode)) log.warn "thermostat mode '$nextMode' is not supported"

	// If the mode is even possible
	if (nextMode in fanModes()) {
		// Try to switch to the mode
		updateState("lastTriedFanMode", nextMode)
		return "$nextMode"()  // Call the function perform the mode switch
	} else {
		// Otherwise, bail
		log.debug("no fan mode method '$nextMode'")
	}
}

// Get Data By Name
// Given the name of a setting/attribute, lookup the setting's value
def getDataByName(String name) {
	state[name] ?: device.getDataValue(name)
}

// Set Thermostat Mode
// Set the device to the named mode
def setThermostatMode(String value) {
	if (value == "cool") {
		def tempValue = device.currentValue("coolingSetpoint")
		log.debug("Sending Temp [$tempValue] for cool mode before enabling mode")
		def setpointMode = physicalgraph.zwave.commands.thermostatsetpointv1.ThermostatSetpointSet.SETPOINT_TYPE_COOLING_1
		sendThermostatSetpointForMode(tempValue,setpointMode)
	}
	if (value == "heat") {
		def tempValue = device.currentValue("heatingSetpoint")
		log.debug("Sending Temp [$tempValue] for heat mode before enabling mode")
		def setpointMode = physicalgraph.zwave.commands.thermostatsetpointv1.ThermostatSetpointSet.SETPOINT_TYPE_HEATING_1
		sendThermostatSetpointForMode(tempValue,setpointMode)
	}
	delayBetween([
		// Command the device to change to the given mode
		zwave.thermostatModeV2.thermostatModeSet(mode: modeMap[value]).format(),
		// Request an update to make sure it worked
		zwave.thermostatModeV2.thermostatModeGet().format()
	])
}


// Set Thermostat Fan Mode
// Set the device to the named fan speed
def setThermostatFanMode(String value) {

	log.debug value + " ${fanModeMap[value]}"
	delayBetween([
		// Command the device to change the fan speed
		zwave.thermostatFanModeV2.thermostatFanModeSet(fanMode: fanModeMap[value]).format(),
		// Request an update to make sure it worked
		zwave.thermostatFanModeV2.thermostatFanModeGet().format()
	])
}

// Mode Commands 
// provide simple access to mode changes

// public interface commands for Thermostat
def cool() {
	switchModeCool()
}

def heat() {
	switchModeHeat()
}

def dry() {
    switchModeDry()
}

def off() {
    log.debug "${device.name} received off request"
	switchModeOff()
}

def on() {
    log.debug "${device.name} received on request"
    // Added "Switch Attribute on/off for Harmony Remote
    // TODO: RRG add preference for on turns on heat or AC hard code to ac for now
    switchModeCool()
}

// switchModeCommands
def switchModeOff() {
	setThermostatMode("off")
}

def switchModeHeat() {
	setThermostatMode("heat")
}

def emergencyHeat() {
	setThermostatMode("emergencyHeat")
}

def switchModeDry() {
	setThermostatMode("dry")
}

def switchModeCool() {
	setThermostatMode("cool")
}

def switchModeAuto() {
	setThermostatMode("auto")
}

def autoChangeover() {
	setThermostatMode("autoChangeover")
}

def fanLow() {
	setThermostatFanMode("fanLow")
}

def fanMedium() {
	setThermostatFanMode("fanMedium")
}

def fanHigh() {
	setThermostatFanMode("fanHigh")
}

def fanAuto() {
	setThermostatFanMode("fanAuto")
}

// Set Remote Code
// tell the ZXT-120 what remote code to use when communicating with the A/C
def setRemoteCode() {
	// Load the user's remote code setting
	def remoteCodeVal = remoteCode.toInteger()
	
	// Divide the remote code into a 2 byte value
	def short remoteCodeLow = remoteCodeVal & 0xFF
	def short remoteCodeHigh = (remoteCodeVal >> 8) & 0xFF
	def remoteBytes = [remoteCodeHigh, remoteCodeLow]
	
	log.debug "New Remote Code: ${remoteBytes}"
	
	delayBetween ([
		// Send the new remote code
		zwave.configurationV1.configurationSet(configurationValue: remoteBytes,
				parameterNumber: commandParameters["remoteCode"], size: 2).format(),
		// Request the device's remote code to make sure the new setting worked
		zwave.configurationV1.configurationGet(parameterNumber: commandParameters["remoteCode"]).format()
	])
}

def setTempOffset() {
	// Load the user's remote code setting
	def tempOffsetVal = tempOffset == null ? 0 : tempOffset.toInteger()
	// Convert negative values into hex value for this param -1 = 0xFF -5 = 0xFB
	if (tempOffsetVal < 0) {
		tempOffsetVal = 256 + tempOffsetVal
	}
	
	def configArray = [tempOffsetVal]
	
	log.debug "TempOffset: ${tempOffsetVal}"
	
	delayBetween ([
		// Send the new remote code
		zwave.configurationV1.configurationSet(configurationValue: configArray,
				parameterNumber: commandParameters["tempOffsetParam"], size: 1).format(),
		// Request the device's remote code to make sure the new setting worked
		zwave.configurationV1.configurationGet(parameterNumber: commandParameters["tempOffsetParam"]).format()
	])
}

// Switch Fan Oscillate
// Toggle fan oscillation on and off
def switchFanOscillate() {
	// Load the current swingmode and invert it (Off becomes true, On becomes false)
	def swingMode = (getDataByName("swingMode") == "off")
	
	// Make the new swingMode happen
	setFanOscillate(swingMode)
}

// Set Fan Oscillate
// Set the fan oscillation to On (swingMode == true) or Off (swingMode == false)
def setFanOscillate(swingMode) {
	// Convert the swing mode requested to 1 for on, 0 for off
	def swingValue = swingMode ? 1 : 0
	
	delayBetween ([
		// Command the new Swing Mode
		zwave.configurationV1.configurationSet(configurationValue: [swingValue],
			parameterNumber: commandParameters["oscillateSetting"], size: 1).format(),
		// Request the device's swing mode to make sure the new setting was accepted
		zwave.configurationV1.configurationGet(parameterNumber: commandParameters["oscillateSetting"]).format()
	])
}