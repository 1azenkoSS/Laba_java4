package org.sto.utils;


import org.modelmapper.ModelMapper;

public class Mapper {
    private static final ModelMapper mapper = new ModelMapper();

    public static  <DTO, Entity> Entity toEntity(final DTO dto, Class<Entity> entityClass) {
        return mapper.map(dto, entityClass);
    }
    public static <DTO, Entity> DTO toDTO(final Entity entity, Class<DTO> dtoClass) {
        return mapper.map(entity, dtoClass);
    }

}
