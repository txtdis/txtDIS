package ph.txtdis.dyvek.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ph.txtdis.dyvek.domain.BillableEntity;
import ph.txtdis.dyvek.model.Billable;
import ph.txtdis.dyvek.service.server.ClientBillAssignmentService;

@RequestMapping("/clientBillAssignments")
@RestController("clientBillAssigmentController")
public class ClientBillAssigmentController
		extends AbstractOpenListedSearchedSpunSavedController<ClientBillAssignmentService, BillableEntity, Billable> {
}