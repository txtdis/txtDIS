package ph.txtdis.service;

import ph.txtdis.dto.SalesRevenue;

public interface SalesRevenueService
		extends BilledAllPickedSalesOrder, ReportService<SalesRevenue>, SellerFiltered<SalesRevenue> {
}