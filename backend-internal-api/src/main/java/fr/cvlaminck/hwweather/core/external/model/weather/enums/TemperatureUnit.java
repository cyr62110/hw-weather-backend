package fr.cvlaminck.hwweather.core.external.model.weather.enums;

public enum TemperatureUnit {
    CELSIUS(1d, 0d),
    KELVIN(1d, 273.15),
    FAHRENHEIT(1 / 1.8d, -32d);

    private double conversionFactorToCelsius;
    private double zeroDegreeCelsiusOffset;

    private TemperatureUnit(double conversionFactorToCelsius, double zeroDegreeCelsiusOffset) {
        this.conversionFactorToCelsius = conversionFactorToCelsius;
        this.zeroDegreeCelsiusOffset = zeroDegreeCelsiusOffset;
    }

    public double convertToCelsius(double temperature) {
        return (temperature + zeroDegreeCelsiusOffset) * conversionFactorToCelsius;
    }
}
