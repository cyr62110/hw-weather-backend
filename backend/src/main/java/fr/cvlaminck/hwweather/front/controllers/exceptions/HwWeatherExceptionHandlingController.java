package fr.cvlaminck.hwweather.front.controllers.exceptions;

import fr.cvlaminck.hwweather.client.protocol.ClientErrorResponse;
import fr.cvlaminck.hwweather.core.exceptions.HwWeatherCoreException;
import fr.cvlaminck.hwweather.core.exceptions.clients.HwWeatherCoreClientException;
import fr.cvlaminck.hwweather.core.utils.DateUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import java.time.ZoneOffset;

@ControllerAdvice
public class HwWeatherExceptionHandlingController {

    @ExceptionHandler(HwWeatherCoreException.class)
    public void handleCoreException(HwWeatherCoreException ex, HttpServletResponse httpResponse) {
        ex.printStackTrace(); //FIXME Find a better way to log the error

        httpResponse.setStatus(500);
    }

    @ExceptionHandler(HwWeatherCoreClientException.class)
    @ResponseBody
    public ClientErrorResponse handleCoreClientException(HwWeatherCoreClientException ex, HttpServletResponse httpResponse) {
        httpResponse.setStatus(ex.getResponseCode());

        ClientErrorResponse.Builder responseBuilder = ClientErrorResponse.newBuilder();
        responseBuilder.setTimestamp(DateUtils.nowTimestamp());
        responseBuilder.setStatus(ex.getResponseCode());
        responseBuilder.setMessage(ex.getMessage());
        return responseBuilder.build();
    }

}
