package ph.txtdis.mgdc.service;

import ph.txtdis.dto.SalesRevenue;
import ph.txtdis.service.ReportService;

public interface SalesRevenueService //
		extends ReportService<SalesRevenue>, SellerFilteredService<SalesRevenue>, VerifiedSalesOrderService {
}