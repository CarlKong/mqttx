/*
 * Copyright 2002-2020 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.jun.mqttx.broker.handler;

import com.jun.mqttx.config.MqttxConfig;
import com.jun.mqttx.service.IPublishMessageService;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.mqtt.MqttMessage;
import io.netty.handler.codec.mqtt.MqttMessageType;
import io.netty.handler.codec.mqtt.MqttPubAckMessage;

/**
 * {@link MqttMessageType#PUBACK} 消息处理器
 *
 * @author Jun
 * @since 1.0.4
 */
@Handler(type = MqttMessageType.PUBACK)
public class PubAckHandler extends AbstractMqttSessionHandler {

    private final IPublishMessageService publishMessageService;

    public PubAckHandler(IPublishMessageService publishMessageService, MqttxConfig config) {
        super(config.getEnableTestMode(), config.getCluster().getEnable());
        this.publishMessageService = publishMessageService;
    }

    @Override
    public void process(ChannelHandlerContext ctx, MqttMessage msg) {
        MqttPubAckMessage mqttPubAckMessage = (MqttPubAckMessage) msg;
        int messageId = mqttPubAckMessage.variableHeader().messageId();
        if (isCleanSession(ctx)) {
            getSession(ctx).removePubMsg(messageId);
        } else {
            publishMessageService.remove(clientId(ctx), messageId);
        }
    }
}