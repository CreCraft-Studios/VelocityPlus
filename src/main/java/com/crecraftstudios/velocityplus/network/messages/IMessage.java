package com.crecraftstudios.velocityplus.network.messages;

import com.google.common.io.ByteArrayDataInput;

public interface IMessage {
    void onMessage(ByteArrayDataInput msg);
}