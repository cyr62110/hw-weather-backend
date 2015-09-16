package fr.cvlaminck.hwweather.core.managers;

import fr.cvlaminck.hwweather.core.exceptions.NoResultForWeatherRefreshOperationException;
import fr.cvlaminck.hwweather.core.model.WeatherData;
import fr.cvlaminck.hwweather.core.utils.DateUtils;
import fr.cvlaminck.hwweather.data.model.WeatherDataType;
import fr.cvlaminck.hwweather.data.model.city.CityEntity;
import fr.cvlaminck.hwweather.data.model.weather.CurrentWeatherEntity;
import fr.cvlaminck.hwweather.data.model.weather.DailyForecastEntity;
import fr.cvlaminck.hwweather.data.model.weather.HourlyForecastEntity;
import fr.cvlaminck.hwweather.data.repositories.CurrentWeatherRepository;
import fr.cvlaminck.hwweather.data.repositories.DailyForecastRepository;
import fr.cvlaminck.hwweather.data.repositories.HourlyForecastRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Collection;

@Component
public class WeatherManager {

    @Autowired
    private CurrentWeatherRepository currentWeatherRepository;

    @Autowired
    private HourlyForecastRepository hourlyForecastRepository;

    @Autowired
    private DailyForecastRepository dailyForecastRepository;

    @Autowired
    private WeatherRefreshQueuesManager weatherRefreshQueuesManager;

    public WeatherData getWeather(CityEntity city, Collection<WeatherDataType> requestedTypes) throws NoResultForWeatherRefreshOperationException {
        return getWeather(city, requestedTypes, true);
    }

    public WeatherData getWeather(CityEntity city, Collection<WeatherDataType> requestedTypes, boolean refreshIfNecessary) throws NoResultForWeatherRefreshOperationException {
        WeatherData data = new WeatherData();
        data.setCity(city);
        data.setTypes(requestedTypes);

        fillWeatherDataWithTypesUsingDatabase(data, requestedTypes);

        if (refreshIfNecessary) {
            Collection<WeatherDataType> typesToRefresh = data.getMissingOrInGracePeriodTypes();
            boolean waitForResult = !data.getMissingTypes().isEmpty();
            if (waitForResult) {
                weatherRefreshQueuesManager.postRefreshOperationForCityAndWaitForResult(city, typesToRefresh);
                //Once every type has been refreshed, we retrieve the data for those types from the database once again.
                fillWeatherDataWithTypesUsingDatabase(data, typesToRefresh);
            } else {
                weatherRefreshQueuesManager.postRefreshForCity(city, typesToRefresh);
            }
        }

        return data;
    }

    private void fillWeatherDataWithTypesUsingDatabase(WeatherData data, Collection<WeatherDataType> types) {
        for (WeatherDataType type : types) {
            switch (type) {
                case WEATHER:
                    fillWeatherDataWithCurrentUsingDatabase(data);
                    break;
                case HOURLY_FORECAST:
                    fillWeatherDataWithHourlyUsingDatabase(data);
                    break;
                case DAILY_FORECAST:
                    fillWeatherDataWithDailyUsingDatabase(data);
                    break;
            }
        }
    }

    private void fillWeatherDataWithCurrentUsingDatabase(WeatherData data) {
        CurrentWeatherEntity current = currentWeatherRepository.findByCityIdAndDay(data.getCity().getId(), DateUtils.today());
        data.setCurrent(current);
    }

    private void fillWeatherDataWithHourlyUsingDatabase(WeatherData data) {
        LocalDate startDate = DateUtils.today();
        LocalDate endDate = startDate.plusDays(1);
        Collection<HourlyForecastEntity> hourlyList = hourlyForecastRepository.findByCityIdAndDayBetween(data.getCity().getId(), startDate, endDate);
        data.setHourlyList(hourlyList);
    }

    private void fillWeatherDataWithDailyUsingDatabase(WeatherData data) {
        LocalDate startDate = DateUtils.today();
        LocalDate endDate = startDate.plusDays(7);
        Collection<DailyForecastEntity> dailyList = dailyForecastRepository.findByCityIdAndWeekBetween(data.getCity().getId(), startDate, endDate);
        data.setDailyList(dailyList);
    }
}
