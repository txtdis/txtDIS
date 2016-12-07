package ph.txtdis.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ph.txtdis.service.BadRmaService;

@RequestMapping("/badRmas")
@RestController("badRmaController")
public class BadRmaController extends AbstractBillableController<BadRmaService> {
}