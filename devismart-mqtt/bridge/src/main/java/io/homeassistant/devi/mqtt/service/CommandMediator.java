package io.homeassistant.devi.mqtt.service;

import java.util.HashMap;
import java.util.Map;
import io.homeassistant.binding.danfoss.internal.DeviRegHandler;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.openhab.core.thing.ChannelUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CommandMediator implements Mediator {
    private static final Logger logger = LoggerFactory.getLogger(CommandMediator.class);

    private Map<String, DeviRegHandler> deviRegHandlers = new HashMap<>();

    public void addDeviRegHandler(String handlerId, DeviRegHandler deviRegHandler) {
        deviRegHandlers.put(handlerId, deviRegHandler);
    }

    @Override
    public void notify(Object sender, InputCommand event) {
        if (sender instanceof MqttCallback) {
            handleMQTTMessage(event);
        } else if (sender instanceof DeviRegHandler) {
            // Handle events from DeviRegHandler if needed
        }
    }

    private void handleMQTTMessage(InputCommand inputCommand) {
        DeviRegHandler handler = deviRegHandlers.get(inputCommand.deviceId);
        if (handler != null) {
            if (!handler.isConnected()) {
                // The bridge cannot deliver it: SDGPeerConnector.Send() would discard it silently
                logger.warn("Command {}={} dropped: thermostat {} is not connected", inputCommand.sensorName,
                        inputCommand.payload, inputCommand.deviceId);
                return;
            }
            handler.handleCommand(inputCommand.sensorName, inputCommand.payload);
        }
    }
}

