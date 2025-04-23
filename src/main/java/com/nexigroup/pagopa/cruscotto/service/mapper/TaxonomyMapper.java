package com.nexigroup.pagopa.cruscotto.service.mapper;

import com.nexigroup.pagopa.cruscotto.domain.Taxonomy;
import com.nexigroup.pagopa.cruscotto.service.dto.TaxonomyDTO;
import org.mapstruct.Mapper;

/**
 * Mapper for the entity {@link Taxonomy} and its DTO {@link TaxonomyDTO}.
 */
@Mapper(componentModel = "spring", uses = { })
public interface TaxonomyMapper extends EntityMapper<TaxonomyDTO, Taxonomy> {

    Taxonomy toEntity(TaxonomyDTO taxonomyDTO);

    TaxonomyDTO toDto(Taxonomy taxonomy);

    default Taxonomy fromId(Long id) {
        if (id == null) {
            return null;
        }
        Taxonomy taxonomy = new Taxonomy();
        taxonomy.setId(id);
        return taxonomy;
    }
}
