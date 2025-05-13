package com.nexigroup.pagopa.cruscotto.service.impl;

import com.nexigroup.pagopa.cruscotto.domain.Module;
import com.nexigroup.pagopa.cruscotto.repository.ModuleRepository;
import com.nexigroup.pagopa.cruscotto.service.ModuleService;
import com.nexigroup.pagopa.cruscotto.service.dto.ModuleDTO;
import com.nexigroup.pagopa.cruscotto.service.mapper.ModuleMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link Module}.
 */
@Service
@Transactional
public class ModuleServiceImpl implements ModuleService {

    private final Logger log = LoggerFactory.getLogger(ModuleServiceImpl.class);

    private final ModuleRepository moduleRepository;

    private final ModuleMapper moduleMapper;

    public ModuleServiceImpl(ModuleRepository moduleRepository, ModuleMapper moduleMapper) {
        this.moduleRepository = moduleRepository;
        this.moduleMapper = moduleMapper;
    }

    @Override
    public Optional<ModuleDTO> findOne(Long id) {
        return moduleRepository.findById(id).map(moduleMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        moduleRepository.deleteById(id);
    }
}
