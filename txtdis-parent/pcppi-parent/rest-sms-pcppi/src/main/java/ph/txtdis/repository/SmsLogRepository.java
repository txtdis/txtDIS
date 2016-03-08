package ph.txtdis.repository;

import org.springframework.stereotype.Repository;

import ph.txtdis.domain.SmsLog;

@Repository("smsLogRepository")
public interface SmsLogRepository extends SpunRepository<SmsLog, Long> {
}
