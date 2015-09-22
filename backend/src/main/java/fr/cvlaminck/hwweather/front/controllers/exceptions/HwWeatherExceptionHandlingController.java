package fr.cvlaminck.hwweather.front.controllers.exceptions;

import fr.cvlaminck.hwweather.client.reponses.ClientErrorResponse;
import fr.cvlaminck.hwweather.core.exceptions.HwWeatherCoreException;
import fr.cvlaminck.hwweather.core.exceptions.clients.HwWeatherCoreClientException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;

@ControllerAdvice
public class HwWeatherExceptionHandlingController {

    @ExceptionHandler(HwWeatherCoreException.class)
    public void handleCoreException(HwWeatherCoreException ex, HttpServletResponse httpResponse) {
        httpResponse.setStatus(500);
    }

    @ExceptionHandler(HwWeatherCoreClientException.class)
    @ResponseBody
    public ClientErrorResponse handleCoreClientException(HwWeatherCoreClientException ex, HttpServletResponse httpResponse) {
        httpResponse.setStatus(ex.getResponseCode());

        ClientErrorResponse response = new ClientErrorResponse();
        response.setStatusCode(ex.getResponseCode());
        response.setMessage(ex.getMessage());
        return response;
    }

}
