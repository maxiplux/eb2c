package app.quantun.eb2c.mapper;

import app.quantun.eb2c.model.contract.request.OrganizationRequestDTO;
import app.quantun.eb2c.model.contract.response.OrganizationResponseDTO;
import app.quantun.eb2c.model.entity.bussines.Organization;
import org.mapstruct.Mapper;
import org.springframework.stereotype.Component;

import java.util.List;

@Mapper(componentModel = "spring", implementationName = "OrganizationMapperImpl")
@Component
public interface OrganizationMapper {

    Organization toEntity(OrganizationRequestDTO requestDTO);

    OrganizationResponseDTO toOrganizationResponseDTO(Organization organization);

    List<OrganizationResponseDTO> toOrganizationResponseDTOList(List<Organization> organizations);
}
