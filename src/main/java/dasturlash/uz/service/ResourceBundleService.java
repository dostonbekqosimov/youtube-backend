package dasturlash.uz.service;

import dasturlash.uz.enums.LanguageEnum;
import lombok.RequiredArgsConstructor;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.stereotype.Service;

import java.util.Locale;

@Service
@RequiredArgsConstructor
public class ResourceBundleService {

    private final ResourceBundleMessageSource resourceBundleMessageSource;

    public String getMessage(String code,LanguageEnum lang) {
        return resourceBundleMessageSource.getMessage(code, null, new Locale(lang.name()));
    }

}
