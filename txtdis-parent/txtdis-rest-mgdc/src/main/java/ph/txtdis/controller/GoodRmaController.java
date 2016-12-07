package ph.txtdis.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ph.txtdis.service.GoodRmaService;

@RequestMapping("/goodRmas")
@RestController("goodRmaController")
public class GoodRmaController extends AbstractBillableController<GoodRmaService> {
}