package dasturlash.uz.util;

import dasturlash.uz.enums.LanguageEnum;

public class LanguageUtil {

    public static LanguageEnum getLanguageFromHeader(String languageHeader) {
        LanguageEnum lang;
        try {
            lang = LanguageEnum.valueOf(
                    languageHeader.contains(",")
                            ? languageHeader.split(",")[0].split("-")[0].toLowerCase()
                            : languageHeader.split("-")[0].toLowerCase()
            );
        } catch (Exception e) {
            lang = LanguageEnum.uz; // default language
        }
        return lang;
    }
}
