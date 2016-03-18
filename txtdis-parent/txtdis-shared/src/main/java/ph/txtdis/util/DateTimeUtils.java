package ph.txtdis.util;

import static java.time.LocalDate.now;
import static java.time.LocalDate.of;
import static java.time.ZoneId.systemDefault;
import static java.time.ZonedDateTime.parse;
import static java.time.format.DateTimeFormatter.ofPattern;
import static java.util.Date.from;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;

public class DateTimeUtils {

	private static DateTimeFormatter dateFormat() {
		return ofPattern("M/d/yyyy");
	}

	public static ZonedDateTime endOfDay(Date d) {
		return d == null ? null : endOfDay(d.toInstant().atZone(zoneHere()).toLocalDate());
	}

	public static ZonedDateTime endOfDay(LocalDate d) {
		return d == null ? null : d.plusDays(1L).atStartOfDay(zoneHere());
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

	private static LocalDate parseDate(String date) {
		try {
			return LocalDate.parse(date, dateFormat());
		} catch (Exception e) {
			return LocalDate.parse(date);
		}
	}

	public static ZonedDateTime startOfDay(Date d) {
		return d == null ? null : startOfDay(d.toInstant().atZone(zoneHere()).toLocalDate());
	}

	public static ZonedDateTime startOfDay(LocalDate d) {
		return d == null ? null : d.atStartOfDay(zoneHere());
	}

	public static LocalDate startOfMonth(LocalDate d) {
		return d == null ? now() : of(d.getYear(), d.getMonthValue(), 1);
	}

	private static DateTimeFormatter timestampFormat() {
		return ofPattern("M/d/yyyy h:mma");
	}

	public static LocalDate toDate(String date) {
		return date == null ? null : parseDate(date);
	}

	public static String toDateDisplay(LocalDate d) {
		return d == null ? "" : d.format(dateFormat());
	}

	public static String toDottedYearMonth(LocalDate d) {
		return d == null ? "" : d.format(ofPattern("yyyy.MM"));
	}

	public static String toFullMonthYear(LocalDate d) {
		return d == null ? "" : d.format(ofPattern("MMMM yyyy"));
	}

	public static String toHypenatedYearMonthDay(LocalDate d) {
		return d == null ? "" : d.format(ofPattern("yyyy-MM-dd"));
	}

	public static String toLongMonthYear(LocalDate d) {
		return d == null ? "" : d.format(ofPattern("MMM-yyyy"));
	}

	public static LocalTime toTime(String s) {
		return s == null ? null : LocalTime.parse(s, ofPattern("Hmm"));
	}

	public static String toTimeDisplay(LocalTime t) {
		return t == null ? null : t.format(ofPattern("hh:mma"));
	}

	public static String toTimestampFilename(ZonedDateTime zdt) {
		return zdt == null ? "" : zdt.withZoneSameInstant(zoneHere()).format(ofPattern("yyyy-MM-dd@hh.mma"));
	}

	public static String toTimestampText(Date d) {
		return d == null ? "" : toTimestampText(d.toInstant().atZone(zoneHere()));
	}

	public static String toTimestampText(ZonedDateTime zdt) {
		return zdt == null ? "" : zdt.withZoneSameInstant(zoneHere()).format(timestampFormat());
	}

	public static Date toUtilDate(LocalDate d) {
		return d == null ? null : from(d.atStartOfDay(zoneHere()).toInstant());
	}

	public static Date toUtilDate(String date) {
		LocalDate d = toDate(date);
		return toUtilDate(d);
	}

	public static ZonedDateTime toZonedDateTime(Date d) {
		return d == null ? null : ZonedDateTime.ofInstant(d.toInstant(), ZoneId.systemDefault());
	}

	public static ZonedDateTime toZonedDateTime(String zdt) {
		return zdt == null ? null : parse(zdt, timestampFormat());
	}

	private static ZoneId zoneHere() {
		return systemDefault();
	}
}
