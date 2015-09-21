package fr.cvlaminck.hwweather.client;

import fr.cvlaminck.builders.uri.Uri;

public class HwWeatherClient {

    private Uri baseUri;

    public Uri getBaseUri() {
        return baseUri;
    }

    /* package */ void setBaseUri(Uri baseUri) {
        this.baseUri = baseUri;
    }
}
