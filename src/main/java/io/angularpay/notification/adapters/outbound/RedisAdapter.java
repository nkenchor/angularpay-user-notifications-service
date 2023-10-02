package io.angularpay.notification.adapters.outbound;

import io.angularpay.notification.ports.outbound.OutboundMessagingPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class RedisAdapter implements OutboundMessagingPort {

    private final RedisHashClient redisHashClient;

    @Override
    public Map<String, String> getPlatformConfigurations(String hashName) {
        return this.redisHashClient.getPlatformConfigurations(hashName);
    }
}
