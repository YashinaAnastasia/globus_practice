package com.sample.mapper;

import com.sample.dto.ServiceDTO;
import com.sample.model.Service;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.stereotype.Component;

import javax.annotation.Generated;

@Mapper(componentModel = "spring")
public interface ServiceMapper {
    @Mapping(target = "sessionID", source = "sessionID")
    @Mapping(target = "message", source = "message")
    ServiceDTO mapWithoutId(Service service);

    @Mapping(target = "sessionID", source = "sessionID")
    @Mapping(target = "message", source = "message")
    Service mapGenerated(com.sample.generated.Service generated);

}