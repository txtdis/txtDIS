package ph.txtdis.service;

import static java.lang.Long.parseLong;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import ph.txtdis.domain.InvoiceBookletEntity;
import ph.txtdis.dto.InvoiceBooklet;
import ph.txtdis.repository.InvoiceBookletRepository;

@Service("invoiceBookletService")
public class InvoiceBookletServiceImpl
		extends AbstractIdService<InvoiceBookletRepository, InvoiceBookletEntity, InvoiceBooklet, Long>
		implements InvoiceBookletService {

	@Value("${invoice.line.item.count}")
	private String linesPerPage;

	@Override
	public InvoiceBooklet findById(String prefix, Long id, String suffix) {
		InvoiceBookletEntity e = repository.findByPrefixAndSuffixAndStartIdLessThanEqualAndEndIdGreaterThanEqual(
				nullIfEmpty(prefix), nullIfEmpty(suffix), id, id);
		return toDTO(e);
	}

	@Override
	public InvoiceBooklet linesPerPage() {
		InvoiceBookletEntity e = new InvoiceBookletEntity();
		e.setEndId(parseLong(linesPerPage));
		return toDTO(e);
	}

	@Override
	public List<InvoiceBooklet> list() {
		List<InvoiceBookletEntity> l = repository.findByOrderByPrefixAscStartIdAscSuffixAsc();
		return l == null ? null : l.stream().map(e -> toDTO(e)).collect(Collectors.toList());
	}

	private String nullIfEmpty(String s) {
		return s.isEmpty() ? null : s;
	}

	@Override
	protected InvoiceBooklet toDTO(InvoiceBookletEntity e) {
		if (e == null)
			return null;
		InvoiceBooklet b = new InvoiceBooklet();
		b.setId(e.getId());
		b.setStartId(e.getStartId());
		b.setEndId(e.getEndId());
		b.setPrefix(e.getPrefix());
		b.setSuffix(e.getSuffix());
		b.setIssuedTo(e.getIssuedTo());
		b.setCreatedBy(e.getCreatedBy());
		b.setCreatedOn(e.getCreatedOn());
		return b;
	}

	@Override
	protected InvoiceBookletEntity toEntity(InvoiceBooklet t) {
		if (t == null)
			return null;
		InvoiceBookletEntity b = new InvoiceBookletEntity();
		b.setId(t.getId());
		b.setStartId(t.getStartId());
		b.setEndId(t.getEndId());
		b.setPrefix(t.getPrefix());
		b.setSuffix(t.getSuffix());
		b.setIssuedTo(t.getIssuedTo());
		b.setCreatedBy(t.getCreatedBy());
		b.setCreatedOn(t.getCreatedOn());
		return b;
	}
}