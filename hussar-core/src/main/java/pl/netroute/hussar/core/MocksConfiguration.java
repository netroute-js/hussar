package pl.netroute.hussar.core;

import pl.netroute.hussar.core.api.Mock;

import java.util.List;

class MocksConfiguration {
    private final List<Mock> mocks;

    MocksConfiguration(List<Mock> mocks) {
        this.mocks = mocks;
    }

    public List<Mock> getMocks() {
        return mocks;
    }

}
