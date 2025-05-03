package app.quantun.eb2c.mapper;

import app.quantun.eb2c.model.contract.contract.request.OrderRequestDTO;
import app.quantun.eb2c.model.contract.contract.response.OrderResponseDTO;
import app.quantun.eb2c.model.entity.bussines.Order;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.stereotype.Component;

import java.util.List;

@Mapper(componentModel = "spring", implementationName = "OrderMapperImpl")
@Component
public interface OrderMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "orderNumber", ignore = true)
    @Mapping(target = "orderDate", ignore = true)
    @Mapping(target = "totalAmount", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "organization.id", source = "organizationId")
    @Mapping(target = "branch.id", source = "branchId")
    @Mapping(target = "orderItems", ignore = true)
    Order toEntity(OrderRequestDTO requestDTO);

    @Mapping(target = "organizationId", source = "organization.id")
    @Mapping(target = "organizationName", source = "organization.name")
    @Mapping(target = "branchId", source = "branch.id")
    @Mapping(target = "branchName", source = "branch.name")
    OrderResponseDTO toOrderResponseDTO(Order order);

    List<OrderResponseDTO> toOrderResponseDTOList(List<Order> orders);
}