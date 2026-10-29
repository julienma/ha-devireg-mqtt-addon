<!-- https://developers.home-assistant.io/docs/add-ons/presentation#keeping-a-changelog -->
## 0.5.1-patched
- Full Danfoss SecureDeviceGrid server list (8 servers instead of 4): thermostats registered on the
  servers missing from opensdg_java 1.0.0 were unreachable ("Connection refused by grid: 1")
- Dedicated ping thread for the grid keepalive (no longer shares the 5-thread "thingHandler" pool)
- A grid connection whose connect() failed is discarded instead of being reused in a broken state
- Exponential reconnection backoff with jitter (10 s to 5 min) instead of a fixed 10 s retry
- Unexpected exceptions while connecting now schedule a reconnect instead of silently stopping it
- Bridge source vendored in bridge/ (bob-tm/ha-devi-mqtt @ dc38368); no more git clone at build time

## 0.5
- Prebuild Images


## 0.4.2
- HA 2026 warnigns
- HVAC action idle / heating
- HVAC modes off / auto / heat

## 0.4.1
- Update temperature units to °C
  
## 0.4
- Java 21
- MQTT host updated

## 0.3 beta2
- Update kwh to kWh. It's required to manualy fix statistics for devi kwh sensors here http://homeassistant/developer-tools/statistics
  
## 0.3 beta
- Fixed Negative Power output for 3200 watt
- MQTT config moved to GUI
- experemental devi_config.json generation inside addon (not tested)

## 0.2.2
- Fixed Unavailable after HA Full Restart
  
## 0.2.1
- New availability sensors
- Added availability for climate entity
  
## 0.2.0

- new Climate entity logic, similar to Devi App
- auto-discovery-templates moved to addon config folder

## 0.1.0

- Initial release
