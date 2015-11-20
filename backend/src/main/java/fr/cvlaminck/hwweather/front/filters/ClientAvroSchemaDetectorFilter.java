package fr.cvlaminck.hwweather.front.filters;

import fr.cvlaminck.hwweather.client.utils.HwWeatherHttpHeaders;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class ClientAvroSchemaDetectorFilter
        implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        if (!(request instanceof HttpServletRequest)) {
            chain.doFilter(request, response);
            return;
        }

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        // We copy the hash of the client avro schema to the response
        // So we can get it during serialization
        String schemaHash = httpRequest.getHeader(HwWeatherHttpHeaders.CLIENT_AVRO_SCHEMA_HASH);
        if (schemaHash != null) { //FIXME: Check that the schema hash is valid
            httpResponse.setHeader(HwWeatherHttpHeaders.CLIENT_AVRO_SCHEMA_HASH, schemaHash);
        }

        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {

    }
}
