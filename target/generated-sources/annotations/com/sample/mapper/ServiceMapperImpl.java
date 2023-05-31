package com.sample.mapper;

import com.sample.dto.ServiceDTO;
import com.sample.model.Service;
import javax.annotation.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2023-05-31T13:41:51+0300",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 20 (Oracle Corporation)"
)
@Component
public class ServiceMapperImpl implements ServiceMapper {

    @Override
    public ServiceDTO mapWithoutId(Service service) {
        if ( service == null ) {
            return null;
        }

        ServiceDTO serviceDTO = new ServiceDTO();

        if ( service.getSessionID() != null ) {
            serviceDTO.setSessionID( service.getSessionID() );
        }
        serviceDTO.setMessage( service.getMessage() );

        return serviceDTO;
    }

    @Override
    public Service mapGenerated(com.sample.generated.Service generated) {
        if ( generated == null ) {
            return null;
        }

        Service service = new Service();

        service.setSessionID( generated.getSessionID() );
        service.setMessage( generated.getMessage() );

        return service;
    }
}
