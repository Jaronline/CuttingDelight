package dev.jaronline.cuttingdelight.common.provider;

import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public interface IClientCommonPacketListenerProvider {
    void send(CustomPacketPayload payload);
}
