package fr.cvlaminck.hwweather.client;

import fr.cvlaminck.builders.Builder;
import fr.cvlaminck.builders.exception.MalformedUriException;
import fr.cvlaminck.builders.uri.Uri;

public class HwWeatherClientFactory
        implements Builder<HwWeatherClient> {

    private String baseUrl;

    public HwWeatherClientFactory baseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
        return this;
    }

    @Override
    public HwWeatherClient build() {
        HwWeatherClient client = new HwWeatherClient();

        try {
            client.setBaseUri(Uri.parse(baseUrl));
        } catch (MalformedUriException ex) {
            throw new IllegalArgumentException("baseUrl is not a valid url.", ex);
        }

        return client;
    }

}
