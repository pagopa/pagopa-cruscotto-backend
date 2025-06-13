package com.nexigroup.pagopa.cruscotto.web.rest;

import com.nexigroup.pagopa.cruscotto.service.PagoPaPaymentReceiptService;
import com.nexigroup.pagopa.cruscotto.service.dto.PagoPaPaymentReceiptDTO;
import com.nexigroup.pagopa.cruscotto.service.filter.PagoPaPaymentReceiptFilter;
import io.swagger.v3.oas.annotations.Parameter;
import java.util.List;

import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.PaginationUtil;

/**
 * REST controller for managing PagoPA payment receipts.
 * <p>
 * This controller provides endpoints for retrieving PagoPA payment receipt data,
 * supporting operations such as paginated retrieval of payment receipt records.
 */
@RestController
@RequestMapping("/api")
public class PagoPaPaymentReceiptResource {

    private final Logger log = LoggerFactory.getLogger(PagoPaPaymentReceiptResource.class);

    private final PagoPaPaymentReceiptService pagoPaPaymentReceiptService;

    public PagoPaPaymentReceiptResource(PagoPaPaymentReceiptService pagoPaPaymentReceiptService) {
        this.pagoPaPaymentReceiptService = pagoPaPaymentReceiptService;
    }

    /**
     * Retrieves all PagoPA payment receipts by filter and with pagination.
     *
     * @param pageable the pagination information, including page number, size, and sort order.
     * @param filter the filter the requested entities should match.
     *
     * @return a {@link ResponseEntity} containing a {@link List} of {@link PagoPaPaymentReceiptDTO}
     *         representing the PagoPA payment receipts, along with pagination headers.
     */
    @GetMapping("/pago-pa/payment-receipt")
    public ResponseEntity<List<PagoPaPaymentReceiptDTO>> getAllPagoPaPaymentReceipt(
        @Parameter(description = "Filtro") @Valid @ParameterObject PagoPaPaymentReceiptFilter filter,
        @Parameter(description = "Pageable", required = true) @ParameterObject Pageable pageable
    ) {
        log.debug("REST request to get all pagoPA payment receipt by filter {}", filter);
        Page<PagoPaPaymentReceiptDTO> page = pagoPaPaymentReceiptService.findAll(filter, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }
}
