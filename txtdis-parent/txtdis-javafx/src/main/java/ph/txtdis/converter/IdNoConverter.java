package ph.txtdis.converter;

import javafx.util.StringConverter;
import ph.txtdis.util.NumberUtils;

public class IdNoConverter extends StringConverter<Long> {

    @Override
    public String toString(Long number) {
        return NumberUtils.toIdText(number);
    }

    @Override
    public Long fromString(String text) {
        return NumberUtils.toLong(text);
    }
}
