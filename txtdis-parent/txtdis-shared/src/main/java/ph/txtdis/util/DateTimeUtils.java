package ph.txtdis.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;

import ph.txtdis.exception.DateBeforeGoLiveException;
import ph.txtdis.exception.EndDateBeforeStartException;

public class DateTimeUtils {

	private static final ZoneId MANILA_TIME = ZoneId.of("Asia/Manila");

	public static ZonedDateTime endOfDay(Date d) {
		return d == null ? null : endOfDay(toLocalDate(d));
	}

	public static ZonedDateTime endOfDay(LocalDate d) {
		return d == null ? null : startOfDay(d.plusDays(1L));
	}

	public static LocalDate endOfMonth(LocalDate d) {
		return startOfMonth(d).plusMonths(1L).minusDays(1L);
	}

	public static Date epochDate() {
		return toUtilDate(LocalDate.of(1970, 1, 1));
	}

	public static LocalDate fromVersionToDate(String version) {
		String[] d = StringUtils.split(version, ".");
		String date = "20" + d[0] + d[1] + "-0" + d[2] + "-0" + d[3];
		return LocalDate.parse(date);
	}

	public static DateTimeFormatter orderConfirmationFormat() {
		return DateTimeFormatter.ofPattern("yyyyMMdd");
	}

	private static LocalDate parseDate(String date) {
		try {
			return LocalDate.parse(date, shortDateFormat());
		} catch (Exception e) {
			return LocalDate.parse(date);
		}
	}

	public static DateTimeFormatter shortDateFormat() {
		return DateTimeFormatter.ofPattern("M/d/yyyy");
	}

	public static ZonedDateTime startOfDay(Date d) {
		return d == null ? null : startOfDay(toLocalDate(d));
	}

	public static ZonedDateTime startOfDay(LocalDate d) {
		return d == null ? null : d.atStartOfDay(MANILA_TIME);
	}

	public static LocalDate startOfMonth(LocalDate d) {
		return d == null ? LocalDate.now() : LocalDate.of(d.getYear(), d.getMonthValue(), 1);
	}

	public static DateTimeFormatter timestampFormat() {
		return DateTimeFormatter.ofPattern("M/d/yyyy h:mma");
	}

	public static DateTimeFormatter timestampOf24HourFormat() {
		return DateTimeFormatter.ofPattern("M/d/yyyy HH:mm");
	}

	public static DateTimeFormatter timestampWithSecondFormat() {
		return DateTimeFormatter.ofPattern("M/d/yyyy h:mm:ss a");
	}

	public static LocalDate toDate(String date) {
		return date == null || date.isEmpty() ? null : parseDate(date);
	}

	public static String toDateDisplay(LocalDate d) {
		return d == null ? "" : d.format(shortDateFormat());
	}

	public static String toDottedYearMonth(LocalDate d) {
		return d == null ? "" : d.format(DateTimeFormatter.ofPattern("yyyy.MM"));
	}

	public static String toFullMonthYear(LocalDate d) {
		return d == null ? "" : d.format(DateTimeFormatter.ofPattern("MMMM yyyy"));
	}

	public static String toHypenatedYearMonthDay(LocalDate d) {
		return d == null ? "" : d.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
	}

	private static LocalDate toLocalDate(Date d) {
		return d.toInstant().atZone(MANILA_TIME).toLocalDate();
	}

	public static String toLongMonthYear(LocalDate d) {
		return d == null ? "" : d.format(DateTimeFormatter.ofPattern("MMM-yyyy"));
	}

	public static String toOrderConfirmationDate(LocalDate d) {
		return d == null ? "" : d.format(orderConfirmationFormat());
	}

	public static LocalTime toTime(String s) {
		return s == null ? null : LocalTime.parse(s, DateTimeFormatter.ofPattern("Hmm"));
	}

	public static String toTimeDisplay(LocalTime t) {
		return t == null ? null : t.format(DateTimeFormatter.ofPattern("hh:mma"));
	}

	public static String toTimestampFilename(ZonedDateTime zdt) {
		return zdt == null ? ""
				: zdt.withZoneSameInstant(MANILA_TIME).format(DateTimeFormatter.ofPattern("yyyy-MM-dd@hh.mma"));
	}

	public static String toTimestampText(Date d) {
		return d == null ? "" : toTimestampText(toZonedDateTime(d));
	}

	public static String toTimestampText(ZonedDateTime zdt) {
		return zdt == null ? "" : zdt.withZoneSameInstant(MANILA_TIME).format(timestampFormat());
	}

	public static String to24HourTimestampText(ZonedDateTime zdt) {
		return zdt == null ? "" : zdt.withZoneSameInstant(MANILA_TIME).format(timestampOf24HourFormat());
	}

	public static String toTimestampWithSecondText(ZonedDateTime zdt) {
		return zdt == null ? "" : zdt.withZoneSameInstant(MANILA_TIME).format(timestampWithSecondFormat());
	}

	public static Date toUtilDate(LocalDate d) {
		return d == null ? null : Date.from(startOfDay(d).toInstant());
	}

	public static Date toUtilDate(String date) {
		LocalDate d = toDate(date);
		return toUtilDate(d);
	}

	public static ZonedDateTime toZonedDateTime(Date d) {
		return d == null ? null : toZonedDateTime(toLocalDate(d));
	}

	public static ZonedDateTime toZonedDateTime(LocalDate d) {
		return d == null ? null : startOfDay(d);
	}

	public static ZonedDateTime toZonedDateTime(String zdt) {
		return zdt == null || zdt.isEmpty() ? null : toZonedDateTime(zdt, timestampFormat());
	}

	private static ZonedDateTime toZonedDateTime(String zdt, DateTimeFormatter timestampFormat) {
		LocalDateTime ldt = LocalDateTime.parse(zdt, timestampFormat);
		return ZonedDateTime.of(ldt, MANILA_TIME);
	}

	public static ZonedDateTime toZonedDateTimeFromDate(String date) {
		return date == null || date.isEmpty() ? null : startOfDay(parseDate(date));
	}

	public static ZonedDateTime toZonedDateTimeFromTimestamp(String zdt) {
		return zdt == null || zdt.isEmpty() ? null : toZonedDateTime(zdt);
	}

	public static ZonedDateTime toZonedDateTimeFromTimestampWithSeconds(String zdt) {
		return zdt == null || zdt.isEmpty() ? null : toZonedDateTime(zdt, timestampWithSecondFormat());
	}

	public static ZonedDateTime toZonedDateTimeFrom24HourTimestamp(String zdt) {
		return zdt == null || zdt.isEmpty() ? null : toZonedDateTime(zdt, timestampOf24HourFormat());
	}

	public static LocalDate validateEndDate(LocalDate startDate, LocalDate endDate, LocalDate goLive)
			throws DateBeforeGoLiveException, EndDateBeforeStartException {
		verifyDateIsOnOrAfterGoLive(endDate, goLive);
		if (endDate.isBefore(startDate))
			throw new EndDateBeforeStartException();
		return endDate;
	}

	public static LocalDate verifyDateIsOnOrAfterGoLive(LocalDate date, LocalDate goLive)
			throws DateBeforeGoLiveException {
		if (date.isBefore(goLive))
			throw new DateBeforeGoLiveException();
		return date;
	}

	public static int weekNo(LocalDate date) {
		int day = date.getDayOfMonth();
		int month = date.getMonthValue() - 1;
		int year = date.getYear();
		Calendar cal = Calendar.getInstance();
		cal.set(year, month, day);
		return cal.get(Calendar.WEEK_OF_MONTH);
	}
}
